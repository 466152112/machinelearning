/**
 * 
 */
package tool.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.SegmentInfos.FindSegmentsFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：Mongo_Source   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年12月23日 下午3:39:43   
 * @modifier：zhouge   
 * @modified_time：2014年12月23日 下午3:39:43   
 * @modified_note：   
 * @version    
 *    
 */
public class Mongo_Source {
	private String ip;
	private int port;
	private String db;
	private String collection;
	private  DBCollection target_collection=null;
	
	/**
	 * @param ip
	 * @param port
	 * @param db_name
	 * @param collection_name
	 */
	public Mongo_Source(String ip,int port,String db_name,String collection_name){
		this.ip=ip;
		this.port=port;
		this.db=db_name;
		this.collection=collection_name;
		try {
		target_collection=new Mongo(ip, port).getDB(db)
				.getCollection(collection);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param select
	 * @return count of the target
	 *@create_time：2014年12月23日下午3:53:51
	 *@modifie_time：2014年12月23日 下午3:53:51
	  
	 */
	public int Count(BasicDBObject select){
		int count=(int) target_collection.count(select);
		return count;
	}
	
	public List<BasicDBObject> select(BasicDBObject select){
			DBCursor cur = target_collection.find(select);
			List<BasicDBObject> result=new ArrayList<>();
			while (cur.hasNext()) {
				BasicDBObject tempDbObject = (BasicDBObject) cur.next();
				result.add(tempDbObject);
			}
			return result;
	}
	
	
	public BasicDBObject findOne(BasicDBObject select){
		BasicDBObject cur = (BasicDBObject) target_collection.findOne(select);
		return cur;
	}
	
	public void save(BasicDBObject save){
		target_collection.save(save);
	}
	
	/**
	 * @return 查询数据库中所有记录，放回游标
	 *@create_time：2015年1月5日下午1:48:03
	 *@modifie_time：2015年1月5日 下午1:48:03
	  
	 */
	public DBCursor find(){
		DBCursor cursor=target_collection.find();
		return cursor;
	}
	public List<BasicDBObject> select_specify_field(BasicDBObject select,String[] field){
		DBCursor cur = target_collection.find(select);
		List<BasicDBObject> result=new ArrayList<>();
		while (cur.hasNext()) {
			BasicDBObject tempDbObject = (BasicDBObject) cur.next();
			BasicDBObject newdb =new BasicDBObject();
			for (String fieldName : field) {
				newdb.append(fieldName, tempDbObject.get(fieldName));
			}
			result.add(newdb);
		}
		return result;
	}

}
