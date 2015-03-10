/**
 * 
 */
package statistics;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * 椤圭洰鍚嶇О锛歸eibo 绫诲悕绉帮細TimeByMonth 绫绘弿杩帮細 鎸夌収鏈堢粺璁″井鍗�鍒涘缓浜猴細zhouge
 * 鍒涘缓鏃堕棿锛�014骞�鏈�5鏃�涓嬪崍6:25:49 淇敼浜猴細zhouge 淇敼鏃堕棿锛�014骞�鏈�5鏃�涓嬪崍6:25:49
 * 淇敼澶囨敞锛�
 * 
 * @version
 * 
 */
public class statisticsWeibo {

	public static void main(String[] args) throws IOException {
		String pathString = "/home/zhouge/database/weibo/20w/";
		String randomWeibo = pathString + "Split.txt";
		String userListFile = pathString + "userList2w.txt";
		WriteUtil<String> writeUtil = new WriteUtil();
		HashMap<String, Integer> userSet = new HashMap<>();
		 try (BufferedReader reader=new BufferedReader(new FileReader(new File(randomWeibo)));
			 BufferedReader readeruser=new BufferedReader(new FileReader(new File(userListFile)));
				 ){
			 String oneLine;
			 while((oneLine=readeruser.readLine())!=null){
				 userSet.put(oneLine.trim(), 0);
			 }
			 
			 oneLine=reader.readLine();
			 while(oneLine!=null){
				String[] oneLineSplit=oneLine.split("\t");
				
				// 鍒ゆ柇鐢ㄦ埛闆嗘槸鍚﹀凡缁忓瓨鍦ㄤ簡姝ょ敤鎴�
				if (userSet.keySet().contains(oneLineSplit[0].trim())) {
					userSet.put(oneLineSplit[0].trim(),
							userSet.get(oneLineSplit[0].trim()) + 1);
				}
				oneLine=reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		
//		HashMap<String, Integer> timeSet = new HashMap<>();
		 String userWeiboNumber = pathString + "userWeiboNumber.txt";
		 writeUtil.writeMapKeyAndValue(userSet, userWeiboNumber);
		
//		String monthWeiboNumber = pathString + "monthdayWeiboNumber.txt";
//		Iterator<String> monthWeiboNumberIterator = timeSet.keySet().iterator();
//		bufferedWriter = new BufferedWriter(new FileWriter(new File(
//				monthWeiboNumber), true));
//		while (monthWeiboNumberIterator.hasNext()) {
//			String monthString = monthWeiboNumberIterator.next();
//			bufferedWriter
//					.append(monthString + "\t" + timeSet.get(monthString));
//			bufferedWriter.newLine();
//		}
//		bufferedWriter.flush();
//		bufferedWriter.close();
//
//		System.out.println(userSet.size());
//		System.out.println(timeSet.size());
	}
}
