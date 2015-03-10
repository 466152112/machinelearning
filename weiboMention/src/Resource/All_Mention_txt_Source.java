/**
 * 
 */
package Resource;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import bean.OnePairMention;
import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import tool.FileTool.FileUtil;
import tool.MyselfMath.MathCal;
import tool.data.DenseVector;
import tool.dataStucture.AvlTree;
import tool.io.FileIO;
import tool.io.Strings;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.Filter;
import Remind.extractFeature.tool.Feature_Covert;
import Resource.superClass.superResource;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：Mention_txt_Source
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月19日 上午11:00:21
 * @modifier：zhouge
 * @modified_time：2015年1月19日 上午11:00:21
 * @modified_note：
 * @version
 * 
 */
public class All_Mention_txt_Source extends superResource {
	final static String path = work_path + "intection/mention1/";
	/* target weibo content database */
	// final static String targetWeibo_file=path+"targetWeibo.txt";
	private static List<String> mention_file_SortByTime = null;
	ExecutorService executor = Executors.newFixedThreadPool(10);
	private Map<String, List<OnePairTweet>> MentionInDay=null;
	static {
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		mention_file_SortByTime = fileUtil.getFileListSortByTimeASC(path,
				dateFormat);
	}

	
	private AvlTree<User>  userTree=null;
	/**
	 * @param userTree the userTree to set
	 */
	public void setUserTree(AvlTree<User> userTree) {
		this.userTree = userTree;
	}
	
