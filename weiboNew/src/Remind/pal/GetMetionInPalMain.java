package Remind.pal;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import Remind.statics.StaticsInRetweetAndMetion;
import Remind.util.BaseUitl;
import bean.AvlTree;
import bean.User;
import util.FileUtil;
import util.ReadUser;
import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name：BPR
 * @class_name：Main
 * @class_describe�?
 * @creator：zhouge
 * @create_time�?014�?�?2�?下午8:51:22
 * @modifier：zhouge
 * @modified_time�?014�?�?2�?下午8:51:22
 * @modified_note�?
 * @version
 * 
 */
public class GetMetionInPalMain {
	private static String resultFile;
	private static int parallelNumber = 20;
	private static ForkJoinPool forkJoinPool;

	String path = "/home/zhouge/database/weibo/new/";
	String profileFile = path + "userid/profile.txt";
	String reminderFile = path + "rem.txt";
	String sourcePath = "/home/zhouge/database/weibo/2012/content/";
	String tagFile = path + "userid/usertagfinal.txt";
	
	public static void main(String[] args) {
		GetMetionInPalMain main = new GetMetionInPalMain();
		forkJoinPool = new ForkJoinPool(parallelNumber);
		
		
		main.getBPRInParallel();
		System.out.println("end");
	}

	/**
	 * @return
	 * @create_time�?014�?�?3日下�?:59:26
	 * @modifie_time�?014�?�?3�?下午6:59:26
	 */
	public void getBPRInParallel() {
		forkJoinPool.invoke(new userTask());
	}

	class userTask extends RecursiveAction {

		
		@Override
		protected void compute() {
			
			HashMap<String, Long> usernameAndIdMap = null;
			usernameAndIdMap = ReadUser
					.getuserNameAndIdMapFromprofileFile(profileFile);
			AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
			useravl = BaseUitl.addTagToUser(useravl, tagFile);
			FileUtil fileUtil = new FileUtil();
			String dateFormat = "yyyy_MM_dd";
			List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
					dateFormat);
			List<RecursiveTask<Map<Long, Map<Long, Double>>>> forks = new LinkedList<>();
			 Map<Long, Map<Long, Double>> metionMap=new HashMap<>();
			for (String OneFile : fileList) {
				ReadTweetInPal readTweetInPal=new ReadTweetInPal(OneFile, useravl, usernameAndIdMap, sourcePath, path);
				forks.add(readTweetInPal);
				readTweetInPal.fork();
				
			}
			//WriteUtil<String> writeUtil = new WriteUtil<String>();
			
			for (RecursiveTask<Map<Long, Map<Long, Double>>> Task : forks) {
				Map<Long, Map<Long, Double>> resultMap=Task.join();
				metionMap=joinmap(metionMap, resultMap);
			}
			
		}
		
		
		public  Map<Long, Map<Long, Double>> joinmap(Map<Long, Map<Long, Double>> base,Map<Long, Map<Long, Double>> add){
			for (long userid : add.keySet()) {
				for (long mentionid : add.get(userid).keySet()) {
						if (base.containsKey(userid)) {
							if (base.get(userid).containsKey(mentionid)) {
								base.get(userid).put(mentionid, base.get(userid).get(mentionid)+1);
							}else {
								base.get(userid).put(mentionid,1.0);
							}
						}else {
							HashMap<Long, Double> tempHashMap=new HashMap<>();
							tempHashMap.put(mentionid, 1.0);
							base.put(userid, tempHashMap);
						}
					}
				}
			return base;
		}
	}

}
