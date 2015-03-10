package Remind;

import java.io.BufferedReader;
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
import bean.AvlTree;
import bean.OnePairTweet;
import bean.RemType;
import bean.Reminder;
import bean.Retweetlist;
import bean.User;

public class StaticInTagLevel {
	
	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");  
		 String path = "J:/workspacedata/weiboNew/data/reminder/";
		String profileFile = path + "userid/profile1.txt";
		String reminderFile = path + "rem.txt";
		String sourcePath="J:/workspacedata/weiboNew/data/reminder/retweet/";
		String tagFile=path+"userid/usertagfinal.txt";
		HashMap<String, Long> usernameAndIdMap = null;
		usernameAndIdMap = ReadUser .getuserNameAndIdMapFromprofileFile(profileFile);
		AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
		useravl=BaseUitl.addTagToUser(useravl, tagFile);
		StaticInTagLevel main=new StaticInTagLevel();
		//main.countDifferentTagLevelGetRemNumber(useravl, reminderFile, path);
		main.countTopKWordInTageLevelWithnInCount(useravl, usernameAndIdMap, sourcePath, path);
		
	}
	
	/**
	 * @param useravl
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @param path
	 */
	public void countTopKWordInTageLevelWithnInCount(AvlTree<User> useravl,HashMap<String, Long> usernameAndIdMap,String sourcePath,String path){
		
		HashMap<String, HashMap<String, Double>> tagAndBeRemCount=new HashMap<>();
		
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		ChineseKeyWordInFudan split=new ChineseKeyWordInFudan();
		for (String OneFile : fileList) {
			System.out.println(OneFile);
			ReadWeiboUtil rw = new ReadWeiboUtil(OneFile);
			Retweetlist listRetweetlist;

			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null)
					continue;
				
				if (listRetweetlist.getRoot().getContent().indexOf("@")==-1) {
					continue;
				}
				ArrayList<String> targetNameList=Filter.getNameFromString(listRetweetlist.getRoot().getContent());
				if (targetNameList!=null&&targetNameList.size()>0) {
					String content=listRetweetlist.getRoot().getContent();
					ArrayList<String> splitWord=split.getKeyWord(content);
					System.out.println(content);
					System.out.println(splitWord);
					System.out.println("------------------------------");
					for (String targetName : targetNameList) {
						if (!usernameAndIdMap.containsKey(targetName)) {
							continue;
						}
						long userid=usernameAndIdMap.get(targetName);
						User targetuser = new User();
						targetuser.setUserId(userid);
						targetuser = useravl.getElement(targetuser, useravl.root);
						if (targetuser.getTag()!=null) {
							String tag=targetuser.getTag();
							for (String word : splitWord) {
								if(tagAndBeRemCount.containsKey(tag)){
									if(tagAndBeRemCount.get(tag).containsKey(word))
									 tagAndBeRemCount.get(tag).put(word, tagAndBeRemCount.get(tag).get(word)+1);
									else {
										tagAndBeRemCount.get(tag).put(word, 1.0);
									}
								}else {
									HashMap<String, Double> wordcount=new HashMap<>();
									wordcount.put(word, 1.0);
									tagAndBeRemCount.put(tag, wordcount);
								}
							}
							
						}
						
					}
					
				}
			}
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		for (String tag : tagAndBeRemCount.keySet()) {
			List<String> keysetSet=new ArrayList<>(tagAndBeRemCount.get(tag).keySet());
			TwoValueComparatorInString comparator=new TwoValueComparatorInString(tagAndBeRemCount.get(tag));
			Collections.sort(keysetSet, comparator);
			writeUtil.writeMapKeyAndValuesplitbyt(tagAndBeRemCount.get(tag), path+"tagword/"+tag+".txt");
		}
		
	}
	
	
	/**
	 * @param useravl
	 * @param reminderFile
	 * @param path
	 */
	public void countDifferentTagLevelGetRemNumber(AvlTree<User> useravl,String reminderFile,String path){
		HashMap<String, Integer> tagAndBeRemCount=new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				reminderFile))) {
			String oneLine;
			
			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				List<Reminder> listrem = remType.getReminders();
				long rootuserid=remType.getRoot().getUserId();
				User rootuser=new User();
				rootuser.setUserId(rootuserid);
				rootuser=useravl.getElement(rootuser, useravl.root);
				for (Reminder reminder : listrem) {
					User targetuser = new User();
					targetuser.setUserId(reminder.getUserId());
					targetuser = useravl.getElement(targetuser, useravl.root);
					if (targetuser.getTag()!=null) {
						String tag=targetuser.getTag();
						if(tagAndBeRemCount.containsKey(tag)){
							tagAndBeRemCount.put(tag, tagAndBeRemCount.get(tag)+1);
						}else {
							tagAndBeRemCount.put(tag, 1);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeMapKeyAndValuesplitbyt(tagAndBeRemCount, path+"tagandBeRemCount.txt");
	}
	
	/**
	 * @param tagFile2
	 * @param tagFile11
	 * @param resultFile
	 */
	public void combineTag(String tagFile2,String tagFile11,String resultFile){
		WriteUtil<String> writeUtil=new WriteUtil<>();
		HashMap<String, String> useridAndTags=new HashMap<>();
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> list=readUtil.readFileByLine(tagFile11);
		for (String oneline : list) {
			String[] split=oneline.split("\t");
			useridAndTags.put(split[0], split[1]);
		}
		
		list=readUtil.readFileByLine(tagFile2);
		for (String oneline : list) {
			String[] split=oneline.split("\t");
			if(split.length==2){
				String[] st=split[1].split(":");
				useridAndTags.put(split[0], st[0]);
			}else {
				int maxvalue=0;
				String maxLabel="";
				for (int i = 1; i < split.length; i++) {
					String[] st=split[1].split(":");
					if(Integer.valueOf(st[1])>maxvalue){
						maxLabel=st[0];	
					}
					
				}
				useridAndTags.put(split[0], maxLabel);
			}
			
		}
		writeUtil.writeMapKeyAndValuesplitbyt(useridAndTags, resultFile);
	}
	/**
	 * @param useravl
	 * @param tagSourceFile
	 * @param resultFile
	 */
	public void runGetwb_cmt(AvlTree<User> useravl,String tagSourceFile,String resultFile){
		WriteUtil<String> writeUtil=new WriteUtil<>();
		HashMap<String, String> useridAndTags=new HashMap<>();
		try (FileInputStream fis = new FileInputStream(tagSourceFile);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(isr);) {
			String temp = null;
			while ((temp  = bufferedReader.readLine())!= null) {
				String[] split=temp.split("\t");
				String friid=split[0];
						if(User.ifInUserAVL(friid, useravl)){
							if (useridAndTags.containsKey(friid)) {
								System.out.println("error");
							}
							useridAndTags.put(friid, split[1]);
						}
					
			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("read file failure");
			e.printStackTrace();
		}
		writeUtil.writeMapKeyAndValuesplitbyt(useridAndTags, resultFile);
	}
	
	/**
	 * @param useravl
	 * @param tagSourceFile
	 * @param resultFile
	 */
	public void runGetTagDistribution(AvlTree<User> useravl,String tagSourceFile,String resultFile){
		WriteUtil<String> writeUtil=new WriteUtil<>();
		HashMap<String, HashMap<String, Integer>> useridAndTags=new HashMap<>();
		try (FileInputStream fis = new FileInputStream(tagSourceFile);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(isr);) {
			String temp = null;
			while ((temp  = bufferedReader.readLine())!= null) {
				String[] split=temp.split("\t");
					String[] friends=split[4].split(",");
					for (String friid : friends) {
						if (friid.indexOf(",")!=-1) {
							friid=friid.substring(0, friid.length()-1);
						}
						if(User.ifInUserAVL(friid, useravl)){
							if (useridAndTags.containsKey(friid)) {
								if(useridAndTags.get(friid).containsKey(split[1])){
									useridAndTags.get(friid).put(split[1], useridAndTags.get(friid).get(split[1])+1);
								}
								else {
									useridAndTags.get(friid).put(split[1], 1);
								}
							}else {
								HashMap<String, Integer> tagandcount=new HashMap<>();
								tagandcount.put(split[1], 1);
								useridAndTags.put(friid, tagandcount);
							}
						}
					}
					
			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("read file failure");
			e.printStackTrace();
		}
		writeUtil.writemapkeyAndValueWhereValueIsMap(useridAndTags, resultFile);
	}
	
	
}
