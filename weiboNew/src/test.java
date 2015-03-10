

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import weibo.Copy2RandomGetUser;
import weibo.CopyOfRandomGetUser;
import weibo.FollowGraph;
import weibo.RandomGetUser;
import weibo.TermSplit;
import weibo.calValue;

public class test {
	static String randomUserWeibo,TF_IDFResultData,resultUserData,SVDResultData,resultTermData,encoding,HTTPREGEXS_STRING,dataPath,TF_IDFCalValue;
	static int randomSize,socaiGraphDBPort,microblogDBPort;
	static String randomUserList,socaiGraphDB,microblogDB,follwGraphResult;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		test test=new test();
		test.getDataSource();
		
		//随机获取200用户，以及微博
		//RandomGetUser randomGetUser=new RandomGetUser(randomSize, dataPath, randomUserList, randomUserWeibo, socaiGraphDB, socaiGraphDBPort, microblogDB, microblogDBPort);
		//CopyOfRandomGetUser copyOfRandomGetUser=new CopyOfRandomGetUser(randomSize, dataPath, randomUserList, randomUserWeibo, socaiGraphDB, socaiGraphDBPort, microblogDB, microblogDBPort);
		
		//Copy2RandomGetUser copyOfRandomGetUser=new Copy2RandomGetUser(randomSize, dataPath, randomUserList, randomUserWeibo, socaiGraphDB, socaiGraphDBPort, microblogDB, microblogDBPort);
		//对上一步获取的微博词语进行划分，并且计算TF_idf值
		//new TermSplit(randomUserWeibo, TF_IDFResultData, resultUserData, resultTermData, encoding, HTTPREGEXS_STRING, dataPath);
		//new FollowGraph(dataPath, resultUserData, socaiGraphDB, socaiGraphDBPort,follwGraphResult);
		new calValue(dataPath, follwGraphResult, SVDResultData,test.getDbCollection("172.17.166.19", 27017, "zhouge", "CalTF_IDF"));
		
		
	}
	//获取数据库的表
	private DBCollection getDbCollection(String DBHost,int DBPort,String dbName,String collectionName){
		DBCollection result=null;
		try {
			Mongo mongo=new Mongo(DBHost, DBPort);
			DB db=mongo.getDB(dbName);
			result=db.getCollection(collectionName);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	//load dataSource and resultOutputsetting
		public void getDataSource(){
			Properties properties=new Properties();
			try {
				InputStream in = new BufferedInputStream(new FileInputStream("dataSource.properties"));
				properties.load(in);
				 dataPath=properties.getProperty("dataPath");
				randomUserWeibo=dataPath+properties.getProperty("randomUserWeibo");
				TF_IDFResultData=dataPath+properties.getProperty("TF_IDFResultData");
				
				resultUserData=dataPath+properties.getProperty("resultUserData");
				
				resultTermData=dataPath+properties.getProperty("resultTermData");
				encoding=properties.getProperty("encoding");
				HTTPREGEXS_STRING=properties.getProperty("HTTPREGEXS_STRING");
				randomUserList=dataPath+properties.getProperty("randomUserList");
				socaiGraphDB=properties.getProperty("socaiGraphDB");
				microblogDB=properties.getProperty("microblogDB");
				randomSize=Integer.parseInt(properties.getProperty("randomSize"));
				socaiGraphDBPort=Integer.parseInt(properties.getProperty("socaiGraphDBPort"));
				microblogDBPort=Integer.parseInt(properties.getProperty("microblogDBPort"));
				follwGraphResult=dataPath+properties.getProperty("follwGraphResult");
				TF_IDFCalValue=dataPath+properties.getProperty("TF_IDFCalValue");
				SVDResultData=dataPath+properties.getProperty("SVDResult");
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
