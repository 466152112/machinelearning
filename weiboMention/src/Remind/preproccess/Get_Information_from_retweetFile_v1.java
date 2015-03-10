package Remind.preproccess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mongodb.BasicDBObject;

import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import tool.FileTool.FileUtil;
import tool.FileTool.WriteUtil;
import tool.data.DenseVector;
import tool.data.SparseVector;
import tool.data.VectorEntry;
import tool.dataStucture.AvlTree;
import tool.mongo.Mongo_Source;
import weibo.util.ReadUser;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseSpliter;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.Filter;
import weibo.util.tweetContent.Spliter;
import Remind.extractFeature.tool.Content_Tool;
import Resource.All_weibo_Source;
import Resource.User_profile_txt_Source;
import Resource.data_Path;
import Resource.mongoDB_Source;
import Resource.word2vec_txt_Source;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：Get_Information_RetweetFile
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年12月15日 下午5:59:51
 * @modifier：zhouge
 * @modified_time：2014年12月15日 下午5:59:51
 * @modified_note：
 * @version
 * 
 */
public class Get_Information_from_retweetFile_v1 {
	static String path = data_Path.getPath();

	// get user all information
	static AvlTree<User> useravl = null;
	static Set<Long> allUser = null;
	Mongo_Source retweet = new mongoDB_Source().getRetweet();
	Mongo_Source intect = new mongoDB_Source().getIntection();

	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		// 用户属性文件
		String profileFile = path + "userid/profile.txt";
		// 转发格式存储目录
		String sourcePath = All_weibo_Source.get_all_weibo_Path();
		// 读取用户名和用户id
		HashMap<String, Long> usernameAndIdMap = null;
		usernameAndIdMap = ReadUser
				.getuserNameAndIdMapFromprofileFile(profileFile);
		// 读取用户id和用户名
		// HashMap<Long, String> useridAndNameMap = null;
		// useridAndNameMap = ReadUser
		// .getuserIdAndNameMapFromprofileFile(profileFile);
		// get user all information
		useravl = ReadUser.getuserFromprofileFile(profileFile);
		useravl = ReadUser.getFollowGraph(useravl,
				User_profile_txt_Source.getFollowgraphFile());

