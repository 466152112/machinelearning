package xiaoqiangdb;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class xiaoqiangdb {
	public static void main(String[] ar) throws UnknownHostException{
		Mongo mongo=new Mongo("172.17.166.71",27017);
		DBCollection qqqunCollection=mongo.getDB("qq").getCollection("qqidqunid");
		BasicDBObject basicDBObject=new BasicDBObject("qunid", 35412211);
		DBCursor dbCursor=qqqunCollection.find(basicDBObject);
		while(dbCursor.hasNext()){
			System.out.println(dbCursor.next());
		}
	}
}
