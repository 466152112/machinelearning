package Remind.preproccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import tool.FileTool.FileUtil;
import tool.FileTool.WriteUtil;
import tool.data.DenseVector;
import tool.data.SparseVector;
import tool.data.VectorEntry;
import tool.dataStucture.AvlTree;
import tool.io.Strings;
import weibo.util.ReadUser;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseSpliter;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.Filter;
import weibo.util.tweetContent.Spliter;
import Remind.extractFeature.tool.Content_Tool;
import Resource.All_weibo_Source;
import Resource.data_Path;
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
public class Get_user_TFIDF_interest {
	static String path = data_Path.getPath();

	// get user all information
	static AvlTree<User> useravl = null;
	static Set<Long> allUser = null;

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
		useravl = ReadUser.getuseridFromuserId(profileFile);

		Get_user_TFIDF_interest main = new Get_user_TFIDF_interest();
		allUser = new HashSet<>();
		for (String name : usernameAndIdMap.keySet()) {
			allUser.add(usernameAndIdMap.get(name));
		}
		main.scanRetweetCorpus(usernameAndIdMap, sourcePath);
		main.WriteUser_interest_vector();
	}
	/**
	 *  get user interest vector from the useravl,and write to file
	 *@create_time：2015年1月4日上午10:21:25
	 *@modifie_time：2015年1月4日 上午10:21:25
	  
	 */
	public void WriteUser_interest_vector(){
		WriteUtil<String> writeUtil=new WriteUtil<>();
		List<String> resultList=new ArrayList<>();
		for (long userid : allUser) {
			User tempUser=new User(userid);
			
			if (useravl.getElement(tempUser).getInterestFeature()!=null&&useravl.getElement(tempUser).getInterestFeature().sum()!=0) {
				StringBuffer temp=new StringBuffer();
				temp.append(userid+"\t");
				temp.append(sparseVectorToString(useravl.getElement(tempUser).getInterestFeature()));
				resultList.add(temp.toString().trim());
			}
		}
		writeUtil.writeList(resultList, word2vec_txt_Source.getUseridWordCountFile());
	}

	/**
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @create_time：2014年12月15日下午6:02:34
	 * @modifie_time：2014年12月15日 下午6:02:34
	 */
 	public void scanRetweetCorpus(HashMap<String, Long> usernameAndIdMap,
			String sourcePath) {

		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		ExecutorService executor = Executors.newFixedThreadPool(10);
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
		final String intectionFile;
		final String mentionFile;
		List<String> intectionRecord = new ArrayList<>();
		List<String> mentionRecordList=new ArrayList<>();
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
			this.intectionFile = path + "intection/date1/" + temp.getName();
			this.mentionFile=path+ "intection/mention1/" + temp.getName();
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

				OnePairTweet roottweet = listRetweetlist.getRoot();
				// the first keyword extract
				DenseVector root_tweet_word_vector = content_Feature_tool
						.weibo_topic(roottweet.getContent());
				Set<Long> usersetThatAndVector=new HashSet<>();
				// if the root weibo's userid is ok
				if (allUser.contains(roottweet.getUserId())) {

					User rootuser = new User(roottweet.getUserId());
					rootuser = useravl.getElement(rootuser);
					if (root_tweet_word_vector != null) {
						// 用户的兴趣向量相加
						useravl.getElement(rootuser).getInterestFeature()
								.add(root_tweet_word_vector);
						usersetThatAndVector.add(roottweet.getUserId());
					}
					// 获取用户mention 到的用户名单
					Set<String> targetNameList = filter.getAtName(roottweet
							.getContent());

					if (targetNameList != null) {
						// if mention
						// 把mention的基本信息写到文件中
						addMention(targetNameList, usernameAndIdMap,root_tweet_word_vector, roottweet,listRetweetlist.getRetweetList());
					}
				}

				List<OnePairTweet> list = listRetweetlist.getRetweetList();

				if (list != null) {

					for (int i = 0, size = list.size(); i < size; i++) {

						if (!allUser.contains(list.get(i).getUserId())) {
							continue;
						}

						// the retweet path if from the retweet tree
						List<String> routeuserList = filter
								.getRouteName(list.get(i).getContent());

						if (routeuserList != null && routeuserList.size() != 0
								&& i != 0) {

							// the first user
							for (String name : routeuserList) {
								if (usernameAndIdMap.containsKey(name)) {
									long anscetorid = usernameAndIdMap
											.get(name);
									if (anscetorid == list.get(i).getUserId()) {
										continue;
									}
									addretweet(anscetorid, list.get(i)
											.getUserId(), list.get(i));
									break;
								}
							}

							// if no anscetor
							if (allUser.contains(roottweet.getUserId())
									&& roottweet.getUserId() != list.get(i)
											.getUserId()) {
								addretweet(roottweet.getUserId(), list.get(i)
										.getUserId(), list.get(i));
							}
						} else {
							if (allUser.contains(roottweet.getUserId())
									&& roottweet.getUserId() != list.get(i)
											.getUserId()) {
								addretweet(roottweet.getUserId(), list.get(i)
										.getUserId(), list.get(i));
							}
						}

						// add mention intaction
						Set<String> targetNameList = filter.getAtName(list.get(
								i).getContent());
						if (targetNameList != null) {
							addMention(targetNameList, usernameAndIdMap,root_tweet_word_vector,
									list.get(i),list.subList(i, list.size()));
						}
						// add word_vector to user ,if the use is add no add .s
						if (root_tweet_word_vector != null&&!usersetThatAndVector.contains(list.get(i).getUserId())) {
							User tempuser = new User(list.get(i).getUserId());
							tempuser = useravl.getElement(tempuser);
							// 用户的兴趣向量相加
							useravl.getElement(tempuser).getInterestFeature()
									.add(root_tweet_word_vector);
							usersetThatAndVector.add(list.get(i).getUserId());
						}
					}
				}
			}
			// write to the file
				writeUtil.writeList(intectionRecord, intectionFile);
				writeUtil.writeList(mentionRecordList, mentionFile);
			System.out.println("finish" + oneFile);
			return true;
		}

		/**
		 * @param sourceuserid
		 * @param targetNameList
		 * @param usernameAndIdMap
		 * @param weibo
		 *            把mention记录保存到文件中
		 * @create_time：2015年1月3日下午7:07:03
		 * @modifie_time：2015年1月3日 下午7:07:03
		 */
		public void addMention(Set<String> targetNameList,
				HashMap<String, Long> usernameAndIdMap,DenseVector root_tweet_word_vector, OnePairTweet weibo,List<OnePairTweet> remweibo) {
			
			// 遍历所有的名字，并且查看是否在我们的目标用户集中
			for (String name : targetNameList) {
				if (usernameAndIdMap.containsKey(name)) {
					// 获取目标用户的id
					long targetid = usernameAndIdMap.get(name);
					if (targetid == weibo.getUserId()) {
						continue;
					}
					// 0 mention 1 retweet
					StringBuffer tempBuffer=new StringBuffer();
					tempBuffer.append(weibo.getWeiboId() + "\t" + weibo.getUserId()
							+ "\t" + targetid + "\t" + weibo.getCreateTime()
							+ "\t" + 0);
					intectionRecord.add(tempBuffer.toString());
					
					
					long retweetWeiboId=0;
					if (remweibo!=null) {
						for (OnePairTweet temp : remweibo) {
							if (temp.getUserId()==targetid) {
								retweetWeiboId=temp.getWeiboId();
								break;
							}
						}
					}
					//type weiboid\t userid\t createTime \t retweetId \t targetId \t weibocontentvector
					//if no retweet ,the retweetId is 0
					if (root_tweet_word_vector!=null) {
						StringBuffer temp=new StringBuffer();
						temp.append(weibo.getWeiboId() + "\t");
						temp.append( weibo.getUserId()+ "\t" );
						temp.append(weibo.getCreateTime()+ "\t" );
						temp.append(retweetWeiboId+"\t"+ targetid +"\t" );
						temp.append(sparseVectorToString(root_tweet_word_vector));
						mentionRecordList.add( temp.toString().trim());
					}
					
				}
			}
			// write to the file
			if (intectionRecord.size() > 10000) {
				writeUtil.writeList(intectionRecord, intectionFile);
				intectionRecord = new ArrayList<>();
			}
			if (mentionRecordList.size() > 10000) {
				writeUtil.writeList(mentionRecordList, mentionFile);
				mentionRecordList = new ArrayList<>();
			}
			
		}

		/**
		 * @param sourceuserid
		 * @param retweeterid
		 * @param weibo
		 *            把retweet记录保存到文件中
		 * @create_time：2015年1月3日下午7:07:01
		 * @modifie_time：2015年1月3日 下午7:07:01
		 */
		public void addretweet(Long sourceuserid, long retweeterid,
				OnePairTweet weibo) {
			if (sourceuserid != retweeterid) {
				StringBuffer temp=new StringBuffer();
				temp.append( weibo.getWeiboId() + "\t" + retweeterid + "\t"
						+ sourceuserid + "\t" + weibo.getCreateTime() + "\t"
						+ 1);
				intectionRecord.add(temp.toString());
			}
		}
	}
	
	public String sparseVectorToString(DenseVector vector){
		StringBuffer temp=new StringBuffer();
		for (int i = 0,size=vector.getSize(); i < size; i++) {
			if (vector.get(i)!= 0)
				temp.append(String.format("%d:%f\t", new Object[] {i, vector.get(i) }));
		}
		return temp.toString();
	}
}
