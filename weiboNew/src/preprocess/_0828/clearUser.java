/**
 * 
 */
package preprocess._0828;

import java.io.File;
import java.io.FileNotFoundException;
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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：MainClear
 * @class_describe�?
 * @creator：zhouge
 * @create_time�?014�?�?8�?上午11:59:03
 * @modifier：zhouge
 * @modified_time�?014�?�?8�?上午11:59:03
 * @modified_note�?
 * @version
 * 
 */
public class clearUser {
	final static  String path = "/home/zhouge/database/weibo/";
	
	final static String userIdFile=path+"userId.txt";
	final static String targetFile = path + "clearUserId.txt/";

	private static ForkJoinPool forkJoinPool;

	public static void main(String[] args) {

		clearUser main = new clearUser();
		final DBCollection followGraph;
		try {
			followGraph = new Mongo("172.31.218.51", 27017).getDB("Weibo") .getCollection("SocialGraph");
			Scanner scanner;
			WriteUtil<String> writeUtil=new WriteUtil<>();
			try {
				scanner = new Scanner(new File(userIdFile));
				List<String> result=new ArrayList<>();
				long count=0L;
				while(scanner.hasNext()){
					String tempString=scanner.next();
					tempString.replace("\"", "");
					long userId=Long.valueOf(tempString);
					BasicDBObject userBasicDBObject=new BasicDBObject("_id", userId);
					if (followGraph.count(userBasicDBObject)!=0) {
						result.add(String.valueOf(userId));
					}
					count++;
					if (count % 100000 == 0) {
						System.out.println(count);
					}
			}
				writeUtil.writeList(result, targetFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		

	}

}
