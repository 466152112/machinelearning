package model.IBPR;

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
 * @create_time锟�014锟�锟�2锟�涓嬪崍8:51:22
 * @modifier锛歾houge
 * @modified_time锟�014锟�锟�2锟�涓嬪崍8:51:22
 * @modified_note锟�
 * @version
 * 
 */
public class RunIBPR {
	private static String path = "J:\\workspacedata\\librec\\lastfm\\";

	private static int parallelNumber = 16;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		RunIBPR runIBPR=new RunIBPR();
		int featureWise=5;
		Map<Long, Set<Long>> trainRating = runIBPR.readDataToRankingType(path+"train0.2_folder_1");
		Map<Long, Set<Long>>  testRating=runIBPR.readDataToRankingType(path+"test0.2_folder_1");
		IBPR iBPR = new IBPR(featureWise, trainRating,
					testRating,2000, 0.005,0.05);
		iBPR.run();
	}
	
	/**
	 * @param fileNAme
	 * @return
	 */
	public Map<Long, Set<Long>> readDataToRankingType(
			String fileNAme) {
		ReadUtil<String> readUtil = new ReadUtil();
		List<String> ratingList = readUtil.readFileByLine(fileNAme);
		Map<Long, Set<Long>> ratingMatrix = new HashMap<>();
		for (String oneLine : ratingList) {
			String[] split = oneLine.split("\t");
			long UserId = Long.valueOf(split[0].trim());
			long ItemId = Long.valueOf(split[1].trim());
			if (ratingMatrix.containsKey(UserId)) {
				if (ratingMatrix.get(UserId).contains(ItemId)) {
					System.out.println("error in file");
				}
				ratingMatrix.get(UserId).add(ItemId);
			} else {
				Set<Long> oneratingMap = new HashSet();
				oneratingMap.add(ItemId);
				ratingMatrix.put(UserId, oneratingMap);
			}
		}
		return ratingMatrix;
	}


}
