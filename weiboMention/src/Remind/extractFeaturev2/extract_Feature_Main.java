package Remind.extractFeaturev2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.mongodb.BasicDBObject;

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

public class extract_Feature_Main {

	Intection_txt_Source Intection_txt_Source = new Intection_txt_Source();
	User_profile_txt_Source user_profile_txt_Source = new User_profile_txt_Source();
	User_profile_Tool user_profile_tool = new User_profile_Tool();
	static AvlTree<User> userAvl = null;
	static Map<Long, Integer> target_user_id_Index_map = null;
	static Set<Long> all_user_id_set = null;

	WriteUtil<String> writeUtil = new WriteUtil<>();

	public static void main(String[] arg) {

		Stopwatch sw = Stopwatch.createStarted();

		extract_Feature_Main main = new extract_Feature_Main();

		long begin_time = sw.elapsed(TimeUnit.MILLISECONDS);
		// 加载用户信息
		main.load_user_id();
		main.Load_user_information();
		// 加载所有微博信息，保存是否转发信息
		main.Load_mention_weibo_cotent();

		long end_Time = sw.elapsed(TimeUnit.MILLISECONDS) - begin_time;
		String time = tool.system.Dates.parse(end_Time);
		System.out.println(time);

	}

	/**
	 * 读取每一条微博的具体内容，并转化为topic向量
	 * 
	 * @throws Exception
	 * @create_time：2014年12月24日下午6:24:47
	 * @modifie_time：2014年12月24日 下午6:24:47
	 */

	public void Load_mention_weibo_cotent() {


			mongoDB_Source mongoDB_Source = new mongoDB_Source();

			for (long targetUserId : target_user_id_Index_map.keySet()) {

				BasicDBObject select = new BasicDBObject();
				select.put("mention_id.user_id", targetUserId);
				List<BasicDBObject> mentionList = mongoDB_Source.getIntection()
						.select(select);
				getFeatureInPal getfeaFeatureInPal = new getFeatureInPal(
						mentionList, targetUserId);
				 getfeaFeatureInPal.call();
			}
	}

	public synchronized void WriteResult(List<String> result,String file) {
		try {
			FileIO.writeList(file, result, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class getFeatureInPal {

		Content_Tool content_tool = null;
		Social_Status_Tool social_Status_Tool = null;
		Social_tie_Tool social_tie_Tool = null;
		mongoDB_Source mongoDB_Source = null;
		final List<BasicDBObject> sourceData;
		CalanderUtil calanderUtil = new CalanderUtil();
		final long targetUserId;

		public getFeatureInPal(List<BasicDBObject> sourceData, long targetUserId) {
			this.sourceData = sourceData;
			content_tool = new Content_Tool();
			social_Status_Tool = new Social_Status_Tool();
			social_tie_Tool = new Social_tie_Tool();
			mongoDB_Source = new mongoDB_Source();
			this.targetUserId = targetUserId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.Callable#call()
		 */
		public List<String> call() {
			List<String> trainresult = new ArrayList<>();
			List<String> testresult = new ArrayList<>();
			
			for (BasicDBObject oneLine : sourceData) {
				String created_time_string = oneLine.getString("created_time");
				Date created_time = calanderUtil.getDate(created_time_string);
			//	System.out.println(created_time);
				if (created_time.compareTo(social_Status_Tool.getTestEnd()) > 0) {
					continue;
				}
				long weiboid = oneLine.getLong("_id");
				List<Double> featureList = new ArrayList<>();
				User sourceUser = new User(oneLine.getLong("user_id"));
				sourceUser = userAvl.getElement(sourceUser);
				if (sourceUser == null) {
					continue;
				}
				User targetUser = new User();
				targetUser.setUserId(targetUserId);
				targetUser = userAvl.getElement(targetUser);
				// 判断是否可用
				if (!target_user_id_Index_map.keySet().contains(
						targetUser.getUserId())
						|| targetUser.getUserId() == sourceUser.getUserId()
						|| targetUser == null)
					continue;
				// 添加用户的人口属性特征
				List<Double> profile_Feature = user_profile_tool
						.getUser_profile_Feature(sourceUser, targetUser);

				featureList.addAll(profile_Feature);
				String content=oneLine.getString("content");
				if (oneLine.containsField("root_weibo")) {
					BasicDBObject root_weibo=(BasicDBObject) oneLine.get("root_weibo");
					String addcount=root_weibo.getString("content");
					content+=" "+addcount;
				}
				// 添加微博的内容的特征
				List<Double> content_Feature = content_tool
						.get_Content_Feature(sourceUser, targetUser,
								content);
				featureList.addAll(content_Feature);
				// 添加用户间的关系特征
				List<Double> social_Feature = social_tie_Tool
						.getSocial_tie_Feature(sourceUser, targetUser,
								created_time, userAvl);

				featureList.addAll(social_Feature);
				// 添加源用户的影响力特征
				List<Double> user_status_feature = social_Status_Tool
						.Social_Status_Feature(sourceUser,
								created_time);
				featureList.addAll(user_status_feature);
				// feature.add(user_influence_Feature.user_influence_Feature(sourceUser));
				// 判断目标用户是否转发了
				if (oneLine.containsField("root_weibo")) {
					BasicDBObject root_weibo=(BasicDBObject) oneLine.get("root_weibo");
					weiboid=root_weibo.getLong("_id");
				}
				
				int targetValue= mongoDB_Source.IfRetweet(oneLine, targetUserId);
				
				DenseVector feature = new DenseVector(featureList.toArray());
				// 转化为svm 数据类型
				String ignoreddata = Strings.toString(new Object[] {targetUserId,sourceUser.getUserId(),oneLine.getString("created_time"),weiboid}, "\t");
				Feature_Covert feature_Covert = new Feature_Covert(
						target_user_id_Index_map.get(targetUser.getUserId()),
						feature, targetValue, ignoreddata);
				//把结果分别写入不同的文档中
				if (created_time.compareTo(social_Status_Tool.getTestBegin()) >= 0) {
					testresult.add(feature_Covert.toString());
				}else {
					trainresult.add(feature_Covert.toString());
				}
			}
			
			WriteResult(testresult,LTR_Type_File.getSvmTypeInput_test());
			WriteResult(trainresult,LTR_Type_File.getSvmTypeInputTrain());
			System.out.println(targetUserId);
			return null;
		}

	}

	/**
	 * @return 加载所有 需要的用户集
	 * @create_time：2014年12月24日下午3:19:11
	 * @modifie_time：2014年12月24日 下午3:19:11
	 */

	public void Load_user_information() {
		// 获取用户
		userAvl = user_profile_txt_Source
				.getLimitUserInTree_limit_by_all_need_file();
	}

	/**
	 * 加载用户id
	 * 
	 * @create_time：2015年1月7日下午4:10:35
	 * @modifie_time：2015年1月7日 下午4:10:35
	 */
	public void load_user_id() {
		target_user_id_Index_map = user_profile_txt_Source
				.get_target_User_InMap();
		all_user_id_set = user_profile_txt_Source.get_all_user_id_inSet();
	}

}
