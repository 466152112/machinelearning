package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bean.AvlTree;
import bean.AvlTree.AvlNode;
import bean.OnePairTweet;
import bean.User;

public class CalUserWeiboNumber {

	String path = "/media/pc/new2/data/wordsplit/";
	String splitfile2011=path+"2011/";
	String splitfile2012=path+"2012/";
	String CalUserWeiboNumberFile=path+"tweetAndretweetCal.txt";
	AvlTree<User> userAvlTree = new AvlTree<>();
	BufferedWriter weibowrite =null;
	public static void main(String[] args) {
		CalUserWeiboNumber termSplit = new CalUserWeiboNumber();
		termSplit.run();
	}

	public void run() {

		try {
			weibowrite= new BufferedWriter(new FileWriter(new File(CalUserWeiboNumberFile)));
			for (int i = 1; i < 2; i++) {

				if (i == 0) {
					File file = new File(splitfile2011);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						getweiboNumber(file2);
					}
				} else {
					File file = new File(splitfile2012);
					File[] listFiles = file.listFiles();
					for (File file2 : listFiles) {
						getweiboNumber(file2);
					}

				}

			}
			inordrtraversa(userAvlTree.root);
			weibowrite.flush();
			weibowrite.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// getBPRInParallel();
	}

	public void getweiboNumber(File file2 ) {

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
				User user=new User();
				user.setUserId(onePairTweet.getUserId());
				if (userAvlTree.contains(user)) {
					if (onePairTweet.getRetweetId()==0L) {
						int number=userAvlTree.getElement(user, userAvlTree.root).getWeiboNumber();
						number++;
						userAvlTree.getElement(user, userAvlTree.root).setWeiboNumber(number);
					}else {
						int number=userAvlTree.getElement(user, userAvlTree.root).getRetweetNumber();
						number++;
						userAvlTree.getElement(user, userAvlTree.root).setRetweetNumber(number);
					}
				}else {
					if (onePairTweet.getRetweetId()==0L) {
						user.setWeiboNumber(1);
						user.setRetweetNumber(0);
					}else {
						user.setRetweetNumber(1);
						user.setWeiboNumber(0);
					}
					userAvlTree.insert(user);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void inordrtraversa(AvlNode<User> root){
		if(root!=null){
			inordrtraversa(root.left);
			
			inordrtraversa(root.right);
			
			User ele=root.getElement();
			try {
				weibowrite.write(ele.getUserId()+"\t"+ele.getWeiboNumber()+"\t"+ele.getRetweetNumber());
				weibowrite.newLine();
				weibowrite.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
}

