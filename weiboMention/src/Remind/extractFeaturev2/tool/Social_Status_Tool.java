/**
 * 
 */
package Remind.extractFeaturev2.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;

import bean.OnePairTweet;
import bean.User;
import Resource.mongoDB_Source;
import tool.data.DenseVector;
import tool.mongo.Mongo_Source;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：Social_Feature   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年12月23日 下午4:20:23   
 * @modifier：zhouge   
 * @modified_time：2014年12月23日 下午4:20:23   
 * @modified_note：   
 * @version    
 *    
 */
public class Social_Status_Tool extends Super_tool{
	
	/**
	 * @param sourceUser
	 * @return 
	 * * 计算所有的基于社交网络的用户影响力特征：所有的特征都是在一个时间区间里面的
	 *  1、用户历史微博数量
	 *  2、用户的粉丝数
	 *  3、source user 的微博平均转发次数
	 * 	4、source user 被@的次数 
	 *  5、source user 被回复的次数
	 *@create_time：2014年12月28日下午9:29:02
	 *@modifie_time：2014年12月28日 下午9:29:02
	  
	 */
	public List<Double> Social_Status_Feature(User sourceUser,Date time){
		List<Double> feature= new ArrayList<>();
		feature.add((double) sourceUser.getStatuses_count());
		feature.add((double)sourceUser.getFollowers_count());
		feature.add(BeRetweet_count(sourceUser.getUserId(), time));
		feature.add((double) getBeAttackedNumber(sourceUser.getUserId(), time));
		feature.add(BeRely_count(sourceUser.getUserId(), time));
		return feature;
	}
	public  Social_Status_Tool() {
	}
	
	/**
	 * @param userid
	 * @param time
	 * @return 获取某个时刻前用户的微博被转发总次数
	 *@create_time：2015年1月28日上午8:46:51
	 *@modifie_time：2015年1月28日 上午8:46:51
	  
	 */
	public double BeRetweet_count(long userid,Date time){
		if (time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getUserWeibo_beRetweet_Number(userid, trainBegin,TestBegin);
		}else {
			return mongoDB_Source.getUserWeibo_beRetweet_Number(userid,trainBegin, TestBegin);
		}
		
	}
	/**
	 * @param userid
	 * @param time
	 * @return 获取某个时刻前用户的微博被转发总次数
	 *@create_time：2015年1月28日上午8:46:51
	 *@modifie_time：2015年1月28日 上午8:46:51
	  
	 */
	public double getBeAttackedNumber(long userid,Date time){
		if (time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getBeAttackedNumber(userid, trainBegin,TestBegin);
		}else {
			return mongoDB_Source.getBeAttackedNumber(userid,trainBegin, TestBegin);
		}
		
	}
	/**
	 * @param userid
	 * @param time
	 * @return 获取某个时刻前用户的mention 被回复的总次数
	 *@create_time：2015年1月28日上午8:46:51
	 *@modifie_time：2015年1月28日 上午8:46:51
	  
	 */
	public double BeRely_count(long userid,Date time){
		if (time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getBeRelyNumber(userid, trainBegin,TestBegin);
		}else {
			return mongoDB_Source.getBeRelyNumber(userid,trainBegin, TestBegin);
		}
		
	}
}
