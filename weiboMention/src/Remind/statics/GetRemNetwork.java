package Remind.statics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.iterators.ArrayListIterator;

import tool.FileTool.FileUtil;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import weibo.util.ReadUser;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.ChineseSpliter;
import weibo.util.tweetContent.Filter;
import weibo.util.tweetContent.Spliter;
import Remind.util.BaseUitl;
import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import bean.Remind.RemType;
import bean.Remind.Reminder;

public class GetRemNetwork {
	public static void main(String[] args) {
		// String path = "/media/pc/new2/data/new/";
		// String path = "J:/workspacedata/weiboNew/data/reminder/";
		String path = "/home/zhouge/database/weibo/new/";
		String tagFile = path + "userid/usertagfinal.txt";
		String sourcePath = "/home/zhouge/database/weibo/2012/content/";
		String profileFile = path + "userid/profile.txt";
		AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
//		useravl = BaseUitl.addTagToUser(useravl, tagFile);
		GetRemNetwork re = new GetRemNetwork();
		HashMap<Long, String> useridAndNameMap = null;
		HashMap<String, Long> usernameAndIdMap = null;
		useridAndNameMap = ReadUser
				.getuserIdAndNameMapFromprofileFile(profileFile);
		usernameAndIdMap = ReadUser
				.getuserNameAndIdMapFromprofileFile(profileFile);

		re.getrootRemnetwork(useravl, useridAndNameMap, usernameAndIdMap, sourcePath,path);
		// re.
		// getValidnetwork(useravl,useridAndNameMap,usernameAndIdMap,sourcePath,path);
	}

