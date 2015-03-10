/**
 * 
 */
package Remind.extractFeaturev2.tool;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;

import bean.User;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：Session_tool
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年2月5日 上午11:24:54
 * @modifier：zhouge
 * @modified_time：2015年2月5日 上午11:24:54
 * @modified_note：
 * @version
 * 
 */
public class Session_tool extends Super_tool {

	/**
	 * @param sourceUser
	 * @param targetUser
	 * @param created_time
	 * @param oneLine
	 * @return 1、是否是targetuser 发起的 2、有 几个 targetuser 的 followee参与了讨论
	 *         3、targetuser 参与转发的次数 4、微博的流行度。即最终转发次数
	 * @create_time：2015年2月5日下午1:02:18
	 * @modifie_time：2015年2月5日 下午1:02:18
	 */
	public List<Double> get_Session_Feature(User sourceUser, User targetUser,
			Date created_time, BasicDBObject oneLine) {
		List<Double> feature = new ArrayList<>();
		try {
			BasicDBObject temp = getRetweetList(oneLine);
			feature.add(ifLaunchByTargetUser(targetUser, temp));
			feature.add(getFolloweeInSession(targetUser, temp, created_time));
			feature.add(TargetUserRetweenBeforeThisMention(targetUser,
					created_time, temp));
			feature.add(getPopularity(temp));

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return feature;
	}

	private BasicDBObject getRetweetList(BasicDBObject oneLine) {
		long weiboid = oneLine.getLong("_id");
		if (oneLine.containsField("root_weibo")) {
			BasicDBObject root_weibo = (BasicDBObject) oneLine
					.get("root_weibo");
			weiboid = root_weibo.getLong("_id");
		}
		return mongoDB_Source.getBeRetweenList(weiboid);
	}

	public double getFolloweeInSession(User targetUser, BasicDBObject retweet,
			Date created_time) {
		if (retweet == null) {
			return 0;
		}
		List<BasicDBObject> list = (List<BasicDBObject>) retweet.get("retweet");
		Set<Long> relFollowee = new HashSet<>();
		if (targetUser.getfollowee() == null) {
			Set<Long> u2f = mongoDB_Source.getFriend_list(targetUser
					.getUserId());
			targetUser.setfollowee(u2f);
		}
		for (BasicDBObject temp : list) {
			if (calanderUtil.getDate(temp.getString("created_time")).compareTo(
					created_time) < 0) {
				long userid = temp.getLong("user_id");
				if (targetUser.getfollowee().contains(userid)) {
					relFollowee.add(userid);
				}
			}
		}
		return relFollowee.size();

	}

	/**
	 * @param targetUser
	 * @param created_time
	 * @param retweet
	 * @return 获取目标用户是否已经多次转发
	 * @create_time：2015年2月5日下午12:48:13
	 * @modifie_time：2015年2月5日 下午12:48:13
	 */
	public double TargetUserRetweenBeforeThisMention(User targetUser,
			Date created_time, BasicDBObject retweet) {
		if (retweet == null) {
			return 0;
		}
		List<BasicDBObject> list = (List<BasicDBObject>) retweet.get("retweet");
		int count = 0;
			for (BasicDBObject temp : list) {
				if (temp.getLong("user_id") == targetUser.getUserId()) {
					if (calanderUtil.getDate(temp.getString("created_time"))
							.compareTo(created_time) < 0) {
						count++;
					}
				}
			}
		return count;
	}

	/**
	 * @param retweet
	 * @return 获取目标微博的流行度
	 * @create_time：2015年2月5日下午12:49:56
	 * @modifie_time：2015年2月5日 下午12:49:56
	 */
	public double getPopularity(BasicDBObject retweet) {
		if (retweet == null) {
			return 0;
		}
		List<BasicDBObject> list = (List<BasicDBObject>) retweet.get("retweet");
		return list.size();
	}

	/**
	 * 是否是目标用户发起的mention
	 */
	public double ifLaunchByTargetUser(User targetUser,
			BasicDBObject retweetList) {
		if (retweetList == null) {
			return 0;
		}
		if (retweetList.getLong("user_id") == targetUser.getUserId()) {
			return 1;
		} else {
			return 0;
		}
	}
}
