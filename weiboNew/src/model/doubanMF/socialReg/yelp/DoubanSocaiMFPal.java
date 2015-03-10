package model.doubanMF.socialReg.yelp;

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
public class DoubanSocaiMFPal {
	private static String path = "/home/zhouge/database/yelp/";

	private static int parallelNumber = 20;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		DoubanSocaiMFPal main = new DoubanSocaiMFPal();
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
			WriteUtil<String> writeUtil = new WriteUtil<String>();
			int featureWise = 10;
			List<RecursiveTask<String>> forks = new LinkedList<>();
			for (int i = 1; i < 6; i++) {
				List<Long> itemidList = getItemList(path+"train0.2_folder_"+i);
				List<Long> useridList = getUserList(path+"train0.2_folder_"+i);
				HashMap<Long, HashMap<Long, Double>> friendPCC = getFriendship(path
						+ "socialPCC"+i);
				Map<Long, Map<Long, Double>> testRating = ConvertToMatirxoneList(path+"test0.2_folder_"+i);
				Map<Long, Map<Long, Double>> trainRating = ConvertToMatirxoneList(path+"train0.2_folder_"+i);
				
				HashMap<Long, HashMap<Long, Double>> friendPCC2 = getFriendship(path
						+ "socialPCC"+i);
				Map<Long, Map<Long, Double>> testRating2 = ConvertToMatirxoneList(path+"test0.2_folder_"+i);
				Map<Long, Map<Long, Double>> trainRating2 = ConvertToMatirxoneList(path+"train0.2_folder_"+i);
				for (double alpeh = 10; alpeh < 100000; ) {
					SocialMF1 socialMF1 = new SocialMF1(featureWise, trainRating,
							testRating, friendPCC, itemidList, useridList,1000, 0.005, 0.001,
							1.0/alpeh,i);
					SocialMF2 socialMF2 = new SocialMF2(featureWise, trainRating2,
							testRating2, friendPCC2, itemidList, useridList,1000, 0.005, 0.001,
							1.0/alpeh,i);
					forks.add(socialMF1);
					socialMF1.fork();
					forks.add(socialMF2);
					socialMF2.fork();
					alpeh*=10;
				}
				
			}
			ArrayList<String> result = new ArrayList<>();
			for (RecursiveTask<String> Task : forks) {
				result.add(Task.join());
			}
			writeUtil.writeList(result, path + "SR-PCC-result1-5.txt");
		}

		/**
		 * @param fileNAme1
		 * @param fileNAme2
		 * @return
		 */
		public List<Long> getItemList( String fileNAme2) {
			ReadUtil<String> readUtil = new ReadUtil();
			Set<Long> itemidset = new HashSet<>();
			List<String> ratingList = readUtil.readFileByLine(fileNAme2);

			for (String oneLine : ratingList) {
				String[] split = oneLine.split("\t");
				long ItemId = Long.valueOf(split[1].trim());
				itemidset.add(ItemId);
			}
			return new ArrayList<>(itemidset);
		}

		public List<Long> getUserList( String fileNAme2) {
			ReadUtil<String> readUtil = new ReadUtil();
			Set<Long> itemidset = new HashSet<>();
			List<String> ratingList= readUtil.readFileByLine(fileNAme2);

			for (String oneLine : ratingList) {
				String[] split = oneLine.split("\t");
				long ItemId = Long.valueOf(split[0].trim());
				itemidset.add(ItemId);
			}
			return new ArrayList<>(itemidset);
		}
		/**
		 * @param fileNAme
		 * @return
		 */
		public Map<Long, Map<Long, Double>> ConvertToMatirxoneList(
				String fileNAme) {
			ReadUtil<String> readUtil = new ReadUtil();
			List<String> ratingList = readUtil.readFileByLine(fileNAme);
			Map<Long, Map<Long, Double>> ratingMatrix = new HashMap<>();
			for (String oneLine : ratingList) {
				String[] split = oneLine.split("\t");
				long UserId = Long.valueOf(split[0].trim());
				long ItemId = Long.valueOf(split[1].trim());
				double Rating = Double.valueOf(split[2].trim());
				if (ratingMatrix.containsKey(UserId)) {
					ratingMatrix.get(UserId).put(ItemId, Rating);
				} else {
					Map<Long, Double> oneratingMap = new HashMap<>();
					oneratingMap.put(ItemId, Rating);
					ratingMatrix.put(UserId, oneratingMap);
				}
			}
			return ratingMatrix;
		}

	
		/**
		 * @param fileName
		 * @return
		 */
		public HashMap<Long, HashMap<Long, Double>> getFriendship(
				String fileName) {
			ReadUtil<String> readUtil = new ReadUtil();
			List<String> followGraph = readUtil.readFileByLine(fileName);
			HashMap<Long, HashMap<Long, Double>> allUser = new HashMap<>();
			// convert the arraylist followGraph into the hashmap
			for (String OneLine : followGraph) {
				String[] split = OneLine.split("\t");

				long friend2 = Long.valueOf(split[0].trim());
				long friend1 = Long.valueOf(split[1].trim());
				double value = Double.valueOf(split[2].trim());
				if (friend1 == friend2) {
					continue;
				}
				if (allUser.containsKey(friend1)) {
					allUser.get(friend1).put(friend2, value);
				} else {
					HashMap<Long, Double> friendlist = new HashMap();
					friendlist.put(friend2, value);
					allUser.put(friend1, friendlist);
				}

				if (allUser.containsKey(friend2)) {
					allUser.get(friend2).put(friend1, value);
				} else {
					HashMap<Long, Double> friendlist = new HashMap();
					friendlist.put(friend1, value);
					allUser.put(friend2, friendlist);
				}
			}
			return allUser;
		}

	}


}
