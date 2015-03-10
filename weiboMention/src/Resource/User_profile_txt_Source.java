/**
 * 
 */
package Resource;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.derby.authentication.UserAuthenticator;

import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import tool.io.FileIO;
import weibo.util.ReadUser;
import bean.User;
import Resource.superClass.superResource;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：User_Profile
 * @class_describe：
 * @creator：鸽
 * @create_time：2014年12月23日 下午8:20:37
 * @modifier：鸽
 * @modified_time：2014年12月23日 下午8:20:37
 * @modified_note：
 * @version
 * 
 */
public class User_profile_txt_Source extends superResource {
	final static String path = work_path + "userid/";

	/* profile_file */
	final static String profile_file = path + "profile.txt";

	/* all_user_need_file */
	final static String all_user_need_file = path + "all_user_need.txt";
	/* mentionuserId_file */
	final static String mentionuserId_file = path
			+ "needuser_beMention50_rely10.txt";
	/* followgraph */
	final static String followGraph_file = path + "FollowGraph.txt";
	private final static String spamuseridFile = work_path + "userid/spamuserid.txt";
	private static Set<Long> spamUserId=new HashSet<>();
	
	private static Map<String, Long> usernameAndIdMap = null;
	
	static{
		try {
			Set<String> temp=FileIO.readAsSet(spamuseridFile);
			for (String oneString : temp) {
				spamUserId.add(Long.valueOf(oneString.trim()));
			}
			usernameAndIdMap = ReadUser
					.getuserNameAndIdMapFromprofileFile(profile_file);
			Set<String> spam = new HashSet<>();
			for (String username : usernameAndIdMap.keySet()) {
				if (spamUserId.contains(usernameAndIdMap.get(username))) {
					spam.add(username);
				}
			}
			for (String name : spam) {
				usernameAndIdMap.remove(name);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public User_profile_txt_Source(){
		super();
	}
	/**
	 * @return the profileFile
	 */
	public static String getProfileFile() {
		return profile_file;
	}

	/**
	 * @return the allUserNeedFile
	 */
	public static String getAllUserNeedFile() {
		return all_user_need_file;
	}

	/**
	 * @return the spamUserId
	 */
	public static Set<Long> getSpamUserId() {
		return spamUserId;
	}
	/**
	 * @return the path
	 */
	public static String getPath() {
		return path;
	}

	/**
	 * @return the mentionuseridFile
	 */
	public static String getMentionuseridFile() {
		return mentionuserId_file;
	}

	/**
	 * @return the followgraphFile
	 */
	public static String getFollowgraphFile() {
		return followGraph_file;
	}
	public  AvlTree<User> addFollowGraph(AvlTree<User> userAvl){
		return  ReadUser.getFollowGraph(userAvl, all_user_id_set,
					User_profile_txt_Source.getFollowgraphFile());
	}
	public Map<String, Long> getUserNameAndIdMap(){
			return usernameAndIdMap;
	}

	// function
	private Set<Long> target_user_id_set = null;

	private List<Long> target_user_id_list = null;
	
	private Map<Long, Integer> target_user_id_Index_map= null;
	
	private Set<Long> all_user_id_set = null;

	/**
	 * @return
	 *@create_time：2015年1月14日上午11:00:04
	 *@modifie_time：2015年1月14日 上午11:00:04
	  
	 */
	public AvlTree<User> getAllUserInTree() {
		return ReadUser.getuserFromprofileFile(profile_file);
	}
	
	public Map<Long, User> getUserInMapFromProfile(){
		Map<Long, User> tempMap=ReadUser.getuserInMapFromprofileFile(profile_file);
		for (long spam : spamUserId) {
			tempMap.remove(spam);
		}
		return tempMap;
	}
	public Map<Long, String> getuserIdAndName(){
		Map<Long, String> tempMap=ReadUser.getuserIdAndNameMapFromprofileFile(profile_file);
		for (long spam : spamUserId) {
			tempMap.remove(spam);
		}
		return tempMap;
	}
	/**
	 * @return
	 *@create_time：2015年1月14日上午11:00:02
	 *@modifie_time：2015年1月14日 上午11:00:02
	  
	 */
	public AvlTree<User> getLimitUserInTree_limit_by_all_need_file(){
			return ReadUser.getuserFromprofileFile_limit_by_userId_file(
				profile_file, all_user_need_file);
	}
	/**
	 * @return
	 *@create_time：2015年1月14日上午10:56:21
	 *@modifie_time：2015年1月14日 上午10:56:21
	  
	 */
	public Set<Long> get_all_user_id_inSet() {
		
		if (all_user_id_set == null) {
			List<String> tempList = tempReadUtil
					.readFileByLine(all_user_need_file);

			all_user_id_set = new HashSet<>();
			for (String oneLine : tempList) {
				String[] split = oneLine.split("\t");
				all_user_id_set.add(Long.valueOf(split[0]));
			}
			all_user_id_set.removeAll(spamUserId);
			return all_user_id_set;
		} else {
			return all_user_id_set;
		}

	}

	/**
	 * @return
	 * @create_time：2015年1月14日上午10:52:37
	 * @modifie_time：2015年1月14日 上午10:52:37
	 */
	public Set<Long> get_target_User_InSet() {
		if (target_user_id_set == null) {
			List<String> tempList = super.tempReadUtil
					.readFileByLine(mentionuserId_file);
			target_user_id_set = new HashSet<>();
			for (String oneLine : tempList) {
				String[] split = oneLine.split("\t");
				target_user_id_set.add(Long.valueOf(split[0]));
			}
			target_user_id_set.removeAll(spamUserId);
			target_user_id_list = new ArrayList<>(target_user_id_set);
			return target_user_id_set;
		} else {
			return target_user_id_set;
		}
	}

	public List<Long> get_target_User_InList() {
		if (target_user_id_list == null) {
			get_target_User_InSet();
			return target_user_id_list;
		} else {
			return target_user_id_list;
		}
	}
	
	/**
	 * @return
	 * user id and index where index begin in 1
	 *@create_time：2015年1月15日下午2:59:41
	 *@modifie_time：2015年1月15日 下午2:59:41
	  
	 */
	public Map<Long,Integer> get_target_User_InMap() {
		if (target_user_id_Index_map == null) {
			get_target_User_InList();
			target_user_id_Index_map=new HashMap<>();
			for (int i = 0; i < target_user_id_list.size(); i++) {
				target_user_id_Index_map.put(target_user_id_list.get(i),i+1);
			}
			return target_user_id_Index_map;
		} else {
			return target_user_id_Index_map;
		}
	}
	
	/**
	 * 从数据库中添加用户的关注信息，并且写人文件中，只调用一次
	 * @create_time：2015年1月7日下午4:11:58
	 * @modifie_time：2015年1月7日 下午4:11:58
	 */

	private void load_followee_from_db() {
		WriteUtil<String> writeUtil=new WriteUtil<>();
		// 添加关注对象
		List<String> tempList = new ArrayList<>();
		mongoDB_Source mongoDB_Source=new mongoDB_Source();
		for (long userid : all_user_id_set) {
			
			Set<Long> friendSet = mongoDB_Source.getFriend_list(userid);

			friendSet.retainAll(all_user_id_set);

			// userAvl.getElement(new User(userid)).setfollowee(friendSet);

			StringBuffer Buffer = new StringBuffer();

			Buffer.append(userid + "\t");

			for (Long long1 : friendSet) {

				Buffer.append(long1 + "\t");

			}

			tempList.add(Buffer.toString());

			if (tempList.size() > 10000) {

				writeUtil.writeList(tempList,
						User_profile_txt_Source.getFollowgraphFile());

				tempList = new ArrayList<>();

			}

		}

		writeUtil.writeList(tempList,
				User_profile_txt_Source.getFollowgraphFile());

		System.out.println("//添加关注对象 ok");
	}
}
