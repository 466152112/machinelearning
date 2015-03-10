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

import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import tool.FileTool.FileUtil;
import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import weibo.util.ReadUser;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseSpliter;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.Filter;
import weibo.util.tweetContent.Spliter;
import Remind.util.BaseUitl;
import Resource.All_weibo_Source;
import Resource.data_Path;

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
public class Get_Information_RetweetFile {
	static String path = data_Path.getPath();

	static String intectionFile = path + "userid/intectionrecord.txt";
	List<String> intectionRecord = new ArrayList<>();

	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		// 用户属性文件
		String profileFile = path + "userid/profile.txt";
		// 转发格式存储目录
		String sourcePath = All_weibo_Source.get_all_weibo_Path();
		// 用户id
		String useridFile = path + "userid/alluserid.txt";
		// 读取用户名和用户id
		HashMap<String, Long> usernameAndIdMap = null;
		usernameAndIdMap = ReadUser
				.getuserNameAndIdMapFromprofileFile(profileFile);
		// 读取用户id和用户名
		HashMap<Long, String> useridAndNameMap = null;
		useridAndNameMap = ReadUser
				.getuserIdAndNameMapFromprofileFile(profileFile);
		//get user all information
		AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
		
		Get_Information_RetweetFile main = new Get_Information_RetweetFile();

