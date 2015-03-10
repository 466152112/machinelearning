/**
 * 
 */
package Remind.extractFeature.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBObject;

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
public class Social_Status_Tool {
	
	
	/**
	 * @param sourceUser
	 * @return 
	 * * 计算所有的基于社交网络的用户影响力特征：
	 *  1、用户历史微博数量
	 *  2、用户的粉丝数
	 *  3、source user 的微博平均转发次数
	 *  4、source user 的微博回复率 
	 * 	5、source user 被@的次数 
	 *@create_time：2014年12月28日下午9:29:02
	 *@modifie_time：2014年12月28日 下午9:29:02
	  
	 */
	public List<Double> Social_Status_Feature(User sourceUser){
		List<Double> feature= new ArrayList<>();
		feature.add((double) sourceUser.getWeiboNumber());
		feature.add((double)sourceUser.getFollowers_count());
		feature.add((double) sourceUser.getBeRetweetNumber());
		feature.add((sourceUser.getBeRelyNumber()*1.0)/sourceUser.getAttackNumber());
		feature.add((double) sourceUser.getBeAttackedNumber());
		return feature;
	}
}
