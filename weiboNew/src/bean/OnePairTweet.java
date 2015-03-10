/**
 * 
 */
package bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import util.CalanderUtil;

/**
 * 
 * 项目名称：liuchuang 类名称：OnePairTweet 类描述： 创建人：zhouge 创建时间：2014年4月29日 下午1:45:46
 * 修改人：zhouge 修改时间：2014年4月29日 下午1:45:46 修改备注：
 * 
 * @version
 * 
 */
public class OnePairTweet implements Comparable<OnePairTweet> {
	private String createTime;
	// 用户id
	private long userId;
	// 微博id
	private long weiboId;
	// 微博内容
	private String content;
	// 转发id
	private long retweetId;
	// 转发用户id
	private long retweetUser_id;
	private Calendar Calendar;
	private List<OnePairTweet> retweetList;
	private String geo;
	public OnePairTweet() {

	}

	
	/**
	 * @return the geo
	 */
	public String getGeo() {
		return geo;
	}


	/**
	 * @param geo the geo to set
	 */
	public void setGeo(String geo) {
		this.geo = geo;
	}


	/**
	 * @param userId
	 * @param weiboId
	 * @param content
	 * @param retweetid
	 * @param retweetUserId
	 * @param time
	 */
	public OnePairTweet(long userId, long weiboId, String content,
			long retweetid, long retweetUserId, String time) {
		this.userId = userId;
		this.weiboId = weiboId;
		this.content = content;
		this.retweetId = retweetid;
		this.retweetUser_id = retweetUserId;
		this.createTime = time;
		setCreate_time(time);

	}

	public OnePairTweet(long userId, long weiboId, String content, String time) {
		this.userId = userId;
		this.weiboId = weiboId;
		this.content = content;
		this.createTime = time;
		setCreate_time(time);

	}

	/**
	 * @return the create_time
	 */
	public Calendar getCreate_time() {
		return this.Calendar;
	}

