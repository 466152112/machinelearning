/**
 * 
 */
package Remind.preproccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



import bean.User;
import Remind.util.BaseUitl;
import Resource.Intection_txt_Source;
import Resource.User_profile_txt_Source;
import Resource.data_Path;
import Resource.word2vec_txt_Source;
import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import weibo.util.ReadUser;

/**
 * 明星用户的 mention 列表推荐
 * 
 * @progject_name：weiboMention
 * @class_name：MentionListRecom
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年12月15日 下午6:26:07
 * @modifier：zhouge
 * @modified_time：2014年12月15日 下午6:26:07
 * @modified_note：
 * @version
 * 
 */
public class MentionListRecom {

	List<String> intectionRecord = new ArrayList<>();
	public static void main(String[] srg) {
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		
//		 HashMap<String, Long> usernameAndIdMap = null;
//		 usernameAndIdMap = ReadUser
//		 .getuserNameAndIdMapFromprofileFile(profileFile);
//		AvlTree<User> useravl = ReadUser.getuserFromprofileFile_limit_by_userId_file(profileFile, all_user_id_name_file);
//		useravl=ReadUser.Add_interest_to_user(useravl, word2vec_txt_Source.getUseridWordCountFile());
//		System.out.println(useravl.root.getHeight());
		MentionListRecom main = new MentionListRecom();
		main.get_all_user_id_name();
	}

	public void get_target_user_id_name() {
		BaseUitl baseUitl = new BaseUitl();
		Map<Long, User> usermap =new User_profile_txt_Source().getUserInMapFromProfile();
		Set<String> topKUserId = baseUitl.get_Top_TargetUser_FromAllMention(
				usermap, Intection_txt_Source.getAllMentionHistoryFile(), 100, 10);
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(topKUserId,User_profile_txt_Source.getMentionuseridFile());
		System.out.println(topKUserId.size());
	}

	
	/**
	 * 获取需要的微博id
	 *@create_time：2014年12月24日下午3:05:03
	 *@modifie_time：2014年12月24日 下午3:05:03
	  
	 */
	public void get_all_user_id_name() {
		BaseUitl baseUitl = new BaseUitl();
		Map<Long, String> useridAndNameMap = new User_profile_txt_Source().getuserIdAndName();
		
		//获取topK_mention_userid
		Set<Long> topk_Mention_userId=baseUitl.get_target_userId(User_profile_txt_Source.getMentionuseridFile());
		//获取发布者信息
		Set<Long> Publisher_userId=baseUitl.get_Publisher_limit_by_target_user_up_down(Intection_txt_Source.getAllMentionHistoryFile(), 100000, 1,topk_Mention_userId);
		System.out.println(Publisher_userId.size());
		Publisher_userId.addAll(topk_Mention_userId);
		Set<Long> spammperset=User_profile_txt_Source.getSpamUserId();
		
		Publisher_userId.remove(spammperset);
		List<String> need_userId_list=new ArrayList();
		for (Long userid : Publisher_userId) {
			need_userId_list.add(userid+"\t"+useridAndNameMap.get(userid));
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(need_userId_list, User_profile_txt_Source.getAllUserNeedFile());
		
		System.out.println(Publisher_userId.size());
		
	}

}
