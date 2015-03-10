/*
 * Author zhouge
 * data 2013-12-4
 */
package weibo;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import weibo.util.ReadUtil;
import weibo.util.WriteUtil;

/*
 * 通过关系链随机选取randomUserSize个用户
 */
@SuppressWarnings("unused")
public class Copy2RandomGetUser {

	// 把运行结果数据存储在这个目录下
	String path;
	// 把随机选取的用户列表保存到randomUserList.txt
	String randomUserList;
	// 随机选取的用户量
	int randomUserSize;
	// 把随机选取的用户的微博选取保存到randomUserWeibo.txt
	String randomUserWeiboFile;
	// 配置数据库信息
	String socaiGraphDB;
	int socaiGraphDBPort;
	String microblogDB;
	int microblogDBPort;

	public Copy2RandomGetUser(int randomSize, String path,
			String randomUserList, String randomUserWeibo, String socaiGraphDB,
			int socaiGraphDBPort, String microblogDB, int microblogDBPort)
			throws IOException {
		this.randomUserSize = randomSize;
		this.path = path;
		this.randomUserList = randomUserList;
		this.randomUserWeiboFile = randomUserWeibo;
		this.socaiGraphDB = socaiGraphDB;
		this.socaiGraphDBPort = socaiGraphDBPort;
		this.microblogDB = microblogDB;
		this.microblogDBPort = microblogDBPort;
		System.out.println("enter RandomGetUser class");
		// 随机选取randomUserSize个用户

		this.getRandomUser(randomUserSize, randomUserList);

		// 抽取随机选取的用户的微博
		this.getUserWeibo(randomUserList, randomUserWeiboFile);
	}

	/*
	 * 抽取随机选取的用户的微博
	 * 
	 * @param k 选取的用户数
	 * 
	 * @param readPathName
	 */
	private void getUserWeibo(String userFileName, String saveFileName)
			throws IOException {
		System.out.println("begin getUserWeibo!");
		ReadUtil readUtil = new ReadUtil();

		ArrayList<String> userIdList = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		// 微博数据库
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

		// 读取选取的用户列表
		userIdList = readUtil.readfromFile(userFileName);
		System.out.println("read userList success");
		int userIdListSize = userIdList.size();

		WriteUtil writeUtil = new WriteUtil();

		// 从mongodb 数据库中获取微博
		for (int i = 0; i < userIdListSize; i++) {
			String kkString = userIdList.get(i);
			BasicDBObject selectDBObject = new BasicDBObject("user_id",
					Long.parseLong(kkString.trim()));
			DBCursor cur = microblogCollection.find(selectDBObject);
			while (cur.hasNext()) {

				BasicDBObject tempDbObject = (BasicDBObject) cur.next();

				if (tempDbObject.get("retweeted_status") != null) {
					BasicDBObject retweetObject = (BasicDBObject) tempDbObject
							.get("retweeted_status");
					String tempsString = tempDbObject.getString("user_id")
							+ "\t" + tempDbObject.getString("created_at")
							+ "\t" + tempDbObject.getString("text") + "\t"
							+ retweetObject.getString("text");
					result.add(tempsString);
					// System.out.println(tempsString);
				} else {
					String tempsString = tempDbObject.getString("user_id")
							+ "\t" + tempDbObject.getString("created_at")
							+ "\t" + tempDbObject.getString("text");
					result.add(tempsString);
				}

			}
			if (result.size() > 1000) {
				writeUtil.WriteIntoFileByEncodingFormat(saveFileName, result,
						"UTF-8");
				result = new ArrayList<String>();
			}
		}
		writeUtil.WriteIntoFileByEncodingFormat(saveFileName, result, "UTF-8");
		System.out.println(" getUserWeibo sucess!");

	}

