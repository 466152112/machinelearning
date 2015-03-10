/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import bean.AvlTree;
import bean.User;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：ReadUser   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月15日 下午10:50:22   
 * @modifier：zhouge   
 * @modified_time：2014年10月15日 下午10:50:22   
 * @modified_note：   
 * @version    
 *    
 */
public class ReadUser {

	/**
	 * @param userIdFile
	 * @return
	 *@create_time：2014年10月15日下午10:50:57
	 *@modifie_time：2014年10月15日 下午10:50:57
	  
	 */
	public static AvlTree<User> getuseridFromuserId(String userIdFile) {
		AvlTree<User> result = new AvlTree<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				userIdFile));) {
			String onelineString = "";
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				User user = new User();
				user.setUserId(Long.valueOf(split[0]));
				result.insert(user);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static AvlTree<User> getuserFromprofileFile(String profileFile) {
		AvlTree<User> result = new AvlTree<>();
		String onelineString =null;
		try (FileInputStream fis = new FileInputStream(profileFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader= new BufferedReader(isr);) {
			//the first oneline is type note
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				
				User user = new User();
				user.setUserId(Long.valueOf(split[0]));
				user.setUserName(split[1]);
				user.setVerified(Integer.valueOf(split[2]));
				//id	name	verified verifiedreason	location	followers_count	friends_count	statuses_count	gende
				if(split.length>9){
					user.setLocation(split[split.length-5]);
					user.setFollowers_count(Integer.valueOf(split[split.length-4]));
					user.setFriends_count(Integer.valueOf(split[split.length-3]));
					user.setStatuses_count(Integer.valueOf(split[split.length-2]));
					user.setGender(split[split.length-1]);
				}else {
					user.setLocation(split[4]);
					user.setFollowers_count(Integer.valueOf(split[5]));
					user.setFriends_count(Integer.valueOf(split[6]));
					user.setStatuses_count(Integer.valueOf(split[7]));
					user.setGender(split[8]);
				}
				
				result.insert(user);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(onelineString);
		}
		return result;
	}
	
	public static HashMap<Long,User> getuserInMapFromprofileFile(String profileFile) {
		HashMap<Long,User> result = new HashMap();
		String onelineString =null;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				profileFile));) {
			//the first oneline is type note
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				
				User user = new User();
				user.setUserId(Long.valueOf(split[0]));
				user.setUserName(split[1]);
				user.setVerified(Integer.valueOf(split[2]));
				//id	name	verified verifiedreason	location	followers_count	friends_count	statuses_count	gende
				if(split.length>9){
					user.setLocation(split[split.length-5]);
					user.setFollowers_count(Integer.valueOf(split[split.length-4]));
					user.setFriends_count(Integer.valueOf(split[split.length-3]));
					user.setStatuses_count(Integer.valueOf(split[split.length-2]));
					user.setGender(split[split.length-1]);
				}else {
					user.setLocation(split[4]);
					user.setFollowers_count(Integer.valueOf(split[5]));
					user.setFriends_count(Integer.valueOf(split[6]));
					user.setStatuses_count(Integer.valueOf(split[7]));
					user.setGender(split[8]);
				}
				
				result.put(user.getUserId(),user);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(onelineString);
		}
		return result;
	}
	
	public static HashMap<String, Long> getuserNameAndIdMapFromprofileFile(String profileFile) {
		HashMap<String, Long> map=new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				profileFile));) {
			//the first oneline is type note
			String onelineString = bufferedReader.readLine();
			
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				//id	name	verified	location	followers_count	friends_count	statuses_count	gende
				map.put(split[1].trim(), Long.valueOf(split[0].trim()));
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static HashMap<Long, String> getuserIdAndNameMapFromprofileFile(String profileFile) {
		HashMap<Long, String> map=new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				profileFile));) {
			//the first oneline is type note
			String onelineString = bufferedReader.readLine();
			
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				//id	name	verified	location	followers_count	friends_count	statuses_count	gende
				map.put(Long.valueOf(split[0].trim()), split[1].trim());
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
