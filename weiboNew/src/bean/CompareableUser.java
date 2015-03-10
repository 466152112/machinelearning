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

public class CompareableUser implements Comparable<CompareableUser>{
	private long userId;
	private String name;
	private Set<String> tags;
	private String educatioin;
	private boolean verified;
	private String birthday;
	private String work;
	private String province;
	private String city;
	private String location;
	private int followers_Count;
	private int friends_Count;
	private int status_count;
	private String description;
	private Set<Long> followeeSet;
	private Set<Long> biFriendSet;
	private boolean isMan;
	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}


	/**
	 * @return the isMan
	 */
	public boolean isMan() {
		return isMan;
	}


	/**
	 * @param isMan the isMan to set
	 */
	public void setMan(boolean isMan) {
		this.isMan = isMan;
	}


	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the tags
	 */
	public Set<String> getTags() {
		return tags;
	}



	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}



	/**
	 * @return the educatioin
	 */
	public String getEducatioin() {
		return educatioin;
	}



	/**
	 * @param educatioin the educatioin to set
	 */
	public void setEducatioin(String educatioin) {
		this.educatioin = educatioin;
	}



	/**
	 * @return the verified
	 */
	public boolean isVerified() {
		return verified;
	}



	/**
	 * @param verified the verified to set
	 */
	public void setVerified(boolean verified) {
		this.verified = verified;
	}



	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}



	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}



	/**
	 * @return the work
	 */
	public String getWork() {
		return work;
	}



	/**
	 * @param work the work to set
	 */
	public void setWork(String work) {
		this.work = work;
	}



	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}



	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}



	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}



	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
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
	 * @return the followers_Count
	 */
	public int getFollowers_Count() {
		return followers_Count;
	}



	/**
	 * @param followers_Count the followers_Count to set
	 */
	public void setFollowers_Count(int followers_Count) {
		this.followers_Count = followers_Count;
	}



	/**
	 * @return the friends_Count
	 */
	public int getFriends_Count() {
		return friends_Count;
	}



	/**
	 * @param friends_Count the friends_Count to set
	 */
	public void setFriends_Count(int friends_Count) {
		this.friends_Count = friends_Count;
	}



	/**
	 * @return the status_count
	 */
	public int getStatus_count() {
		return status_count;
	}



	/**
	 * @param status_count the status_count to set
	 */
	public void setStatus_count(int status_count) {
		this.status_count = status_count;
	}



	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}



	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}




	/**
	 * @return the followeeSet
	 */
	public Set<Long> getFolloweeSet() {
		return followeeSet;
	}


	/**
	 * @param followeeSet the followeeSet to set
	 */
	public void setFolloweeSet(Set<Long> followeeSet) {
		this.followeeSet = followeeSet;
	}


	/**
	 * @return the biFriendSet
	 */
	public Set<Long> getBiFriendSet() {
		return biFriendSet;
	}


	/**
	 * @param biFriendSet the biFriendSet to set
	 */
	public void setBiFriendSet(Set<Long> biFriendSet) {
		this.biFriendSet = biFriendSet;
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CompareableUser o) {
		// TODO Auto-generated method stub
		if (this.userId>o.userId) {
			return 1;
		}else if(this.userId<o.userId){
			 return -1;
		}
		else {
			return 0;
		}
	}
}