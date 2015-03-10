package weibo;
 
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;

import org.bson.BSON;
import org.bson.BasicBSONObject;

import weibo.util.WriteUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
 

public class SocialGraph {
	
	public static void main(String[] args) throws UnknownHostException, MongoException {
        Mongo mg = new Mongo("172.17.166.124",27017);
        ArrayList<String> list= (ArrayList<String>) mg.getDatabaseNames();
        for(int i=0;i<list.size();i++){
        	System.out.println(list.get(i));
        }
        DB db = mg.getDB("Weibo");
        Set<String> set=db.getCollectionNames();
        Object[] setList=set.toArray();
        for(int i=0;i<set.size();i++)
        	System.out.println((String)setList[i]);
     
       DBCollection socialGraph = db.getCollection("SocialGraph");
       // DBCollection socialGraph = db.getCollection("bi");
        //DBCollection socialGraph = db.getCollection("ra");
        DBCursor cur = socialGraph.find();
        System.out.println(socialGraph.findOne());
        int count=100000;
       
        while(cur.hasNext()&&count>=0){
        	//121036374
        	if(count%23==0){
        		BasicBSONObject basicBSONObject=(BasicBSONObject) cur.next();
                
            	System.out.println(basicBSONObject);
        	}
        	else {
				cur.next();
			}
        	count--;
        }
        /*
         * 每一万条数据就写入一个文档
         */
        int fineName=0;
        //保存数据
        ArrayList<String> tempList=new ArrayList<String>();
        System.out.println(cur.count());
        System.out.println(cur.getCursorId());
      //  System.out.println(JSON.serialize(cur));
    }
}