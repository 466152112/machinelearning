package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class Time {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Mongo microblog=null;
		try {
			microblog=new Mongo("172.17.166.19", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashSet<String> time=new HashSet<>();
		DB microblogDB = microblog.getDB("test");
		DBCollection microblogCollection = microblogDB.getCollection("microblogs");
		DBCursor dbCursor=microblogCollection.find();
		while(dbCursor.hasNext()){
			BasicDBObject basicDBObject=(BasicDBObject)dbCursor.next();
			time.add(basicDBObject.getString("created_at"));
			
		}
		Iterator<String> timeIterator=time.iterator();
		File file=new File("E://time.txt");
		BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(file));
		while (timeIterator.hasNext()) {
			String key = (String) timeIterator.next();
			bufferedWriter.write(key);
			bufferedWriter.newLine();
		}
	}

}
