/**
 * 
 */
package Remind.statics;

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
import java.util.Stack;

import util.FileUtil;
import util.Filter;
import util.ReadUser;
import util.ReadWeiboUtil;
import util.WriteUtil;
import Remind.util.BaseUitl;
import bean.AvlTree;
import bean.AvlTree.AvlNode;
import bean.OnePairTweet;
import bean.RemType;
import bean.Reminder;
import bean.Retweetlist;
import bean.User;

/**
 * 
 * @progject_name锛歸eiboNew
 * @class_name锛歴taticreminderweibo
 * @class_describe锛�
 * @creator锛歾houge
 * @create_time锛�014骞�0鏈�5鏃�涓嬪崍10:43:40
 * @modifier锛歾houge
 * @modified_time锛�014骞�0鏈�5鏃�涓嬪崍10:43:40
 * @modified_note锛�
 * @version
 * 
 */
public class staticreminderweibo {

	public static void main(String[] args) {
		// String path = "/media/pc/new2/data/new/";
		// String path = "J:/workspacedata/weiboNew/data/reminder/";
		String path = "/home/zhouge/database/weibo/new/";
		String sourceDir = path + "retweet/";

		// String sourceFile =
		// "/home/zhouge/database/weibo/2012/content/retweetType19.txt";
		String profileFile = path + "userid/profile.txt";
		String followGraph = path + "userid/FollowGraph.txt";
		String reminderFile = path + "rem.txt";
		staticreminderweibo re = new staticreminderweibo();
		HashMap<String, Long> usernameAndIdMap = null;
		HashMap<Long, String> useridAndNameMap = null;

		 usernameAndIdMap = ReadUser
		 .getuserNameAndIdMapFromprofileFile(profileFile);
		AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
		HashMap<Long, User> usermap = ReadUser
				.getuserInMapFromprofileFile(profileFile);
		usermap=BaseUitl.addFollowee(usermap,followGraph);
		 useridAndNameMap = ReadUser
		 .getuserIdAndNameMapFromprofileFile(profileFile);
		 re.writeReminderWeiboInDirectory(usernameAndIdMap, useridAndNameMap,
		 sourceDir, reminderFile);
		// re.statisticsDifferenceTypePeople( useravl,
		// reminderFile, path);
		//re.statisticsDifferenceTypePeople(usermap, reminderFile, path);
		//re.statisticsDifferenceTypePeople(useravl, reminderFile, path);
		//re.statisticInProfile(useravl);
		// re.statisticInProfile(useravl);
	}

	
	public void statisticInProfile(AvlTree<User> useravl) {

		// 鍏堝簭闈為�褰掗亶鍘嗕簩鍙夋爲
		Stack<AvlNode<User>> stack = new Stack<>();
		AvlNode<User> puser = useravl.root;
		List<Integer> followeeNumber = new ArrayList<>();
		int[] Number = new int[4];
		
		while (puser != null || !stack.isEmpty()) {
			while (puser != null) {
				followeeNumber.add(puser.getElement().getFollowers_count());
				
				if(puser.getElement().getGender().equals("m")){
					Number[0]++;
					if (puser.getElement().getVerified() == 1)
						Number[1]++;
				}
				else {
					Number[2]++;
					if (puser.getElement().getVerified() == 1)
						Number[3]++;
					}
				stack.push(puser);
				puser = puser.left;
			}
			if (!stack.isEmpty()) {
				puser = stack.pop();
				puser = puser.right;
			}
		}
		System.out.println(Number[0]+"\t"+Number[1]+"\t"+Number[2]+"\t"+Number[3]+"\t");
		
		// Collections.sort(followeeNumber);
		// int top1000=followeeNumber.get(followeeNumber.size()-1000);
		// System.out.println(top1000);
	}

