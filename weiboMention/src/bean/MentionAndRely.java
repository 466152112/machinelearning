/**
 * 
 */
package bean;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：sdf
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月29日 下午8:08:38
 * @modifier：zhouge
 * @modified_time：2015年1月29日 下午8:08:38
 * @modified_note：
 * @version
 * 
 */

public class MentionAndRely implements Comparable<MentionAndRely> {
	private long userId;
	private int beMention = 0;
	private int rely = 0;
	private List<BasicDBObject> revlentMention;
	
	public MentionAndRely(long userId) {
		this.userId = userId;
	}

	
	/**
	 * @return the revlentMention
	 */
	public List<BasicDBObject> getRevlentMention() {
		return revlentMention;
	}


	/**
	 * @param revlentMention the revlentMention to set
	 */
	public void setRevlentMention(List<BasicDBObject> revlentMention) {
		this.revlentMention=new ArrayList<>();
		this.revlentMention = revlentMention;
	}


	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}


	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}


	/**
	 * @return the beMention
	 */
	public int getBeMention() {
		return beMention;
	}

	/**
	 * @param beMention
	 *            the beMention to set
	 */
	public void setBeMention(int beMention) {
		this.beMention = beMention;
	}

	/**
	 * @return the rely
	 */
	public int getRely() {
		return rely;
	}

	/**
	 * @param rely
	 *            the rely to set
	 */
	public void setRely(int rely) {
		this.rely = rely;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MentionAndRely o) {
		// TODO Auto-generated method stub
		if (o.userId > this.userId) {
			return 1;
		} else if (o.userId < this.userId) {
			return -1;
		}
		return 0;
	}

}
