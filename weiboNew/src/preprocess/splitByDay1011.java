/**
 * 
 */
package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import bean.AvlTree;

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
public class splitByDay1011 {

	final SimpleDateFormat sf1 = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

	public static void main(String[] dk) {
		String path = "/media/pc/new2/data/";
		//String path="J:/workspace/weiboNew/data/";
		String targetPath=path+"new/weibocontent/";
		String sourceFile1 = path + "2011.txt";
		String sourceFile2 = path + "2012.txt";
		String userIdFile="/media/pc/new2/data/new/userid/useridweiboNumber.txt";
	
		splitByDay1011 compareTime=new splitByDay1011();
		AvlTree<Long> avlTree =compareTime.getuserid(userIdFile);
		compareTime.splitFileByDay(sourceFile1,targetPath,avlTree);
		compareTime.splitFileByDay(sourceFile2, targetPath, avlTree);
	}

	public AvlTree<Long> getuserid(String userIdFile){
		AvlTree<Long> result=new AvlTree<Long>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				userIdFile));) {
			String onelineString="";
			while((onelineString=bufferedReader.readLine())!=null){
				String[] split=onelineString.split("\t");
				result.insert(Long.valueOf(split[0]));
			}
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 	result;
	}
	
	public void splitFileByDay(String sourceFile,String targetPath,AvlTree<Long> avlTree) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				sourceFile));
				BufferedWriter wordsplitWriter= new BufferedWriter(new FileWriter(new File("/media/pc/new2/data/new/wordsplit/wordsplit.txt"), true));
				) {

			String tempLine = null;
			long count = 0L;
			long weiboIdFlag = 0L;
			HashMap<String, BufferedWriter> writers=new HashMap<>();
			try {
				String time = null;
				while ((tempLine = bufferedReader.readLine()) != null) {
					tempLine = tempLine.trim();
					// System.out.println(oneLine);;
					String oneLine = tempLine;
					try {
						if (oneLine.indexOf("\t") == -1) {
							continue;
						}
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
							if (!avlTree.contains(user_id)) {
								continue;
							}
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
							// ��ȡ�û�id
							long retweetuser_id = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
						
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
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
						Date date= getDate(time);
						Calendar cal=new GregorianCalendar();
						cal.setTime(date);
					
						int month=cal.get(Calendar.MONTH)+1;
						String fileName=cal.get(Calendar.YEAR)+"_"+month+"_"+cal.get(Calendar.DATE)+".txt";
						if (!writers.containsKey(fileName)) {
							writers.put(fileName, getWriter(targetPath+fileName));
						}
						writers.get(fileName).write(tempLine);
						writers.get(fileName).newLine();
						count++;
						if (count%10000000==0) {
							System.out.println(count);
							Iterator<String> Iterator=writers.keySet().iterator();
							while(Iterator.hasNext()){
								String key=Iterator.next();
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
			Iterator<String> Iterator=writers.keySet().iterator();
			while(Iterator.hasNext()){
				String key=Iterator.next();
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
