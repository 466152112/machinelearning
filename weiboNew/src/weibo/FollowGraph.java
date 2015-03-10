package weibo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.BSON;
import org.bson.types.ObjectId;

import weibo.util.ReadUtil;
import weibo.util.WriteUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class FollowGraph {
	// 工作目录
 static String workPath ;
	// 抽取用户的follow结果集
	static String FollwGraphResult ;
	// 用户的数据保存位置
	static String userSource ;
	//数据库设置
	static String socaiGraphDB;
	static int socaiGraphDBPort;
	public FollowGraph(String workPath,String userSet,String socaiGraphDB, int socaiGraphDBPort,String FollwGraphResult) throws UnknownHostException, MongoException{
		this.workPath=workPath;
		this.FollwGraphResult=FollwGraphResult;
		this.userSource=userSet;
		this.socaiGraphDB=socaiGraphDB;
		this.socaiGraphDBPort=socaiGraphDBPort;
		System.out.println("enter FollowGraph class");
		calIsFollow();
	}
	
	private void calIsFollow() throws UnknownHostException,
			MongoException {
		System.out.println("begin to cal follow relatship");
		// 读文件工作类
		ReadUtil readUtil = new ReadUtil();
		// 写文件工作类
		WriteUtil writeUtil = new WriteUtil();
		
		// 首先读取5K用户
		ArrayList<String> userList = new ArrayList<String>();
		try {
			userList = readUtil.readfromFile(userSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("faliure to read UserList");

		}
		final Mongo mg = new Mongo(socaiGraphDB, socaiGraphDBPort);
		// 打印mongodb数据库已连接的数据集
		// 使用名为Weibo数据库
		final DB db = mg.getDB("Weibo");
		// 使用名为SocialGraph的表格
		final DBCollection users = db.getCollection("SocialGraph");
		int userListSize = userList.size();
		// 5K用户的follow图保存于
		ArrayList<String> followGraph = new ArrayList<String>();
		// 首先保证容量
		followGraph.ensureCapacity(userListSize);
		int[][] result=new int[userListSize][userListSize];
		for (int i = 0; i < userListSize; i++) {
			// 如果数组数据量大于某个值则写入文件中
			String user1 = userList.get(i);
			//followGraph.add(user1+"\t"+user2+"\t"+(isFollow(user1, user2,users)?1:0));
			BasicDBObject findBasicDBObject = new BasicDBObject("_id",
					Long.parseLong("2154848361"));
			findBasicDBObject = (BasicDBObject) users.findOne(findBasicDBObject);
			String adjiString=findBasicDBObject.getString("ids");
			for (int j= 0; j < userListSize; j++){
				
				String user2=userList.get(j);
				
				if (j==i) {
					//followGraph.add(user1+"\t"+user2+"\t"+"0");
				result[i][j]=0;
				}else {
					result[i][j]=isFollow(adjiString, "2525878170")?1:0;
				}
			}		
			}
		
		writeUtil.write2Array(FollwGraphResult, result);
	
		System.out.println("sucess to cal follow relatship");
		
	}

	// 判断user1是否follow user2
	private boolean isFollow(String tempString, String user2) {
		if (tempString != null) {
			return tempString.indexOf(user2)!=-1?true:false;
		}else {
			return false;
		}
		
	}
}



