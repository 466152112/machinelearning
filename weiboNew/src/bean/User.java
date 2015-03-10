/**
 * 
 */
package bean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

public class User  implements Comparable<User>{
	private long userId;
	private double[] feature;
	private double[] topKFeature;
	private double[] changeFeature;
	private Set<Long> followeeList=new HashSet();
	private boolean flag = true;
	private int weiboNumber;
	private int retweetNumber;
	private String userName;
	private String location;
	private int verified;
	private String gender;
	private int statuses_count;
	private int beAttackedNumber=0;
	private int attackNumber=0,relyNumber=0,beRelyNumber=0;
	private int followers_count,friends_count;
	private String tag;
	
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
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

	public double[] getChangeFeature() {
		return changeFeature;
	}

	public void setChangeFeature(double[] changeFeature) {
		this.changeFeature = changeFeature;
	}

	public Set<Long> getFolloweeList() {
		return followeeList;
	}

	public void setFolloweeList(Set<Long> followeeList) {
		this.followeeList = followeeList;
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
	 * @param followers_count the followers_count to set
	 */
	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	/**
	 * @return the friends_count
	 */
	public int getFriends_count() {
		return friends_count;
	}

	/**
	 * @param friends_count the friends_count to set
	 */
	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
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
	 * @param verified the verified to set
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
	 * @param gender the gender to set
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
	 * @param statuses_count the statuses_count to set
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
		this.userId =Long.valueOf(userId) ;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the feature
	 */
	public double[] getFeature() {
		return feature;
	}
	
	/**
	 * @param feature
	 *            the feature to set
	 */
	public void setFeature(String line) {
		String[] split = line.split("\t");
		if (split.length != 100) {
			System.out.println(split.length);
			System.exit(0);
		}

		double[] temp = new double[split.length];
		int sum = 0;
		for (int i = 0; i < split.length; i++) {
			double a = Double.valueOf(split[i]);
			temp[i] = a;
			sum += a;
		}

		for (int i = 0; i < split.length; i++) {
			temp[i] = temp[i] / sum;
		}
		if (sum < 500)
			flag = false;
		this.feature = temp;
	}
	/**
	 * @param feature
	 *            the feature to set
	 */
	public void setFeature(double[] temp) {

		this.feature = temp;
	}
	public void addFeature(double[] temp) {
		if (this.feature.length!=temp.length) {
			System.out.println("error in addFeature");
			System.exit(0);
		}
		for (int i = 0; i < temp.length; i++) {
			this.feature[i] += temp[i];
		}
		
	}
	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param k
	 * @return
	 * @create_time：2014年8月24日下午4:05:58
	 * @modifie_time：2014年8月24日 下午4:05:58
	 */
	private void setTopKFeature(int k) {

		if (topKFeature == null) {
			// cal the top k element
			topKFeature = feature.clone();

			Arrays.sort(topKFeature);

			double topKValue = topKFeature[topKFeature.length - k];

			topKFeature = feature.clone();
			for (int i = 0; i < feature.length; i++) {
				if (topKFeature[i] < topKValue) {
					topKFeature[i] = 0;
				}
			}

			double sum = 0;
			for (double oneElement : topKFeature) {
				sum += oneElement;
			}

			for (int i = 0; i < topKFeature.length; i++) {
				topKFeature[i] = topKFeature[i] / sum;
			}

		}
	}

	public double[] getTopKFeature(int k) {
		if (topKFeature == null) {
			setTopKFeature(k);
		}
		double[] temp = topKFeature;
		return temp;
	}

	/**
	 * @param followee
	 * @param k
	 * @return
	 * @create_time：2014年8月24日下午4:05:56
	 * @modifie_time：2014年8月24日 下午4:05:56
	 */
	public double[] calTopKFeature(int k) {
		if (changeFeature == null) {
			System.exit(0);
		}
		return changeFeature;
	}
	
	public void setcalTopKFeature(double[] Feature) {
		double sum=0;
		for (double d : Feature) {
			sum+=d;
		}
		changeFeature=new double[Feature.length];
		for (int i = 0; i < Feature.length; i++) {
			 changeFeature[i]=Feature[i]/sum;
		}
		
	}

	public Set<Long> getfollowee() {
		return followeeList;
	}

	public void setfollowee(Set<Long> userList) {
		
		followeeList = userList;
	}

	/**
	 * @return the weiboNumber
	 */
	public int getWeiboNumber() {
		return weiboNumber;
	}

	/**
	 * @param weiboNumber the weiboNumber to set
	 */
	public void setWeiboNumber(int weiboNumber) {
		this.weiboNumber = weiboNumber;
	}

	/**
	 * @return the retweetNumber
	 */
	public int getRetweetNumber() {
		return retweetNumber;
	}

	/**
	 * @param retweetNumber the retweetNumber to set
	 */
	public void setRetweetNumber(int retweetNumber) {
		this.retweetNumber = retweetNumber;
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(User o) {
		// TODO Auto-generated method stub
		if(o.userId>this.userId){
			return 1;
		}else if(o.userId<this.userId){
			return -1;
		}
		return 0;
	}
	public static boolean  ifInUserAVL(String userid,AvlTree<User> useravl ){
		User user=new User();
		user.setUserId(userid);
		if (useravl.contains(user)) {
			return true;
		}else {
			return false;
		}
		
	}
}