/**
 * 
 */
package Remind.staticsFinal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import tool.MyselfMath.MathCal;
import tool.dataStucture.AvlTree;
import tool.dataStucture.AvlTree.AvlNode;
import tool.drawplot.Logbin;
import tool.io.FileIO;
import weibo.util.ReadUser;
import Resource.All_Mention_txt_Source;
import Resource.FollowGraph_txt_Source;
import Resource.Intection_txt_Source;
import Resource.Stat_txt_Souce;
import Resource.User_profile_txt_Source;
import bean.User;

import com.google.common.base.Stopwatch;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：BaiscStaticInMention   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月19日 下午6:50:14   
 * @modifier：zhouge   
 * @modified_time：2015年1月19日 下午6:50:14   
 * @modified_note：   
 * @version    
 *    
 */
public class BasicStatInMention {
	static AvlTree<User> userAvl = null;
	 All_Mention_txt_Source all_Mention_txt_Source=new All_Mention_txt_Source();
	public static void main(String[] arg) {
		Stopwatch sw = Stopwatch.createStarted();
		long begin_time = sw.elapsed(TimeUnit.MILLISECONDS);
		BasicStatInMention tempMaInMention=new BasicStatInMention();
		//tempMaInMention.getDayDistri();
		User_profile_txt_Source upts=new User_profile_txt_Source();
		userAvl = upts.getAllUserInTree();
	
		tempMaInMention.getMention_User_Number_Distri();
		
		long end_Time = sw.elapsed(TimeUnit.MILLISECONDS) - begin_time;
		String time = tool.system.Dates.parse(end_Time);
		System.out.println(time);
	}
	/**
	 * 统计时间分布
	 *@create_time：2015年1月20日下午2:22:26
	 *@modifie_time：2015年1月20日 下午2:22:26
	  
	 */
	public void getDayDistri(){
		all_Mention_txt_Source.getDay_Count_Distribution(Stat_txt_Souce.getPath()+"dayDistri.txt");
	}
	/**
	 * 统计性别差别
	 *@create_time：2015年1月20日下午2:22:39
	 *@modifie_time：2015年1月20日 下午2:22:39
	  
	 */
	public void getsexDistri(){
		all_Mention_txt_Source.setUserTree(userAvl);
		all_Mention_txt_Source.get_Sex_Distribution(Stat_txt_Souce.getPath()+"sexDistri.txt");
	}
	/**
	 * 统计关注差异
	 *@create_time：2015年1月20日下午2:23:03
	 *@modifie_time：2015年1月20日 下午2:23:03
	  
	 */
	public void getRelationShip_statu(){
		userAvl=ReadUser.getFollowGraph(userAvl, FollowGraph_txt_Source.getFollowgraphFile());
		all_Mention_txt_Source.setUserTree(userAvl);
		all_Mention_txt_Source.getFollowShip_statu(Stat_txt_Souce.getPath()+"followShip_statu.txt");
	}
	/**
	 * 基本统计一个用户会mention 多少个不同的用户的分布情况
	 * @param resultFile
	 *@create_time：2015年1月20日下午2:00:57
	 *@modifie_time：2015年1月20日 下午2:00:57
	  
	 */
	public void getMention_User_Number_Distri(){
		all_Mention_txt_Source.	getMention_User_Number_Distri(Stat_txt_Souce.getPath()+"Mention_User_Number_Distri.txt");
	}
	
