package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import util.ChineseSpliter;
import util.ReadUtil;
import util.ReadWeiboUtil;
import bean.AvlTree;
import bean.OnePairTweet;


/**   
*    
* @progject_name：weiboNew   
* @class_name：WeiboTermSplit   
* @class_describe�?  
* @creator：zhouge   
* @create_time�?014�?�?�?下午3:01:56   
* @modifier：zhouge   
* @modified_time�?014�?�?�?下午3:01:56   
* @modified_note�?  
* @version    
*    
*/
public class WeiboTermSplit {

//	private static int parallelNumber = 8;
//	private static ForkJoinPool forkJoinPool;
//	private List<OnePairTweet> tweetsList;
//	String path = "J:/workspace/weiboNew/";
//	String userWeiboFile = path + "sub.txt";
//	String retweetWeiboFile = path + "subR.txt";
//	String resultString = path + "Split.txt";
	
	private static int parallelNumber = 28;
	private static ForkJoinPool forkJoinPool;
	private List<OnePairTweet> tweetsList;
	String path = "/home/zhouge/database/weibo/20w/";
	//static String path = "J:/workspace/weiboNew/data/";
	String userWeiboFile = path + "userWeibo.txt";
	String retweetWeiboFile = path + "userRetweetWeibo.txt";
	String resultString = path + "Split1k.txt";
	String userList=path+"userList1k.txt";
	
	public static void main(String[] args) {
		WeiboTermSplit termSplit = new WeiboTermSplit();
		termSplit.run();
	}

	public void run() {

		// 并行�?
		forkJoinPool = new ForkJoinPool(parallelNumber);

		try (BufferedReader weiboReader = new BufferedReader(new FileReader(
				userWeiboFile));
			 BufferedReader userListReader=new BufferedReader(new FileReader(userList));
		) {
			OnePairTweet temp = null;
			long count = 0L;

			AvlTree<OnePairTweet> retweetTree = new AvlTree<>();
			AvlTree<Long> userIdTree=new AvlTree<Long>();
			AvlTree<Long> tweetIdTree=new AvlTree<>();
			ReadWeiboUtil readWeiboUtil = null;
			
			String tempLine="";
			readWeiboUtil = new ReadWeiboUtil(retweetWeiboFile);
			while ((temp = readWeiboUtil.readOnePairTweetInMen()) != null) {
				retweetTree.insert(temp);
			}
			
			while((tempLine =userListReader.readLine()) != null){
				userIdTree.insert(Long.valueOf(tempLine));
			}
			
			tweetsList = new ArrayList<>();
			// 读取微博信息
			while ((tempLine = weiboReader.readLine()) != null) {
				tempLine = tempLine.trim();
				long userId=OnePairTweet.covertUserId(tempLine);
				if (userId==0L||!userIdTree.contains(userId)) {
					continue;
				}
				long weiboId=OnePairTweet.crawlingWeiboId(tempLine);
				if(weiboId==0L||tweetIdTree.contains(weiboId)){
					continue;
				}
				
				OnePairTweet onePairTweet =OnePairTweet.covert(tempLine);
				
				if (onePairTweet != null) {
					// 添加转发微博的内容信�?
					if (onePairTweet.getRetweetId() != 0L) {
						OnePairTweet temPairTweet = new OnePairTweet();
						temPairTweet.setWeiboId(onePairTweet.getRetweetId());
						if (tweetIdTree.contains(onePairTweet.getRetweetId())) {
							continue;
						}
						temPairTweet = retweetTree.getElement(temPairTweet, retweetTree.root);

						if (temPairTweet != null) {
							tweetsList.add(temPairTweet);
							tweetIdTree.insert(temPairTweet.getWeiboId());
						}

					}
					tweetsList.add(onePairTweet);
					tweetIdTree.insert(onePairTweet.getWeiboId());
					//eaco 1000000 deal one 
					if (tweetsList.size()>100000) {
						getBPRInParallel();
						tweetsList=new ArrayList<>();
						System.out.println(count++);
					}
					
				}
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		getBPRInParallel();
	}

	/**
	 * @return
	 * @create_time�?014�?�?3日下�?:59:26
	 * @modifie_time�?014�?�?3�?下午6:59:26
	 */
	public void getBPRInParallel() {
		forkJoinPool.invoke(new ChineseSpliterTask());

	}

	class ChineseSpliterTask extends RecursiveTask<String> {

		@Override
		protected String compute() {
			List<RecursiveTask<String>> forks = new LinkedList<>();
			for (OnePairTweet onePairTweet : tweetsList) {
				ChineseSpliter chineseSpliter = new ChineseSpliter(	onePairTweet);
				forks.add(chineseSpliter);
				chineseSpliter.fork();
			}

			try (BufferedWriter spliterWriter = new BufferedWriter(
					new FileWriter(resultString,true));) {
				for (RecursiveTask<String> Task : forks) {
					spliterWriter.write(Task.join());
					spliterWriter.newLine();
				}
				spliterWriter.flush();
				spliterWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}
}
