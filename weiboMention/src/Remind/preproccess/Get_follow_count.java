/**



 * 



 */

package Remind.preproccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import bean.User;
import Resource.mongoDB_Source;
import Resource.User_profile_txt_Source;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import tool.dataStucture.AvlTree.AvlNode;
import tool.mongo.Mongo_Source;
import weibo.util.ReadUser;

public class Get_follow_count {

	public static void main(String[] arg) {

		Get_follow_count kkCount = new Get_follow_count();

		kkCount.run();

	}

	public void run() {
		AvlTree<UserId_value> userandFolloweeCount = new AvlTree<>();
		//addFollow(userandFolloweeCount);
		addFollowNumberToMap(userandFolloweeCount);
		secondStep(userandFolloweeCount);
		writeSecondResult(userandFolloweeCount);
	}

	class UserId_value implements Comparable<UserId_value>{
		long userid;
		long[] follow = new long[2];
		
		public UserId_value() {
		}
		public UserId_value(long userid) {
			this.userid=userid;
		}
		public UserId_value(int int1, int int2) {
			follow[0] = int1;
			follow[1] = int2;
		}
		public UserId_value(long userid,int int1, int int2) {
			this.userid=userid;
			follow[0] = int1;
			follow[1] = int2;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(UserId_value o) {
			// TODO Auto-generated method stub
			if (o.userid > this.userid) {
				return 1;
			} else if (o.userid < this.userid) {
				return -1;
			}
			return 0;
		}

	}

	// 添加粉丝

	public void addFollow(AvlTree<UserId_value> userandFolloweeCount) {

		

		DBCursor cursor = new mongoDB_Source().getSocialGraph().find();
		
		long count = 0;
		
		while (cursor.hasNext()) {

			BasicDBObject object = (BasicDBObject) cursor.next();

			long followee = object.getLong("_id");

			List<Object> followeeList = (List<Object>) object.get("ids");

			for (int i = 0; i < followeeList.size(); i++) {

				long long1 = 0;

				if (followeeList.get(i) instanceof Integer) {
					long1 = Long.valueOf(String.valueOf(followeeList.get(i)));
				} else {
					long1 = (long) followeeList.get(i);
				}
				UserId_value temp=new UserId_value(long1);
				if (userandFolloweeCount.contains(temp)) {
					userandFolloweeCount.getElement(temp).follow[0]+=1;
				} else {
					 temp=new UserId_value(long1, 1, 0);
					userandFolloweeCount.insert(temp);

				}
			}
			if (count++ % 1000000 == 0) {

				System.out.println(count);
			}
		}
		writeFollowNumberToFile(userandFolloweeCount);
	}
	public void addFollowNumberToMap(AvlTree<UserId_value> userandFolloweeCount){
		final File file1 = new File("userid_follow_Count.txt");

		try (BufferedReader bufferedReader= new BufferedReader(new FileReader(file1));) {
			String oneLine=bufferedReader.readLine();
			while((oneLine=bufferedReader.readLine())!=null){
				String[] split=oneLine.split("\t");
				UserId_value temp=new UserId_value(Long.valueOf(split[0]), Integer.valueOf(split[1]), 0);
				userandFolloweeCount.insert(temp);
			}
		} catch (IOException e) {

		}
	}
	/**
	 * @param userandFolloweeCount
	 *@create_time：2015年1月13日上午8:45:54
	 *@modifie_time：2015年1月13日 上午8:45:54
	  
	 */
	public void writeFollowNumberToFile(AvlTree<UserId_value> userandFolloweeCount){
		final File file1 = new File("userid_follow_Count.txt");

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				file1, true));) {
			Stack<AvlNode<UserId_value>> stack = new Stack<>();
			AvlNode<UserId_value> puser = userandFolloweeCount.root;
			while (puser != null || !stack.isEmpty()) {
				while (puser != null) {
					UserId_value tempUser=puser.getElement();
					bufferedWriter.write(tempUser.userid+ "\t" + tempUser.follow[0]);
					bufferedWriter.newLine();
					stack.push(puser);
					puser = puser.left;
				}
				if (!stack.isEmpty()) {
					puser = stack.pop();
					puser = puser.right;
				}
			}

		} catch (IOException e) {

			// TODO Auto-generated catch block

		}
	}
	/**
	 * @param userandFolloweeCount
	 *@create_time：2015年1月13日上午8:45:57
	 *@modifie_time：2015年1月13日 上午8:45:57
	  
	 */
	public void secondStep(AvlTree<UserId_value> userandFolloweeCount){
		System.out.println("second");
		DBCursor cursor = new mongoDB_Source().getSocialGraph().find();
		cursor = new mongoDB_Source().getSocialGraph().find();
		long count = 0;
		
		while (cursor.hasNext()) {
			BasicDBObject object = (BasicDBObject) cursor.next();
			long followee = object.getLong("_id");
			List<Object> followeeList = (List<Object>) object.get("ids");
			UserId_value followee_user=new UserId_value(followee);
			followee_user=userandFolloweeCount.getElement(followee_user);
			if (followee_user==null) {
				continue;
			}
			for (int i = 0; i < followeeList.size(); i++) {
				long long1 = 0;
				if (followeeList.get(i) instanceof Integer) {
					long1 = Long.valueOf(String.valueOf(followeeList.get(i)));
				} else {
					long1 = (long) followeeList.get(i);
				}
				UserId_value temp=new UserId_value(long1);
				if (!userandFolloweeCount.contains(temp)) continue;
				userandFolloweeCount.getElement(temp).follow[1] += followee_user.follow[0];
			}
			if (count++ % 1000000 == 0) {
				System.out.println(count);
			}

		}

	}
	/**
	 * @param userandFolloweeCount
	 *@create_time：2015年1月13日上午8:47:04
	 *@modifie_time：2015年1月13日 上午8:47:04
	  
	 */
	public void writeSecondResult(AvlTree<UserId_value> userandFolloweeCount){
		final File file2 = new File("userid_follow_followCount.txt");

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				file2, true));) {
			Stack<AvlNode<UserId_value>> stack = new Stack<>();
			AvlNode<UserId_value> puser = userandFolloweeCount.root;
			while (puser != null || !stack.isEmpty()) {
				while (puser != null) {
					UserId_value tempUser=puser.getElement();

					bufferedWriter.write(tempUser.userid+ "\t" + tempUser.follow[0]+"\t"+tempUser.follow[1]);
					bufferedWriter.newLine();
					stack.push(puser);
					puser = puser.left;
				}
				if (!stack.isEmpty()) {
					puser = stack.pop();
					puser = puser.right;
				}
			}

		} catch (IOException e) {

			// TODO Auto-generated catch block

		}
	}

}
