package Remind.statics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.link.pageRank.tools.TwoValueComparator;
import montylingua.string;
import util.CalanderUtil;
import util.ChineseKeyWordInFudan;
import util.ChineseSpliter;
import util.FileUtil;
import util.Filter;
import util.ReadUser;
import util.ReadUtil;
import util.ReadWeiboUtil;
import util.TwoValueComparatorInString;
import util.WriteUtil;
import Remind.util.BaseUitl;
import bean.AvlTree;
import bean.OnePairTweet;
import bean.RemType;
import bean.Reminder;
import bean.Retweetlist;
import bean.User;

public class CovertToKeyword {
	
	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");  
		 String path = "/home/zhouge/database/weibo/new/";
		String profileFile = path + "userid/profile.txt";
		String reminderFile = path + "rem.txt";
		String sourcePath="/home/zhouge/database/weibo/2012/content/";
		String keywordPath="/home/zhouge/database/weibo/2012/keyword1/";
		String tagFile=path+"userid/usertagfinal.txt";
		HashMap<String, Long> usernameAndIdMap = null;
		usernameAndIdMap = ReadUser.getuserNameAndIdMapFromprofileFile(profileFile);
		AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
		useravl=BaseUitl.addTagToUser(useravl, tagFile);
		CovertToKeyword main=new CovertToKeyword();
		//main.countDifferentTagLevelGetRemNumber(useravl, reminderFile, path);
		main.countTopKWordInTageLevelWithnInCount(useravl, usernameAndIdMap, sourcePath, keywordPath);
		
	}
	
	/**
	 * @param useravl
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @param path
	 */
	public void countTopKWordInTageLevelWithnInCount(AvlTree<User> useravl,HashMap<String, Long> usernameAndIdMap,String sourcePath,String tagpath){
		
		HashMap<String, HashMap<String, Double>> tagAndBeRemCount=new HashMap<>();
		
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		fileList=fileList.subList(71, fileList.size());
		ChineseKeyWordInFudan split=new ChineseKeyWordInFudan();
		for (String OneFile : fileList) {
			System.out.println(OneFile);
			ReadWeiboUtil rw = new ReadWeiboUtil(OneFile);
			Retweetlist listRetweetlist;
			File kkfFile=new File(OneFile);
			WriteUtil<String> writeLiUtil=new WriteUtil<>();
			List<String> listResult=new ArrayList<>();
			while ((listRetweetlist = rw.readRetweetList()) != null) {
				
				if (listRetweetlist.getRoot() == null)
					continue;
				String content=listRetweetlist.getRoot().getContent();
				ArrayList<String> splitWord=split.getKeyWord(content,10);
				StringBuffer buffer=new StringBuffer();
				for (String keyword : splitWord) {
					buffer.append(keyword+",");
				}
				buffer.append("\t");
				if (listRetweetlist.getRoot().getContent().indexOf("@")!=-1) {
					ArrayList<String> targetNameList=Filter.getNameFromString(listRetweetlist.getRoot().getContent());
					if (targetNameList!=null) {
						for (String name : targetNameList) {
							if (usernameAndIdMap.containsKey(name)) {
								buffer.append(usernameAndIdMap.get(name)+",");
							}
						}
					}
					
				}
				listRetweetlist.getRoot().setContent(buffer.toString());
				listResult.add(listRetweetlist.getRoot().toString());
				List<OnePairTweet> list=listRetweetlist.getRetweetListSortByTime();
				
				for (int i = 0; i < list.size(); i++) {
					content=list.get(i).getContent();
					 splitWord=split.getKeyWord(content);
					 buffer=new StringBuffer();
					for (String keyword : splitWord) {
						buffer.append(keyword+",");
					}
					buffer.append("\t");
					if (list.get(i).getContent().indexOf("@")!=-1) {
						ArrayList<String> targetNameList=Filter.getNameFromString(listRetweetlist.getRoot().getContent());
						if (targetNameList!=null) {
						for (String name : targetNameList) {
							if (usernameAndIdMap.containsKey(name)) {
								buffer.append(usernameAndIdMap.get(name)+",");
							}
						}
						}
					}
					list.get(i).setContent(buffer.toString());
					listResult.add(list.get(i).toString());
				}
				listResult.add("\r");
				if (listResult.size()>100000) {
					writeLiUtil.writeList(listResult, tagpath+kkfFile.getName());
					listResult=new ArrayList<>();
				}
				
			}
			writeLiUtil.writeList(listResult, tagpath+kkfFile.getName());
			listResult=new ArrayList<>();
		}
		
	}
	

	
}
