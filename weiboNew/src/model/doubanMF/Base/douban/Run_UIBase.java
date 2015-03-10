package model.doubanMF.Base.douban;

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
 * @progject_name锛欱PR
 * @class_name锛歁ain
 * @class_describe锟�
 * @creator锛歾houge
 * @create_time锟�014锟�锟�2锟�涓嬪�?:51:22
 * @modifier锛歾houge
 * @modified_time锟�014锟�锟�2锟�涓嬪�?:51:22
 * @modified_note锟�
 * @version
 * 
 */
public class Run_UIBase {
	private static String path = "/home/zhouge/database/Douban/yulu/";

	private static int parallelNumber = 16;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		Run_UIBase main = new Run_UIBase();
		forkJoinPool = new ForkJoinPool(parallelNumber);
		main.getBPRInParallel();
	}

	/**
	 * @return
	 * @create_time锟�014锟�锟�3鏃ヤ笅锟�?59:26
	 * @modifie_time锟�014锟�锟�3锟�涓嬪�?:59:26
	 */
	public void getBPRInParallel() {
		forkJoinPool.invoke(new userTask());
	}

	class userTask extends RecursiveAction {

		@Override
		protected void compute() {
			WriteUtil<String> writeUtil = new WriteUtil<String>();
			List<RecursiveTask<String>> forks = new LinkedList<>();
			for (int i = 1; i < 6; i++) {
				Map<Long, Map<Long, Double>> testRating = ConvertToMatirxoneList(path
						+ "test0.2_folder_" + i);
				Map<Long, Map<Long, Double>> trainRating = ConvertToMatirxoneList(path
						+ "train0.2_folder_" + i);

				Map<Long, Map<Long, Double>> testRating2 = ConvertToMatirxoneList(path
						+ "test0.2_folder_" + i);
				Map<Long, Map<Long, Double>> trainRating2 = ConvertToMatirxoneList(path
						+ "train0.2_folder_" + i);
				UPrediction uPrediction = new UPrediction(trainRating,
						testRating, i);
				IPrediction iPrediction = new IPrediction(trainRating2,
						testRating2, i);
				forks.add(uPrediction);
				uPrediction.fork();
				forks.add(iPrediction);
				iPrediction.fork();
			}
			ArrayList<String> result = new ArrayList<>();
			for (RecursiveTask<String> Task : forks) {
				result.add(Task.join());
			}
			writeUtil.writeList(result, path + "UIAvg-result1-5.txt");
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
	}
}