		Set<Long> alluserset = main.getAllUserid(useridFile, path);
		main.GetUserTopicDistribution(path);
		main.scanRetweetCorpus(usernameAndIdMap, sourcePath);
		main.scanRootMentionHistory(useridAndNameMap, usernameAndIdMap,
				sourcePath, path);
		main.scanAll_Mention_History(useridAndNameMap, usernameAndIdMap,
				sourcePath, path);
		main.getMentionFromWeiboSource(path, alluserset, sourcePath);
		
	}
	
	//convert weibo content to word2vec vector
	public void GetUserTopicDistribution(String path) {
		ReadUtil<String> readUtil = new ReadUtil<>();
		String wordMapFile = path + "userid/weibo_classes.txt";
		String userIdAndWordFile = path + "userid/weiboContentKeyWordId.txt";
		List<String> wordidList = readUtil.readFileByLine(wordMapFile);
		Map<String, Integer> wordMap = new HashMap<>();
		for (String oneLine : wordidList) {
			String[] split = oneLine.split(" ");
			wordMap.put(split[0], Integer.valueOf(split[1]));
		}
		Map<Long, Map<Integer, Integer>> userIdAndwordCountMap = new HashMap<>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(userIdAndWordFile)));
			String oneLine = null;
			while ((oneLine = bufferedReader.readLine()) != null) {
				String[] split = oneLine.split("\t");
				long userid = Long.valueOf(split[0]);
				String[] wordsplit = split[1].split(" ");
				for (String word : wordsplit) {
					if (wordMap.containsKey(word)) {
						if (userIdAndwordCountMap.containsKey(userid)) {
							if (userIdAndwordCountMap.get(userid).containsKey(
									wordMap.get(word))) {
								userIdAndwordCountMap.get(userid).put(
										wordMap.get(word),
										userIdAndwordCountMap.get(userid).get(
												wordMap.get(word)) + 1);
							} else {
								userIdAndwordCountMap.get(userid).put(
										wordMap.get(word), 1);
							}
						} else {
							Map<Integer, Integer> temp = new HashMap<>();
							temp.put(wordMap.get(word), 1);
							userIdAndwordCountMap.put(userid, temp);
						}
					}
				}
			}
			WriteUtil<String> writeUtil = new WriteUtil<>();
			writeUtil.wirteUserWordCountToFile(userIdAndwordCountMap, path
					+ "userid/userId_word_count.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param useridFile
	 * @param path
	 * @return all the userid
	 * @create_time：2014年11月30日下午3:10:56
	 * @modifie_time：2014年11月30日 下午3:10:56
	 */
	public Set<Long> getAllUserid(String useridFile, String path) {

		File file = new File(useridFile);
		// get limit userid
		Set<Long> alluseIdset = new HashSet<>();
		if (file.exists()) {
			ReadUtil<String> readUtil = new ReadUtil();
			List<String> list = readUtil.readFileByLine(file);
			for (String userid : list) {
				alluseIdset.add(Long.valueOf(userid));
			}
		} else {
			ReadUtil<String> readUtil = new ReadUtil<>();
			List<String> deleteid = readUtil.readFileByLine(path
					+ "userid/needuser/spamuserid.txt");
			Set<Long> deleteidset = new HashSet();
			for (String string : deleteid) {
				deleteidset.add(Long.valueOf(string));
			}
			Set<Long> publishers = getPublisherFromRemNetwork(path, deleteidset);
			Set<Long> Audiences = getAudiencesFromRemNetwork(publishers, path,
					deleteidset);
			alluseIdset.addAll(publishers);
			alluseIdset.addAll(Audiences);

			alluseIdset.removeAll(deleteidset);
			WriteUtil<Long> writeUtil = new WriteUtil<>();
			writeUtil.writeList(alluseIdset, useridFile);
		}

		return alluseIdset;
	}

	public Set<Long> getAllUserid(String useridFile) {

		File file = new File(useridFile);
		// get limit userid
		Set<Long> alluseIdset = new HashSet<>();
		if (file.exists()) {
			ReadUtil<String> readUtil = new ReadUtil();
			List<String> list = readUtil.readFileByLine(file);
			for (String userid : list) {
				alluseIdset.add(Long.valueOf(userid));
			}
		} else {
			System.out.println("the File no exist");
		}
		return alluseIdset;
	}

	/**
	 * @param path
	 * @param deleteidset
	 * @return get the limit userid and exclude spam
	 * @create_time：2014年11月30日下午3:11:11
	 * @modifie_time：2014年11月30日 下午3:11:11
	 */
	public Set<Long> getPublisherFromRemNetwork(String path,
			Set<Long> deleteidset) {
		String ReminHistory = path + "userid/root_Mention_History.txt";
		File file = new File(path + "userid/needuser/publisheruserid.txt");
		// get limit userid
		Set<Long> avluserId = new HashSet<>();
		if (file.exists()) {
			ReadUtil<String> readUtil = new ReadUtil();
			List<String> list = readUtil.readFileByLine(file);
			for (String userid : list) {
				avluserId.add(Long.valueOf(userid));
			}

		} else {
			// get limit userid
			avluserId = BaseUitl.getLimitPublisher(ReminHistory, 10, 1000,
					deleteidset);
			System.out.println(avluserId.size());
			WriteUtil<Long> writeUtil = new WriteUtil<>();
			List<Long> list = new ArrayList<>(avluserId);
			writeUtil.writeList(list, path
					+ "userid/needuser/publisheruserid.txt");
		}

		return avluserId;
	}

	/**
	 * @param path
	 * @return read publisher form file
	 * @create_time：2014年11月30日下午3:12:06
	 * @modifie_time：2014年11月30日 下午3:12:06
	 */
	public Set<Long> getPublisherFromRemNetwork(String path) {
		File file = new File(path + "userid/needuser/publisheruserid.txt");
		// get limit userid
		Set<Long> avluserId = new HashSet<>();
		ReadUtil<String> readUtil = new ReadUtil();
		List<String> list = readUtil.readFileByLine(file);
		for (String userid : list) {
			avluserId.add(Long.valueOf(userid));
		}
		return avluserId;
	}

	/**
	 * @param publishers
	 * @param path
	 * @param deleteidset
	 * @return read publishers' audience
	 * @create_time：2014年11月30日下午3:12:29
	 * @modifie_time：2014年11月30日 下午3:12:29
	 */
	public Set<Long> getAudiencesFromRemNetwork(Set<Long> publishers,
			String path, Set<Long> deleteidset) {

		String rootweiboremNetwork = path + "userid/root_Mention_History.txt";
		// get limit userid
		// 3396814711663049 1791254744 Sun Jan 01 00:00:00 +0800 2012 3
		// 1689345677:0 1595431092:Sun Jan 01 00:29:00 +0800 2012 1650781714:0
		// 1680587862:0
		File file = new File(path + "userid/needuser/audienceuserid.txt");
		// get limit userid
		Set<Long> avluserId = new HashSet<>();
		if (file.exists()) {
			ReadUtil<String> readUtil = new ReadUtil();
			List<String> list = readUtil.readFileByLine(file);
			for (String userid : list) {
				avluserId.add(Long.valueOf(userid));
			}
		} else {
			avluserId = BaseUitl.getAudience(publishers, rootweiboremNetwork,
					deleteidset);
			WriteUtil<Long> writeUtil = new WriteUtil<>();
			List<Long> list = new ArrayList<>(avluserId);
			writeUtil.writeList(list, path
					+ "userid/needuser/audienceuserid.txt");
			System.out.println(avluserId.size());
		}
		return avluserId;
	}

	public Set<Long> getAudiencesFromFile(String path) {
		File file = new File(path + "userid/needuser/audienceuserid.txt");
		// get limit userid
		Set<Long> avluserId = new HashSet<>();
		if (file.exists()) {
			ReadUtil<String> readUtil = new ReadUtil();
			List<String> list = readUtil.readFileByLine(file);
			for (String userid : list) {
				avluserId.add(Long.valueOf(userid));
			}
		} else {
		}
		return avluserId;
	}

	// get mention example
	public void getMentionFromWeiboSource(String path, Set<Long> useridSet,
			String sourcePath) {
		Set<Long> publishers = getPublisherFromRemNetwork(path);
		Set<Long> weiboId = BaseUitl.getMentionWeiboId(path
				+ "userid/rootweiboremNetwork.txt", publishers);

		String rootMentionContent = path + "userid/weiboMention.txt";
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);

		for (String OneFile : fileList) {

			System.out.println(OneFile);
			ReadWeibo rw = new ReadWeibo(OneFile);
			Retweetlist listRetweetlist;
			WriteUtil<String> writeLiUtil = new WriteUtil<>();
			List<String> listResult = new ArrayList<>();

			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0l)
					continue;
				if (weiboId.contains(listRetweetlist.getRoot().getWeiboId())) {
					listResult.add(listRetweetlist.getRoot().toString());
				}
			}
			writeLiUtil.writeList(listResult, rootMentionContent);
			listResult = new ArrayList<>();
		}

	}

	/**
	 * @param useridSet
	 * @param sourcePath
	 *            从转发文件库中获取 userid + content +root content
	 * @create_time：2014年12月15日下午5:59:53
	 * @modifie_time：2014年12月15日 下午5:59:53
	 */
	public void getTweetCorpusFromWeiboSource(Set<Long> useridSet,
			String sourcePath) {

		String weiboContentCorpus = path + "userid/weiboContentCorpus.txt";
		System.out.println("user size:" + useridSet.size());
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);

		for (String OneFile : fileList) {

			System.out.println(OneFile);
			ReadWeibo rw = new ReadWeibo(OneFile);
			Retweetlist listRetweetlist;
			WriteUtil<String> writeLiUtil = new WriteUtil<>();
			List<String> listResult = new ArrayList<>();

			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0l)
					continue;
				String content = listRetweetlist.getRoot().getContent();

				if (useridSet.contains(listRetweetlist.getRoot().getUserId())) {
					listResult.add(listRetweetlist.getRoot().getUserId() + "\t"
							+ content);
				}

				List<OnePairTweet> list = listRetweetlist.getRetweetList();
				for (int i = 0; i < list.size(); i++) {
					content = list.get(i).getContent();
					if (useridSet.contains(list.get(i).getUserId())) {
						content = list.get(i).getContent();
						listResult.add(list.get(i).getUserId() + "\t" + content
								+ " " + listRetweetlist.getRoot().getContent());
					}
				}
				if (listResult.size() > 100000) {
					writeLiUtil.writeList(listResult, weiboContentCorpus);
					listResult = new ArrayList<>();
				}
			}
			writeLiUtil.writeList(listResult, weiboContentCorpus);
			listResult = new ArrayList<>();
		}

	}

	/**
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @create_time：2014年12月15日下午6:02:34
	 * @modifie_time：2014年12月15日 下午6:02:34
	 */
	public void scanRetweetCorpus(HashMap<String, Long> usernameAndIdMap,
			String sourcePath) {
		// read all the userid
		Set<Long> allUser = getAllUserid(path + "userid/alluserid.txt", path);
		// read publishers
		Set<Long> publishers = getPublisherFromRemNetwork(path);
		Set<Long> audiences = getAudiencesFromFile(path);

		// save all the keywords
		String weiboContentkeyWords = path + "userid/weiboContentKeyword.txt";
		String weiboContentkeyWordsUserId = path
				+ "userid/weiboContentKeyWordId.txt";
		// save the root weibo that contains keywords
		String root_weibo_mention = path + "userid/root_weibo_mention.txt";
		// save all the user interaction number

		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		WriteUtil<String> writeUtil = new WriteUtil<>();
		Spliter spliter = new ChineseSpliter();
		Filter filter = new ChineseFilter();
		for (String OneFile : fileList) {
			System.out.println(OneFile);
			ReadWeibo rw = new ReadWeibo(OneFile);
			Retweetlist listRetweetlist;

			List<String> weiboContentKeyWordList = new ArrayList<>();
			List<String> weiboContentKeywordAndUserIdList = new ArrayList<>();
			List<String> rootWeibo_mentionList = new ArrayList<>();

			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0)
					continue;

				OnePairTweet roottweet = listRetweetlist.getRoot();
				// the first keyword extract
				String splitWord = spliter.spliterResultInString(roottweet
						.getContent());
				// if the root weibo's userid is ok
				if (allUser.contains(roottweet.getUserId())) {

					List<String> targetNameList = null;

					if (roottweet.getContent().indexOf("@") != -1) {
						targetNameList = filter.getAtNameInList(roottweet
								.getContent());
					}
					if (!splitWord.equals("")) {
						// System.out.println(splitWord);
						// System.out.println(roottweet.getContent());
						// System.out.println();
						weiboContentKeyWordList.add(splitWord);
						weiboContentKeywordAndUserIdList.add(roottweet
								.getUserId() + "\t" + splitWord);

						// if publishers then write to file
						if (publishers.contains(roottweet.getUserId())
								&& targetNameList != null) {
							String rootMentionType = rootMention(
									targetNameList, usernameAndIdMap,
									roottweet, audiences, splitWord);
							if (rootMentionType != null) {
								rootWeibo_mentionList.add(rootMentionType);
							}
						}
					}

					if (targetNameList != null) {
						// if mention
						addMention(roottweet.getUserId(), targetNameList,
								usernameAndIdMap, roottweet);
					}
				}

				List<OnePairTweet> list = listRetweetlist.getRetweetList();

				if (list != null) {

					for (int i = 0, size = list.size(); i < size; i++) {

						if (!allUser.contains(list.get(i).getUserId())) {
							continue;
						}

						if (!splitWord.equals("")) {
							weiboContentKeyWordList.add(splitWord);
							weiboContentKeywordAndUserIdList.add(list.get(i)
									.getUserId() + "\t" + splitWord);
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
						List<String> targetNameList = filter
								.getAtNameInList(list.get(i).getContent());
						if (targetNameList != null) {
							addMention(list.get(i).getUserId(), targetNameList,
									usernameAndIdMap, list.get(i));
						}
					}
				}
				if (weiboContentKeyWordList.size() > 100000) {
					// save all the keywords
					writeUtil.writeList(weiboContentKeyWordList,
							weiboContentkeyWords);
					writeUtil.writeList(weiboContentKeywordAndUserIdList,
							weiboContentkeyWordsUserId);
					writeUtil.writeList(rootWeibo_mentionList,
							root_weibo_mention);

					weiboContentKeyWordList = new ArrayList<>();
					weiboContentKeywordAndUserIdList = new ArrayList<>();
					rootWeibo_mentionList = new ArrayList<>();

				}
			}
			// save all the keywords
			writeUtil.writeList(weiboContentKeyWordList, weiboContentkeyWords);
			writeUtil.writeList(weiboContentKeywordAndUserIdList,
					weiboContentkeyWordsUserId);
			writeUtil.writeList(rootWeibo_mentionList, root_weibo_mention);
		}
		writeUtil.writeList(intectionRecord, intectionFile);
		intectionRecord = new ArrayList<>();

	}

	public void scanRootMentionHistory(HashMap<Long, String> useridAndNameMap,
			HashMap<String, Long> usernameAndIdMap, String sourcePath,
			String path) {
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		Spliter spliter = new ChineseSpliter();
		Filter filter = new ChineseFilter();
		for (String fileName : fileList) {
			System.out.println(fileName);
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(path + "userid/rootMentionHistory.txt"), true))) {
				ReadWeibo readWeibo = new ReadWeibo(fileName);
				Retweetlist retweetlist = null;
				while ((retweetlist = readWeibo.readRetweetList()) != null) {
					OnePairTweet rootTweet = retweetlist.getRoot();
					if (rootTweet == null || rootTweet.getRetweetId() != 0) {
						continue;
					}
					// if content contain this char then find :
					if (rootTweet.getContent().contains("@")) {
						List<String> reminderName = filter
								.getAtNameInList(rootTweet.getContent());
						if (reminderName == null) {
							continue;
						}
						reminderName = getListThatInUserList(reminderName,
								usernameAndIdMap);
						if (reminderName != null) {
							StringBuffer buffer = new StringBuffer();

							List<OnePairTweet> list = retweetlist
									.getRetweetListSortByTime();

							Map<String, String> retweetUserNameAndTime = new HashMap<>();
							for (OnePairTweet onePairTweet : list) {
								String name = useridAndNameMap.get(onePairTweet
										.getUserId());
								if (!retweetUserNameAndTime.containsKey(name)) {
									retweetUserNameAndTime.put(name,
											onePairTweet.getCreateTime());
								}
							}

							buffer.append(rootTweet.getWeiboId() + "\t"
									+ rootTweet.getUserId() + "\t"
									+ rootTweet.getCreateTime() + "\t"
									+ list.size() + "\t");

							for (int i = 0; i < reminderName.size(); i++) {
								if (retweetUserNameAndTime.keySet().contains(
										reminderName.get(i))) {
									buffer.append(usernameAndIdMap
											.get(reminderName.get(i))
											+ ":"
											+ retweetUserNameAndTime
													.get(reminderName.get(i))
											+ "\t");
									break;

								} else {
									buffer.append(usernameAndIdMap
											.get(reminderName.get(i))
											+ ":"
											+ 0
											+ "\t");
								}
							}
							writer.write(buffer.toString().trim());
							writer.newLine();
						}

					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param useridAndNameMap
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @param path
	 *            获取用户所有接收到的mention ，不管是不是用户集里面发送的。格式
	 *            sourceId\tTargetId\tCreate_time\tsplitWord
	 * @create_time：2014年12月4日上午10:19:20
	 * @modifie_time：2014年12月4日 上午10:19:20
	 */
	public void scanAll_Mention_History(HashMap<Long, String> useridAndNameMap,
			HashMap<String, Long> usernameAndIdMap, String sourcePath,
			String path) {
		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		Spliter spliter = new ChineseSpliter();
		Filter filter = new ChineseFilter();
		// write type souceid\t targetid\t createTime\t splitword
		for (String fileName : fileList) {
			System.out.println(fileName);
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(
					new File(path + "userid/All_Mention_History.txt"), true))) {
				ReadWeibo readWeibo = new ReadWeibo(fileName);
				Retweetlist retweetlist = null;
				while ((retweetlist = readWeibo.readRetweetList()) != null) {
					OnePairTweet rootTweet = retweetlist.getRoot();
					if (rootTweet == null || rootTweet.getRetweetId() != 0) {
						continue;
					}
					// the first keyword extract
					String splitWord = spliter.spliterResultInString(rootTweet
							.getContent());
					// if content contain this char then find :
					if (rootTweet.getContent().contains("@")) {
						List<String> reminderName = filter
								.getAtNameInList(rootTweet.getContent());
						if (reminderName != null) {
							// 获取在里面的用户名称
							reminderName = getListThatInUserList(reminderName,
									usernameAndIdMap);
							if (reminderName != null) {
								for (String name : reminderName) {
									StringBuffer buffer = new StringBuffer();
									buffer.append(rootTweet.getUserId() + "\t"
											+ usernameAndIdMap.get(name) + "\t"
											+ rootTweet.getCreateTime() + "\t"
											+ splitWord);
									writer.write(buffer.toString().trim());
									writer.newLine();
								}
							}
						}
					}
					List<OnePairTweet> list = retweetlist
							.getRetweetListSortByTime();
					Set<String> userPairMap = new HashSet<>();
					for (OnePairTweet onePairTweet : list) {
						// if content contain this char then find :
						String retweetsplitWord = spliter
								.spliterResultInString(onePairTweet
										.getContent());
						if (onePairTweet.getContent().contains("@")) {
							List<String> reminderName = filter
									.getAtNameInList(onePairTweet.getContent());
							if (reminderName != null) {
								// 获取在里面的用户名称
								reminderName = getListThatInUserList(
										reminderName, usernameAndIdMap);
								if (reminderName != null) {
									for (String name : reminderName) {
										if (!userPairMap.contains(onePairTweet
												.getUserId()
												+ "\t"
												+ usernameAndIdMap.get(name))) {
											StringBuffer buffer = new StringBuffer();
											buffer.append(onePairTweet
													.getUserId()
													+ "\t"
													+ usernameAndIdMap
															.get(name)
													+ "\t"
													+ onePairTweet
															.getCreateTime()
													+ "\t"
													+ splitWord
													+ " "
													+ retweetsplitWord);
											writer.write(buffer.toString()
													.trim());
											writer.newLine();
											userPairMap.add(onePairTweet
													.getUserId()
													+ "\t"
													+ usernameAndIdMap
															.get(name));
										}
									}
								}

							}
						}
					}
				}
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> getListThatInUserList(
			List<String> reminderName,
			HashMap<String, Long> usernameAndIdMap) {
		ArrayList<String> result = null;
		for (String name : reminderName) {
			if (usernameAndIdMap.containsKey(name)) {
				if (result == null) {
					result = new ArrayList<>();
				}
				result.add(name);
			}

		}
		return result;
	}

	public Map<Long, Integer> addRemToMap(Map<Long, Integer> useridAndRemCount,
			long sourceId, HashMap<String, Long> usernameAndIdMap,
			ArrayList<String> targetNameList) {

		for (String name : targetNameList) {
			if (usernameAndIdMap.containsKey(name)) {
				if (useridAndRemCount.containsKey(sourceId)) {
					useridAndRemCount.put(sourceId,
							useridAndRemCount.get(sourceId) + 1);
				} else {
					useridAndRemCount.put(sourceId, 1);
				}
			}
		}
		return useridAndRemCount;
	}

	public String rootMention(List<String> targetNameList,
			HashMap<String, Long> usernameAndIdMap, OnePairTweet weibo,
			Set<Long> audiences, String keywords) {

		StringBuffer buffer = new StringBuffer();
		List<Long> audienceResult = new ArrayList<>();

		for (String name : targetNameList) {
			if (usernameAndIdMap.containsKey(name)) {
				long userid = usernameAndIdMap.get(name);
				if (audiences.contains(userid)) {
					audienceResult.add(userid);
				}
			}
		}
		if (audienceResult.size() > 0) {
			buffer.append(weibo.getUserId() + "\t");
			buffer.append(weibo.getCreateTime() + "\t");
			for (Long userid : audienceResult) {
				buffer.append(userid + ",");
			}
			buffer.append("\t" + keywords);
			return buffer.toString();
		} else {
			return null;
		}
	}

	public void addMention(Long sourceuserid, List<String> targetNameList,
			HashMap<String, Long> usernameAndIdMap, OnePairTweet weibo) {
		List<String> result = new ArrayList<>();
		for (String name : targetNameList) {
			if (usernameAndIdMap.containsKey(name)) {

				long targetid = usernameAndIdMap.get(name);
				if (targetid == sourceuserid) {
					continue;
				}
				// 0 mention 1 retweet
				result.add(sourceuserid + "\t" + targetid + "\t"
						+ weibo.getCreateTime() + "\t" + 0);
			}
		}

		if (result.size() != 0) {
			intectionRecord.addAll(result);
		}
		if (intectionRecord.size() > 100000) {
			WriteUtil<String> writeUtil = new WriteUtil<>();
			writeUtil.writeList(intectionRecord, intectionFile);
			intectionRecord = new ArrayList<>();
		}

	}

	public void addretweet(Long sourceuserid, long retweeterid,
			OnePairTweet weibo) {
		if (sourceuserid != retweeterid) {
			List<String> result = new ArrayList<>();
			result.add(retweeterid + "\t" + sourceuserid + "\t"
					+ weibo.getCreateTime() + "\t" + 1);
			WriteUtil<String> writeUtil = new WriteUtil<>();
			intectionRecord.addAll(result);
		}
	}

}
