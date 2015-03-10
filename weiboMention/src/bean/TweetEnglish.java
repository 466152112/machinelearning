/**
 * 
 */
package bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import tool.TimeTool.CalanderUtil;


/**   
 *    
 * @progject_name：twitter   
 * @class_name：Tweet   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月15日 下午6:55:13   
 * @modifier：zhouge   
 * @modified_time：2014年9月15日 下午6:55:13   
 * @modified_note：   
 * @version    
 *    
 */
public class TweetEnglish extends OnePairTweet{

	
	public TweetEnglish(){
	}
	public TweetEnglish(String userName,String content,Calendar Calendar){
		this.userName=userName;
		this.content=content;
		super.Calendar=Calendar;
	}
	/**
	 * @param create_time
	 *            the create_time to set
	 */
	public void setCreate_time(String create_time) {
		create_time = create_time.substring(1).trim();
		Date date = CalanderUtil.getDateEnglish(create_time);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		super.Calendar=cal;
	}
	
	public static OnePairTweet covert(List<String> oneLineList){
		
		if (oneLineList.size()==3) {
			TweetEnglish tweetEnglish=new TweetEnglish();
			tweetEnglish.setCreate_time(oneLineList.get(0));
			tweetEnglish.setUserName(oneLineList.get(1));
			tweetEnglish.setContent(oneLineList.get(2));
			return tweetEnglish;	
		}else {
			System.out.println("error in covertToTweetEnglish : the length erro");
			return null;
		}
		
		
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 * @example: U	http://twitter.com/barkingunicorn
	 */
	public void setUserName(String oneLine) {
		int lastSlashindex=oneLine.lastIndexOf("/")+1;
		this.userName = oneLine.substring(lastSlashindex).trim();
	}
	
	public void setContent(String oneLine) {
		this.content = oneLine.substring(1).trim();
	}
 
	

}
