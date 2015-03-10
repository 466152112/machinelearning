package Remind.extractFeaturev2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.rules.TemporaryFolder;

import com.google.common.base.Stopwatch;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import tool.FileTool.WriteUtil;
import tool.TimeTool.CalanderUtil;
import tool.data.DenseVector;
import tool.dataStucture.AvlTree;
import tool.io.FileIO;
import tool.io.Strings;
import Remind.extractFeaturev2.tool.Content_Tool;
import Remind.extractFeaturev2.tool.Feature_Covert;
import Remind.extractFeaturev2.tool.Social_Status_Tool;
import Remind.extractFeaturev2.tool.Social_tie_Tool;
import Remind.extractFeaturev2.tool.User_profile_Tool;
import Resource.LTR_Type_File;
import Resource.Intection_txt_Source;
import Resource.User_profile_txt_Source;
import Resource.mongoDB_Source;
import bean.User;

public class ChangeMentionTable {

	mongoDB_Source mongoDB_Source = new mongoDB_Source();
	public static void main(String[] arg) {

		Stopwatch sw = Stopwatch.createStarted();

		ChangeMentionTable main = new ChangeMentionTable();

		long begin_time = sw.elapsed(TimeUnit.MILLISECONDS);
		// 加载用户信息
		main.moveFollowGraph();

		long end_Time = sw.elapsed(TimeUnit.MILLISECONDS) - begin_time;
		String time = tool.system.Dates.parse(end_Time);
		System.out.println(time);

	}

	public void moveFollowGraph(){
		User_profile_txt_Source user_profile_txt_Source = new User_profile_txt_Source();
		Set<Long> allUser = user_profile_txt_Source.get_all_user_id_inSet();
		for (Long userid : allUser) {
			BasicDBObject select =new BasicDBObject();
			select.put("_id", userid);
			select=mongoDB_Source.getSocialGraph().findOne(select);
			//mongoDB_Source.getsocialgraph().save(select);
		}
	}
	/**
	 * 
	 * @throws Exception
	 * @create_time：2014年12月24日下午6:24:47
	 * @modifie_time：2014年12月24日 下午6:24:47
	 */

	public void Load_mention_weibo_cotent() {
			
			DBCursor cursor=mongoDB_Source.getIntection().find();
			CalanderUtil calanderUtil = new CalanderUtil();
			int count=0;
			while(cursor.hasNext()){
				BasicDBObject temp=(BasicDBObject) cursor.next();
				String created_time_string = temp.getString("created_time");
				Date created_time = calanderUtil.getDate(created_time_string);
				// 判断目标用户是否转发了
				long weiboid=temp.getLong("_id");
				if (temp.containsField("root_weibo")) {
					BasicDBObject root_weibo=(BasicDBObject) temp.get("root_weibo");
					weiboid=root_weibo.getLong("_id");
				}
				List<Long> friend = (List<Long>) temp.get("mention_id");
				List<BasicDBObject> tempmention=new ArrayList<>();
				for (long targetUserId: friend) {
					int targetValue = mongoDB_Source.IfRetweet(targetUserId, weiboid,
							created_time);
					BasicDBObject kk=new BasicDBObject();
					kk.put("user_id", targetUserId);
					kk.put("retweet", targetValue);
					tempmention.add(kk);
				}
				temp.put("mention_id", tempmention);
				mongoDB_Source.getIntectionv2().save(temp);
				if (count%10000==0) {
					System.out.println(count);
				}
				count++;
			}
	}
}