	/**
	 * get the mention static in day
	 *@create_time：2015年1月19日下午6:29:42
	 *@modifie_time：2015年1月19日 下午6:29:42
	  
	 */
	public void getDay_Count_Distribution(String resultFile){
		if (MentionInDay==null) {
			getMentionList(false);
		}
		List<String> result=new ArrayList<>();
		for (String dayName : MentionInDay.keySet()) {
			result.add(dayName+"\t"+MentionInDay.get(dayName).size());
		}
		try {
			FileIO.writeList(resultFile, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 基本统计mention的用户之间的关系情况
	 * 包括：1：是否是双向关注，占比情况，成功情况
	 *     2：是否是朋友，占比情况，成功情况
	 *     3、不是朋友的情况。占比情况，成功情况
	 * @param resultFile
	 *@create_time：2015年1月20日下午2:00:57
	 *@modifie_time：2015年1月20日 下午2:00:57
	  
	 */
 	public void getFollowShip_statu(String resultFile){
		checkMentionList();
		checkUserTree();
		mongoDB_Source mongoDB_Source=new mongoDB_Source();
		
		List<String> result=new ArrayList<>();
		
		//用以保存 mentioin 朋友次数、非朋友次数，成功次数
		double[] friend_and_NoFriend=new double[4];
		//用以保存被 friend mention的次数。和回复次数
		double[] friend=new double[2];
		//用以保存 mentioin 双向关注朋友次数、成功次数
		double[] bifriend=new double[2];
		
		for (String dayName : MentionInDay.keySet()) {
			for (OnePairTweet tweet : MentionInDay.get(dayName)) {
				User sourceUser=new User(tweet.getUserId());
				sourceUser=userTree.getElement(sourceUser);
				//第一步加载用户的关注信息
				if (sourceUser.getfollowee()==null) {
					Set<Long> friendList=Collections.EMPTY_SET;
					userTree.getElement(sourceUser).setfollowee(friendList);
					sourceUser=userTree.getElement(sourceUser);
				}
				User targetUser=new User(tweet.getRetweetUser_id());
				targetUser=userTree.getElement(targetUser);
				if (targetUser.getfollowee()==null) {
					Set<Long> friendList=Collections.EMPTY_SET;
					userTree.getElement(targetUser).setfollowee(friendList);
					targetUser=userTree.getElement(targetUser);
				}
				//是否是SU 的朋友
				if (sourceUser.getfollowee().contains(targetUser.getUserId())) {
					friend_and_NoFriend[0]++;
					//是否转发了
					if (tweet.getRetweetId()!=0L) {
						friend_and_NoFriend[2]++;
					}
				}else {
					friend_and_NoFriend[1]++;
					if (tweet.getRetweetId()!=0L) {
						friend_and_NoFriend[3]++;
					}
				}
				if (targetUser.getfollowee().contains(sourceUser.getUserId())) {
					friend[0]++;
					//是否转发了
					if (tweet.getRetweetId()!=0L) {
						friend[2]++;
					}
				}
				//是否双向关注
				if (mongoDB_Source.ifBifriend(sourceUser.getUserId(), sourceUser.getfollowee(), targetUser.getUserId(), targetUser.getfollowee())) {
					bifriend[0]++;
					if (tweet.getRetweetId()!=0L) {
						bifriend[1]++;
					}
				}
			}
			
		}
		result.add("mention friend : noFriend");
		result.add(friend_and_NoFriend[0]+"\t"+friend_and_NoFriend[1]);
		result.add("");
		result.add("mention sucess friend : noFriend");
		result.add(friend_and_NoFriend[2]+"\t"+friend_and_NoFriend[3]);
		result.add("");
		result.add("mention sucess ratio friend : noFriend");
		result.add(friend_and_NoFriend[2]/friend_and_NoFriend[0]+"\t"+friend_and_NoFriend[3]/friend_and_NoFriend[1]);
		result.add("");
		result.add("be mention by friend count: sucess count : sucess ratio ");
		result.add(friend[0]+"\t"+friend[1]+"\t"+friend[1]/friend[0]);
		result.add("");
		result.add("mention bifriend count: sucess count : sucess ratio ");
		result.add(bifriend[0]+"\t"+bifriend[1]+"\t"+bifriend[1]/bifriend[0]);
		result.add("");
		try {
			FileIO.writeList(resultFile, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
 	/**
	 * 基本统计一个用户会mention 多少个不同的用户的分布情况
	 * @param resultFile
	 *@create_time：2015年1月20日下午2:00:57
	 *@modifie_time：2015年1月20日 下午2:00:57
	  
	 */
 	public void getMention_User_Number_Distri(String resultFile){
		checkMentionList();
		//用以保存一个用户和其对应的mention用户列表
		Map<Long, Set<Long>> userId_Mention_listMap=new HashMap<>();
		for (String dayName : MentionInDay.keySet()) {
			for (OnePairTweet tweet : MentionInDay.get(dayName)) {
				if (userId_Mention_listMap.containsKey(tweet.getUserId())) {
					userId_Mention_listMap.get(tweet.getUserId()).add(tweet.getRetweetUser_id());
				}else {
					Set<Long> temp=new HashSet<>();
					temp.add(tweet.getRetweetUser_id());
					userId_Mention_listMap.put(tweet.getUserId(), temp);
				}
			}
		}
		
		Map<Integer, Integer> resultMap=new HashMap<>();
		for (Long userid : userId_Mention_listMap.keySet()) {
			int size=userId_Mention_listMap.get(userid).size();
			if (resultMap.containsKey(size)) {
				resultMap.put(size, resultMap.get(size)+1);
			}else {
				resultMap.put(size, 1);
			}
		}
		try {
			FileIO.writeMap(resultFile, resultMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *检查用户是否加载到内存中来了 
	 *@create_time：2015年1月20日下午2:00:00
	 *@modifie_time：2015年1月20日 下午2:00:00
	  
	 */
	public void checkUserTree(){
		assert userTree!=null;
	}
	/**
	 * 检查数据是否从文件中读取到内存中来了
	 *@create_time：2015年1月20日下午1:59:31
	 *@modifie_time：2015年1月20日 下午1:59:31
	  
	 */
	public void checkMentionList(){
		if (MentionInDay==null) {
			getMentionList(false);
		}
	}
	/**
	 * get the mention static in day
	 *@create_time：2015年1月19日下午6:29:42
	 *@modifie_time：2015年1月19日 下午6:29:42
	  
	 */
	public void get_Sex_Distribution(String resultFile){
		checkMentionList();
		checkUserTree();
		List<String> result=new ArrayList<>();
		int[] sexSourceCount=new int[2];
		double[] sexIntection=new double[4];
		double[] sexIntectionSucess=new double[4];
		for (String dayName : MentionInDay.keySet()) {
			for (OnePairTweet tweet : MentionInDay.get(dayName)) {
				User sourceUser=new User(tweet.getUserId());
				sourceUser=userTree.getElement(sourceUser);
				if (sourceUser==null) {
					continue;
				}
				User targetUser=new User(tweet.getRetweetUser_id());
				targetUser=userTree.getElement(targetUser);
				if (targetUser==null) {
					continue;
				}
				if (sourceUser.getGender().equals("m")) {
					sexSourceCount[0]++;
					if (targetUser.getGender().equals("m")) {
						sexIntection[0]++;
						if (tweet.getRetweetId()!=0) {
							sexIntectionSucess[0]++;
						}
					}else {
						sexIntection[1]++;
						if (tweet.getRetweetId()!=0) {
							sexIntectionSucess[1]++;
						}
					}
				}else {
					sexSourceCount[1]++;
					if (targetUser.getGender().equals("m")) {
						sexIntection[2]++;
						if (tweet.getRetweetId()!=0) {
							sexIntectionSucess[2]++;
						}
					}else {
						sexIntection[3]++;
						if (tweet.getRetweetId()!=0) {
							sexIntectionSucess[3]++;
						}
					}
				}
				
			}
			
		}
		result.add("man : women");
		for (double d : sexSourceCount) {
			result.add("sex source count: "+ d);
		}
		result.add("");
		result.add("man : women intection");
		for (double d : sexIntection) {
			result.add(" sexIntection count: "+ d);
		}	
		result.add("");
		result.add("man : women intection sucess");
		result.add("M T M : "+sexIntectionSucess[0]/sexIntection[0]);
		result.add("M T W : "+sexIntectionSucess[1]/sexIntection[1]);
		result.add("W T M : "+sexIntectionSucess[2]/sexIntection[2]);
		result.add("W T W : "+sexIntectionSucess[3]/sexIntection[3]);
		try {
			FileIO.writeList(resultFile, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param LoadContent 是否把内容也加载进来
	 * @return 把对每个文件的读取结果保存在 List Future中。方便后端调用。处理
	 *  方法是通过多线程的方式读取文件
	 *  
	 *@create_time：2015年1月19日下午3:07:04
	 *@modifie_time：2015年1月19日 下午3:07:04
	  
	 */
	private void getMentionList(boolean LoadContent) {
		System.out.println("load mention form dir:****************************");
		List<String> fileList = mention_file_SortByTime;
		List<Future<List<OnePairTweet>>> results = new ArrayList<>();
		for (String OneFile : fileList) {
			results.add(executor.submit(new scanMentionFile(OneFile, LoadContent)));
		}
		MentionInDay=new HashMap<String, List<OnePairTweet>>();
		for (Future<List<OnePairTweet>> future : results) {
			List<OnePairTweet> resultOfOneDay;
			try {
				resultOfOneDay = future.get();
				Calendar dayCalendar=resultOfOneDay.get(0).getCalendar();
				int month=dayCalendar.get(Calendar.MONTH)+1;
				int day=dayCalendar.get(Calendar.DAY_OF_MONTH);
				MentionInDay.put(month+"/"+day, resultOfOneDay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("sucess load mention form dir:****************************");
		executor.shutdown();
	}

	private class scanMentionFile implements Callable {
		final String oneFile;
		final boolean LoadContent;
		public scanMentionFile(String oneFile, boolean LoadContent) {
			this.LoadContent=LoadContent;
			this.oneFile = oneFile;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public List<OnePairTweet> call() throws Exception {
			Set<Long> spamuserid=User_profile_txt_Source.getSpamUserId();
			BufferedReader reader=FileIO.getReader(oneFile);
			String oneLine=null;
			List<OnePairTweet> resultList=new ArrayList<>();
			while ((oneLine = reader.readLine()) != null) {
				OnePairTweet tweet=OnePairMention.covert(oneLine,LoadContent);
				if (spamuserid.contains(tweet.getUserId())||spamuserid.contains(tweet.getRetweetUser_id())) {
					continue;
				}
				resultList.add(OnePairMention.covert(oneLine,LoadContent));
			}
			System.out.println("finish" + oneFile);
			return resultList;
		}
	}
}
