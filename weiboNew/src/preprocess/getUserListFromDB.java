/**
 * 
 */
package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bson.NewBSONDecoder;

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
public class getUserListFromDB {
	public static void main(String[] dk) {
		String result="20wFollowGraph.txt";
		String userFileString="20WuserList.txt";
//		List<String> userList=getUserSet(userFileString);
//		getFollowGraph(result, userFileString);
	}

	
	/**
	 * @param result target file name
	 *  get the userList followGraph from database
	 *@create_time�?014�?�?日下�?:43:46
	 *@modifie_time�?014�?�?�?下午2:43:46
	  
	 */
	public static void getFollowGraph(String result,String userFileString){
		DBCollection socialGraph = getCollection();
		
		List<String> userList = new ArrayList();
		//String userFileString="20WuserList.txt";
		userList=getUserSet(userFileString);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				result, true));) {
			BasicDBObject oneuser=null;
			
			String userId = null;
			List<String> folleeList = null;
			int index=0;
			
			Set<String> userSet=new HashSet<>();
			userSet.addAll(userList);
			while (index < userList.size()) {
				
				oneuser = new BasicDBObject("_id", Long.valueOf(userList
						.get(index)));
				oneuser = (BasicDBObject) socialGraph.findOne(oneuser);
				index++;
				userId = String.valueOf(oneuser.getLong("_id"));
				folleeList = (List<String>) oneuser.get("ids");
				
				for (int i = 0; i < folleeList.size(); i++) {
					String folleeId = String.valueOf(folleeList.get(i));
					if (userSet.contains(folleeId)) {
						bufferedWriter.write(userId + "," + folleeId);
						bufferedWriter.newLine();
					}

				}
				System.out.println(index);
			}
		
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * @param fileName
	 * @return read userList from file
	 *@create_time�?014�?�?日下�?:44:27
	 *@modifie_time�?014�?�?�?下午2:44:27
	  
	 */
	public static List<String> getUserSet(String fileName){
		List<String> userSet=new ArrayList();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				fileName));) {
			String oneLineString;
			while((oneLineString=bufferedReader.readLine().trim())!=null)
			{
				userSet.add(oneLineString);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return userSet;
	}
	
	/**
	 * @param result
	 * @param size
	 * @return
	 *@create_time�?014�?�?日下�?:51:33
	 *@modifie_time�?014�?�?�?下午2:51:33
	  
	 */
	public List<String> getUserSet(String result,int size){
		DBCollection socialGraph = getCollection();
		DBCollection weiboNumber = getWeiboNumber();
		List<String> userList = new ArrayList();
		
			DBCursor dbCursor = socialGraph.find();
			BasicDBObject oneuser=null;
			
			String userId = null;
			List<String> folleeList = null;
			int index=0;
			while (true) {
				oneuser = (BasicDBObject) dbCursor.next();
				userId = String.valueOf(oneuser.getLong("_id"));
				if (ifWeiboNumberBigger(Long.valueOf(userId), weiboNumber)) {
					userList.add(userId);
					folleeList = (List<String>) oneuser.get("ids");
					for (int i = 0; i < folleeList.size(); i++) {
						String folleeId = String.valueOf(folleeList.get(i));
						//find if the user in followGraph
						long count = socialGraph.count(new BasicDBObject("_id",
								Long.valueOf(folleeId)));
						if (count == 0) {
							continue;
						}
						//find the weiboNumber 
						if (ifWeiboNumberBigger(Long.valueOf(folleeId), weiboNumber)) {
							userList.add(folleeId);
						}
					}
				}
				if (userList.size()>100) {
					break;
				}
			}
			
			while (userList.size() < size) {
				oneuser = new BasicDBObject("_id", Long.valueOf(userList
						.get(index)));
				oneuser = (BasicDBObject) socialGraph.findOne(oneuser);
				index++;
				userId = String.valueOf(oneuser.getLong("_id"));
				folleeList = (List<String>) oneuser.get("ids");
				for (int i = 0; i < folleeList.size(); i++) {
					String folleeId = String.valueOf(folleeList.get(i));
					//find if in the followGraph 
					long count = socialGraph.count(new BasicDBObject("_id",
							Long.valueOf(folleeId)));
					
					if (count == 0) {
						//the next user
						continue;
					}
					//find if the weiboNumber is true
					if(!ifWeiboNumberBigger(Long.valueOf(folleeId), weiboNumber)){
						//the next user
						continue;
					}
					if (!userList.contains(folleeId)) {
						userList.add(folleeId);
						if (userList.size()%10000==0) {
							System.out.println(userList.size());
						}
						
					}
				
				}
			}

			WriteUtil<String> writeUtil=new WriteUtil<>();
			writeUtil.writeList(userList, result);
			return userList;
	}
	/**
	 * @return
	 *@create_time�?014�?�?日下�?:44:39
	 *@modifie_time�?014�?�?�?下午2:44:39
	  
	 */
	public static DBCollection getCollection() {
		DBCollection socialGraph = null;
		try {
			socialGraph = new Mongo("172.31.218.51", 27017).getDB("Weibo")
					.getCollection("SocialGraph");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socialGraph;
	}

	/**
	 * @return
	 *@create_time�?014�?�?日下�?:44:43
	 *@modifie_time�?014�?�?�?下午2:44:43
	  
	 */
	public static DBCollection getWeiboNumber() {
		DBCollection socialGraph = null;
		try {
			socialGraph = new Mongo("172.31.218.51", 27018).getDB("test")
					.getCollection("userInfo");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socialGraph;
	}

	/**
	 * @param userId
	 * @param weiboNumber
	 * @return if the user weibo number big than weiboNumber
	 * @create_time�?014�?�?1日下�?:52:26
	 * @modifie_time�?014�?�?1�?下午1:52:26
	 */
	public static boolean ifWeiboNumberBigger(long userId, DBCollection weiboNumber) {
		BasicDBObject oneuser = new BasicDBObject("_id", userId);
		oneuser = (BasicDBObject) weiboNumber.findOne(oneuser);
		if (oneuser != null) {
			int count = oneuser.getInt("WN");
			if (count > 50) {
				return true;
			}
		}
		return false;
	}
}
