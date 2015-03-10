/**
 * 
 */
package Remind.extractFeature.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;

import bean.User;
import Resource.mongoDB_Source;
import tool.TimeTool.CalanderUtil;
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
public class Social_tie_Tool {
	
	CalanderUtil calanderUtil=new CalanderUtil();
	mongoDB_Source mongoDB_Source=new mongoDB_Source();
	
	public Social_tie_Tool(){
	}
	
	/**
	 * @param sourceUser
	 * @return 
	 * * 计算所有的基于社交网络的用户关系特征：
	 *  1、是否是双向关注 0，不是 1是
	 *  2、target user 转发source user 的次数
	 *  3、target user @ source user 的次数
	 *  4、source user转发target user 的次数
	 *  5、source user @target user 的次数
	 *  6、source user 和目标用户共同转发同样微博次数
	 *@create_time：2014年12月28日下午9:29:02
	 *@modifie_time：2014年12月28日 下午9:29:02
	  
	 */
	public List<Double> getSocial_tie_Feature(User sourceUser,User targetUser,String time){
		Date createTime=calanderUtil.getDate(time);
		List<Double> feature= new ArrayList<>();
		feature.add(ifBiFriend(sourceUser, targetUser)?1.0:0);
		feature.add((double) mongoDB_Source.getUser_Retweet_Count(sourceUser.getUserId(), targetUser.getUserId(),createTime));
		feature.add((double) mongoDB_Source.getUser_Mention_Count(targetUser.getUserId(), sourceUser.getUserId(),createTime));
		feature.add((double) mongoDB_Source.getUser_Retweet_Count( targetUser.getUserId(),sourceUser.getUserId(),createTime));
		feature.add((double) mongoDB_Source.getUser_Mention_Count( sourceUser.getUserId(),targetUser.getUserId(),createTime));
		feature.add(mongoDB_Source.getCoRetweet_count(sourceUser.getUserId(), targetUser.getUserId(),createTime));
		return feature;
	}
	
	public boolean ifBiFriend(User sourceUser,User targetUser){
		if (sourceUser.getfollowee().contains(targetUser.getUserId())&&targetUser.getfollowee().contains(sourceUser.getUserId())) {
			return true;
		}
		return false;
	}
	
}
