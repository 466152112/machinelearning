/**
 * 
 */
package Remind.extractFeature.tool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.User;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：User_profile_Feature   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月15日 上午10:49:29   
 * @modifier：zhouge   
 * @modified_time：2015年1月15日 上午10:49:29   
 * @modified_note：   
 * @version    
 *    
 */
public class User_profile_Tool {
	/**
	 * @param sourceUser
	 * @return 
	 * * 计算所有用户属性的特征：
	 *  1、source user 是否是男人 1 ：0
	 *  2、target user 是否是男人 1 ：0
	 *  3、 source 用户是否是验证用户
	 *@create_time：2014年12月28日下午9:29:02
	 *@modifie_time：2014年12月28日 下午9:29:02
	  
	 */
	public List<Double> getUser_profile_Feature(User sourceUser,User targetUser){
		List<Double> feature= new ArrayList<>();
		feature.add((double) (sourceUser.getGender().equals("m")?1:0));
		feature.add((double) (targetUser.getGender().equals("m")?1:0));
		feature.add((double)sourceUser.getVerified());
		return feature;
	}
}
