/**
 * 
 */
package Remind.extractFeaturev2.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;

import bean.User;
import Resource.mongoDB_Source;
import tool.Math.Sims;
import tool.TimeTool.CalanderUtil;
import tool.data.DenseVector;
import tool.dataStucture.AvlTree;
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
public class Social_tie_Tool extends Super_tool{
	
	AvlTree<User> userAvl=null;
	public Social_tie_Tool(){
	
	}
	
	/**
	 * @param sourceUser
	 * @return 
	 * * 计算所有的基于社交网络的用户关系特征：
	 *  1、是否是双向关注 0，不是 1是
	 *  2、TU是否关注SU
	 *  3、用户followGraph上面的RA指标
	 *  4、target user 转发source user 的次数
	 *  5、target user @ source user 的次数
	 *  6、source user 转发target user 的次数
	 *  7、source user @ target user 的次数
	 *  8、source user @ target user 成功的次数
	 *  9、source user 和目标用户共同转发同样微博次数
	 *  10、source user 和target user出现在同一个mention列表的次数
	 *  
	 *@create_time：2014年12月28日下午9:29:02
	 *@modifie_time：2014年12月28日 下午9:29:02
	  
	 */
	public List<Double> getSocial_tie_Feature(User sourceUser,User targetUser,Date time,AvlTree<User> userAvl){
		if (this.userAvl==null) {
			this.userAvl=userAvl;
		}
		List<Double> feature= new ArrayList<>();
		
		feature.add(ifBiFriend(sourceUser, targetUser)?1.0:0);
		
		feature.add(ifFollowSU(sourceUser, targetUser)?1.0:0);
		
		feature.add(SocialSim(sourceUser, targetUser));
		
		feature.add(getUser_Retweet_Count(sourceUser.getUserId(), targetUser.getUserId(), time));
		
		feature.add(getUser_Mention_Count(targetUser.getUserId(), sourceUser.getUserId(),time));
		
		feature.add(getUser_Retweet_Count( targetUser.getUserId(),sourceUser.getUserId(),time));
		
		feature.add(getUser_Mention_Count( sourceUser.getUserId(),targetUser.getUserId(),time));
		
		feature.add(getUser_Mention_Success_Count( sourceUser.getUserId(),targetUser.getUserId(),time));
		
		feature.add(getCoRetweet_count(sourceUser.getUserId(), targetUser.getUserId(),time));
		
		feature.add(getCoBeMention_count(sourceUser.getUserId(), targetUser.getUserId(),time));
		
		return feature;
	}
	
	private double getUser_Retweet_Count(long sourceUser,long targetUser,Date Time){
		if (Time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getUser_Retweet_Count(sourceUser, targetUser,trainBegin,TestBegin);
		}else {
			return mongoDB_Source.getUser_Retweet_Count(sourceUser, targetUser,trainBegin,TestBegin);
		}
	}
	
	private double getUser_Mention_Count(long sourceUser,long targetUser,Date Time){
		if (Time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getUser_Mention_Count(sourceUser, targetUser,trainBegin,TestBegin);
		}else {
			return mongoDB_Source.getUser_Mention_Count(sourceUser, targetUser,trainBegin,TestBegin);
		}
	}
	
	private double getUser_Mention_Success_Count(long sourceUser,long targetUser,Date Time){
		if (Time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getUser_Mention_Success_Count(sourceUser, targetUser,trainBegin,Time);
		}else {
			return mongoDB_Source.getUser_Mention_Success_Count(sourceUser, targetUser,trainBegin,TestBegin);
		}
	}
	
	private double getCoRetweet_count(long sourceUser,long targetUser,Date Time){
		if (Time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getCoRetweet_count(sourceUser, targetUser,trainBegin,TestBegin);
		}else {
			return mongoDB_Source.getCoRetweet_count(sourceUser, targetUser,trainBegin,TestBegin);
		}
	}
	
