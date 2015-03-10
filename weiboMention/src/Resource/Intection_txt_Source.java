package Resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.OnePairTweet;
import bean.User;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import tool.io.FileIO;
import weibo.util.ReadUser;
import Resource.superClass.superResource;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：Mention_Source   
 * @class_describe：   
 * @creator：鸽   
 * @create_time：2014年12月23日 下午8:14:04   
 * @modifier：鸽   
 * @modified_time：2014年12月23日 下午8:14:04   
 * @modified_note：   
 * @version    
 *    
 */
public class Intection_txt_Source extends superResource{
	
	final static String path=work_path+"intection/";
	/* target weibo content database */
	//final static String targetWeibo_file=path+"targetWeibo.txt";
	
	/*All_Mention_History file*/
	final static String All_Mention_History_file=path+"All_Mention_History.txt";
	
	/*need_Mention_History file*/
	final static String need_Mention_History_file=path+"need_Mention_History.txt";
	
	final static String userId_Beretweet_count_File=path+"userId_beRetweet_Number.txt";
	
	final static String userId_mention_beMention_reply_beReply_count=path+"userId_mention_beMention_reply_beReply_count.txt";
	
	final static String need_weibo_id_content=path+"need_weibo_id_content.txt";
	public Intection_txt_Source(){
		super();
	}
	/**
	 * @return the path
	 */
	public static String getPath() {
		return path;
	}

	
	/**
	 * @return the allMentionHistoryFile
	 */
	public static String getAllMentionHistoryFile() {
		return All_Mention_History_file;
	}
	
	/**
	 * @return the needMentionHistoryFile
	 */
	private static String getNeedMentionHistoryFile() {
		return need_Mention_History_file;
	}

	/**
	 * @return the useridBeretweetNumberFile
	 */
	private static String getUseridBeretweetNumberFile() {
		return userId_Beretweet_count_File;
	}
	
	public static Map<Long, String> getneedWeiboContent(){
		BufferedReader temp=null;
		try {
			temp = FileIO.getReader(need_weibo_id_content);
			String oneLine=null;
			Map<Long, String> map=new HashMap<Long, String>();
			try {
				while((oneLine=temp.readLine())!=null){
					OnePairTweet tweet=OnePairTweet.covert(oneLine);
					map.put(tweet.getWeiboId(), tweet.getContent());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return map;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @return the useridBeretweetNumberFile
	 */
	public static AvlTree<User> add_beRetweet_history_to_userTree(AvlTree<User> userAvl) {
		return ReadUser.Add_Beretweet_Count_to_user(userAvl,
				userId_Beretweet_count_File);
	}
	/**
	 * @return  add mention history 
	 */
	public static AvlTree<User> add_mention_history_to_userTree(AvlTree<User> userAvl) {
		return ReadUser.add_Mention_Statistics_Record_to_user(userAvl,userId_mention_beMention_reply_beReply_count);
	}
	/**
	 * @return the useridMentionBementionReplyBereplyCount
	 */
	public static String getUseridMentionBementionReplyBereplyCount() {
		return userId_mention_beMention_reply_beReply_count;
	}
	
	public BufferedReader  getAllMention(){
		BufferedReader temp=null;
		try {
			temp = FileIO.getReader(All_Mention_History_file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	public BufferedReader  getNeedMention(){
		BufferedReader temp=null;
		try {
			temp = FileIO.getReader(need_Mention_History_file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	/**
	 * 
	 * 当不存在用户和被转发次数统计时调用，并写人文件中
	 * 
	 * @create_time：2015年1月7日下午4:10:01
	 * 
	 * @modifie_time：2015年1月7日 下午4:10:01
	 */

	private void load_user_beretweet_from_db() {
		User_profile_txt_Source user_profile_txt_Source=new User_profile_txt_Source();
		List<String> result = new ArrayList<>();
		Set<Long> all_user_id_set=user_profile_txt_Source.get_all_user_id_inSet();
		if (all_user_id_set==null) {
			all_user_id_set=user_profile_txt_Source.get_all_user_id_inSet();
		}
		mongoDB_Source mongoDB_Source=new mongoDB_Source();
		for (long userid : all_user_id_set) {

			int beRetweetNumber = mongoDB_Source.getUserWeibo_beRetweet_Number(userid);

			// userAvl.getElement(new User(userid)).setBeRetweetNumber(

			// beRetweetNumber);

			result.add(userid + "\t" + beRetweetNumber);

		}

		WriteUtil<String> writeUtil = new WriteUtil<>();

		writeUtil.writeList(result, "userId_beretweetNumber");

		System.out.println("/从数据库中添加用户被转发微博量 ok");

	}
}
