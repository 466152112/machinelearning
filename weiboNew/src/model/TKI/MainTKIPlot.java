package model.TKI;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import util.WriteUtil;

/**
 * 
 * @progject_nameï¼šBPR
 * @class_nameï¼šMain
 * @class_describeï¼?
 * @creatorï¼šzhouge
 * @create_timeï¼?014å¹?æœ?2æ—?ä¸‹åˆ8:51:22
 * @modifierï¼šzhouge
 * @modified_timeï¼?014å¹?æœ?2æ—?ä¸‹åˆ8:51:22
 * @modified_noteï¼?
 * @version
 * 
 */
public class MainTKIPlot {

	// private static String RatingFile;
	// private static String path="/home/zhouge/database/twitter/twitter/";
	// private static String path="J:/workspace/weiboNew/data/2w/";

	private static String path = "J:/workspace/twitter/data/twitter/";
	private static String userListFileName = "userList.txt";
	private static String userFeatureFileName = "userfeature.txt";
	private static String followGraphFileName = "followgraph.txt";

	private static String resultFile;
	private static int parallelNumber = 1;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		MainTKIPlot main = new MainTKIPlot();
		main.getDataSource();
		forkJoinPool = new ForkJoinPool(parallelNumber);
		main.getBPRInParallel();
		System.out.println("end");
	}

	/**
	 * @return
	 * @create_timeï¼?014å¹?æœ?3æ—¥ä¸‹å?:59:26
	 * @modifie_timeï¼?014å¹?æœ?3æ—?ä¸‹åˆ6:59:26
	 */
	public void getBPRInParallel() {
		forkJoinPool.invoke(new BPRTask());

	}

	class BPRTask extends RecursiveTask<String> {
		WriteUtil<String> writeUtil = new WriteUtil<String>();

		@Override
		protected String compute() {
			List<RecursiveTask<String>> forks = new LinkedList<>();
			for (int featureWise = 2; featureWise < 10;) {

				 featureWise=33;
				for (int i = 0; i < 1; i++) {
					TKIPlotcluster<String> bpr = new TKIPlotcluster(path, userListFileName,
							userFeatureFileName, followGraphFileName, featureWise);
					forks.add(bpr);
					bpr.fork();
					
				}
			
				 break;
				// 33 0.919697276384339 2w
				//featureWise = featureWise + 2;
			}
//			for (int featureWise = 10; featureWise < 50;) {
//
//				// featureWise=33;
//				for (int i = 0; i < 10; i++) {
//					TKI<String> bpr = new TKI<String>(path, userListFileName,
//							userFeatureFileName, followGraphFileName, featureWise);
//					forks.add(bpr);
//					bpr.fork();
//				}
//				
//				// break;
//				// 33 0.919697276384339 2w
//				featureWise = featureWise + 5;
//			}
//			for (int featureWise = 50; featureWise < 100;) {
//
//				// featureWise=33;
//				for (int i = 0; i < 10; i++) {
//					TKI<String> bpr = new TKI<String>(path, userListFileName,
//							userFeatureFileName, followGraphFileName, featureWise);
//					forks.add(bpr);
//					bpr.fork();
//				}
//				
//				// break;
//				// 33 0.919697276384339 2w
//				featureWise = featureWise + 10;
//			}
			ArrayList<String> result = new ArrayList<>();
			for (RecursiveTask<String> Task : forks) {
				result.add(Task.join());
				System.out.println(Task.join());
			}
			// writeUtil.writeList(result, "feature-0-100-5.txt");
			return null;
		}

	}

	/**
	 * è®¾ç½®æ•°æ®æº?åˆ›å»ºæ—¶é—´ï¼?014å¹?æœ?æ—¥ä¸‹å?:46:07 ä¿®æ”¹æ—¶é—´ï¼?014å¹?æœ?æ—?ä¸‹åˆ2:46:07
	 */
	public void getDataSource() {
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					"dataSource.properties"));
			// properties.load(in);
			// this.path = properties.getProperty("path");
			// this.RatingFile = path + properties.getProperty("RatingFile");
			// this.resultFile = path + properties.getProperty("");
			// this.parallelNumber = Integer.valueOf(properties
			// .getProperty("parallelNumber"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
