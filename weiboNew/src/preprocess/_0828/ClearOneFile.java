/**
 * 
 */
package preprocess._0828;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.RecursiveAction;

import util.CalanderUtil;
import bean.AvlTree;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：ClearOneFile   
 * @class_describe�?  
 * @creator：zhouge   
 * @create_time�?014�?�?8�?上午11:42:47   
 * @modifier：zhouge   
 * @modified_time�?014�?�?8�?上午11:42:47   
 * @modified_note�?  
 * @version    
 *    
 */
public class ClearOneFile extends RecursiveAction{

	final BufferedReader bufferedReader;
	final BufferedWriter bufferedWriter;
	//final DBCollection userInfo;
	AvlTree<Long> avlTree;
	final DBCollection weiboinfo;
	final File sourceFile;
	public ClearOneFile(File sourceFile,File targetFile,AvlTree<Long> avlTree ,DBCollection weiboinfo) throws IOException{
	
		this.avlTree=avlTree;
		this.weiboinfo=weiboinfo;
		this.sourceFile=sourceFile;
		bufferedReader=new BufferedReader(new FileReader(sourceFile));
		bufferedWriter=new BufferedWriter(new FileWriter(targetFile));
	}
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override

	protected void compute() {
		// TODO Auto-generated method stub
		try 
		{
			String tempLine = null;
			long count = 0L;
			long weiboIdFlag = 0L;
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
							if(avlTree.contains(user_id)){
							
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
//							if(userInfo.count(userInfoUser)==0){
//								continue;
//							}
//							
							if (!avlTree.contains(retweetuser_id)) {
								continue;
							}
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
							
							BasicDBObject Rweibo=new BasicDBObject("_id", retweetId);
//							Rweibo=(BasicDBObject) weiboinfo.findOne(Rweibo);
//							
//							if (Rweibo==null) {
//								Rweibo=new BasicDBObject("_id", retweetId);
//								Rweibo.put("user_id", retweetuser_id);
//								Rweibo.put("RN", 1);
//							}else {
//								Rweibo.put("RN",Rweibo.getInt("RN")+1 );
//							}
//							weiboinfo.save(Rweibo);
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
//						
//						Date date = CalanderUtil.getDate(time);
//						Calendar cal = new GregorianCalendar();
//						cal.setTime(date);
//						weibo.append("created_time", time);
//						if (cal.get(Calendar.YEAR) != 2012 ) {
//							// System.out.println(month);
//							continue;
//						}
						
						
						//save to the mongodb 
//						BasicDBObject tempdb=new BasicDBObject("_id",weibo.getLong("_id"));
//						tempdb=(BasicDBObject) weiboinfo.findOne(tempdb);
//					
//						if (tempdb==null) {
//							weibo.put("RN", 0);
//							weiboinfo.save(weibo);
//						}else {
//							tempdb.append("created_time", weibo.get("created_time"));
//							weiboinfo.save(tempdb);
//						}
					
						bufferedWriter.write(tempLine);
						bufferedWriter.newLine();
						count++;
						if (count % 100000 == 0) {
							System.out.println(sourceFile.getName()+count);
							bufferedWriter.flush();
						}
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}

				}
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

}
