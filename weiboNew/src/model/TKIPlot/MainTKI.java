package model.TKIPlot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

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
public class MainTKI {

	// private static String RatingFile;
	// private static String path="/home/zhouge/database/twitter/twitter/";
	// private static String path="J:/workspacedata/weiboNew/data/2w/";

	private static String path = "J:/workspacedata/twitter/data/twitter/";
	private static String userListFileName = "userList.txt";
	private static String userFeatureFileName = "userfeature.txt";
	private static String followGraphFileName = "followgraph.txt";

	private static String resultFile;
	private static int parallelNumber = 1;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		MainTKI main = new MainTKI();
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
		forkJoinPool.invoke(new BPRTask());

	}

	@SuppressWarnings("serial")
	class BPRTask extends RecursiveTask<String> {
		WriteUtil<String> writeUtil = new WriteUtil<String>();

		@Override
		protected String compute() {
			List<RecursiveTask<String>> forks = new LinkedList<>();
			 int featureWise=33;
				for (int i = 0; i < 1; i++) {
					CaseTKI bpr = new CaseTKI(path, userListFileName,
							userFeatureFileName, followGraphFileName, featureWise);
					forks.add(bpr);
					bpr.fork();
					
				}
			
			ArrayList<String> result = new ArrayList<>();
			
			for (RecursiveTask<String> Task : forks) {
				result.add(Task.join());
				System.out.println(Task.join());
			}
			
			// writeUtil.writeList(result, "feature-0-100-5.txt");
			return null;
		}

	}


}
