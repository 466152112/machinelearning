package Remind;

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
import java.util.Set;

import util.FileUtil;
import util.Filter;
import util.ReadWeiboUtil;
import util.WriteUtil;
import bean.AvlTree;
import bean.OnePairTweet;
import bean.RemType;
import bean.Reminder;
import bean.Retweetlist;
import bean.User;

public class BaseUitl {
	
	public static HashMap<Long, User> addFollowee(HashMap<Long, User> usermap,
			String followGraphFile) {
		try (BufferedReader reader = new BufferedReader(new FileReader(
				followGraphFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split(",");
				long user1 = Long.valueOf(split[0]);
				long user2 = Long.valueOf(split[1]);
				usermap.get(user1).getFolloweeList().add(user2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usermap;
	}
	
	public static AvlTree<User>  addFollowee(AvlTree<User> useravl,
			String followGraphFile) {
		System.out.println("add Followee to useravl");
		try (BufferedReader reader = new BufferedReader(new FileReader(
				followGraphFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split(",");
				long user1 = Long.valueOf(split[0]);
				long user2 = Long.valueOf(split[1]);
				User user=new User();
				user.setUserId(user1);
				useravl.getElement(user, useravl.root).getFolloweeList().add(user2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finsih Followee to useravl");
		return useravl;
	}
	

	public static AvlTree<User>  addTagToUser(AvlTree<User> useravl,
			String tagFile) {
		System.out.println("add profile to useravl");
		try (BufferedReader reader = new BufferedReader(new FileReader(
				tagFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split("\t");
				long userid = Long.valueOf(split[0]);
				User user=new User();
				user.setUserId(userid);
				//10457	Hellena	0		���� ������	7381	787	5690	f
				useravl.getElement(user).setTag(split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finsih add tag to useravl");
		return useravl;
	}
	public static AvlTree<User>  addProfileNameToReminHistory(AvlTree<User> useravl,
			String profile) {
		System.out.println("add profile to useravl");
		try (BufferedReader reader = new BufferedReader(new FileReader(
				profile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split("\t");
				long userid = Long.valueOf(split[0]);
				User user=new User();
				user.setUserId(userid);
				//10457	Hellena	0		���� ������	7381	787	5690	f
				useravl.getElement(user).setUserName(split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finsih profile to useravl");
		return useravl;
	}
	public static AvlTree<User> readUserRemHistory(String fileName){
		//Id	AttackNumber	BeAttackNumber	RelyNumber	BeRelyNumber
		//Verified	Gender	Friends_count	Followers_count  Statuses_count
		AvlTree<User> result = new AvlTree<>();
		String onelineString =null;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				fileName));) {
			//the first oneline is type note
			onelineString = bufferedReader.readLine();
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				
				User user = new User();
				user.setUserId(Long.valueOf(split[0]));
				user.setAttackNumber(Integer.valueOf(split[1]));
				user.setBeAttackedNumber(Integer.valueOf(split[2]));
				user.setRelyNumber(Integer.valueOf(split[3]));
				user.setBeRelyNumber(Integer.valueOf(split[4]));
				user.setVerified(Integer.valueOf(split[5]));
				user.setGender(split[6]);
				user.setFriends_count(Integer.valueOf(split[7]));
				user.setFollowers_count(Integer.valueOf(split[8]));
				user.setStatuses_count(Integer.valueOf(split[9]));
				result.insert(user);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(onelineString);
		}
		return result;
	}
	
	public void statisticsInuserWriteToFile(HashMap<Long, User> usermap,
			String reminderFile, String FileName) {
		// final int top1000followee=326341;

		try (BufferedReader reader = new BufferedReader(new FileReader(
				reminderFile))) {
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				List<Reminder> listrem = remType.getReminders();
				User user = new User();
				user = usermap.get(remType.getRoot().getUserId());
				user.setAttackNumber(user.getAttackNumber() + listrem.size());
				for (Reminder reminder : listrem) {
					User remuserUser = usermap.get(reminder.getUserId());
					remuserUser.setBeAttackedNumber(remuserUser
							.getBeAttackedNumber() + 1);
					if (reminder.isIfRely()) {
						remuserUser
								.setRelyNumber(remuserUser.getRelyNumber() + 1);
						user.setBeRelyNumber(user.getBeRelyNumber() + 1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Iterator<Long> iterable = usermap.keySet().iterator();
		List<String> resultList = new ArrayList<>();
		String HeadLine="Id\t"+"AttackNumber\t"+"BeAttackNumber\t"+"RelyNumber\t"+"BeRelyNumber\t"+"Verified\t"+"Gender\t"+"Friends_count\t"+"Followers_count\t"+"Statuses_count";
		resultList.add(HeadLine);
		while (iterable.hasNext()) {
			User user = usermap.get(iterable.next());
			StringBuffer temp = new StringBuffer();
			temp.append(user.getUserId() + "\t" + user.getAttackNumber()
					+ "\t" + user.getBeAttackedNumber() + "\t"
					+ user.getRelyNumber() + "\t" + user.getBeRelyNumber()+
					"\t"+user.getVerified()+ "\t"+user.getGender()
					+ "\t" + user.getFriends_count() + "\t"
					+ user.getFollowers_count()+ "\t"+user.getStatuses_count());
			resultList.add(temp.toString());
		}
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeList(resultList, FileName);
	}
	
	
}
