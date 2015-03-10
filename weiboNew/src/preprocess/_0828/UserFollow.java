/**
 * 
 */
package preprocess._0828;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import bean.AvlTree;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;


/**   
 *    
 * 项目名称：liuchuang   
 * 类名称：UserFollow   
 * 类描述：   
 * 创建人：zhouge   
 * 创建时间�?014�?�?�?上午11:14:49   
 * 修改人：zhouge   
 * 修改时间�?014�?�?�?上午11:14:49   
 * 修改备注�?  
 * @version    
 *    
 */
public class UserFollow {
	public static void main(String[] args) throws IOException{
		List<Long> userList=new ArrayList();
		AvlTree<Long> avlTree=new AvlTree<>();
		String path="/home/zhouge/database/weibo/2012/";
		//String path="J:/workspace/weiboNew/";
		//第一步读取所有用�?
		BufferedReader bufferedReader=null;
		bufferedReader=new BufferedReader(new FileReader(new File(path+"userId.txt")));
		String oneLine=bufferedReader.readLine();
		while(oneLine!=null){
			oneLine=oneLine.trim();
			
			long userId=Long.valueOf(oneLine);
			avlTree.insert(userId);
			userList.add(userId);
			oneLine=bufferedReader.readLine();
			
		}
		
		//从数据库中取出他们之间的关系
		DBCollection follow=new Mongo("172.31.218.51", 27017).getDB("Weibo").getCollection("SocialGraph");
		
		long count=0L;
		try (BufferedWriter writer=new BufferedWriter(new FileWriter(new File(path+"socialGraph.txt")))){
			for (Long userId : userList) {
				BasicDBObject temp=new BasicDBObject("_id",userId);
				temp=(BasicDBObject) follow.findOne(temp);
				if (temp==null) {
					continue;
				}
				String ids = temp.getString("ids");
				// 删除不必要字�?
				int index = ids.indexOf('[');
				ids = ids.substring(index + 1);
				index = ids.lastIndexOf(']');
				ids = ids.substring(0, index);
				String[] idsSplit = ids.split(",");
				for (int i = 0; i < idsSplit.length; i++) {
					if (idsSplit[i].trim().equals("")) {
						continue;
					}
					long tempuserId=Long.valueOf(idsSplit[i].trim());
					if (avlTree.contains(tempuserId)) {
						writer.write(userId+","+tempuserId);
						writer.newLine();
					}
				}
				count++;
				if (count%10000==0) {
					writer.flush();
				}
			}
			writer.flush();
			writer.close();
		}
	
		System.out.println();
	}
	
}
