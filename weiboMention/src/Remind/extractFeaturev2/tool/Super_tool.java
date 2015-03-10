/**
 * 
 */
package Remind.extractFeaturev2.tool;

import java.util.Date;

import Resource.mongoDB_Source;
import tool.TimeTool.CalanderUtil;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.ChineseSpliter;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：Super_tool
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月27日 下午1:21:41
 * @modifier：zhouge
 * @modified_time：2015年1月27日 下午1:21:41
 * @modified_note：
 * @version
 * 
 */
public class Super_tool {
	static CalanderUtil calanderUtil = new CalanderUtil();
	static ChineseSpliter spliter = null;
	static ChineseFilter filter = null;
	static Date trainBegin = null;
	static Date TestBegin = null;
	static Date TestEnd = null;
	
	static mongoDB_Source mongoDB_Source=null;
	static {
		spliter = new ChineseSpliter();
		filter = new ChineseFilter();
		String type = "yyyy/MM/dd";
		trainBegin = calanderUtil.getDate("2012/01/01", type);
		TestBegin = calanderUtil.getDate("2012/6/1", type);
		TestEnd = calanderUtil.getDate("2012/7/1", type);
		mongoDB_Source=new mongoDB_Source();
	}

	public Super_tool() {
	}

	/**
	 * @return the trainBegin
	 */
	public static Date getTrainBegin() {
		return trainBegin;
	}

	/**
	 * @param trainBegin the trainBegin to set
	 */
	public static void setTrainBegin(Date trainBegin) {
		Super_tool.trainBegin = trainBegin;
	}

	/**
	 * @return the testBegin
	 */
	public static Date getTestBegin() {
		return TestBegin;
	}

	/**
	 * @param testBegin the testBegin to set
	 */
	public static void setTestBegin(Date testBegin) {
		TestBegin = testBegin;
	}

	/**
	 * @return the testEnd
	 */
	public static Date getTestEnd() {
		return TestEnd;
	}

	/**
	 * @param testEnd the testEnd to set
	 */
	public static void setTestEnd(Date testEnd) {
		TestEnd = testEnd;
	}

}