	/**统计不同的粉丝数量（这个粉丝数为用户profile文件中的值）下不同用户接收到的Mention个数统计
	 * 分别取 avg media 
	 *@create_time：2015年1月20日下午2:59:58
	 *@modifie_time：2015年1月20日 下午2:59:58
	  
	 */
	public void get_beMention_followee_statu(){
		//添加用户的被mention记录和rely 记录信息
		userAvl=Intection_txt_Source.add_mention_history_to_userTree(userAvl);
		
		Stack<AvlNode<User>> stack = new Stack<>();
		AvlNode<User> puser = userAvl.root;
		//如果被mention的次数为0 则不保存。
		Map<Integer, List<Double>> beMentionCountMap=new HashMap<Integer, List<Double>>();
		
		while (puser != null || !stack.isEmpty()) {
			while (puser != null) {
				User tempUser=puser.getElement();
				//获取该用户的粉丝数量
				int followerC=tempUser.getFollowers_count();
				//对于0 不给予计算
				if (tempUser.getBeAttackedNumber()!=0&&followerC!=0) {
					if(beMentionCountMap.containsKey(followerC)){
						beMentionCountMap.get(followerC).add(1.0*tempUser.getBeAttackedNumber());
					}else {
						List<Double> temp=new ArrayList<>();
						temp.add(1.0*tempUser.getBeAttackedNumber());
						beMentionCountMap.put(followerC, temp);
					}
				}
				
				stack.push(puser);
				puser = puser.left;
			}
			if (!stack.isEmpty()) {
				puser = stack.pop();
				puser = puser.right;
			}
		}
		List<String> resultList=new ArrayList<>();
		for (int followerC : beMentionCountMap.keySet()) {
			double avg=MathCal.getAverage(beMentionCountMap.get(followerC));
			double media=MathCal.getMedia(beMentionCountMap.get(followerC));
			resultList.add(followerC+"\t"+avg+"\t"+media+"\t"+0);
			//System.out.println(followerC+"\t"+avg+"\t"+media);
		}
		Logbin logbin=new Logbin();
		Map<Double, Double> logbinResult=logbin.LogBinning(beMentionCountMap, 100);
		for (Double followerC : logbinResult.keySet()) {
			resultList.add(followerC+"\t"+0+"\t"+0+"\t"+logbinResult.get(followerC));
			//System.out.println(followerC+"\t"+avg+"\t"+media);
		}
		//保存结果，不同粉丝数下，接收到的mention数量。去除了哪些完全没有接受到mention的用户
		try {
			FileIO.writeList(Stat_txt_Souce.getPath()+"beMention_followee_statu_logbin.txt", resultList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}
	
	/**统计不同的粉丝数量（这个粉丝数为用户profile文件中的值）下不同用户回复个数统计
	 * 分别取 avg media 
	 *@create_time：2015年1月20日下午2:59:58
	 *@modifie_time：2015年1月20日 下午2:59:58
	  
	 */
	public void get_Reply_followee_statu(){
		//添加用户的被mention记录和rely 记录信息
		userAvl=Intection_txt_Source.add_mention_history_to_userTree(userAvl);
		
		Stack<AvlNode<User>> stack = new Stack<>();
		AvlNode<User> puser = userAvl.root;
		//如果被mention的次数为0 则不保存。
		Map<Integer, List<Double>> beMentionCountMap=new HashMap<Integer, List<Double>>();
		
		while (puser != null || !stack.isEmpty()) {
			while (puser != null) {
				User tempUser=puser.getElement();
				//获取该用户的粉丝数量
				int followerC=tempUser.getFollowers_count();
				//对于0 不给予计算
				if (tempUser.getRelyNumber()!=0&&followerC!=0) {
					if(beMentionCountMap.containsKey(followerC)){
						beMentionCountMap.get(followerC).add(1.0*tempUser.getRelyNumber());
					}else {
						List<Double> temp=new ArrayList<>();
						temp.add(1.0*tempUser.getRelyNumber());
						beMentionCountMap.put(followerC, temp);
					}
				}
				stack.push(puser);
				puser = puser.left;
			}
			if (!stack.isEmpty()) {
				puser = stack.pop();
				puser = puser.right;
			}
		}
		List<String> resultList=new ArrayList<>();
		for (int followerC : beMentionCountMap.keySet()) {
			double avg=MathCal.getAverage(beMentionCountMap.get(followerC));
			double media=MathCal.getMedia(beMentionCountMap.get(followerC));
			resultList.add(followerC+"\t"+avg+"\t"+media+"\t"+0);
			//System.out.println(followerC+"\t"+avg+"\t"+media);
		}
		Logbin logbin=new Logbin();
		Map<Double, Double> logbinResult=logbin.LogBinning(beMentionCountMap, 100);
		for (Double followerC : logbinResult.keySet()) {
			resultList.add(followerC+"\t"+0+"\t"+0+"\t"+logbinResult.get(followerC));
			//System.out.println(followerC+"\t"+avg+"\t"+media);
		}
		//保存结果，不同粉丝数下，接收到的mention数量。去除了哪些完全没有接受到mention的用户
		try {
			FileIO.writeList(Stat_txt_Souce.getPath()+"Reply_followee_statu.txt", resultList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}
	
	/**统计不同的粉丝数量（这个粉丝数为用户profile文件中的值）下不同用户接收到的回复率
	 * 分别取 avg media logbin 
	 *@create_time：2015年1月20日下午2:59:58
	 *@modifie_time：2015年1月20日 下午2:59:58
	  
	 */
	public void get_ByReply_followee_statu(){
		//添加用户的被mention记录和rely 记录信息
		userAvl=Intection_txt_Source.add_mention_history_to_userTree(userAvl);
		
		Stack<AvlNode<User>> stack = new Stack<>();
		AvlNode<User> puser = userAvl.root;
		//如果被mention的次数为0 则不保存。
		Map<Integer, List<Double>> beMentionCountMap=new HashMap<Integer, List<Double>>();
		
		while (puser != null || !stack.isEmpty()) {
			while (puser != null) {
				User tempUser=puser.getElement();
				//获取该用户的粉丝数量
				int followerC=tempUser.getFollowers_count();
				//对于0 不给予计算
				if (tempUser.getAttackNumber()!=0&&followerC!=0) {
					if(beMentionCountMap.containsKey(followerC)){
						beMentionCountMap.get(followerC).add(1.0*tempUser.getBeRelyNumber()/tempUser.getAttackNumber());
					}else {
						List<Double> temp=new ArrayList<>();
						temp.add(1.0*tempUser.getBeRelyNumber()/tempUser.getAttackNumber());
						beMentionCountMap.put(followerC, temp);
					}
				}
				stack.push(puser);
				puser = puser.left;
			}
			if (!stack.isEmpty()) {
				puser = stack.pop();
				puser = puser.right;
			}
		}
		List<String> resultList=new ArrayList<>();
		for (int followerC : beMentionCountMap.keySet()) {
			double avg=MathCal.getAverage(beMentionCountMap.get(followerC));
			double media=MathCal.getMedia(beMentionCountMap.get(followerC));
			resultList.add(followerC+"\t"+avg+"\t"+media+"\t"+0);
			//System.out.println(followerC+"\t"+avg+"\t"+media);
		}
		Logbin logbin=new Logbin();
		Map<Double, Double> logbinResult=logbin.LogBinning(beMentionCountMap, 100);
		for (Double followerC : logbinResult.keySet()) {
			resultList.add(followerC+"\t"+0+"\t"+0+"\t"+logbinResult.get(followerC));
			//System.out.println(followerC+"\t"+avg+"\t"+media);
		}
		try {
			FileIO.writeList(Stat_txt_Souce.getPath()+"BeReplyRatio_followee_statu.txt", resultList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}
}
