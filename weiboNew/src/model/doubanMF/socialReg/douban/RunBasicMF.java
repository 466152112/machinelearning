package model.doubanMF.socialReg.douban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

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
public class RunBasicMF {
	private static String path = "/home/zhouge/database/Douban/";
	 //private static String path = "H:/dataset/recommendation/Douban/";
	// private static String path="J:/workspace/weiboNew/data/1k/";
	private static String userRatingFileName = path + "uir.index";
	private static String followGraphFileName = path + "social.index";

	private static String resultFile;
	private static int parallelNumber = 10;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		RunBasicMF main = new RunBasicMF();
		//forkJoinPool = new ForkJoinPool(parallelNumber);
		main.compute();
		System.out.println("end");
	}


		protected void compute() {
			WriteUtil<String> writeUtil = new WriteUtil<String>();
			int featureWise = 10;
		
			for (int i = 0; i < 1; i++) {
				List<Long> itemidList = getItemList(path + "5-cross/"+i+"train.txt");
				List<Long> useridList = getUserList(path + "5-cross/"+i+"train.txt");
				HashMap<Long, HashMap<Long, Double>> friendPCC = getFriendship(path
						+ "5-cross/" + i + "pcc.txt");
				Map<Long, Map<Long, Double>> testRating = ConvertToMatirxoneList(path
						+ "5-cross/" + i + "test.txt");
				Map<Long, Map<Long, Double>> trainRating = ConvertToMatirxoneList(path
						+ "5-cross/" + i + "train.txt");
				BaseMF baseMF = new BaseMF(featureWise, trainRating,
						testRating, friendPCC, itemidList,useridList, 100, 0.005, 0.001,
						0.001);
				baseMF.IterBySGD();
				
			}
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
				value = PCC.getChangePCC(value);
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
