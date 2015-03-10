/**
 * 
 */
package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;

/**
 * 
 * 椤圭洰鍚嶇О锛歸eibo 绫诲悕绉帮細statisticsFollowerAndfriend 绫绘弿杩帮細 鍒涘缓浜猴細zhouge
 * 鍒涘缓鏃堕棿锛�014骞�鏈�5鏃�涓嬪崍10:08:14 淇敼浜猴細zhouge 淇敼鏃堕棿锛�014骞�鏈�5鏃�涓嬪崍10:08:14
 * 淇敼澶囨敞锛�
 * 
 * @version
 * 
 */
public class statisticsFollowerAndfriend {
	public static void main(String[] args) {
		staticFromTxt();
	}

	/**
	 * 
	 * @create_time：2014年10月21日下午8:43:19
	 * @modifie_time：2014年10月21日 下午8:43:19
	 */
	public static void staticFromTxt() {
		 String path = "J:/workspacedata/weiboNew/data/2w/";
		 String followGraphFile=path+"followgraph.txt";
		 ReadUtil<String> readUtil=new ReadUtil<>();
		 List<String> followList=readUtil.readFileByLine(followGraphFile);
		 HashMap<String, Integer> friendSet = new HashMap<>();
		 HashMap<String, Integer> followerSet = new HashMap<>();
		 Set<String> userSet=new HashSet();
		 for (String oneLine : followList) {
			String[] split=oneLine.split(",");
			if (friendSet.keySet().contains(split[0].trim())) {
				friendSet.put(split[0].trim(),
						friendSet.get(split[0].trim()) + 1);
			}else {
				friendSet.put(split[0].trim(),1);
			}
			
			if (followerSet.keySet().contains(split[1].trim())) {
				followerSet.put(split[1].trim(),
						followerSet.get(split[1].trim()) + 1);
			}else {
				followerSet.put(split[1].trim(),1);
			}
			userSet.add(split[0]);
			userSet.add(split[1]);	
		}
		 WriteUtil<String> writeUtil = new WriteUtil();
//		 String friendSetFile = path + "twitterfriendSetNumber.txt";
//		 writeUtil.writeMapKeyAndValue(friendSet, friendSetFile);
//		 
//		 String followerSetFile = path + "twitterfollowerSetNumber.txt";
//		 writeUtil.writeMapKeyAndValue(followerSet, followerSetFile);
		 List<String> result=new ArrayList<>();
		 for (String userid : userSet) {
			 int[] temp=new int[2];
			 if (friendSet.containsKey(userid)) {
				 temp[0]=friendSet.get(userid);
			}
			 if (followerSet.containsKey(userid)) {
				 temp[1]=followerSet.get(userid);
			}
			 
			 result.add(temp[0]+","+temp[1]);
		}
		 String friendandfollowerSetFile = path + "weibofriendandfollowerSetNumber.csv";
		 writeUtil.writeList(result, friendandfollowerSetFile);
	}	

	public void staticFromMongo() throws IOException {
		String pathString = "J:\\weibo\\5k\\20140312\\";
		String userSetFile = pathString + "randomUserList.txt";

		ReadUtil<String> readUtil = new ReadUtil();
		ArrayList<String> userset = null;
		int usersetSize = 0;
		userset = (ArrayList<String>) readUtil.readFileByLine(userSetFile);
		usersetSize = userset.size();

		Mongo microblog = null;
		DB microblogDB = null;
		DBCollection socialGraph = null;
		try {
			microblog = new Mongo("172.17.166.4", 27018);
			microblogDB = microblog.getDB("Weibo");
			socialGraph = microblogDB.getCollection("SocialGraph");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(" getUser failue!");
		}
		HashMap<String, Integer> userAndFollower = new HashMap<>();
		DBCursor dbCursor = socialGraph.find();
		long allCount = socialGraph.count();

		int count = 0;
		while (dbCursor.hasNext()) {
			BasicDBList followList = (BasicDBList) dbCursor.next().get("ids");
			HashSet<String> followSet = new HashSet<>();
			for (int i = 0; i < followList.size(); i++) {
				followSet.add(String.valueOf(followList.get(i)));
			}
			for (String userId : userset) {
				if (followSet.contains(userId)) {
					if (userAndFollower.containsKey(userId)) {
						userAndFollower.put(userId,
								userAndFollower.get(userId) + 1);
					} else {
						userAndFollower.put(userId, 1);
						System.out.println("dd");
					}
				}
			}

		}
		String userFollowerAndFriendFile = pathString + "userFriend.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				new File(userFollowerAndFriendFile), true));
		Iterator<String> userList = userAndFollower.keySet().iterator();
		while (userList.hasNext()) {
			String userId = userList.next();
			bufferedWriter.append(userId + "\t" + userAndFollower.get(userId));
			bufferedWriter.newLine();
		}

		bufferedWriter.flush();
		bufferedWriter.close();
		System.out.println();
	}
}