	private double getCoBeMention_count(long sourceUser,long targetUser,Date Time){
		if (Time.compareTo(TestBegin)<0) {
			return mongoDB_Source.getCoBemention_count(sourceUser, targetUser,trainBegin,TestBegin);
		}else {
			return mongoDB_Source.getCoBemention_count(sourceUser, targetUser,trainBegin,TestBegin);
		}
	}
	/**
	 * @param sourceUser
	 * @param targetUser
	 * @return
	 *@create_time：2015年1月28日上午9:16:14
	 *@modifie_time：2015年1月28日 上午9:16:14
	  
	 */
	private boolean ifBiFriend(long sourceUser,long targetUser){
		return mongoDB_Source.ifBifriend(sourceUser, targetUser);
	}
	
	/**
	 * @param sourceUser
	 * @param targetUser
	 * @return
	 *@create_time：2015年1月28日上午9:16:14
	 *@modifie_time：2015年1月28日 上午9:16:14
	  
	 */
	private boolean ifFollowSU(User sourceUser,User targetUser){
		if (sourceUser.getfollowee()==null) {
			sourceUser.setfollowee(mongoDB_Source.getFriend_list(sourceUser.getUserId()));
		}
		if (sourceUser.getfollowee().contains(targetUser.getUserId())) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * @param sourceUser
	 * @param targetUser
	 * @return
	 *@create_time：2015年1月28日上午9:16:14
	 *@modifie_time：2015年1月28日 上午9:16:14
	  
	 */
	private boolean ifBiFriend(User sourceUser,User targetUser){
		if (sourceUser.getfollowee()==null) {
			sourceUser.setfollowee(mongoDB_Source.getFriend_list(sourceUser.getUserId()));
		}
		if (targetUser.getfollowee()==null) {
			targetUser.setfollowee(mongoDB_Source.getFriend_list(targetUser.getUserId()));
		}
		if (sourceUser.getfollowee().contains(targetUser.getUserId())&&targetUser.getfollowee().contains(sourceUser.getUserId())) {
			return true;
		}
		return false;
	}
	/**
	 * @param sourceUser
	 * @param targetUser
	 * @return 计算用户在follow结构上的相似度
	 *  选用的是RA指标
	 *@create_time：2015年1月27日下午12:46:33
	 *@modifie_time：2015年1月27日 下午12:46:33
	  
	 */
	private double SocialSim(User sourceUser,User targetUser){
		if (sourceUser.getfollowee()==null) {
			Set<Long> u1f=mongoDB_Source.getFriend_list(sourceUser.getUserId());
			sourceUser.setfollowee(u1f);
		}
		if (targetUser.getfollowee()==null) {
			Set<Long> u2f=mongoDB_Source.getFriend_list(targetUser.getUserId());
			targetUser.setfollowee(u2f);
		}
		
		Set<Long> u1f=sourceUser.getfollowee();
		Set<Long> u2f=targetUser.getfollowee();
		return Sims.jaccard(u1f, u2f);
	}
	
	/**
	 * @param sourceUser
	 * @param targetUser
	 * @return
	 *@create_time：2015年1月28日上午9:14:14
	 *@modifie_time：2015年1月28日 上午9:14:14
	  
	 */
	private double SocialSim(long sourceUser,long targetUser){
		Set<Long> u1f=mongoDB_Source.getFriend_list(sourceUser);
		Set<Long> u2f=mongoDB_Source.getFriend_list(targetUser);
		return Sims.jaccard(u1f, u2f);
	}
	
	private Map<Long, Integer> followeeCount(long sourceUserid){
		Map<Long,Integer> sourceUserFollowee=new HashMap<Long, Integer>();
		for (long userid : mongoDB_Source.getFriend_list(sourceUserid)) {
			User tempUser=new User(userid);
			if (userAvl.contains(tempUser)) {
				tempUser=userAvl.getElement(tempUser);
				sourceUserFollowee.put(userid, tempUser.getFriends_count());
			}
		}
		return sourceUserFollowee;
	}
	
	private Map<Long, Integer> followeeCount(User sourceUser){
		Map<Long,Integer> sourceUserFollowee=new HashMap<Long, Integer>();
		for (long userid : sourceUser.getfollowee()) {
			User tempUser=new User(userid);
			if (userAvl.contains(tempUser)) {
				tempUser=userAvl.getElement(tempUser);
				sourceUserFollowee.put(userid, tempUser.getFriends_count());
			}
		}
		return sourceUserFollowee;
	}
}
