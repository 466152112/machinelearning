/**
 * 
 */
package preprocess._0825;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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
public class ClearByUser {

	final SimpleDateFormat sf1 = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

	public static void main(String[] dk) {

		//String path = "/media/zhouge/new2/data/";
		 String path="J:/workspace/weiboNew/data/";
		String targetPath = path + "split/";
		String sourceFile = path + "aa.txt";
		ClearByUser compareTime = new ClearByUser();
		final DBCollection followGraph;
		final DBCollection userInfo;
		final DBCollection weiboinfo;
		try {
			followGraph = new Mongo("172.31.218.51", 27017).getDB("Weibo")
					.getCollection("SocialGraph");
			userInfo = new Mongo("172.31.218.51", 27017).getDB("Weibo")
					.getCollection("userInfo");
			weiboinfo = new Mongo("172.31.218.51", 27018).getDB("test")
					.getCollection("weiboinfo");
			compareTime.splitFileByDay(sourceFile, targetPath,followGraph,userInfo,weiboinfo);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
	}

	/**
	 * @param sourceFile
	 * @param targetPath
	 * @param followGraph
	 * @param userInfo
	 * @param weiboCount
	 *@create_time：2014年8月28日上午10:19:11
	 *@modifie_time：2014年8月28日 上午10:19:11
	  
	 */
	public void splitFileByDay(String sourceFile, String targetPath,DBCollection followGraph, DBCollection userInfo,DBCollection weiboinfo) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				sourceFile));) {

			String tempLine = null;
			long count = 0L;
			long weiboIdFlag = 0L;
			HashMap<String, BufferedWriter> writers = new HashMap<>();
			try {
				
				
				while ((tempLine = bufferedReader.readLine()) != null) {
					tempLine = tempLine.trim();
					// System.out.println(oneLine);;
					String oneLine = tempLine;
					String time = null;
					try {
						if (oneLine.indexOf("\t") == -1) {
							continue;
						}
						BasicDBObject weibo=new BasicDBObject();
						
						try {
							// 截取微博id
							long weiboId = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							if (weiboIdFlag == weiboId) {
								// System.out.println("same ");
								continue;
							} else {
								weiboIdFlag = weiboId;
							}
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
							// 截取用户id
							long user_id = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							BasicDBObject followUser=new BasicDBObject("_id", user_id);
							BasicDBObject userInfoUser=new BasicDBObject("id", user_id);
							if(followGraph.count(followUser)!=0&&userInfo.count(userInfoUser)!=0){
							
							}else {
								continue;
							}
							weibo.append("_id", weiboId);
							weibo.append("user_id", user_id);
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();

						} catch (NumberFormatException e) {
							continue;
						}

						try {
							long retweetId = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
							
							// 截取用户id
							long retweetuser_id = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							BasicDBObject followUser=new BasicDBObject("_id", retweetuser_id);
							BasicDBObject userInfoUser=new BasicDBObject("id", retweetuser_id);
							if(followGraph.count(followUser)==0||userInfo.count(userInfoUser)==0){
								continue;
							}
							
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
							
							BasicDBObject Rweibo=new BasicDBObject("_id", retweetId);
							Rweibo=(BasicDBObject) weiboinfo.findOne(Rweibo);
							
							if (Rweibo==null) {
								Rweibo=new BasicDBObject("_id", retweetId);
								Rweibo.put("user_id", retweetuser_id);
								Rweibo.put("RN", 1);
							}else {
								Rweibo.put("RN",Rweibo.getInt("RN")+1 );
							}
							weiboinfo.save(Rweibo);
							try {
								time = oneLine.substring(0,
										oneLine.indexOf("\t"));
							} catch (StringIndexOutOfBoundsException e) {
								time = oneLine.trim();
							}

						} catch (NumberFormatException e) {

							time = oneLine.substring(0, oneLine.indexOf("\t"));
							
						} catch (StringIndexOutOfBoundsException e) {
							continue;
						}
						
						Date date = getDate(time);
						Calendar cal = new GregorianCalendar();
						cal.setTime(date);
						weibo.append("created_time", time);
						if (cal.get(Calendar.YEAR) != 2012 ) {
							// System.out.println(month);
							continue;
						}
						
						
						//save to the mongodb 
						BasicDBObject tempdb=new BasicDBObject("_id",weibo.getLong("_id"));
						tempdb=(BasicDBObject) weiboinfo.findOne(tempdb);
					
						if (tempdb==null) {
							weibo.put("RN", 0);
							weiboinfo.save(weibo);
						}else {
							tempdb.append("created_time", weibo.get("created_time"));
							weiboinfo.save(tempdb);
						}
						int month=cal.get(Calendar.MONTH)+1;
						String fileName = cal.get(Calendar.YEAR) + "_" + month + "_" + cal.get(Calendar.DATE) + ".txt";
					//	System.out.println(month);
						if (!writers.containsKey(fileName)) {
							writers.put(fileName, getWriter(targetPath
									+ fileName));
						}
						writers.get(fileName).write(tempLine);
						writers.get(fileName).newLine();
						count++;
						if (count % 10000000 == 0) {
							System.out.println(count);
							Iterator<String> Iterator = writers.keySet()
									.iterator();
							while (Iterator.hasNext()) {
								String key = Iterator.next();
								writers.get(key).flush();
							}
						}
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}

				}
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Iterator<String> Iterator = writers.keySet().iterator();
			while (Iterator.hasNext()) {
				String key = Iterator.next();
				writers.get(key).flush();
				writers.get(key).close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public Date getDate(String time) {

		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public BufferedReader getReader(String fileName) {
		try {

			File csFileMuluFile = new File(fileName);
//			// 读取目录下所有文件
//			File[] Filelist = csFileMuluFile.listFiles();
//			// 遍历所有文件
//			for (int j = 0; j < Filelist.length; j++) {
//				File file = Filelist[j];
//				if (file.isFile()) {
//					return new BufferedReader(new FileReader(file));
//				}
//			}
			return new BufferedReader(new FileReader(csFileMuluFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedWriter getWriter(String fileName) {
		try {
			return new BufferedWriter(new FileWriter(new File(fileName), true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
