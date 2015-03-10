/**

 * By zhouge

 */

package Remind.extractFeature.tool;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tool.FileTool.ReadUtil;
import tool.Math.Stats;
import tool.MyselfMath.MathCal;
import tool.data.DenseVector;
import tool.data.SparseVector;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.ChineseSpliter;
import weibo.util.tweetContent.Spliter;
import Resource.Luence_txt_Source;
import Resource.word2vec_txt_Source;
import bean.User;

/**
 * 
 * 
 * 
 * @progject_name：weiboMention
 * 
 * @class_name：Content_Feature
 * 
 * @class_describe：
 * 
 * @creator：zhouge
 * 
 * @create_time：2014年12月23日 下午7:37:29
 * 
 * @modifier：zhouge
 * 
 * @modified_time：2014年12月23日 下午7:37:29
 * 
 * @modified_note：
 * 
 * @version
 * 
 * 
 */

public class Content_Tool {

	final static double Max_KL = Double.MAX_VALUE;

	final static int topicNumber = 100;

	Map<String, Integer> word_class_map = new HashMap<String, Integer>();
	Map<String, Integer> word_index_map = new HashMap<String, Integer>();
	
	static ChineseSpliter spliter = null;
	static ChineseFilter filter=null;
	Luence_txt_Source luencetooL=null;
	static {
		spliter = new ChineseSpliter();
		filter=new ChineseFilter();
	}
	public Content_Tool() {
		init();
	}
	
	/**
	 * @param content
	 * @param userid
	 * @return
	 *@create_time：2015年1月14日下午6:21:17
	 *@modifie_time：2015年1月14日 下午6:21:17
	  
	 */
	private double getweibo_user_score(String content,long userid){
		if (luencetooL==null) {
			luencetooL=new Luence_txt_Source();
		}
		return luencetooL.get_score_weibo_user(content, userid);
	}
	
	/**
	 * @param sourceUser
	 * @param targetUser
	 * @param content
	 * @return
	 * 基于内容的特征向量
	 * 1、微博与用户的兴趣匹配程度
	 * 2、微博内容长度
	 * 3、是否包含短链接
	 * 4、@的其他用户质量情况 (待定)
	 *@create_time：2015年1月14日下午6:22:47
	 *@modifie_time：2015年1月14日 下午6:22:47
	  
	 */
	public List<Double> get_Content_Feature(User sourceUser,User targetUser,String content){
		List<Double> feature= new ArrayList<>();
		double score=getweibo_user_score(content, targetUser.getUserId());
		feature.add(score);
		//微博有效长度
		feature.add((double) filter.getFilterResultString(content).length());
		feature.add(filter.ifContainLink(content)?1.0:0);
		return feature;
	}
	 
	/**
	 * 
	 * load wordmap
	 * 
	 * @create_time：2014年12月23日下午8:42:57
	 * 
	 * @modifie_time：2014年12月23日 下午8:42:57
	 */

	private void init() {

		ReadUtil<String> readUtil = new ReadUtil<>();

		List<String> wordClass = readUtil.readFileByLine(word2vec_txt_Source
				.getWordClassFile());
		int count=0;
		for (String oneLine : wordClass) {

			String[] split = oneLine.split(" ");

			word_class_map.put(split[0].trim(), Integer.valueOf(split[1]));
			word_index_map.put(split[0].trim(), count++);
		}

	}

	/**
	 * 
	 * @param sourceUser
	 * 
	 * @param targetUser
	 * 
	 * @return two user KL in topic
	 * 
	 * @create_time：2014年12月23日下午7:48:10
	 * 
	 * @modifie_time：2014年12月23日 下午7:48:10
	 */

	private double user_Interest_KL(User sourceUser, User targetUser) {

		DenseVector feature1 = sourceUser.getInterestFeature();

		DenseVector feature2 = targetUser.getInterestFeature();

		double result = MathCal.KLDistance(feature1, feature2);

		return result;

	}

	/**
	 * 
	 * @param weibo_content
	 * 
	 * @param sourceUser
	 * 
	 * @param targetUser
	 * 
	 * @return weibo_user_interest_KL
	 * 
	 * @create_time：2014年12月23日下午9:07:26
	 * 
	 * @modifie_time：2014年12月23日 下午9:07:26
	 */

	private double weibo_user_Interest_KL(DenseVector weibo_topic_dis,
			User targetUser) {

		if (weibo_topic_dis.sum() == 0.0) {

			return Max_KL;

		}

		DenseVector feature2 = targetUser.getInterestFeature();

		double result = MathCal.KLDistance(weibo_topic_dis, feature2);

		return result;

	}

	private DenseVector weibo_topic_distribution(String weibo_content) {

		List<String> wordsplit = spliter.spliterResultInList(weibo_content);

		double[] topic = new double[topicNumber];

		int sum = 0;

		for (String word : wordsplit) {

			if (word_class_map.containsKey(word)) {

				topic[word_class_map.get(word)] += 1;

				sum++;

			}

		}

		if (sum == 0) {

			return null;

		}

		for (int i = 0; i < topicNumber; i++) {

			topic[i] /= sum;

		}

		DenseVector result = new DenseVector(topic);

		return result;

	}

	private DenseVector weibo_topic_distribution_inputVector(String weibo) {
		String[] split = weibo.split("\t");
		double[] temp = new double[100];
		int sum = 0;
		for (int i = 5; i < split.length; i++) {
			String[] split1 = split[i].split(":");
			double count = Double.valueOf(split1[1]);
			temp[Integer.valueOf(split1[0])] = count;
			sum += count;
		}
		if (sum == 0) {
			return null;
		}
		for (int i = 0; i < topicNumber; i++) {
			temp[i] /= sum;
		}
		DenseVector result = new DenseVector(temp);
		return result;
	}

	public DenseVector weibo_topic(String weibo_content) {
		List<String> wordsplit = spliter.spliterResultInList(weibo_content);
		double[] topic = new double[topicNumber];
		int sum = 0;
		for (String word : wordsplit) {

			if (word_class_map.containsKey(word)) {
				topic[word_class_map.get(word)] += 1;
				sum++;
			}
		}

		if (sum == 0) {
			return null;
		}

		DenseVector result = new DenseVector(topic);

		return result;
	}
	
	public SparseVector weibo_Word_Vector(String weibo_content) {
		List<String> wordsplit = spliter.spliterResultInList(weibo_content);
		double[] wordCount = new double[word_index_map.keySet().size()];
		int sum = 0;
		for (String word : wordsplit) {

			if (word_index_map.containsKey(word)) {
				wordCount[word_index_map.get(word)]+=1;
			}
		}

		if (sum == 0) {
			return null;
		}

		SparseVector result = new SparseVector(word_index_map.keySet().size(),wordCount);

		return result;
	}

	/**
	 * 
	 * @param sourceUser
	 * 
	 * @param targetUser
	 * 
	 * @return the weibo and target user interest KL
	 * 
	 * @create_time：2014年12月23日下午7:48:10
	 * 
	 * @modifie_time：2014年12月23日 下午7:48:10
	 */

	private double weibo_user_Interest_KL(double[] array, User targetUser) {

		if (Stats.sum(array) == 0.0) {

			return Max_KL;

		}

		DenseVector feature2 = targetUser.getInterestFeature();

		DenseVector feature1 = new DenseVector(array);

		double result = MathCal.KLDistance(feature1, feature2);

		return result;

	}

}
