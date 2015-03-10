/**
 * 
 */
package preprocess._0825;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import model.doubanMF.DoubanMF;
import model.doubanMF.TKI;
import bean.AvlTree;

import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：MainClear
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年8月28日 上午11:59:03
 * @modifier：zhouge
 * @modified_time：2014年8月28日 上午11:59:03
 * @modified_note：
 * @version
 * 
 */
public class MainClear {
//	String path = "/home/zhouge/data/1/";
	//String path = "/media/zhouge/new2/data/split/1/";
	String path = "/home/pc/weibo/6/";
	//String path="J:/workspace/weiboNew/data/0/";
	String userIdFile="/home/pc/weibo/userId.txt";
	String targetPath = path + "clear/";

	private static int parallelNumber = 8;
	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {

		MainClear main = new MainClear();
		forkJoinPool = new ForkJoinPool(parallelNumber);
		main.runInParallel();
		System.out.println("end");

	}

	/**
	 * @return
	 * @create_time锛?014骞?鏈?3鏃ヤ笅鍗?:59:26
	 * @modifie_time锛?014骞?鏈?3鏃?涓嬪崍6:59:26
	 */
	public void runInParallel() {
		forkJoinPool.invoke(new BPRTask());

	}

	class BPRTask extends RecursiveAction {

		@Override
		protected void compute() {
			final DBCollection followGraph;
			final DBCollection userInfo;
			final DBCollection weiboinfo;
			List<RecursiveAction> forks = new LinkedList<>();
			File csFileMuluFile = new File(path);
			// 读取目录下所有文件
			File[] Filelist = csFileMuluFile.listFiles();
			
			
			// 遍历所有文件
			try {
				// 构造平衡二叉树
				AvlTree<Long> avlTree = new AvlTree<>();
				Scanner scanner = new Scanner(new File(userIdFile));
				while(scanner.hasNext()){
					long userId=scanner.nextLong();
					avlTree.insert(userId);
					//System.out.println(userId);
				}
				// followGraph = new Mongo("172.31.218.51",
				// 27017).getDB("Weibo")
				// .getCollection("SocialGraph");
//				userInfo = new Mongo("172.31.218.51", 27017).getDB("Weibo")
//						.getCollection("userInfo");
				// weiboinfo = new Mongo("172.31.218.51", 27018).getDB("test")
				// .getCollection("weiboinfo");
				weiboinfo = new Mongo("172.17.166.4", 27018).getDB("test")
						.getCollection("weiboinfo");
				for (int j = 0; j < Filelist.length; j++) {
					File source = Filelist[j];
					if (source.isFile()) {
						File targetFile = new File(targetPath + source.getName());
						ClearOneFile clearOneFile = new ClearOneFile(source,targetFile, avlTree, weiboinfo);
						forks.add(clearOneFile);
						clearOneFile.fork();
					}
					
				}

			} catch (UnknownHostException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (RecursiveAction Task : forks) {
				Task.join();
			}
		}

	}

}
