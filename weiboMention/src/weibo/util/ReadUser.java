/**
 * 
 */
package weibo.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tool.FileTool.ReadUtil;
import tool.dataStucture.AvlTree;
import Resource.User_profile_txt_Source;
import bean.User;
import bean.Remind.RemType;
import bean.Remind.Reminder;

/**   
 *    
 * @progject_name��weiboNew   
 * @class_name��ReadUser   
 * @class_describe��   
 * @creator��zhouge   
 * @create_time��2014��10��15�� ����10:50:22   
 * @modifier��zhouge   
 * @modified_time��2014��10��15�� ����10:50:22   
 * @modified_note��   
 * @version    
 *    
 */
public class ReadUser{

	/**
	 * @param userIdFile
	 * @return
	 *@create_time��2014��10��15������10:50:57
	 *@modifie_time��2014��10��15�� ����10:50:57
	  
	 */
	public static AvlTree<User> getuseridFromuserId(String userIdFile) {
		AvlTree<User> result = new AvlTree<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				userIdFile));) {
			String onelineString = "";
			Set<Long> spamuserId=User_profile_txt_Source.getSpamUserId();
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				User user = new User();
				user.setUserId(Long.valueOf(split[0]));
				if (spamuserId.contains(user.getUserId())) {
					continue;
				}
				result.insert(user);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static User  convertTo_User(String onelineString){
		String[] split = onelineString.split("\t");
		User user = new User();
		user.setUserId(Long.valueOf(split[0]));
		user.setUserName(split[1]);
		user.setVerified(Integer.valueOf(split[2]));
		//id	name	verified verifiedreason	location	followers_count	friends_count	statuses_count	gende
		if(split.length>9){
			//截取属性
			int beginindex=onelineString.indexOf(split[1])+split[1].length()+3;
			int endIndex=onelineString.indexOf(split[split.length-5])-1;
			user.setBiography(onelineString.substring(beginindex, endIndex));
			user.setLocation(split[split.length-5]);
			user.setFollowers_count(Integer.valueOf(split[split.length-4]));
			user.setFriends_count(Integer.valueOf(split[split.length-3]));
			user.setStatuses_count(Integer.valueOf(split[split.length-2]));
			user.setGender(split[split.length-1]);
		}else {
			user.setBiography(split[3]);
			user.setLocation(split[4]);
			user.setFollowers_count(Integer.valueOf(split[5]));
			user.setFriends_count(Integer.valueOf(split[6]));
			user.setStatuses_count(Integer.valueOf(split[7]));
			user.setGender(split[8]);
		}
		return user;
	}
	
	/**
	 * @param useravl
	 * @param MentionRecordFile
	 * @return and mention record to user 
	 *@create_time：2014年12月28日下午8:56:28
	 *@modifie_time：2014年12月28日 下午8:56:28
	  
	 */
	public  static AvlTree<User> add_Mention_Record(AvlTree<User> useravl,String MentionRecordFile){
		try (BufferedReader reader = new BufferedReader(new FileReader(
				MentionRecordFile))) {
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				RemType remType = RemType.covert(oneLine);
				List<Reminder> listrem = remType.getReminders();
				long rootuserid=remType.getRoot().getUserId();
				User rootuser=new User();
				rootuser.setUserId(rootuserid);
				rootuser=useravl.getElement(rootuser);
				for (Reminder reminder : listrem) {
					User targetuser = new User();
					targetuser.setUserId(reminder.getUserId());
					targetuser = useravl.getElement(targetuser);
					if (targetuser!=null) {
						useravl.getElement(targetuser).setBeAttackedNumber(targetuser.getBeAttackedNumber()+1);
						if (reminder.isIfRely()) {
							useravl.getElement(targetuser).setRelyNumber(targetuser.getRelyNumber()+1);
						}
					}
					if (rootuser!=null) {
						useravl.getElement(rootuser).setAttackNumber(rootuser.getAttackNumber()+1);
						if (reminder.isIfRely()) {
							useravl.getElement(rootuser).setBeRelyNumber(rootuser.getBeRelyNumber()+1);
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return useravl;
	}
	
	public  static AvlTree<User> add_Mention_Statistics_Record_to_user(AvlTree<User> useravl,String MentionRecordFile){
		try (BufferedReader reader = new BufferedReader(new FileReader(
				MentionRecordFile))) {
			String oneLine= reader.readLine();
			while ((oneLine = reader.readLine()) != null) {
				String[] splitS=oneLine.split("\t");
				//Id name	mentionNumber	BeMentionNumber	RelyNumber	BeRelyNumber	
				long userid=Long.valueOf(splitS[0]);
				User user=new User(userid);
				if (useravl.contains(user)) {
					useravl.getElement(user).setAttackNumber(Integer.valueOf(splitS[2]));
					useravl.getElement(user).setBeAttackedNumber(Integer.valueOf(splitS[3]));
					useravl.getElement(user).setRelyNumber(Integer.valueOf(splitS[4]));
					useravl.getElement(user).setBeRelyNumber(Integer.valueOf(splitS[5]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return useravl;
	}
	/**
	 * @param profileFile
	 * @param userId_file
	 * @return 获取有限的用户信息
	 *@create_time：2014年12月24日下午3:14:51
	 *@modifie_time：2014年12月24日 下午3:14:51
	  
	 */
	public static AvlTree<User> getuserFromprofileFile_limit_by_userId_file(String profileFile,String userId_file) {
		String onelineString =null;
		AvlTree<User> result = new AvlTree<>();
		Set<Long> setuserid=new HashSet<>();
		Set<Long> spamuserId=User_profile_txt_Source.getSpamUserId();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				userId_file));) {
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				setuserid.add(Long.valueOf(split[0]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setuserid.removeAll(spamuserId);
		try (FileInputStream fis = new FileInputStream(profileFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader= new BufferedReader(isr);) {
			//the first oneline is type note
			while ((onelineString = bufferedReader.readLine()) != null) {
				User user=convertTo_User(onelineString);
				if (setuserid.contains(user.getUserId())) {
					result.insert(user);
				}
				
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(onelineString);
		}
		return result;
	}
	public static AvlTree<User> getuserFromprofileFile(String profileFile) {
		AvlTree<User> result = new AvlTree<>();
		String onelineString =null;
		Set<Long> spamuserId=User_profile_txt_Source.getSpamUserId();
		try (FileInputStream fis = new FileInputStream(profileFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader= new BufferedReader(isr);) {
			//the first oneline is type note
			while ((onelineString = bufferedReader.readLine()) != null) {
				User user=convertTo_User(onelineString);
				if(spamuserId.contains(user.getUserId()))
					continue;
				result.insert(user);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(onelineString);
		}
		return result;
	}
	
	public static AvlTree<User> Add_interest_to_user(AvlTree<User> avlUser,String interestFile) {
		
		String oneline =null;
		try (FileInputStream fis = new FileInputStream(interestFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader= new BufferedReader(isr);) {
			//the first oneline is type note
			while ((oneline = bufferedReader.readLine()) != null) {
				String[] split = oneline.split("\t");
				long userId=Long.valueOf(split[0]);
				User temp=new User();
				temp.setUserId(userId);
				if (avlUser.contains(temp)) {
					avlUser.getElement(temp).setInterest_feature(oneline);
				}
			}
			return avlUser;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(oneline);
		}
		return avlUser;
	}
	
	public static AvlTree<User> Add_Beretweet_Count_to_user(AvlTree<User> avlUser,String retweetFile) {
		
		String oneline =null;
		try (FileInputStream fis = new FileInputStream(retweetFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader= new BufferedReader(isr);) {
			//the first oneline is type note
			while ((oneline = bufferedReader.readLine()) != null) {
				String[] split = oneline.split("\t");
				long userId=Long.valueOf(split[0]);
				User temp=new User();
				temp.setUserId(userId);
				if (avlUser.contains(temp)) {
					avlUser.getElement(temp).setBeRetweetNumber(Integer.valueOf(split[1]));
				}
			}
			return avlUser;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(oneline);
		}
		return avlUser;
	}
	
	public static HashMap<Long,User> getuserInMapFromprofileFile(String profileFile) {
		HashMap<Long,User> result = new HashMap();
		String onelineString =null;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				profileFile));) {
			//the first oneline is type note
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				User user = convertTo_User(onelineString);
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
			Set<Long> spamuserId=User_profile_txt_Source.getSpamUserId();
			while ((onelineString = bufferedReader.readLine()) != null) {
				String[] split = onelineString.split("\t");
				//id	name	verified	location	followers_count	friends_count	statuses_count	gende
				long userid=Long.valueOf(split[0].trim());
				if (!spamuserId.contains(userid)) {
					map.put(split[1].trim(), userid);
				}
				
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
	
	
	/**
	 * @param avlUser
	 * @param all_user_id_set
	 * @param followGraphFileName
	 * @return 添加follow 到 avluser 中去
	 *@create_time：2014年12月29日下午1:42:13
	 *@modifie_time：2014年12月29日 下午1:42:13
	  
	 */
	public static AvlTree<User> getFollowGraph(AvlTree<User> avlUser,Set<Long> all_user_id_set,String followGraphFileName) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				followGraphFileName))) {
			String OneLine;
			while ((OneLine = bufferedReader.readLine()) != null) {
				if (OneLine.equals("")) {
					continue;
				}
				String[] split = OneLine.split("\t");
				long user1 = Long.valueOf(split[0].trim());
				Set<Long> friendList=new HashSet<Long>();
				for (int i = 1; i < split.length; i++) {
					long user2 =Long.valueOf(split[i].trim());
					if (user1==user2||!all_user_id_set.contains(user1)||!all_user_id_set.contains(user2)) {
						continue;
					}
					friendList.add(user2);
				}
				User user=new User(user1);
				avlUser.getElement(user).setfollowee(friendList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return avlUser;
	}

/**
 * @param avlUser
 * @param followGraphFileName
 * @return 添加follow 到 avluser 中去
 *@create_time：2014年12月29日下午1:42:13
 *@modifie_time：2014年12月29日 下午1:42:13
  
 */
public static AvlTree<User> getFollowGraph(AvlTree<User> avlUser,String followGraphFileName) {
	try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
			followGraphFileName))) {
		String OneLine;
		while ((OneLine = bufferedReader.readLine()) != null) {
			if (OneLine.equals("")) {
				continue;
			}
			String[] split = OneLine.split("\t");
			long user1 = Long.valueOf(split[0].trim());
			Set<Long> friendList=new HashSet<Long>();
			for (int i = 1; i < split.length; i++) {
				long user2 =Long.valueOf(split[i].trim());
				if (user1==user2) {
					continue;
				}
				friendList.add(user2);
			}
			User user=new User(user1);
			if (avlUser.contains(user)) {
				avlUser.getElement(user).setfollowee(friendList);
			}
			
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return avlUser;
}
}
