package Remind.util;

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

import tool.FileTool.FileUtil;
import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseFilter;
import Remind.statics.staticInTime;
import Resource.Intection_txt_Source;
import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import bean.Remind.RemType;
import bean.Remind.Reminder;

public class BaseUitl {

	/**
	 * @param usermap
	 *            targetobject
	 * @param followGraphFile
	 *            in type: 12,23
	 * @return targetobject 添加好友列表后的形式
	 * @create_time：2014年12月15日下午4:51:56
	 * @modifie_time：2014年12月15日 下午4:51:56
	 */
	public static HashMap<Long, User> addFollowee(HashMap<Long, User> usermap,
			String followGraphFile) {
		try (BufferedReader reader = new BufferedReader(new FileReader(
				followGraphFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split(",");
				long user1 = Long.valueOf(split[0]);
				long user2 = Long.valueOf(split[1]);
				usermap.get(user1).getfollowee().add(user2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usermap;
	}

	/**
	 * @param useravl
	 *            targetobjectavltree
	 * @param followGraphFile
	 *            in type: 12,23
	 * @return
	 * @create_time：2014年12月15日下午4:54:25
	 * @modifie_time：2014年12月15日 下午4:54:25
	 */
	public static AvlTree<User> addFollowee(AvlTree<User> useravl,
			String followGraphFile) {
		System.out.println("add Followee to useravl");
		try (BufferedReader reader = new BufferedReader(new FileReader(
				followGraphFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split(",");
				long user1 = Long.valueOf(split[0]);
				long user2 = Long.valueOf(split[1]);
				User user = new User();
				user.setUserId(user1);
				useravl.getElement(user, useravl.root).getfollowee()
						.add(user2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finsih Followee to useravl");
		return useravl;
	}

	/**
	 * @param useravl
	 * @param tagFile
	 * @return add profile to useravl
	 * @create_time：2014年12月15日下午4:54:55
	 * @modifie_time：2014年12月15日 下午4:54:55
	 */
	public static AvlTree<User> addTagToUser(AvlTree<User> useravl,
			String tagFile) {
		System.out.println("add profile to useravl");
		try (BufferedReader reader = new BufferedReader(new FileReader(tagFile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split("\t");
				long userid = Long.valueOf(split[0]);
				User user = new User();
				user.setUserId(userid);
				// 10457 Hellena 0 ���� ������ 7381 787 5690 f
				useravl.getElement(user).setTag(split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finsih add tag to useravl");
		return useravl;
	}

	public static AvlTree<User> addProfileName(AvlTree<User> useravl,
			String profile) {
		System.out.println("add profile to useravl");
		try (BufferedReader reader = new BufferedReader(new FileReader(profile))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				String[] split = oneLine.split("\t");
				long userid = Long.valueOf(split[0]);
				User user = new User();
				user.setUserId(userid);
				// 10457 Hellena 0 ���� ������ 7381 787 5690 f
				useravl.getElement(user).setUserName(split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finsih profile to useravl");
		return useravl;
	}

	public static AvlTree<User> readUserRemHistory(String fileName) {
		// Id AttackNumber BeAttackNumber RelyNumber BeRelyNumber
		// Verified Gender Friends_count Followers_count Statuses_count
		AvlTree<User> result = new AvlTree<>();
		String onelineString = null;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				fileName));) {
			// the first oneline is type note
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

	public static Set<Long> getAudience(Set<Long> publishers,
			String rootweiboremNetwork, Set<Long> deleteidset) {
		// final int top1000followee=326341;
		int promotionNumber = 0, adoptionNumber = 0;

		Set<Long> avluser = new HashSet();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				rootweiboremNetwork))) {
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				List<Reminder> listrem = remType.getReminders();

				if (!publishers.contains(remType.getRoot().getUserId())) {
					continue;
				}
				promotionNumber++;
				for (Reminder reminder : listrem) {
					avluser.add(reminder.getUserId());
					adoptionNumber++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		avluser.removeAll(deleteidset);
		System.out.println("promotionNumber:" + promotionNumber);
		System.out.println("adoptionNumber:" + adoptionNumber);

		return avluser;
	}

	/**
	 * @param rootweiboremNetwork
	 * @param lowLimit
	 * @param upLimit
	 * @param deleteidset
	 * @return
	 * @create_time：2014年12月24日上午10:19:42
	 * @modifie_time：2014年12月24日 上午10:19:42
	 */
	public static Set<Long> getLimitPublisher(String rootweiboremNetwork,
			int lowLimit, int upLimit, Set<Long> deleteidset) {

		if (upLimit <= lowLimit) {
			System.out
					.println("the up and low limit is fail in getLimitPublisher");
			System.exit(0);
		}
		HashMap<Long, Integer> publishers = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				rootweiboremNetwork))) {
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				List<Reminder> listrem = remType.getReminders();
				int count = 0;
				for (Reminder reminder : listrem) {
					if (!deleteidset.contains(reminder.getUserId())
							&& reminder.getUserId() != remType.getRoot()
									.getUserId()) {
						count++;
					}
				}
				if (publishers.keySet().contains(remType.getRoot().getUserId())) {
					publishers.put(remType.getRoot().getUserId(),
							publishers.get(remType.getRoot().getUserId())
									+ count);
				} else {
					publishers.put(remType.getRoot().getUserId(), count);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get limit userid
		Set<Long> avluserId = new HashSet<>();
		for (Long userid : publishers.keySet()) {
			int count = publishers.get(userid);
			if (count > upLimit || count < lowLimit) {
				continue;
			}
			avluserId.add(userid);
		}
		return avluserId;
	}

	public static AvlTree<RemType> get_need_mention_history_From_all(
			String weiboremNetwork, AvlTree<User> userAvlTree) {
		AvlTree<RemType> result = new AvlTree<>();
		// use only once for get need _mention_history_from_all
		// List<String> need_mention_history=new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(
				weiboremNetwork))) {
			String oneLine;
			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				List<Reminder> listrem = remType.getReminders();
				long sourceid = remType.getRoot().getUserId();
				User tempuser = new User();
				tempuser.setUserId(sourceid);
				if (!userAvlTree.contains(tempuser)) {
					continue;
				}
				boolean flag = false;
				for (Reminder reminder : listrem) {
					tempuser = new User();
					tempuser.setUserId(reminder.getUserId());
					if (userAvlTree.contains(tempuser)) {
						flag = true;
						break;
					}

				}
				if (flag) {
					List<Reminder> deleterrem = new ArrayList<>();
					for (int i = 0; i < listrem.size(); i++) {
						long userid = listrem.get(i).getUserId();
						tempuser = new User();
						tempuser.setUserId(userid);
						if (!userAvlTree.contains(tempuser)) {
							deleterrem.add(listrem.get(i));
						}
					}
					remType.getReminders().removeAll(deleterrem);
					result.insert(remType);
					// need_mention_history.add(oneLine);
					// if (need_mention_history.size()>100000) {
					// //use only once for get need _mention_history_from_all
					// WriteUtil<String> writeUtil=new WriteUtil<>();
					// writeUtil.writeList(need_mention_history,
					// Mention_txt_Source.getNeedMentionHistoryFile());
					// need_mention_history=new ArrayList<>();
					// }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// use only once for get need _mention_history_from_all
		// WriteUtil<String> writeUtil=new WriteUtil<>();
		// writeUtil.writeList(need_mention_history,
		// Mention_txt_Source.getNeedMentionHistoryFile());
		return result;
	}

	/**
	 * @param rootweiboremNetwork
	 * @param lowLimit
	 * @param upLimit
	 * @return
	 * @create_time：2014年12月24日上午10:19:38
	 * @modifie_time：2014年12月24日 上午10:19:38
	 */
	public static Set<Long> getLimitPublisher(String rootweiboremNetwork,
			int lowLimit, int upLimit) {
		Set<Long> deleteUser = new HashSet<>();
		return getLimitPublisher(rootweiboremNetwork, lowLimit, upLimit,
				deleteUser);

	}

	/**
	 * @param rootweiboremNetwork
	 * @param publishers
	 * @return
	 * @create_time：2014年12月15日下午5:37:40
	 * @modifie_time：2014年12月15日 下午5:37:40
	 */
	public static Set<Long> getMentionWeiboId(String rootweiboremNetwork,
			Set<Long> publishers) {

		// get limit userid
		Set<Long> weiboid = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				rootweiboremNetwork))) {
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				if (publishers.contains(remType.getRoot().getUserId())) {
					weiboid.add(remType.getRoot().getWeiboId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return weiboid;
	}

	public void statistics_user_WriteToFile(HashMap<Long, User> usermap,
			String reminderFile, String FileName) {
		// final int top1000followee=326341;
		try (BufferedReader reader = new BufferedReader(new FileReader(
				reminderFile))) {
			String oneLine;
			// 读取@网络
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
		String HeadLine = "Id\t" + "AttackNumber\t" + "BeAttackNumber\t"
				+ "RelyNumber\t" + "BeRelyNumber\t" + "Verified\t" + "Gender\t"
				+ "Friends_count\t" + "Followers_count\t" + "Statuses_count";
		resultList.add(HeadLine);
		// 把基本的统计结果写入文件
		while (iterable.hasNext()) {
			User user = usermap.get(iterable.next());
			StringBuffer temp = new StringBuffer();
			temp.append(user.getUserId() + "\t" + user.getAttackNumber() + "\t"
					+ user.getBeAttackedNumber() + "\t" + user.getRelyNumber()
					+ "\t" + user.getBeRelyNumber() + "\t" + user.getVerified()
					+ "\t" + user.getGender() + "\t" + user.getFriends_count()
					+ "\t" + user.getFollowers_count() + "\t"
					+ user.getStatuses_count());
			resultList.add(temp.toString());
		}
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeList(resultList, FileName);
	}

	public Set<String> get_Top_TargetUser_FromAllMention(
			Map<Long, User> usermap, String reminderFile,
			int BeMentionlimit, int relyLimit) {
		// final int top1000followee=326341;
		try (BufferedReader reader = new BufferedReader(new FileReader(
				reminderFile))) {
			String oneLine;
			// 读取@网络
			while ((oneLine = reader.readLine()) != null) {

				// type weiboid\t userid\t createTime \t retweetId \t targetId
				// \t weibocontentvector
				// if no retweet ,the retweetId is 0
				String[] splits=oneLine.split("\t");
				
				User user = new User();
				user = usermap.get(Long.valueOf(splits[1]));
				user.setAttackNumber(user.getAttackNumber() + 1);
				User remuserUser = usermap.get(Long.valueOf(splits[4]));
				remuserUser.setBeAttackedNumber(remuserUser
						.getBeAttackedNumber() + 1);
				if (!splits[3].equals("0")) {
					remuserUser.setRelyNumber(remuserUser.getRelyNumber() + 1);
					user.setBeRelyNumber(user.getBeRelyNumber() + 1);
				}

				// RemType remType = RemType.covert(oneLine);
				// List<Reminder> listrem = remType.getReminders();
				// User user = new User();
				// user = usermap.get(remType.getRoot().getUserId());
				// user.setAttackNumber(user.getAttackNumber() +
				// listrem.size());
				// for (Reminder reminder : listrem) {
				// User remuserUser = usermap.get(reminder.getUserId());
				// remuserUser.setBeAttackedNumber(remuserUser
				// .getBeAttackedNumber() + 1);
				// if (reminder.isIfRely()) {
				// remuserUser
				// .setRelyNumber(remuserUser.getRelyNumber() + 1);
				// user.setBeRelyNumber(user.getBeRelyNumber() + 1);
				// }
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Iterator<Long> iterable = usermap.keySet().iterator();
		Set<String> topKTarget = new HashSet<>();
		// 把基本的统计结果放回
		// 写人文件中
		List<String> resultList = new ArrayList<>();
		while (iterable.hasNext()) {
			User user = usermap.get(iterable.next());
			if (user.getBeAttackedNumber() >= BeMentionlimit
					&& user.getRelyNumber() >= relyLimit) {
				topKTarget.add(user.getUserId() + "\t" + user.getUserName());
			}
//			//保存格式 用户id ,用户名	用户@次数，用户被@次数，用户回复次数，用户被回复次数
//			resultList.add(user.getUserId() + "\t" + user.getUserName() + "\t"
//					+ user.getAttackNumber() + "\t"
//					+ user.getBeAttackedNumber() + "\t" + user.getRelyNumber()
//					+ "\t" + user.getBeRelyNumber());
		}
		//WriteUtil<String> writeUtil=new WriteUtil<>();
		//writeUtil.writeList(resultList, intection_txt_Source.getUseridMentionBementionReplyBereplyCount());
		return topKTarget;
	}

	public Set<Long> get_target_userId(String fileName) {
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> tempList = readUtil.readFileByLine(fileName);
		Set<Long> targetuserId = new HashSet<>();
		for (String oneLine : tempList) {
			String[] split = oneLine.split("\t");
			targetuserId.add(Long.valueOf(split[0]));
		}
		return targetuserId;
	}

	/**
	 * @param allMentionHistoryFile
	 * @param i
	 * @param j
	 * @param topk_Mention_userId
	 * @return
	 * @create_time：2014年12月24日上午10:22:20
	 * @modifie_time：2014年12月24日 上午10:22:20
	 */
	public Set<Long> get_Publisher_limit_by_target_user_up_down(
			String allMentionHistoryFile, int upLimit, int lowLimit,
			Set<Long> topk_Mention_userId) {
		if (upLimit <= lowLimit) {
			System.out
					.println("the up and low limit is fail in get_Publisher_limit_by_target_user_up_down");
			System.exit(0);
		}
		HashMap<Long, Integer> publishers = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				allMentionHistoryFile))) {
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				
				
				// type weiboid\t userid\t createTime \t retweetId \t targetId
				// \t weibocontentvector
				// if no retweet ,the retweetId is 0
				String[] splits=oneLine.split("\t");
				
				long sourceUserId=Long.valueOf(splits[1]);
				long targetuserId=Long.valueOf(splits[4]);
				if (sourceUserId==targetuserId) {
					continue;
				}
				if (topk_Mention_userId.contains(targetuserId)) {
					if (publishers.containsKey(sourceUserId)) {
						publishers.put(sourceUserId, publishers.get(sourceUserId)+1);
					}else {
						publishers.put(sourceUserId, 1);
					}
				}
				
//				RemType remType = RemType.covert(oneLine);
//				List<Reminder> listrem = remType.getReminders();
//				int count = 0;
//				for (Reminder reminder : listrem) {
//					if (reminder.getUserId() == remType.getRoot().getUserId())
//						continue;
//					count++;
//					if (topk_Mention_userId.contains(reminder.getUserId())
//							&& !publishers.keySet().contains(
//									remType.getRoot().getUserId())) {
//						publishers.put(remType.getRoot().getUserId(), 0);
//					}
//				}
//				if (publishers.keySet().contains(remType.getRoot().getUserId())) {
//					publishers.put(remType.getRoot().getUserId(),
//							publishers.get(remType.getRoot().getUserId())
//									+ count);
//				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get limit userid
		Set<Long> avluserId = new HashSet<>();
		for (Long userid : publishers.keySet()) {
			int count = publishers.get(userid);
			if (count > upLimit || count < lowLimit) {
				continue;
			}
			avluserId.add(userid);
		}
		return avluserId;

	}

}
