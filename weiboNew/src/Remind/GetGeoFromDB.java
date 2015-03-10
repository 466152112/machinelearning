/**
 * 
 */
package Remind;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import util.WriteUtil;
import bean.OnePairTweet;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：GetGeoFromDB
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年11月21日 下午11:15:13
 * @modifier：zhouge
 * @modified_time：2014年11月21日 下午11:15:13
 * @modified_note：
 * @version
 * 
 */
public class GetGeoFromDB {

	public static void main(String[] mains) {
		List<DBCollection>  listdb = getCollection();
		
		for (DBCollection dbCollection : listdb) {
			getGeo(dbCollection);
		}
	}
	
public  static void getGeo(DBCollection dbCollection){
	 //String path = "J:/workspacedata/weiboNew/data/reminder/geo/";
	 String path="/media/pc/new2/data/new/geo/";
	List<String> result = new ArrayList();
	DBCursor dbCursor = dbCollection.find();
	BasicDBObject onetweet = null;
	WriteUtil<String> writeUtil=new WriteUtil<>();
	while (dbCursor.hasNext()) {
		onetweet = (BasicDBObject) dbCursor.next();
		if (onetweet.get("geo") != null) {
			OnePairTweet onePairTweet = new OnePairTweet();
			onePairTweet.setContent(onetweet.getString("text"));
			onePairTweet.setWeiboId(onetweet.getLong("_id"));
			onePairTweet.setUserId(onetweet.getLong("user_id"));
			onePairTweet.setCreateTime(onetweet.getString("created_at"));
			BasicDBObject geo = (BasicDBObject) onetweet.get("geo");
			result.add(String.valueOf(geo.get("coordinates"))
					+ "\t" + onePairTweet.toString());
			if (result.size()>100000) {
				writeUtil.writeList(result, path+"geoweibo.txt");
				result = new ArrayList();
			}
		}

	}
}
	/**
	 * @return
	 * @create_time�?014�?�?日下�?:44:39
	 * @modifie_time�?014�?�?�?下午2:44:39
	 */
	public static List<DBCollection> getCollection() {
	
		List<DBCollection> list=new ArrayList<>();
		try {
			for (int i =2; i < 3; i++) {
				DBCollection socialGraph = null;
				socialGraph = new Mongo("172.17.166.4", 27031+i).getDB("test")
						.getCollection("microblogs");
				list.add(socialGraph);
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