	/**
	 * @param create_time
	 *            the create_time to set
	 */
	public void setCreate_time(String create_time) {
		Date date = CalanderUtil.getDate(create_time);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		this.Calendar = cal;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return the weiboId
	 */
	public long getWeiboId() {
		return weiboId;
	}

	/**
	 * @param weiboId
	 *            the weiboId to set
	 */
	public void setWeiboId(long weiboId) {
		this.weiboId = weiboId;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the retweetList
	 */
	public List<OnePairTweet> getRetweetList() {
		return retweetList;
	}

	/**
	 * @param retweetList
	 *            the retweetList to set
	 */
	public void setRetweetList(List<OnePairTweet> retweetList) {
		this.retweetList = retweetList;
	}

	/**
	 * @param retweetList
	 *            the retweetList to set
	 */
	public void addRetweetList(List<OnePairTweet> retweetList) {
		if (this.retweetList == null) {
			this.retweetList = retweetList;
		} else {
			this.retweetList.addAll(retweetList);
		}
	}

	public void addOneRetweetList(OnePairTweet retweet) {
		if (this.retweetList == null) {
			this.retweetList = new ArrayList<>();
			this.retweetList.add(retweet);
		} else {
			boolean flag = true;
			for (OnePairTweet compare : this.retweetList) {
				if (compare.getWeiboId() == retweet.getWeiboId()) {
					// System.out.println("error in addOneRetweetList");
					flag = false;
					break;
				}
			}
			if (flag) {
				this.retweetList.add(retweet);
			}

		}
	}

	/**
	 * @return the retweetId
	 */
	public long getRetweetId() {
		return retweetId;
	}

	/**
	 * @param retweetId
	 *            the retweetId to set
	 */
	public void setRetweetId(long retweetId) {
		this.retweetId = retweetId;
	}

	/**
	 * @return the retweetUser_id
	 */
	public long getRetweetUser_id() {
		return retweetUser_id;
	}

	/**
	 * @param retweetUser_id
	 *            the retweetUser_id to set
	 */
	public void setRetweetUser_id(long retweetUser_id) {
		this.retweetUser_id = retweetUser_id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(OnePairTweet o) {
		// TODO Auto-generated method stub
		if (this.getWeiboId() > o.getWeiboId()) {
			return 1;
		} else if (this.getWeiboId() < o.getWeiboId()) {
			return -1;
		} else {
			return 0;
		}
	}

	public String toString() {
		if (retweetId == 0L) {
			StringBuffer tempBuffer=new StringBuffer();
			tempBuffer.append(weiboId + "\t");
			tempBuffer.append(userId + "\t");
			tempBuffer.append(createTime + "\t");
			tempBuffer.append(content);
			return tempBuffer.toString();
		} else
		{
			StringBuffer tempBuffer=new StringBuffer();
			tempBuffer.append(weiboId + "\t");
			tempBuffer.append(userId + "\t");
			tempBuffer.append( retweetId + "\t");
			tempBuffer.append(retweetUser_id + "\t");
			tempBuffer.append(createTime + "\t");
			tempBuffer.append(content);
			return tempBuffer.toString();
			
		}
			
	}

	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the calendar
	 */
	public Calendar getCalendar() {
		return Calendar;
	}

	public static OnePairTweet covert(String oneLine) {

		String tempString = oneLine;
		OnePairTweet onePairTweet;
		long userId, weiboId, retweetid = 0L, retweetUserId = 0L;
		String content, time;
		try {
			weiboId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));

			tempString = tempString.substring(tempString.indexOf("\t")).trim();

			userId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));
			if (tempString.trim().equals("")) {
				
			}
			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			try {
				// 查看是否能转为转发格式
				retweetid = Long.valueOf(tempString.substring(0,
						tempString.indexOf("\t")));

				tempString = tempString.substring(tempString.indexOf("\t"))
						.trim();

				retweetUserId = Long.valueOf(tempString.substring(0,
						tempString.indexOf("\t")));

				tempString = tempString.substring(tempString.indexOf("\t"))
						.trim();

				if (tempString.indexOf("\t") == -1) {
					time = tempString;
					content = "";
				} else {
					time = tempString.substring(0, tempString.indexOf("\t"));

					tempString = tempString.substring(tempString.indexOf("\t"))
							.trim();

					content = tempString.trim();
				}

			} catch (NumberFormatException e) {
				if (oneLine.indexOf("\t") == -1) {
					time = tempString;
					content = "";
				} else {
					time = tempString.substring(0, tempString.indexOf("\t"));

					tempString = tempString.substring(tempString.indexOf("\t"))
							.trim();

					content = tempString.trim();
				}

			}
			if (retweetid == 0L) {
				return new OnePairTweet(userId, weiboId, content, time);
			} else {
				return new OnePairTweet(userId, weiboId, content, retweetid,
						retweetUserId, time);
			}
		} catch (Exception e) {
			System.out.println("error in :"+oneLine);
			//e.printStackTrace();
			return null;
		}

	}

	public static OnePairTweet covertSimple(String oneLine) {

		String tempString = oneLine;
		OnePairTweet onePairTweet;
		long userId, weiboId, retweetid = 0L, retweetUserId = 0L;
		String content, time;
		try {
			weiboId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));

			tempString = tempString.substring(tempString.indexOf("\t")).trim();

			userId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));

			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			try {
				// 查看是否能转为转发格式
				retweetid = Long.valueOf(tempString.substring(0,
						tempString.indexOf("\t")));

				tempString = tempString.substring(tempString.indexOf("\t"))
						.trim();

				retweetUserId = Long.valueOf(tempString.substring(0,
						tempString.indexOf("\t")));
				onePairTweet = new OnePairTweet();
				onePairTweet.setWeiboId(weiboId);
				onePairTweet.setUserId(userId);
				onePairTweet.setRetweetId(retweetid);
				onePairTweet.setRetweetUser_id(retweetUserId);
				return onePairTweet;
			} catch (NumberFormatException e) {
				onePairTweet = new OnePairTweet();
				onePairTweet.setWeiboId(weiboId);
				onePairTweet.setUserId(userId);
				return onePairTweet;
			}

		} catch (Exception e) {
			System.out.println(tempString);
			e.printStackTrace();
			return null;
		}

	}

	public static OnePairTweet covertSimpleSplitByComma(String oneLine) {

		String tempString = oneLine;
		OnePairTweet onePairTweet;
		long userId, weiboId, retweetid = 0L, retweetUserId = 0L;
		String content, time;
		try {
			String[] split = oneLine.split(",");
			if (split.length!=5) {
				return null;
			}
			weiboId = Long.valueOf(split[0]);

			userId = Long.valueOf(split[1]);

			retweetid = Long.valueOf(split[2]);
			retweetUserId = Long.valueOf(split[3]);
			
			onePairTweet = new OnePairTweet();
			onePairTweet.setWeiboId(weiboId);
			onePairTweet.setUserId(userId);
			onePairTweet.setRetweetId(retweetid);
			onePairTweet.setRetweetUser_id(retweetUserId);
			return onePairTweet;

		} catch (Exception e) {
			System.out.println(tempString);
			e.printStackTrace();
			return null;
		}

	}

	public static long covertRetweetId(String oneLine) {

		long retweetid;

		oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();

		oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();
		try {
			retweetid = Long
					.valueOf(oneLine.substring(0, oneLine.indexOf("\t")));
			return retweetid;
		} catch (NumberFormatException e) {
			return 0;
		} catch (StringIndexOutOfBoundsException e) {
			return 0;
		}

	}

	public static long covertUserId(String oneLine) {

		long UserId;

		try {

			oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();
			UserId = Long.valueOf(oneLine.substring(0, oneLine.indexOf("\t")));
			return UserId;
		} catch (NumberFormatException e) {
			return 0;
		} catch (StringIndexOutOfBoundsException e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}

	}

	public static Long crawlingWeiboId(String oneLine) {
		long resultRetweenWeiboid;
		try {

			oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();
			oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();
			resultRetweenWeiboid = Long.valueOf(oneLine.substring(0,
					oneLine.indexOf("\t")));
			return resultRetweenWeiboid;
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			return 0L;
		}

	}

	public static Long crawlingRetweenWeiboId(String oneLine) {
		long resultweiboid;
		try {

			resultweiboid = Long.valueOf(oneLine.substring(0,
					oneLine.indexOf("\t")));
			return resultweiboid;
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			return 0L;
		}

	}

}
