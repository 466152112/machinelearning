/**
 * 
 */
package preprocess.statics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bson.NewBSONDecoder;

import util.ReadUtil;
import util.WriteUtil;
import bean.AvlTree;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：getUserFromDatabase
 * @class_describe�?
 * @creator：zhouge
 * @create_time�?014�?�?4�?下午10:17:19
 * @modifier：zhouge
 * @modified_time�?014�?�?4�?下午10:17:19
 * @modified_note�?
 * @version
 * 
 */
public class getUserWeibo {
	public static void main(String[] dk) {

		Set<String> userList = new HashSet<>();
		String path = "/media/zhouge/new2/data/";
		String userListFile = path + "userList2.txt";
		String userWeiboFile = path + "userWeibo.txt";
		String retweetWeiboFile=path + "userRetweetWeibo.txt";
		String retweetIdString=path+"retweetId.txt";
		String sourceFile = path + "merge.txt";
		ReadUtil readUtil = new ReadUtil();
		userList = readUtil.readSetFromFile(userListFile);
		AvlTree<Long> tree=new AvlTree<>();
		
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				userWeiboFile, true));
				BufferedWriter retweetWriter = new BufferedWriter(new FileWriter(
						retweetWeiboFile, true));
				BufferedWriter retweetId=new BufferedWriter(new FileWriter(
						retweetIdString, true));
				BufferedReader bufferedReader = new BufferedReader(
						new FileReader(sourceFile));
				//获取retweet
				BufferedReader Reader = new BufferedReader(
						new FileReader(sourceFile));
				
				) {
			String tempLine = null;
			long count = 0L;
			
			// 读取�?��
			try {
				while ((tempLine = bufferedReader.readLine()) != null) {
					tempLine = tempLine.trim();
					// System.out.println(oneLine);;
					String oneLine = tempLine;
					try {
						if (oneLine.indexOf("\t") == -1) {
							continue;
						}
						
						try {
							// 截取微博id
							long weiboId = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
							// 截取用户id
							long user_id = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							if (!userList.contains(String.valueOf(user_id))) {
								continue;
							}
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
						
						} catch (NumberFormatException e) {
							continue;
						}

						try {
							// 截取转发微博id
							long retweet_id = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							if (!tree.contains(retweet_id)) {
								tree.insert(retweet_id);
								retweetId.write(String.valueOf(retweet_id));
								retweetId.newLine();
							}
							

						} catch (NumberFormatException e) {
							
						} catch (StringIndexOutOfBoundsException e) {
							continue;
						}

						bufferedWriter.write(tempLine);
						bufferedWriter.newLine();
						
						count++;
						if (count % 100000 == 0) {
							System.out.println(count);
						}

					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// 读取�?��
			try {
				while ((tempLine = Reader.readLine()) != null) {
					tempLine = tempLine.trim();
					// System.out.println(oneLine);;
					String oneLine = tempLine;
					try {
						if (oneLine.indexOf("\t") == -1) {
							continue;
						}
						
						try {
							// 截取微博id
							long weiboId = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							if (!tree.contains(weiboId)) {
								continue;
							}
							retweetWriter.write(tempLine);
							retweetWriter.newLine();
							
							count++;
							if (count % 100000 == 0) {
								System.out.println(count);
							}
						} catch (NumberFormatException e) {
							continue;
						}

					

					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}
				
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			retweetId.flush();
			retweetId.close();
			bufferedWriter.flush();
			bufferedWriter.close();
			retweetWriter.flush();
			retweetWriter.close();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
