package model.TKI;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import util.ReadUtil;
import util.WriteUtil;

/**   
*    
* @progject_name��weiboNew   
* @class_name��DynamicFeatureOfMainTKI   
* @class_describe��   
* @creator��zhouge   
* @create_time��2014��9��2�� ����10:49:55   
* @modifier��zhouge   
* @modified_time��2014��9��2�� ����10:49:55   
* @modified_note��   
* @version    
*    
*/
public class DynamicFeatureOfMainTKI {
	
	
	//private static String RatingFile;
	//private static String path="/home/zhouge/database/weibo/2w/";
	private static String path="J:/workspace/weiboNew/data/2w/";
	//private static String path="J:/workspace/weiboNew/data/1k/";
	private static String userListFileName=path+"userList.txt";
	private static String userFeatureFileName="Split2W.txt";
	private static String wordclassFileName=path+"classes5sorted.txt";
	private static String followGraphFileName="followgraph.txt";
	
	private static String resultFile;
	private static int parallelNumber = 1;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		DynamicFeatureOfMainTKI main = new DynamicFeatureOfMainTKI();
		main.getDataSource();
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

	class BPRTask extends RecursiveTask<String> {
		WriteUtil<String> writeUtil = new WriteUtil<String>();

		@Override
		protected String compute() {
			HashMap<String, Integer> wordClass=new HashMap<>();
			ReadUtil<String> readUtil=new ReadUtil<>();
			List<String> wordclassList=readUtil.readfromFileByStream(wordclassFileName);
			List<String> userIdList=readUtil.readFileByLine(userListFileName);
			for (String oneLine : wordclassList) {
				String[] split=oneLine.split(" ");
				String word=split[0];
				Integer classLabe=Integer.valueOf(split[1]);
				wordClass.put(word, classLabe);
			}
			
			List<RecursiveTask<String>> forks = new LinkedList<>();
			for (int featureWise = 20; featureWise <100;) {
				//featureWise = featureWise+5;
				featureWise=33;
				DynamicFeatureOfTKI<String> bpr=new DynamicFeatureOfTKI<String>(path, userIdList,userFeatureFileName,followGraphFileName,wordClass,featureWise);
					 forks.add(bpr);
					 bpr.fork();
					 break;
					 //33	0.919697276384339 2w
				}

			ArrayList<String> result = new ArrayList<>();
			for (RecursiveTask<String> Task : forks) {
				result.add(Task.join());
				System.out.println(Task.join());
			}
			//writeUtil.writeList(result, "feature-0-50.txt");
			return null;
		}

	}

	/**
	 * 设置数据�?创建时间�?014�?�?日下�?:46:07 修改时间�?014�?�?�?下午2:46:07
	 */
	public void getDataSource() {
		Properties properties = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					"dataSource.properties"));
//			properties.load(in);
//			this.path = properties.getProperty("path");
//			this.RatingFile = path + properties.getProperty("RatingFile");
//			this.resultFile = path + properties.getProperty("");
//			this.parallelNumber = Integer.valueOf(properties
//					.getProperty("parallelNumber"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