		Get_Information_from_retweetFile_v1 main = new Get_Information_from_retweetFile_v1();
		allUser = new HashSet<>();
		for (String name : usernameAndIdMap.keySet()) {
			allUser.add(usernameAndIdMap.get(name));
		}
		main.scanRetweetCorpus(usernameAndIdMap, sourcePath);
	}

	/**
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @create_time：2014年12月15日下午6:02:34
	 * @modifie_time：2014年12月15日 下午6:02:34
	 */
	public void scanRetweetCorpus(HashMap<String, Long> usernameAndIdMap,
			String sourcePath) {

		List<String> fileList = All_weibo_Source.get_all_weibo_File_by_Sorted();

		ExecutorService executor = Executors.newFixedThreadPool(15);
		List<Future<Object>> results = new ArrayList<>();
		for (String OneFile : fileList) {
			results.add(executor.submit(new scanRetweetFile(OneFile,
					usernameAndIdMap)));
		}
		executor.shutdown();
		for (Future<Object> result : results) {
			try {
				result.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class scanRetweetFile implements Callable {

		final String oneFile;
		// tool for spliter and filter
		final Spliter spliter = new ChineseSpliter();
		final Filter filter = new ChineseFilter();
		// final String intectionFile;
		final String mentionFile;
		final HashMap<String, Long> usernameAndIdMap;
		final Content_Tool content_Feature_tool = new Content_Tool();
		WriteUtil<String> writeUtil = new WriteUtil<>();

		/**
		 * @param oneFile
		 *            当前处理的文件名称
		 * @param usernameAndIdMap
		 *            用户名和id映射 ，用于获取@和路径
		 */
		public scanRetweetFile(String oneFile,
				HashMap<String, Long> usernameAndIdMap) {
			this.oneFile = oneFile;
			this.usernameAndIdMap = usernameAndIdMap;
			File temp = new File(oneFile);
			// this.intectionFile = path + "intection/date1/" + temp.getName();
			this.mentionFile = path + "intection/mention2/" + temp.getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Boolean call() throws Exception {
			// TODO Auto-generated method stub
			ReadWeibo rw = new ReadWeibo(this.oneFile);
			Retweetlist listRetweetlist;
			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0)
					continue;
				saveRetweet(listRetweetlist);
				OnePairTweet roottweet = listRetweetlist.getRoot();
				if (allUser.contains(roottweet.getUserId())) {
					User rootuser = new User(roottweet.getUserId());
					rootuser = useravl.getElement(rootuser);
					Set<String> targetNameList = filter.getAtName(roottweet
							.getContent());
					if (targetNameList != null) {
						addMention(targetNameList, usernameAndIdMap, roottweet,
								null);
					}
				}
				List<OnePairTweet> list = listRetweetlist.getRetweetList();

				if (list != null) {
					for (int i = 0, size = list.size(); i < size; i++) {
						if (!allUser.contains(list.get(i).getUserId())) {
							continue;
						}

						// add mention intaction
						Set<String> targetNameList = filter.getAtName(list.get(
								i).getContent());
						if (targetNameList != null) {
							addMention(targetNameList, usernameAndIdMap,
									list.get(i), roottweet);
						}

					}
				}
			}
			System.out.println("finish" + oneFile);
			return true;
		}

		/**
		 * 把转发数据保存到数据库中
		 * 
		 * @param listRetweetlist
		 * @create_time：2015年1月27日下午3:45:52
		 * @modifie_time：2015年1月27日 下午3:45:52
		 */
		public void saveRetweet(Retweetlist listRetweetlist) {
			List<BasicDBObject> tempList = new ArrayList<>();
			for (OnePairTweet tweet : listRetweetlist
					.getRetweetListSortByTime()) {
				BasicDBObject kk = new BasicDBObject();
				kk.put("_id", tweet.getWeiboId());
				kk.put("user_id", tweet.getUserId());
				kk.put("created_time", tweet.getCreateTime());
				tempList.add(kk);
			}
			if (tempList.size() > 0) {
				BasicDBObject temp = new BasicDBObject();
				temp.put("_id", listRetweetlist.getRoot().getWeiboId());
				temp.put("user_id", listRetweetlist.getRoot().getUserId());
				temp.put("created_time", listRetweetlist.getRoot().getCreateTime());
				
				temp.put("retweet", tempList);
				retweet.save(temp);
			}
		}

		public void addMention(Set<String> targetNameList,
				HashMap<String, Long> usernameAndIdMap, OnePairTweet weibo,
				OnePairTweet rootweibo) {

			List<Long> mentionUserId = new ArrayList<>();
			for (String name : targetNameList) {
				if (usernameAndIdMap.containsKey(name)) {
					// 获取目标用户的id
					long targetid = usernameAndIdMap.get(name);
					if (targetid == weibo.getUserId()) {
						continue;
					}
					mentionUserId.add(targetid);
				}
			}
			if (mentionUserId.size() > 0) {

				BasicDBObject temp = new BasicDBObject();
				temp.put("_id", weibo.getWeiboId());
				temp.put("user_id", weibo.getUserId());
				temp.put("created_time", weibo.getCreateTime());
				temp.put("content", weibo.getContent());
				temp.put("mention_id", mentionUserId);

				if (rootweibo != null) {
					BasicDBObject roottemp = new BasicDBObject();
					roottemp.put("_id", rootweibo.getWeiboId());
					roottemp.put("user_id", rootweibo.getUserId());
					roottemp.put("created_time", rootweibo.getCreateTime());
					roottemp.put("content", rootweibo.getContent());
					temp.put("root_weibo", roottemp);
				}
				intect.save(temp);
			}

		}
	}
}
