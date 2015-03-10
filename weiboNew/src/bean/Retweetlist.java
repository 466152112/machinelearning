/**
 * 
 */
package bean;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import util.CalanderUtil;
import util.TweetCalendarComparator;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：Retweetlist   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月15日 下午11:27:16   
 * @modifier：zhouge   
 * @modified_time：2014年10月15日 下午11:27:16   
 * @modified_note：   
 * @version    
 *    
 */
public class Retweetlist {
	
	private OnePairTweet root;
	private List<OnePairTweet> retweetList;
	/**
	 * @return the root
	 */
	public OnePairTweet getRoot() {
		return root;
	}
	/**
	 * @param root the root to set
	 */
	public void setRoot(OnePairTweet root) {
		this.root = root;
	}
	/**
	 * @return the retweetList
	 */
	public List<OnePairTweet> getRetweetList() {
		return retweetList;
	}
	/**
	 * @param retweetList the retweetList to set
	 */
	public void setRetweetList(List<OnePairTweet> retweetList) {
		this.retweetList = retweetList;
	}
	
	public List<OnePairTweet> getRetweetListSortByTime(){
		TweetCalendarComparator cp=new TweetCalendarComparator();
		Collections.sort(this.retweetList, cp);
		return this.retweetList;
	}
}
