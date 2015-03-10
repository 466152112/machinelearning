/**
 * 
 */
package statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import util.ReadUtil;

/**
 * 
 * 椤圭洰鍚嶇О锛歸eibo 绫诲悕绉帮細staticWeiboNumber 绫绘弿杩帮細 鍒涘缓浜猴細zhouge 鍒涘缓鏃堕棿锛�014骞�鏈�3鏃�涓嬪崍4:19:40
 * 淇敼浜猴細zhouge 淇敼鏃堕棿锛�014骞�鏈�3鏃�涓嬪崍4:19:40 淇敼澶囨敞锛�
 * 
 * @version
 * 
 */
public class staticWeiboNumber {

	public static void main(String[] args) throws IOException {

		ArrayList<String> userList = new ArrayList<>();
		// 鎵弿鐢ㄦ埛鍒楄〃鏂囦欢
		Scanner scanner = new Scanner(new File(
				"J:/weibo/5k/20140312/randomUserList.txt"));
		while (scanner.hasNext()) {
			// 璇诲彇涓�
			String oneLine = scanner.next();
			userList.add(oneLine.trim());
		}
		ArrayList<Integer> needUserId = new ArrayList<>();
		// 鎵弿闇�鐨勭敤鎴峰垪琛ㄦ枃浠�
		Scanner scanner1 = new Scanner(new File(
				"J:/weibo/5k/20140312/needUserId.txt"));

		while (scanner1.hasNext()) {
			// 璇诲彇涓�
			String oneLine = scanner1.next();
			needUserId.add(Integer.parseInt(oneLine.trim()));
		}
		ArrayList<String> needUserList = new ArrayList<>();
		for (Integer userId : needUserId) {
			needUserList.add(userList.get(userId));
		}

		// 寰崥鏁版嵁搴�
		Mongo microblog = null;
		DB microblogDB = null;
		DBCollection microblogCollection = null;
		try {
			microblog = new Mongo("172.17.166.19", 27017);
			microblogDB = microblog.getDB("test");
			microblogCollection = microblogDB.getCollection("microblogs");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(" getUserWeibo failue!");
		}
		int[] result = new int[needUserId.size()];
		Long.parseLong("2309762582");
		for (int i = 0; i < needUserId.size(); i++) {
			String userIdString = needUserList.get(i).trim();
			BasicDBObject basicDBObject = new BasicDBObject("user_id",
					Long.parseLong(userIdString));
			result[i] = (int) microblogCollection.count(basicDBObject);
			System.out.println(userIdString + "\t" + result[i]);
		}

	}

}
