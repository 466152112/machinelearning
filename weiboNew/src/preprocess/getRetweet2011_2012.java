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

public class getRetweet2011_2012 {

	String path = "/media/pc/new2/data/wordsplit/";
	String splitfile2011=path+"2011/";
	String splitfile2012=path+"2012/";
	String retweetFile=path+"retweet.txt";
	String userWeiboFile2011 = "/media/pc/new2/data/2011/content/";
	String userWeiboFile2012 = "/media/pc/new2/data/2012/content/";
	AvlTree<Long> retweetidAvlTree = new AvlTree<Long>();

	public static void main(String[] args) {
		getRetweet2011_2012 termSplit = new getRetweet2011_2012();
		termSplit.run();
	}

	public void run() {

		try (BufferedWriter weibowrite = new BufferedWriter(new FileWriter(new File(retweetFile)));){
			for (int i = 1; i < 2; i++) {

				if (i == 0) {
					File file = new File(splitfile2011);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						getretweetid(file2);
					}
				} else {
					File file = new File(splitfile2012);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						getretweetid(file2);
					}

				}

			}
			
			for (int i = 1; i < 2; i++) {

				if (i == 0) {
					File file = new File(userWeiboFile2011);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						getRetweetContent(file2, weibowrite);
					}
				} else {
					File file = new File(userWeiboFile2012);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						getRetweetContent(file2, weibowrite);
					}

				}

			}

			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// getBPRInParallel();
	}

	public void getretweetid(File file2) {

		BufferedReader weiboReader;
		try {
			weiboReader = new BufferedReader(new FileReader(file2));
			String temp;
			List<OnePairTweet> list = new ArrayList<>();
			while ((temp = weiboReader.readLine()) != null) {
				OnePairTweet onePairTweet = OnePairTweet.covert(temp);
				if (onePairTweet==null) {
					continue;
				}
				if(onePairTweet.getRetweetId()!=0L){
					retweetidAvlTree.insert(onePairTweet.getRetweetId());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param source
	 * @param spliterWriter
	 *@create_time：2014年10月10日上午10:46:48
	 *@modifie_time：2014年10月10日 上午10:46:48
	  
	 */
	public void getRetweetContent(File source,BufferedWriter spliterWriter) {

		BufferedReader weiboReader;
		try {
			weiboReader = new BufferedReader(new FileReader(source));
			String temp;
			ChineseSpliter chineseSpliter = new ChineseSpliter();
			while ((temp = weiboReader.readLine()) != null) {
				OnePairTweet onePairTweet = OnePairTweet.covert(temp);
				if (onePairTweet==null) {
					continue;
				}
				if(retweetidAvlTree.contains(onePairTweet.getWeiboId())){
					String splitList = chineseSpliter.spliter(onePairTweet
							.getContent());
					if (splitList != null && !splitList.trim().equals("")) {
						onePairTweet.setContent(splitList);
						spliterWriter.write(onePairTweet.toString());
						spliterWriter.newLine();
					}
				}
			}
			spliterWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
