package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
public class weiboTermSplit2011_2012 {

	private static ForkJoinPool forkJoinPool;
	private List<OnePairTweet> tweetsList = new ArrayList<>();
	String path = "/media/new2/data/wordsplit/";
	String userWeiboFile2011 = "/media/new2/data/2011/content";
	String userWeiboFile2012 = "/media/new2/data/2012/content";
	//String resultString = path + "/Split2011-2012.txt";

	public static void main(String[] args) {
		weiboTermSplit2011_2012 termSplit = new weiboTermSplit2011_2012();
		termSplit.run();
	}

	public void run() {

		try {
			long count = 0;
			for (int i = 1; i < 2; i++) {
				BufferedReader weiboReader = null;

				if (i == 0) {
					File file = new File(userWeiboFile2011);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						BufferedWriter spliterWriter = new BufferedWriter(
								new FileWriter(path + "2011/" + file2.getName(),
										true));
						split(spliterWriter, file2.toString());
					}
				} else {
					File file = new File(userWeiboFile2012);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						BufferedWriter spliterWriter = new BufferedWriter(
								new FileWriter(path + "2012/" + file2.getName(),
										true));
						split(spliterWriter, file2.toString());
					}

				}

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	//	getBPRInParallel();
	}

	public void split(BufferedWriter spliterWriter, String sourceFile) {
		BufferedReader weiboReader;
		try {
			weiboReader = new BufferedReader(new FileReader(sourceFile));
			String temp;
			List<OnePairTweet> list=new ArrayList<>();
			ChineseSpliter chineseSpliter = new ChineseSpliter();
			while ((temp = weiboReader.readLine()) != null) {
				OnePairTweet onePairTweet = OnePairTweet.covert(temp);
				if (onePairTweet == null) {
					continue;
				}
				list.add(onePairTweet);
				//tweetsList.add(onePairTweet);
				// eaco 1000000 deal one
				if (list.size() > 100000) {
					// getBPRInParallel(spliterWriter);
				//	 tweetsList=new ArrayList<>();
					for (OnePairTweet tempTweet : list) {
						
						String splitList = chineseSpliter.spliter(tempTweet
								.getContent());
						if (splitList != null && !splitList.trim().equals("")) {
							tempTweet.setContent(splitList);
							spliterWriter.write(tempTweet.toString());
							spliterWriter.newLine();
						}
					}
					list=new ArrayList<>();
				}
			}
//			getBPRInParallel(spliterWriter);
//			tweetsList=new ArrayList<>();
			for (OnePairTweet tempTweet : list) {
				String splitList = chineseSpliter.spliter(tempTweet.getContent());
				if (splitList != null && !splitList.trim().equals("")) {
					tempTweet.setContent(splitList);
					spliterWriter.write(tempTweet.toString());
					spliterWriter.newLine();
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @return
	 * @create_time�?014�?�?3日下�?:59:26
	 * @modifie_time�?014�?�?3�?下午6:59:26
	 */
	public void getBPRInParallel(BufferedWriter spliterWriter) {
		forkJoinPool.invoke(new ChineseSpliterTask(spliterWriter));

	}

	class ChineseSpliterTask extends RecursiveTask<String> {
		final  BufferedWriter spliterWriter;
		/**
		 * @param resultString
		 */
		public ChineseSpliterTask(BufferedWriter spliterWriter) {
			// TODO Auto-generated constructor stub
			this.spliterWriter=spliterWriter;
		}

		@Override
		protected String compute() {
			List<RecursiveTask<String>> forks = new LinkedList<>();
			for (OnePairTweet onePairTweet : tweetsList) {
				ChineseSpliter chineseSpliter = new ChineseSpliter(onePairTweet);
				forks.add(chineseSpliter);
				chineseSpliter.fork();
			}

			try{
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
