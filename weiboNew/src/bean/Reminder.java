/**
 * 
 */
package bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import preprocess._1014.staticreminderweibo;
import util.CalanderUtil;
import model.link.covert;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：Reminder   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月27日 下午2:25:04   
 * @modifier：zhouge   
 * @modified_time：2014年10月27日 下午2:25:04   
 * @modified_note：   
 * @version    
 *    
 */
public class Reminder {
	private long userId;
	private String replyTime;
	private Calendar replycalCalendar;
	private boolean ifRely;
	
	
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
	 * @return the replyTime
	 */
	public String getReplyTime() {
		return replyTime;
	}
	/**
	 * @param replyTime the replyTime to set
	 */
	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
		setReplycalCalendar(replyTime);
	}
	/**
	 * @return the replycalCalendar
	 */
	public Calendar getReplycalCalendar() {
		return replycalCalendar;
	}
	/**
	 * @param replycalCalendar the replycalCalendar to set
	 */
	public void setReplycalCalendar(String replycalCalendar) {
		Date date = CalanderUtil.getDate(replycalCalendar);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		this.replycalCalendar = cal;
	}
	/**
	 * @return the ifRely
	 */
	public boolean isIfRely() {
		return ifRely;
	}
	/**
	 * @param ifRely the ifRely to set
	 */
	public void setIfRely(boolean ifRely) {
		this.ifRely = ifRely;
	}
	
	public static Reminder covert(String oneLine){
		int index=oneLine.indexOf(":");
		String split1=oneLine.substring(0, index);
		String split2=oneLine.substring(index+1);
		if(split2.equals("0")){
			Reminder reminder=new Reminder();
			reminder.setIfRely(false);
			reminder.setUserId(Long.valueOf(split1));
			return reminder;
		}else {
			Reminder reminder=new Reminder();
			reminder.setIfRely(true);
			reminder.setUserId(Long.valueOf(split1));
			reminder.setReplyTime(split2);
			return reminder;
		}
	}
}