	// 从mongodb数据库Weibo中选取表SocialGraph的数据
	private void getRandomUser(int randomNumber, String saveFileName) {
		System.out.println("begin getRandomeUser!");
		ArrayList<String> randUserList = new ArrayList<String>();
		ArrayList<String> userFollowAndNoFollow = new ArrayList<String>();
		// 调用数据库
		Mongo socaiGraph = null;
		// 微博数据库
		Mongo microblog = null;
		try {
			socaiGraph = new Mongo(this.socaiGraphDB, this.socaiGraphDBPort);
			microblog = new Mongo(this.microblogDB, this.microblogDBPort);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(" getRandomeUser failure!");
		}
		// 使用weibo数据库的SocialGraph表
		DB socaiGraphDB = socaiGraph.getDB("Weibo");
		DBCollection socialGraphCollection = socaiGraphDB
				.getCollection("SocialGraph");
		DB microblogDB = microblog.getDB("test");
		DBCollection microblogCollection = microblogDB
				.getCollection("microblogs");
		// 随机器
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		// 选取第一个
		BasicDBObject basicDBObject = (BasicDBObject) socialGraphCollection
				.findOne(1906207627);
		String ids = basicDBObject.get("ids").toString();
		// 删除不必要字符
		int index = ids.indexOf('[');
		ids = ids.substring(index + 1);
		index = ids.lastIndexOf(']');
		ids = ids.substring(0, index);
		String[] idsSplit = ids.split(",");
		for (int i = 0; i < idsSplit.length && randUserList.size() < 5; i++) {
			String kk = idsSplit[i];
			/*
			 * 筛选条件：1、随机数小于2 2、不包含在已有的数据集randUserList中 3、用户集randUserList小于2
			 * 4、该用户含有至少20条微博
			 */
			BasicDBObject currentBasic = new BasicDBObject("user_id",
					Long.parseLong(kk.trim()));

			int currentNumber = microblogCollection.find(currentBasic).count();
			if (currentNumber > 100 && !randUserList.contains(kk.trim())
					&& randUserList.size() < 5) {
				randUserList.add(kk.trim());
			}
		}
		new WriteUtil(saveFileName, randUserList);

		for (int j = 0; j < randomNumber; j++) {
			ArrayList<String> tempArrayList = new ArrayList<>();
			String randomString = randUserList.get(j);
			basicDBObject = new BasicDBObject("_id",
					Long.parseLong(randomString));
			basicDBObject = (BasicDBObject) socialGraphCollection
					.findOne(basicDBObject);
			// 如果查找不到该账号，或者该用户没有好友，则随机再选取一个
			if (basicDBObject == null || basicDBObject.get("ids") == null) {
				randomString = randUserList.get(++j);
				continue;
			}

			ids = basicDBObject.get("ids").toString();
			// 删除不必要字符
			index = ids.indexOf('[');
			ids = ids.substring(index + 1);
			index = ids.lastIndexOf(']');
			ids = ids.substring(0, index);
			idsSplit = ids.split(",");
			for (int i = 0; i < idsSplit.length && !idsSplit[i].isEmpty(); i++) {
				String kk = idsSplit[i].trim();
				if (!randUserList.contains(kk)) {
					/*
					 * 筛选条件：1、随机数小于2 2、不包含在已有的数据集randUserList中
					 * 3、用户集randUserList小于2 4、该用户含有至少10条微博
					 */

					BasicDBObject currentBasic = null;
					currentBasic = new BasicDBObject("_id", Long.parseLong(kk));
					//当小于三分之一时不考虑。当大于三分之一时考虑
					if (randUserList.size()<randomNumber/3) {
						int currentNumber = socialGraphCollection
								.find(currentBasic).count();
						if (currentNumber > 0) {
							currentBasic = new BasicDBObject("user_id",
									Long.parseLong(kk));
							currentNumber = microblogCollection.find(currentBasic)
									.count();
							if (currentNumber > 10) {
								randUserList.add(kk);
								tempArrayList.add(kk);
								// System.out.println("add one user"+kk.trim());

							}
						}
					}else {
						currentBasic=(BasicDBObject) socialGraphCollection.findOne(currentBasic);
						if (currentBasic!=null) {
							ids = currentBasic.get("ids").toString();
							currentBasic = new BasicDBObject("user_id",
									Long.parseLong(kk));
							int currentNumber = microblogCollection.find(currentBasic)
									.count();
							if (commonFollow(randUserList, ids)>2&&currentNumber>10) {
								
									randUserList.add(kk);
									tempArrayList.add(kk);
									// System.out.println("add one user"+kk.trim());
							}
						}
						}
				}

			}

			new WriteUtil(saveFileName, tempArrayList);
		}

		System.out.println(randUserList.size());
		// new WriteUtil(saveFileName, randUserList);
		System.out.println(" getRandomeUser sucess!");
	}

	private static int commonFollow(ArrayList<String> user1Follow, String user2Follow) {
		int count = 0;

		// 删除不必要字符
		int index = user2Follow.indexOf('[');
		user2Follow = user2Follow.substring(index + 1);
		index = user2Follow.lastIndexOf(']');
		user2Follow = user2Follow.substring(0, index);
		String[] idsSplit = user2Follow.split(",");
			for (int j = 0, length2 = idsSplit.length; j < length2; j++) {
				if (user1Follow.contains(idsSplit[j].trim())) {
					count++;
				}
		}
		return count;
	}
}
