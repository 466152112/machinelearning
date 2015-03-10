/**
 * 
 */
package CCF.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import util.CalanderUtil;
import bean.OnePairTweet;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：newBrowseRecord   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月25日 上午9:13:34   
 * @modifier：zhouge   
 * @modified_time：2014年9月25日 上午9:13:34   
 * @modified_note：   
 * @version    
 *    
 */
public class NewsBrowseRecord extends News {

	private long userId;
	private Calendar browseTime;

	public NewsBrowseRecord(){
		
	}
	
	public NewsBrowseRecord(long userId,long newsId,Calendar browseTime,String title,String content,Calendar createTime){
		this.userId=userId;
		this.newsId=newsId;
		this.browseTime=browseTime;
		this.title=title;
		this.content=content;
		this.createTime=createTime;
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
	 * @return the browseTime
	 */
	public Calendar getBrowseTime() {
		return browseTime;
	}


	/**
	 * @param browseTime the browseTime to set
	 */
	public void setBrowseTime(Calendar browseTime) {
		this.browseTime = browseTime;
	}




	public static NewsBrowseRecord covert(String oneLine){
		//8936831	100657188	1395915013	彭丽媛法国看望患病儿童	3月26日下午，彭丽媛访问了位于巴黎市区的尼克尔儿童医院。图片来源@时政考2/3	2014年03月27日08:53
		String tempString = oneLine;
		NewsBrowseRecord newRecord;
		long userId, newsId;
		Calendar browseTime,createTime;
		String content, title;
		
		final String sf1 = "yyyy年MM月dd日HH:mm";
		//2014年03月17日
		final String sf2 = "yyyy年MM月dd日";
		try {
			userId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));

			tempString = tempString.substring(tempString.indexOf("\t")).trim();

			newsId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));
			
			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			
			String timestamp=tempString.substring(0,
					tempString.indexOf("\t"));
			if (timestamp.equals("NULL")) {
				return null;
			}
			long time=new Long(timestamp);
			time*=1000;
			browseTime=CalanderUtil.getdateFromTimestamp(time);
			
			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			
			title=tempString.substring(0,tempString.indexOf("\t"));
			if (title.equals("NULL")) {
				return null;
			}
			
			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			
			content=tempString.substring(0,tempString.indexOf("\t")).trim();
			if (content.equals("NULL")) {
				return null;
			}
			
			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			
			if (tempString.trim().equals("NULL")) {
				createTime=null;
			}else if (tempString.trim().length()==11) {
				createTime=CalanderUtil.getCalander(tempString.trim(), sf2);
			}else {
				createTime=CalanderUtil.getCalander(tempString.trim(), sf1);
			}
			newRecord=	new NewsBrowseRecord(userId, newsId, browseTime, title.trim(), content.trim(), createTime);
			return newRecord;
		} catch (Exception e) {
			System.out.println(tempString);
			e.printStackTrace();
			return null;
		}

	}
}
