package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.ChineseSpliter;
import util.ReadUtil;
import bean.AvlTree;
import bean.AvlTree.AvlNode;
import bean.OnePairTweet;
import bean.User;

public class getUserTopic {

	static String path = "/media/pc/new2/data/new/";
	String splitfileByDate = path + "weibocontent/";

	final static int topicNumber = 100;

	// String CalUserWeiboNumberFile=path+"tweetAndretweetCal.txt";
	static String usertopicFile = path + "topic/userTopic.txt";
	static String useridFile = path + "userid/useridweiboNumber.txt";
	static String wordtopicmap = path + "topic/topic.txt";
//	static String ret11File="/home/zhouge/database/weibo/2011/content/retweetType19.txt";
//	static String ret12File="/home/zhouge/database/weibo/2012/content/retweetType19.txt";
	
	static HashMap<String, Integer> wordmap = null;
	static AvlTree<User> userAvlTree = new AvlTree<>();

	BufferedWriter weibowrite = null;

	public static void main(String[] args) {
		getUserTopic termSplit = new getUserTopic();

		termSplit.wordmap(wordtopicmap);

		termSplit.run();

	}

	public AvlTree<User> getuserid(String userIdFile) {
		AvlTree<User> result = new AvlTree<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				userIdFile));) {
			String onelineString = "";
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				User user = new User();
				user.setUserId(Long.valueOf(split[0]));
				double[] feature = new double[topicNumber];
				user.setFeature(feature);
				result.insert(user);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	

	public void run() {

		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(
				useridFile));) {

			weibowrite = new BufferedWriter(new FileWriter(new File(
					usertopicFile)));
			String onelineString = "";
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				User user = new User();
				user.setUserId(Long.valueOf(split[0]));
				double[] feature = new double[topicNumber];
				user.setFeature(feature);
				userAvlTree.insert(user);
				if (userAvlTree.root.height==19) {
					run2();
					userAvlTree=new AvlTree<User>();
				}
			}
			run2();
			weibowrite.flush();
			weibowrite.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// getBPRInParallel();
	}
	
	public void run2(){
		File file = new File(splitfileByDate);
		File[] listFiles = file.listFiles();
		for (File file2 : listFiles) {
			getuserTopicFromDayFile(file2);
		}
//		getuserTopicFromRetweetType(ret11File);
//		getuserTopicFromRetweetType(ret12File);
		
		inordrtraversa(userAvlTree.root);
	}
	public void wordmap(String fileName) {
		wordmap = new HashMap<>();
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> readList = readUtil.readFileByLine(fileName);
		for (String oneline : readList) {
			String[] split = oneline.split(" ");
			wordmap.put(split[0], Integer.valueOf(split[1]));
		}
	}
	public void getuserTopicFromDayFile(File file2) {

		BufferedReader weiboReader;
		ChineseSpliter chineseSpliter = new ChineseSpliter();
		try {
			weiboReader = new BufferedReader(new FileReader(file2));
			String temp;
			List<OnePairTweet> list = new ArrayList<>();
			while ((temp = weiboReader.readLine()) != null) {
				OnePairTweet onePairTweet = OnePairTweet.covert(temp);

				if (onePairTweet == null
						|| onePairTweet.getContent().equals("")) {
					continue;
				}

				User user = new User();
				user.setUserId(Long.valueOf(onePairTweet.getUserId()));

				if (userAvlTree.contains(user)
						&& onePairTweet.getRetweetId() == 0L) {
					double[] feature=getFeatureFromString(onePairTweet.getContent());
					userAvlTree.getElement(user, userAvlTree.root).addFeature(
							feature);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getuserTopicFromRetweetType(String file2) {

		BufferedReader weiboReader;

		try {
			weiboReader = new BufferedReader(new FileReader(new File(file2)));
			String temp;
			List<OnePairTweet> list = new ArrayList<>();
			double[] rootFeature=new double[topicNumber];
			temp = weiboReader.readLine();
			rootFeature=getFeatureFromString(OnePairTweet.covert(temp).getContent());
			
			while ((temp = weiboReader.readLine()) != null) {
				//if is null is nead to update the rootFeature
				if (temp.trim().equals("")) {
					temp = weiboReader.readLine();
					OnePairTweet onePairTweet = OnePairTweet.covert(temp);
					if(onePairTweet.getContent()==null){
						rootFeature=new double [topicNumber];
						System.out.println("error in : "+temp);
					}else {
						rootFeature=getFeatureFromString(onePairTweet.getContent());
						continue;
					}
					
				}
				
				OnePairTweet onePairTweet = OnePairTweet.covert(temp);
				if (onePairTweet == null
						|| onePairTweet.getContent().equals("")) {
					continue;
				}

				User user = new User();
				user.setUserId(Long.valueOf(onePairTweet.getUserId()));

				if (userAvlTree.contains(user)&& onePairTweet.getRetweetId() != 0L) {
					double[] feature=getFeatureFromString(onePairTweet.getContent());
					userAvlTree.getElement(user, userAvlTree.root).addFeature(
							feature);
					userAvlTree.getElement(user, userAvlTree.root).addFeature(
							rootFeature);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param root
	 *@create_time：2014年10月13日下午8:10:16
	 *@modifie_time：2014年10月13日 下午8:10:16
	  
	 */
	public void inordrtraversa(AvlNode<User> root) {
		if (root != null) {
			inordrtraversa(root.left);

			inordrtraversa(root.right);

			User ele = root.getElement();
			try {
				StringBuffer writeLine = new StringBuffer();
				writeLine.append(ele.getUserId() + "\t");
				double[] feature = ele.getFeature();
				double sum = 0;
				for (double d : feature) {
					sum += d;
				}
				for (double d : feature) {
					writeLine.append(d / sum + "\t");
				}
				weibowrite.write(writeLine.toString().trim());
				weibowrite.newLine();
				weibowrite.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * @param oneLine
	 * @return
	 *@create_time：2014年10月13日下午8:10:11
	 *@modifie_time：2014年10月13日 下午8:10:11
	  
	 */
	public  double[] getFeatureFromString(String oneLine) {
		ChineseSpliter chineseSpliter = new ChineseSpliter();
		ArrayList<String> splitList = chineseSpliter.spliterChinese(oneLine);
		double[] feature = new double[topicNumber];
		for (String d : splitList) {
			if (wordmap.containsKey(d)) {
				int index = wordmap.get(d);
				feature[index]++;
			}
			
		}
		return feature;
	}
}