	/**
	 * @param useravl
	 * @param reminderFile
	 * @param path
	 * @create_time锛�014骞�0鏈�7鏃ヤ笅鍗�:39:41
	 * @modifie_time锛�014骞�0鏈�7鏃�涓嬪崍7:39:41
	 */
	public void statisticsDifferenceTypePeople(AvlTree<User> useravl,
			String reminderFile, String path) {
		//final int top1000followee = 326341;
		long manattackman = 0;
		long womanattackwoman = 0;
		long manattackWoman=0;
		long womanattackman=0;
		int[] attackNumber=new int[4];
		try (BufferedReader reader = new BufferedReader(new FileReader(
				reminderFile))) {
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				List<Reminder> listrem = remType.getReminders();
				long rootuserid=remType.getRoot().getUserId();
				User rootuser=new User();
				rootuser.setUserId(rootuserid);
				rootuser=useravl.getElement(rootuser, useravl.root);
				
				for (Reminder reminder : listrem) {
					User targetuser = new User();
					targetuser.setUserId(reminder.getUserId());
					targetuser = useravl.getElement(targetuser, useravl.root);
				
					if(rootuser.getGender().equals("f")&&targetuser.getGender().equals("f")){
						womanattackwoman++;
						if(reminder.isIfRely())
							attackNumber[0]++;
					}
					else if(rootuser.getGender().equals("m")&&targetuser.getGender().equals("m")){
						manattackman++;
						if(reminder.isIfRely())
							attackNumber[1]++;
					}
					else if(rootuser.getGender().equals("m")&&targetuser.getGender().equals("f")){
						
						manattackWoman++;
						if(reminder.isIfRely())
							attackNumber[2]++;
					}else {
						womanattackman++;
						if(reminder.isIfRely())
							attackNumber[3]++;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(womanattackwoman+"\t"+attackNumber[0]);
		System.out.println(manattackman+"\t"+attackNumber[1]);
		System.out.println(manattackWoman+"\t"+attackNumber[2]);
		System.out.println(womanattackman+"\t"+attackNumber[3]);
	}

	public void statisticsDifferenceTypePeople(HashMap<Long, User> usermap,
			String reminderFile, String path) {
		// final int top1000followee=326341;
		long totalreply = 0;
		long celebrityattack = 0;

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
		String followeeAndAttackNumber = path
				+ "result/followeeAndAttackNumber.txt";
		String statusAndAttackNumber = path
				+ "result/statusAndAttackNumber.txt";
		String replyAndAttackNumber = path + "result/replyAndAttackNumber.txt";
		String statusAndBeAttackNumber = path + "statusAndBeAttackNumber.txt";
		String followeeAndBeRelyNumber = path
				+ "result/followeeAndBeRelyNumber.txt";
		String followeeAndBeattackNumber = path
				+ "result/followeeAndBeattackNumber.txt";

		List<String> followeeAndAttackNumberList = new ArrayList<>();
		List<String> statusAndAttackNumberList = new ArrayList<>();
		List<String> statusAndBeAttackNumberList = new ArrayList<>();
		List<String> replyAndAttackNumberList = new ArrayList<>();
		List<String> followeeAndBeRelyNumberList = new ArrayList<>();
		List<String> followeeAndBeAttackNumberList = new ArrayList<>();

		while (iterable.hasNext()) {
			long userid = iterable.next();
			User user = usermap.get(userid);
			if (user.getAttackNumber() != 0) {
				followeeAndAttackNumberList.add(user.getUserId() + "\t"
						+ user.getFollowers_count() + "\t"
						+ user.getAttackNumber());
				statusAndAttackNumberList.add(user.getUserId() + "\t"
						+ user.getStatuses_count() + "\t"
						+ user.getAttackNumber());
			}
			if (user.getAttackNumber() != 0) {
				replyAndAttackNumberList.add(user.getUserId() + "\t"
						+ user.getRelyNumber() + "\t" + user.getAttackNumber());
			}
			if (user.getBeRelyNumber() != 0) {
				followeeAndBeRelyNumberList.add(user.getUserId() + "\t"
						+ user.getFollowers_count() + "\t"
						+ user.getBeRelyNumber());
			}
			if (user.getBeAttackedNumber() != 0) {
				followeeAndBeAttackNumberList.add(user.getUserId() + "\t"
						+ user.getFollowers_count() + "\t"
						+ user.getBeAttackedNumber());
			}
			if (user.getBeAttackedNumber() != 0) {
				statusAndBeAttackNumberList.add(user.getUserId() + "\t"
						+ user.getStatuses_count() + "\t"
						+ user.getBeAttackedNumber());
			}

		}
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeList(statusAndBeAttackNumberList,
				statusAndBeAttackNumber);
		// writeUtil.writeList(followeeAndAttackNumberList,
		// followeeAndAttackNumber);
		// writeUtil.writeList(statusAndAttackNumberList,
		// statusAndAttackNumber);
		// writeUtil.writeList(replyAndAttackNumberList, replyAndAttackNumber);
		// writeUtil.writeList(followeeAndBeRelyNumberList,
		// followeeAndBeRelyNumber);
		// writeUtil.writeList(followeeAndBeAttackNumberList,
		// followeeAndBeattackNumber);
		System.out.println(totalreply);
		System.out.println(celebrityattack);
	}

	

	/**
	 * @param usernameAndIdMap
	 * @param useridAndNameMap
	 * @param sourceFile
	 * @param reusltFile
	 * @create_time锛�014骞�0鏈�6鏃ヤ笂鍗�0:59:41
	 * @modifie_time锛�014骞�0鏈�6鏃�涓婂崍10:59:41
	 */
	public void getReminderWeiboInOneFile(
			HashMap<String, String> usernameAndIdMap,
			HashMap<String, String> useridAndNameMap, String sourceFile,
			String reusltFile) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(
				new File(reusltFile)));) {
			ReadWeiboUtil readWeiboUtil = new ReadWeiboUtil(sourceFile);
			Retweetlist retweetlist = null;
			while ((retweetlist = readWeiboUtil.readRetweetList()) != null) {
				OnePairTweet rooTweet = retweetlist.getRoot();
				if (useridAndNameMap.containsKey(rooTweet.getUserId())) {
					List<OnePairTweet> list = retweetlist
							.getRetweetListSortByTime();
					System.out.println(useridAndNameMap.get(rooTweet
							.getUserId()));
					System.out.println(rooTweet.toString());

					for (OnePairTweet onePairTweet : list) {
						System.out.println(useridAndNameMap.get(onePairTweet
								.getUserId()));
						System.out.println(onePairTweet.toString());
					}
					System.out.println();
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param usernameAndIdMap
	 * @param useridAndNameMap
	 * @param reminderDir
	 * @param reusltFile
	 * @create_time锛�014骞�0鏈�6鏃ヤ笂鍗�0:59:38
	 * @modifie_time锛�014骞�0鏈�6鏃�涓婂崍10:59:38
	 */
	public void writeReminderWeiboInDirectory(
			HashMap<String, Long> usernameAndIdMap,
			HashMap<Long, String> useridAndNameMap, String reminderDir,
			String reusltFile) {

		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(reminderDir,
				dateFormat);
		int RemNumber = 0;
		int replyNumber = 0;
		for (String fileName : fileList) {
			System.out.println(fileName);
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(reusltFile), true))) {
				ReadWeiboUtil readWeiboUtil = new ReadWeiboUtil(fileName);
				Retweetlist retweetlist = null;
				while ((retweetlist = readWeiboUtil.readRetweetList()) != null) {
					OnePairTweet rootTweet = retweetlist.getRoot();
					if (rootTweet == null) {
						continue;
					}
					// if content contain this char then find :
					if (rootTweet.getContent().contains("@")) {
						ArrayList<String> reminderName = Filter
								.getAtName(rootTweet.getContent());
						if (reminderName == null) {
							continue;
						}
						reminderName = getListThatInUserList(reminderName,
								usernameAndIdMap);
						if (reminderName != null) {
							StringBuffer buffer = new StringBuffer();
							RemNumber += reminderName.size();

							List<OnePairTweet> list = retweetlist
									.getRetweetListSortByTime();

							Set<String> retweetUserName = new HashSet<>();
							for (OnePairTweet onePairTweet : list) {
								retweetUserName.add(useridAndNameMap
										.get(onePairTweet.getUserId()));
							}
							for (String one : reminderName) {
								if (retweetUserName.contains(one))
									replyNumber++;
							}

							buffer.append(rootTweet.getWeiboId() + "\t"
									+ rootTweet.getUserId() + "\t"
									+ rootTweet.getCreateTime() + "\t"
									+ list.size() + "\t");
							for (int i = 0; i < reminderName.size(); i++) {
								if (retweetUserName.contains(reminderName
										.get(i))) {
									for (OnePairTweet onePairTweet : list) {
										if (useridAndNameMap.get(
												onePairTweet.getUserId())
												.equals(reminderName.get(i))) {
											buffer.append(usernameAndIdMap
													.get(reminderName.get(i))
													+ ":"
													+ onePairTweet
															.getCreateTime()
													+ "\t");
											break;
										}
									}

								} else {
									buffer.append(usernameAndIdMap
											.get(reminderName.get(i))
											+ ":"
											+ 0
											+ "\t");
								}
							}
							writer.write(buffer.toString().trim());
							writer.newLine();
						}

					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(RemNumber);
		System.out.println(replyNumber);
	}

	/**
	 * @param reminderName
	 * @param usernameAndIdMap
	 * @return
	 * @create_time锛�014骞�0鏈�7鏃ヤ笂鍗�:50:28
	 * @modifie_time锛�014骞�0鏈�7鏃�涓婂崍9:50:28
	 */
	public ArrayList<String> getListThatInUserList(
			ArrayList<String> reminderName,
			HashMap<String, Long> usernameAndIdMap) {
		ArrayList<String> result = null;
		for (String name : reminderName) {
			if (usernameAndIdMap.containsKey(name)) {
				if (result == null) {
					result = new ArrayList<>();
				}
				result.add(name);
			}

		}
		return result;
	}
}
