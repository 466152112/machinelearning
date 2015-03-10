/**
 * 
 */
package bean;

import java.util.ArrayList;
import java.util.List;

import model.link.covert;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：RemType   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月27日 下午2:21:54   
 * @modifier：zhouge   
 * @modified_time：2014年10月27日 下午2:21:54   
 * @modified_note：   
 * @version    
 *    
 */
public class RemType {
	private OnePairTweet root;
	private int retweetNumber;
	private List<Reminder> reminders;
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
	/**
	 * @return the reminders
	 */
	public List<Reminder> getReminders() {
		return reminders;
	}
	/**
	 * @param reminders the reminders to set
	 */
	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}
	
	public static RemType covert(String oneline){
		RemType remType=new RemType();
		String[] split=oneline.trim().split("\t");
		OnePairTweet rootTweet=new OnePairTweet();
		rootTweet.setWeiboId(Long.valueOf(split[0]));
		rootTweet.setUserId(Long.valueOf(split[1]));
		rootTweet.setCreate_time(split[2]);
		remType.setRoot(rootTweet);
		remType.setRetweetNumber(Integer.valueOf(split[3]));
		
		if(split.length>4){
			List<Reminder> lists=new ArrayList<>();
			for (int i = 4; i < split.length; i++) {
				lists.add(Reminder.covert(split[i]));
			}
			remType.setReminders(lists);
		}
		return remType;
	}
}
