package Remind;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import util.CalanderUtil;
import util.FileUtil;
import util.Filter;
import util.MathCal;
import util.ReadUser;
import util.ReadWeiboUtil;
import util.WriteUtil;
import weibo.util.ReadUtil;
import bean.AvlTree;
import bean.OnePairTweet;
import bean.RemType;
import bean.Reminder;
import bean.Retweetlist;
import bean.User;

public class staticInTime {
	public static void main(String[] srg) {
		String path = "J:/workspacedata/weiboNew/data/reminder/";
		String sourcePath = "J:/workspacedata/weiboNew/data/2012/content/";
		// String path = "/home/zhouge/database/weibo/new/";
		// String sourcePath="/home/zhouge/database/weibo/2012/content/";
		String profileFile = path + "userid/profile.txt";
		String userReminHistoryFile = path + "userid/userReminHistory.txt";
		String reminderFile = path + "rem.txt";
		String TimeDistributionFile=path+"TimeDistrution.txt";
		// String followGraph = path + "userid/FollowGraph.txt";
//		AvlTree<User> useravl = BaseUitl
//				.readUserRemHistory(userReminHistoryFile);
//		useravl = BaseUitl.addProfileNameToReminHistory(useravl, profileFile);
		// useravl=BaseUitl.addFollowee(useravl, followGraph);
		staticInTime staticInTime = new staticInTime();
		//staticInTime.calTimeDifferenceInOriginal(sourcePath, useravl,path);
		//staticInTime.calTimeDifferenceInRemFile(reminderFile,useravl,path);
		staticInTime.TimeDisInResultFile(TimeDistributionFile);
	}
	public void TimeDisInResultFile(String TimeDisFile){
		int[] retweet=new int[500];
		int[] rem=new int[500];
		double sumrem=0;
		double sumretweet=0;
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> list;
		try {
			list = readUtil.readfromFile(TimeDisFile);
			for (String oneLine : list) {
				String[] splits=oneLine.split("\t");
				int time=Integer.valueOf(splits[0]);
				int retweetcount=Integer.valueOf(splits[1]);
				int remcount=Integer.valueOf(splits[2]);
				time=time/(60*60);
				retweet[time]+=retweetcount;
				rem[time]+=remcount;
				sumrem+=remcount;
				sumretweet+=retweetcount;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double remCount=0,retweetCount=0;
		for (int i = 0; i <500; i++) {
			retweetCount+=retweet[i];
			remCount+=rem[i];
			System.out.println(i+"\t"+retweetCount/sumretweet+"\t"+remCount/sumrem);
		}
	}
	
	public void calTimeDifferenceInRemFile(String reminderFile,AvlTree<User> useravl,String path){
		
		int[] attachNumber=new int[10*24*60*6];
		
		try (BufferedReader reader = new BufferedReader(new FileReader(
				reminderFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				for(Reminder reminder :remType.getReminders()){
					if(reminder.isIfRely()){
						int differ=CalanderUtil.getTimeDifferInSeconds(remType.getRoot().getCalendar(), reminder.getReplycalCalendar());
						differ/=10;
						if(differ<10*24*60*6){
							attachNumber[differ]++;
						}
						
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> result=new ArrayList<>();
		for (int i = 0; i < 10*24*60*6; i++) {
			int number=(i+1)*10;
			if(attachNumber[i]==0)
				continue;
			result.add(number+"\t"+attachNumber[i]);
		}
		WriteUtil<String> writeUtil=new WriteUtil<String>();
		writeUtil.writeList(result, path+"TimeDistrution.txt");
	}
	
	
	
	/**
	 * @param path
	 * @param useravl
	 *@create_time：2014年11月4日下午9:29:14
	 *@modifie_time：2014年11月4日 下午9:29:14
	  
	 */
	public void calTimeDifferenceInOriginal(String sourcePath, AvlTree<User> useravl,String path) {
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		int[] attachNumber=new int[24*60*10];
		for (String OneFile : fileList) {
			ReadWeiboUtil rw = new ReadWeiboUtil(OneFile);
			Retweetlist listRetweetlist;

			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null||listRetweetlist.getRoot().getRetweetId()!=0)
					continue;
				User rootUser = new User();
				rootUser.setUserId(listRetweetlist.getRoot().getUserId());
				rootUser = useravl.getElement(rootUser);
				if (rootUser == null) {
					continue;
				}
				if (listRetweetlist.getRetweetList() != null
						&& listRetweetlist.getRetweetList().size() != 0) {
					for (OnePairTweet onePairTweet : listRetweetlist.getRetweetList()) {
						if (onePairTweet.getContent().indexOf("@")==-1&&onePairTweet.getContent().indexOf("//@")==-1) {
							int differ=CalanderUtil.getTimeDifferInSeconds(listRetweetlist.getRoot().getCalendar(), onePairTweet.getCalendar());
							differ/=10;
							if(differ<24*60*10){
								attachNumber[differ]++;
							}
						}
					}

				}
			}
		}
		
		List<String> result=new ArrayList<>();
		for (int i = 0; i < 24*60*10; i++) {
			int number=(i+1)*10;
			if(attachNumber[i]==0)
				continue;
			result.add(number+"\t"+attachNumber[i]);
		}
		WriteUtil<String> writeUtil=new WriteUtil<String>();
		writeUtil.writeList(result, path+"TimeDistrutionInsource.txt");
	}

	public List<Integer> retweetRouteTimeInSecondLevel(Retweetlist oneList,
			AvlTree<User> useravl) {
		List<OnePairTweet> listRetweet = oneList.getRetweetListSortByTime();
		List<Integer> retweetTimeSequnce = new ArrayList<>();
		
		for (int i = 0; i < listRetweet.size(); i++) {
			OnePairTweet onePairTweet = listRetweet.get(i);
			int diff = 0;
			if (onePairTweet.getContent().indexOf("//@") == -1) {
				diff = CalanderUtil.getTimeDifferInSeconds(oneList.getRoot()
						.getCalendar(), onePairTweet.getCalendar());
				retweetTimeSequnce.add(diff);
			}
		}
		return retweetTimeSequnce;
	}

	/**
	 * @param oneList
	 * @param useravl
	 * @return
	 * @create_time：2014年11月4日下午8:40:28
	 * @modifie_time：2014年11月4日 下午8:40:28
	 */
	public int[] retweetRouteTimeSequence(Retweetlist oneList,
			AvlTree<User> useravl) {
		List<OnePairTweet> listRetweet = oneList.getRetweetListSortByTime();
		int[] retweetTimeSequnce = new int[listRetweet.size()];

		HashMap<String, Long> usernameAndIdMap = new HashMap<>();
		HashMap<String, OnePairTweet> usernameAndTheFirstTweet = new HashMap<>();

		for (int i = 0; i < listRetweet.size(); i++) {
			OnePairTweet onePairTweet = listRetweet.get(i);
			int diff = 0;
			if (onePairTweet.getContent().indexOf("//@") != -1) {

				// get the route name from string
				ArrayList<String> routeNameList = Filter
						.getRouteNameFromString(onePairTweet.getContent());

				// judge if the two collection have joint

				// if have user in common ,then find the first common user
				if (!Collections.disjoint(usernameAndIdMap.keySet(),
						routeNameList)) {
					// the function : At least one collection is empty. Nothing
					// will match.
					for (String userNameInContent : routeNameList) {
						if (usernameAndTheFirstTweet
								.containsKey(userNameInContent)) {
							diff = CalanderUtil.getTimeDifferInSeconds(
									usernameAndTheFirstTweet.get(
											userNameInContent).getCalendar(),
									onePairTweet.getCalendar());
							break;
						}
					}
				}
				// if no then think the retweet from the root
				else {
					diff = CalanderUtil.getTimeDifferInSeconds(oneList
							.getRoot().getCalendar(), onePairTweet
							.getCalendar());
				}

			}
			// come from the root weibo
			else {
				diff = CalanderUtil.getTimeDifferInSeconds(oneList.getRoot()
						.getCalendar(), onePairTweet.getCalendar());
			}
			retweetTimeSequnce[i] = diff;

			User user = new User();
			user.setUserId(onePairTweet.getUserId());
			user = useravl.getElement(user);
			usernameAndIdMap.put(user.getUserName(), user.getUserId());
			if (!usernameAndTheFirstTweet.containsKey(user.getUserName())) {
				usernameAndTheFirstTweet.put(user.getUserName(), onePairTweet);
			}
		}
		return retweetTimeSequnce;
	}

}
