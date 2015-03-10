package Remind.extractFeature;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;





import org.apache.derby.impl.sql.execute.InsertConstantAction;





import com.google.common.base.Stopwatch;





import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.data.DenseVector;
import tool.dataStucture.AvlTree;
import tool.io.FileIO;
import tool.io.Strings;
import weibo.util.ReadUser;
import Remind.extractFeature.tool.Content_Tool;
import Remind.extractFeature.tool.Feature_Covert;
import Remind.extractFeature.tool.Social_Status_Tool;
import Remind.extractFeature.tool.Social_tie_Tool;
import Remind.extractFeature.tool.User_profile_Tool;
import Resource.LTR_Type_File;
import Resource.Intection_txt_Source;
import Resource.User_profile_txt_Source;
import Resource.mongoDB_Source;
import Resource.word2vec_txt_Source;
import bean.User;



public class extract_Feature_Main {



	Intection_txt_Source Intection_txt_Source = new Intection_txt_Source();

	User_profile_txt_Source user_profile_txt_Source = new User_profile_txt_Source();
	User_profile_Tool user_profile_tool=new User_profile_Tool();
	
	static AvlTree<User> userAvl = null;

	// static AvlTree<RemType> needMention = null;

	static Map<Long, Integer> target_user_id_Index_map = null;

	static Set<Long> all_user_id_set = null;



	Content_Tool content_tool = new Content_Tool();

	Social_Status_Tool social_Status_Tool = new Social_Status_Tool();

	Social_tie_Tool social_tie_Tool = new Social_tie_Tool();

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

	 * 加载所有微博mention 记录，记录是否转发和没有转发

	 * 

	 * @create_time：2014年12月24日下午4:21:43

	 * @modifie_time：2014年12月24日 下午4:21:43

	 */



	// public void Load_need_mention_weibo_id() {

	// // 加载过滤过的mention记录

	// needMention = BaseUitl.get_need_mention_history_From_all(

	// intection_txt_Source.getNeedMentionHistoryFile(), userAvl);

	// System.out.println(needMention.root.getHeight());

	// }



	/**

	 * 读取每一条微博的具体内容，并转化为topic向量

	 * 

	 * @throws Exception

	 * @create_time：2014年12月24日下午6:24:47

	 * @modifie_time：2014年12月24日 下午6:24:47

	 */



	public void Load_mention_weibo_cotent() {



		try (BufferedWriter writer = FileIO.getWriter(LTR_Type_File

				.getSvmTypeInput());) {

			String oneLine = null;

			// 读取微博内容

			Map<Long, String> weiboId_content = Intection_txt_Source

					.getneedWeiboContent();

			

			BufferedReader temp = Intection_txt_Source.getNeedMention();

			int count = 0;

			while ((oneLine = temp.readLine()) != null) {

				String[] split = oneLine.split("\t");

				long weiboid = Long.valueOf(split[0]);

				List<Double> featureList = new ArrayList<>();

				User sourceUser = new User(Long.valueOf(split[1]));

				sourceUser = userAvl.getElement(sourceUser);

				if (sourceUser == null) {

					continue;

				}

				User targetUser = new User();

				targetUser.setUserId(Long.valueOf(split[4]));

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
				// 添加微博的内容的特征

				List<Double> content_Feature = content_tool

						.get_Content_Feature(sourceUser, targetUser,

								weiboId_content.get(weiboid));

				featureList.addAll(content_Feature);

				// 添加用户间的关系特征

				List<Double> social_Feature = social_tie_Tool

						.getSocial_tie_Feature(sourceUser, targetUser, split[2]);



				featureList.addAll(social_Feature);

				// 添加源用户的影响力特征

				List<Double> user_status_feature = social_Status_Tool

						.Social_Status_Feature(sourceUser);

				featureList.addAll(user_status_feature);

				// feature.add(user_influence_Feature.user_influence_Feature(sourceUser));

				// 判断目标用户是否转发了

				int targetValue = Long.valueOf(split[3]) != 0L ? 1 : 0;

				DenseVector feature = new DenseVector(featureList.toArray());

				// 转化为svm 数据类型

				String ignoreddata = Strings.toString(new Object[] { split[0],

						split[1], split[2], split[3], split[4] }, " ");

				Feature_Covert feature_Covert = new Feature_Covert(

						target_user_id_Index_map.get(targetUser.getUserId()),

						feature, targetValue, ignoreddata);

				writer.write(feature_Covert.toString());

				writer.newLine();

			//	writer.flush();

			}

			writer.flush();

			writer.close();

			System.out.println(count);



		} catch (Exception e) {

			e.printStackTrace();

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

		// // 添加用户兴趣词向量

		// userAvl = ReadUser.Add_interest_to_user(userAvl,

		// word2vec_txt_Source.getUseridWordCountFile());

		// 添加mention行为记录

		userAvl = Intection_txt_Source.add_mention_history_to_userTree(userAvl);

		// 添加用户的微博被转发次数记录

		userAvl = Intection_txt_Source

				.add_beRetweet_history_to_userTree(userAvl);

		// 添加关注信息

		userAvl = user_profile_txt_Source.addFollowGraph(userAvl);

		System.out.println("and Load_user_information finish");

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

		System.out.println("and load_user_id finish");

	}



}

