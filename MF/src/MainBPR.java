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

import model.BPR;
import MF.util.WriteUtil;

/**
 * 
 * @progject_name：BPR
 * @class_name：Main
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年7月22日 下午8:51:22
 * @modifier：zhouge
 * @modified_time：2014年7月22日 下午8:51:22
 * @modified_note：
 * @version
 * 
 */
public class MainBPR {
	private static String RatingFile;
	private static String path;
	private static String resultFile;
	private static int parallelNumber = 8;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {
		MainBPR main = new MainBPR();
		main.getDataSource();
		forkJoinPool = new ForkJoinPool(parallelNumber);
		main.getBPRInParallel();
		System.out.println("end");
	}

	/**
	 * @return
	 * @create_time：2014年7月23日下午6:59:26
	 * @modifie_time：2014年7月23日 下午6:59:26
	 */
	public void getBPRInParallel() {
		forkJoinPool.invoke(new BPRTask());

	}

	class BPRTask extends RecursiveTask<String> {
		WriteUtil<String> writeUtil = new WriteUtil<String>();

		@Override
		protected String compute() {
			List<RecursiveTask<String>> forks = new LinkedList<>();
//			for (int featureWise = 20; featureWise < 50;) {
//				featureWise = featureWise + 5;
//				long loopMax=100000;
//				for (int loopMaxIndex = 1; loopMaxIndex < 100; ) {
//					loopMaxIndex=loopMaxIndex*10;
//					BPR bpr = new BPR(RatingFile, featureWise,loopMax*loopMaxIndex, 0.01, 0.01, 0.01);
//					forks.add(bpr);
//					bpr.fork();
//				}
//				
//			}

			 BPR<Long> bpr=new BPR<Long>(RatingFile, 20, 100000,0.01, 0.01, 0.01);
			 forks.add(bpr);
			 bpr.fork();
			ArrayList<String> result = new ArrayList<>();
			for (RecursiveTask<String> Task : forks) {
				//result.add(Task.join());
				System.out.println(Task.join());
			}
			//writeUtil.writeList(result, "feature-0-50.txt");
			return null;
		}

	}

	/**
	 * 设置数据源 创建时间：2014年4月9日下午2:46:07 修改时间：2014年4月9日 下午2:46:07
	 */
	public void getDataSource() {
		Properties properties = new Properties();
		try {

			InputStream in = new BufferedInputStream(new FileInputStream(
					"dataSource.properties"));
			properties.load(in);
			this.path = properties.getProperty("path");
			this.RatingFile = path + properties.getProperty("RatingFile");
			this.resultFile = path + properties.getProperty("");
			this.parallelNumber = Integer.valueOf(properties
					.getProperty("parallelNumber"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
