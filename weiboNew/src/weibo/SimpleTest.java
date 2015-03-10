package weibo;
 
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.BSON;

import weibo.util.WriteUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
 

public class SimpleTest {
	
	final static String  path="D://weibodata/part-";
    final static int lineLimit=5000;
	public static void main(String[] args) throws UnknownHostException, MongoException {
        Mongo mg = new Mongo("172.17.166.124",27017);
        ArrayList<String> list= (ArrayList<String>) mg.getDatabaseNames();
        for(int i=0;i<list.size();i++){
        	System.out.println(list.get(i));
        }
        DB db = mg.getDB("test");
        DBCollection users = db.getCollection("microblogs");
        DBCursor cur = users.find();
      
        /*
         * 每一万条数据就写入一个文档
         */
        int count=0;
        int fineName=0;
        //保存数据
        ArrayList<String> tempList=new ArrayList<String>();
        long begintime=System.currentTimeMillis();
        while (cur.hasNext()) {
        
        	BasicDBObject tempDbObject=(BasicDBObject) cur.next();	
        	if (count==lineLimit) {
				//写入文件系统
        		try{
        			new WriteUtil(path+fineName,tempList);
            		System.out.println(fineName++);
        		}catch(Exception e){
        			System.out.println("failure to write into file");
        		}
        		count=0;
				tempList=new ArrayList<String>();
			}
        	if (tempDbObject.get("retweeted_status")!=null) {
        		BasicDBObject retweetObject=(BasicDBObject) tempDbObject.get("retweeted_status");
        		String tempsString=tempDbObject.getString("user_id")+"\t"+tempDbObject.getString("created_at")+
        				"\t"+tempDbObject.getString("text")+"\t"+retweetObject.getString("text");
        		tempList.add(tempsString);
        		//System.out.println(tempsString);
			}else {
				String tempsString=tempDbObject.getString("user_id")+"\t"+tempDbObject.getString("created_at")+
        				"\t"+tempDbObject.getString("text");
        		tempList.add(tempsString);
        		//System.out.println(tempsString);
			}
            count++;
        }
        long endtime=System.currentTimeMillis();
        System.out.println(endtime-begintime);
        System.out.println(cur.count());
        System.out.println(cur.getCursorId());
      //  System.out.println(JSON.serialize(cur));
    }
}