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
	// ����Ŀ¼
 static String workPath ;
	// ��ȡ�û���follow�����
	static String FollwGraphResult ;
	// �û������ݱ���λ��
	static String userSource ;
	//���ݿ�����
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
		// ���ļ�������
		ReadUtil readUtil = new ReadUtil();
		// д�ļ�������
		WriteUtil writeUtil = new WriteUtil();
		
		// ���ȶ�ȡ5K�û�
		ArrayList<String> userList = new ArrayList<String>();
		try {
			userList = readUtil.readfromFile(userSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("faliure to read UserList");

		}
		final Mongo mg = new Mongo(socaiGraphDB, socaiGraphDBPort);
		// ��ӡmongodb���ݿ������ӵ����ݼ�
		// ʹ����ΪWeibo���ݿ�
		final DB db = mg.getDB("Weibo");
		// ʹ����ΪSocialGraph�ı��
		final DBCollection users = db.getCollection("SocialGraph");
		int userListSize = userList.size();
		// 5K�û���followͼ������
		ArrayList<String> followGraph = new ArrayList<String>();
		// ���ȱ�֤����
		followGraph.ensureCapacity(userListSize);
		int[][] result=new int[userListSize][userListSize];
		for (int i = 0; i < userListSize; i++) {
			// �����������������ĳ��ֵ��д���ļ���
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

	// �ж�user1�Ƿ�follow user2
	private boolean isFollow(String tempString, String user2) {
		if (tempString != null) {
			return tempString.indexOf(user2)!=-1?true:false;
		}else {
			return false;
		}
		
	}
}



