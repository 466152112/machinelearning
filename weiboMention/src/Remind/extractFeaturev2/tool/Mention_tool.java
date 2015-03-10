/**
 * 
 */
package Remind.extractFeaturev2.tool;


import bean.MentionAndRely;
import Resource.mongoDB_Source;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：Mention_tool   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月29日 下午8:20:22   
 * @modifier：zhouge   
 * @modified_time：2015年1月29日 下午8:20:22   
 * @modified_note：   
 * @version    
 *    
 */
public class Mention_tool extends Super_tool{
	
	/**
	 * @param sourceUser
	 * @param ifTrain
	 * @return 获取目标用户在测试集或者训练集的时间区间中的被转发的情况
	 *@create_time：2015年2月5日下午1:08:04
	 *@modifie_time：2015年2月5日 下午1:08:04
	  
	 */
	public MentionAndRely getUser_BeMention_rely(long sourceUser,boolean ifTrain){
		if (ifTrain) {
			return	mongoDB_Source.getMentionAndRely(sourceUser, trainBegin, TestBegin);
		}else {
			return	mongoDB_Source.getMentionAndRely(sourceUser, TestBegin, TestEnd);
		}
	}
}
