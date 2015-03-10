/**
 * 
 */
package bean;

import java.util.HashSet;
import java.util.Set;

import tool.data.DenseVector;
import tool.dataStucture.AvlTree;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：User
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年8月18日 下午4:10:56
 * @modifier：zhouge
 * @modified_time：2014年8月18日 下午4:10:56
 * @modified_note：
 * @version
 * 
 */

public class User implements Comparable<User> {
	private long userId;
	private DenseVector interest_feature = null;
	private Set<Long> followee;
	private boolean flag = true;
	private int retweetNumber;
	private String userName;
	private String location;
	private int verified;
	private String gender;
	private int statuses_count;
	private int beAttackedNumber = 0, beRetweetNumber = 0;
	private int attackNumber = 0, relyNumber = 0, beRelyNumber = 0;
	private int followers_count, followee_count;
	private String tag;
	private StringBuffer tweets_content = new StringBuffer();
	private StringBuffer tag_content = new StringBuffer();
	private String biography;
	
	/**
	 * @return the tweets_content
	 */
	public StringBuffer getTweets_content() {
		return tweets_content;
	}

	/**
	 * @return the biography
	 */
	public String getBiography() {
		return biography;
	}

	/**
	 * @param biography the biography to set
	 */
	public void setBiography(String biography) {
		this.biography = biography;
	}

	/**
	 * @return the tag_content
	 */
	public StringBuffer getTag_content() {
		return tag_content;
	}
	/**
	 * @param userid2
	 */
	public User(long userid2) {
		this.userId = userid2;
	}

	public User() {

	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getBeRelyNumber() {
		return beRelyNumber;
	}

	public void setBeRelyNumber(int beRelyNumber) {
		this.beRelyNumber = beRelyNumber;
	}


	public int getRelyNumber() {
		return relyNumber;
	}

	public void setRelyNumber(int relyNumber) {
		this.relyNumber = relyNumber;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public int getBeAttackedNumber() {
		return beAttackedNumber;
	}

	public void setBeAttackedNumber(int beAttackedNumber) {
		this.beAttackedNumber = beAttackedNumber;
	}

	public int getAttackNumber() {
		return attackNumber;
	}

	public void setAttackNumber(int attackNumber) {
		this.attackNumber = attackNumber;
	}

	/**
	 * @return the followers_count
	 */
	public int getFollowers_count() {
		return followers_count;
	}

	/**
	 * @param followers_count
	 *            the followers_count to set
	 */
	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	/**
	 * @return the friends_count
	 */
	public int getFriends_count() {
		return followee_count;
	}

	/**
	 * @param friends_count
	 *            the friends_count to set
	 */
	public void setFriends_count(int friends_count) {
		this.followee_count = friends_count;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the verified
	 */
	public int getVerified() {
		return verified;
	}

	/**
	 * @param verified
	 *            the verified to set
	 */
	public void setVerified(int verified) {
		this.verified = verified;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the statuses_count
	 */
	public int getStatuses_count() {
		return statuses_count;
	}

	/**
	 * @param statuses_count
	 *            the statuses_count to set
	 */
	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
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

	public void setUserId(String userId) {
		this.userId = Long.valueOf(userId);
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the beRetweetNumber
	 */
	public int getBeRetweetNumber() {
		return beRetweetNumber;
	}

	/**
	 * @param beRetweetNumber
	 *            the beRetweetNumber to set
	 */
	public void setBeRetweetNumber(int beRetweetNumber) {
		this.beRetweetNumber = beRetweetNumber;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the feature
	 */
	public DenseVector getInterestFeature() {
		return interest_feature;
	}

	/**
	 * @param interest_feature
	 *            the feature to set
	 */
	public void setInterest_feature(String line) {
		// 1736381701 0:3 3:2 4:1 6:8 9:5 10:4 11:3 13:2 14:2 15:10 17:3 16:5
		// 18:7 20:59 23:2 22:3 24:2 27:2 26:3 29:2 28:1 31:9 34:4 35:7 32:108
		// 39:1 36:1 42:1 43:1 40:9 41:2 47:6 44:1 45:2 51:5 50:3 49:1 53:4
		// 52:15 59:3 58:4 63:22 62:6 61:44 60:6 68:10 69:3 71:1 64:3 65:7 76:3
		// 78:1 74:1 75:5 85:60 84:3 80:1 83:2 82:75 93:7 92:4 95:1 94:4 89:36
		// 88:1 91:3 90:2 97:4
		String[] split = line.split("\t");

		double[] temp = new double[100];
		int sum = 0;
		for (int i = 1; i < split.length; i++) {
			String[] split1 = split[i].split(":");
			double count = Double.valueOf(split1[1]);
			temp[Integer.valueOf(split1[0])] = count;
			sum += count;
		}

		for (int i = 0; i < 100; i++) {
			temp[i] = temp[i] / sum;
		}
		if (sum < 500)
			flag = false;
		this.interest_feature = new DenseVector(temp);
	}

	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	public Set<Long> getfollowee() {
		return followee;
	}
	public void setfollowee(Set<Long> userList) {
		followee=new HashSet<>();
		followee = userList;
	}



	/**
	 * @return the retweetNumber
	 */
	public int getRetweetNumber() {
		return retweetNumber;
	}

	/**
	 * @param retweetNumber
	 *            the retweetNumber to set
	 */
	public void setRetweetNumber(int retweetNumber) {
		this.retweetNumber = retweetNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(User o) {
		// TODO Auto-generated method stub
		if (o.userId > this.userId) {
			return 1;
		} else if (o.userId < this.userId) {
			return -1;
		}
		return 0;
	}

	public static boolean ifInUserAVL(String userid, AvlTree<User> useravl) {
		User user = new User();
		user.setUserId(userid);
		if (useravl.contains(user)) {
			return true;
		} else {
			return false;
		}

	}
	/**
	 * @return
	 *@create_time：2015年1月19日下午6:42:04
	 *@modifie_time：2015年1月19日 下午6:42:04
	  
	 */
	@Override
	public boolean equals(Object  compare){
		  if(compare==null)
		      return false;
		    if(this == compare){
		      return true;
		    }
		    if (compare instanceof User) {
		    	User other = (User) compare;
		      return  this.userId==other.userId?true:false;
		    }
		    return false;
	}
}