	public void getValidnetwork(AvlTree<User> useravl,
			HashMap<Long, String> useridAndNameMap,
			HashMap<String, Long> usernameAndIdMap, String sourcePath,
			String tagpath) {
		WriteUtil<String> writeLiUtil = new WriteUtil<>();
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		// List<String> reTweetNumberAndRemNumber=new ArrayList<>();
		Filter filter=new ChineseFilter();
		
		for (String OneFile : fileList) {

			int count = 0;
			ReadWeibo rw = new ReadWeibo(OneFile);
			Retweetlist listRetweetlist;
			// List<String> listResult=new ArrayList<>();
			while ((listRetweetlist = rw.readRetweetList()) != null) {
				List<String> tellTarget = new ArrayList<>();

				count++;
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0l)
					continue;
				int remCount = 0;
				Set<Long> remset = new HashSet<>();
				boolean flag = false;
				if (listRetweetlist.getRoot().getContent().indexOf("@") != -1) {
					User rootUser = new User();
					rootUser.setUserId(listRetweetlist.getRoot().getUserId());
					rootUser = useravl.getElement(rootUser);

					Set<String> targetNameList = filter
							.getAtName(listRetweetlist.getRoot()
									.getContent());

					if (targetNameList != null) {
						if (targetNameList.contains("小米手机")) {
							flag = true;
						}
						// for (String name : targetNameList) {
						// if (usernameAndIdMap.containsKey(name)) {
						// User targetUser=new User();
						// targetUser.setUserId(usernameAndIdMap.get(name));
						// targetUser=useravl.getElement(targetUser);
						// remset.add(targetUser.getUserId());
						// }
						// }
					}
				}
				List<OnePairTweet> list = listRetweetlist
						.getRetweetListSortByTime();

				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getContent().indexOf("@") != -1) {
						User rootUser = new User();
						rootUser.setUserId(list.get(i).getUserId());
						rootUser = useravl.getElement(rootUser);

						Set<String> targetNameList = filter
								.getAtName(list.get(i).getContent());
						if (targetNameList != null) {
							if (targetNameList.contains("小米手机")) {
								flag = true;
							}
							// for (String name : targetNameList) {
							// if (usernameAndIdMap.containsKey(name)){
							// User targetUser=new User();
							// targetUser.setUserId(usernameAndIdMap.get(name));
							// targetUser=useravl.getElement(targetUser);
							// remset.add(targetUser.getUserId());
							// }
							// }
						}
					}
				}
				if (flag) {
					tellTarget.add(listRetweetlist.getRoot().toString());
					for (OnePairTweet onePairTweet : list) {
						tellTarget.add(onePairTweet.toString());
					}
					tellTarget.add("");
					writeLiUtil.writeList(tellTarget, tagpath
							+ "/result/content/小米手机.txt");
				}
				// if (list.size()>=50) {
				// for (OnePairTweet onePairTweet : list) {
				// if(remset.contains(onePairTweet.getUserId()))
				// remCount++;
				// }
				// reTweetNumberAndRemNumber.add(list.size()+"\t"+remCount);
				// }
			}
			System.out.println(OneFile + "\t the tweet number is:" + count);
		}

	}

	/**
	 * @param useravl
	 * @param useridAndNameMap
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @param tagpath
	 */
	public void getrootRemnetwork(AvlTree<User> useravl,
			HashMap<Long, String> useridAndNameMap,
			HashMap<String, Long> usernameAndIdMap, String sourcePath,
			String path) {
		WriteUtil<String> writeLiUtil = new WriteUtil<>();
		FileUtil fileUtil = new FileUtil();
		HashMap<String, Integer> tagCount = new HashMap<>();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		List<String> reTweetNumberAndRemNumber = new ArrayList<>();
		Map<Long, Integer> useridAndRemCount=new HashMap<>();
		Filter filter=new ChineseFilter();
		
		for (String OneFile : fileList) {
			System.out.println(OneFile);
			ReadWeibo rw = new ReadWeibo(OneFile);
			Retweetlist listRetweetlist;
			List<String> listResult = new ArrayList<>();
			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0l)
					continue;
				int remCount = 0;
				if (listRetweetlist.getRoot().getContent().indexOf("@") != -1) {
					
					Set<String> targetNameList = filter
							.getAtName(listRetweetlist.getRoot()
									.getContent());
					
					if (targetNameList != null) {
						
						for (String name : targetNameList) {
							if (usernameAndIdMap.containsKey(name)) {
								//add count 
								useridAndRemCount=addRemToMap(useridAndRemCount, listRetweetlist.getRoot().getUserId(), usernameAndIdMap, targetNameList);
								
								listResult.add(listRetweetlist.getRoot()
										.getUserId()
										+ ","
										+ usernameAndIdMap.get(name)
										+ ","
										+ listRetweetlist.getRoot()
												.getCreateTime());
							}
						}
					}
				}
//				List<OnePairTweet> list = listRetweetlist
//						.getRetweetListSortByTime();
//
//				for (int i = 0; i < list.size(); i++) {
//					
//					if (list.get(i).getContent().indexOf("@") != -1) {
//
//						ArrayList<String> targetNameList = Filter
//								.getNameFromString(list.get(i).getContent());
//						if (targetNameList != null) {
//							for (String name : targetNameList) {
//								remCount++;
//								listResult.add(list.get(i).getUserId() + ","
//										+ usernameAndIdMap.get(name) + ","
//										+ list.get(i).getCreateTime());
//							}
//							
//						}
//					}
//				}
				
				if (listResult.size() > 100000) {
//					writeLiUtil.writeList(listResult, path
//							+ "allremNetwork.net");
					listResult = new ArrayList<>();
				}
			}
			//writeLiUtil.writeList(listResult, path + "allremNetwork.net");
			listResult = new ArrayList<>();
			
		}
		writeLiUtil.writeMapKeyAndValuesplitbyt(useridAndRemCount, path+"useridAndRemCount.txt");
	}

	public Map<Long, Integer> addRemToMap(Map<Long, Integer> useridAndRemCount,long sourceId,HashMap<String, Long> usernameAndIdMap,Set<String> targetNameList){
		
		for (String name : targetNameList) {
			if (usernameAndIdMap.containsKey(name)) {
				if (useridAndRemCount.containsKey(sourceId)) {
					useridAndRemCount.put(sourceId, useridAndRemCount.get(sourceId)+1);
				}else {
					useridAndRemCount.put(sourceId,1);
				}
			}
		}
		return useridAndRemCount;
	}
}
