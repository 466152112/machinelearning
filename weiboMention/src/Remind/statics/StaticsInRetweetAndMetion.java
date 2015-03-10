package Remind.statics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.util.Hash;

import tool.FileTool.FileUtil;
import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.MapTool.TwoValueComparatorInLong;
import tool.MapTool.TwoValueComparatorInString;
import tool.TimeTool.CalanderUtil;
import tool.dataStucture.AvlTree;
import weibo.util.ReadUser;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.Filter;
import Remind.preproccess.Get_Information_from_retweetFile;
import Remind.util.BaseUitl;
import Resource.User_profile_txt_Source;
import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import bean.Remind.RemType;
import bean.Remind.Reminder;

public class StaticsInRetweetAndMetion {
	static String path = "/home/zhouge/database/weibo/new/";
	static String intectionFile=path+"userid/intectionrecord.txt";
	
	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		String profileFile = path + "userid/profile.txt";
		String reminderFile = path + "rem.txt";
		String sourcePath = "/home/zhouge/database/weibo/2012/content/";
		String tagFile = path + "userid/usertagfinal.txt";
		HashMap<String, Long> usernameAndIdMap = null;
		usernameAndIdMap = ReadUser
				.getuserNameAndIdMapFromprofileFile(profileFile);
		AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
//		useravl = BaseUitl.addTagToUser(useravl, tagFile);
		StaticsInRetweetAndMetion main = new StaticsInRetweetAndMetion();
		main.run(useravl, usernameAndIdMap, sourcePath, path);
		//main.comparementionAndRetweet(path);
		// main.mentionDayDistribution(path);
		// main.TheTopAnalysis(path, useravl);
	}

	public void mentionDayDistribution(String path) {
		String mentionNetworkTimeFile = path + "userid/remNetwork.net";
		ReadUtil<String> readUtil = new ReadUtil<>();
		Map<String, Integer> mentionday = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(
				new File(mentionNetworkTimeFile)))) {
			String oneline;
			String simpledaString = "EEE MMM dd HH:mm:ss z yyyy";
			while ((oneline = reader.readLine()) != null) {
				String split[] = oneline.split(",");
				if (split[0].equals(split[1])) {
					continue;
				}
				String splittime[] = split[2].split(" ");

				String day = splittime[1] + "\t" + splittime[2];
				// System.out.println(time.get(Calendar.MONTH));
				if (mentionday.containsKey(day)) {
					mentionday.put(day, mentionday.get(day) + 1);
				} else {
					mentionday.put(day, 1);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeMapKeyAndValue(mentionday, path
				+ "result/mentiondaydistribution1.txt");
	}

	public void comparementionAndRetweet(String path) {
		String mentionNetworkFile = path + "result/mentionnetwork.txt";
		String retweetNetwork = path + "result/retweetroutenetwork.txt";
		ReadUtil<String> readUtil = new ReadUtil<>();
		Map<String, Integer> mentionRetweetPair = new HashMap<>();
		List<String> mention = readUtil.readFileByLine(mentionNetworkFile);
		// List<String> retweetList=readUtil.readFileByLine(retweetNetwork);
		HashMap<Double, Integer> map = new HashMap<>();
		Map<Long, Map<Long, Double>> mentionmap = new HashMap<>();
		for (String oneline : mention) {
			String[] split = oneline.split("\t");
			long sourceuserid = Long.valueOf(split[0]);
			long targetid = Long.valueOf(split[1]);

			if (sourceuserid == targetid) {
				continue;
			}

			double value = Double.valueOf(split[2]);
			if (value < 1) {
				continue;
			}
			if (mentionmap.containsKey(sourceuserid)) {
				mentionmap.get(sourceuserid).put(targetid, value);
			} else {
				HashMap<Long, Double> tempHashMap = new HashMap<>();
				tempHashMap.put(targetid, value);
				mentionmap.put(sourceuserid, tempHashMap);
			}
		}

		double[][] grid = new double[200][200];
		Set<String> flag = new HashSet<>();
		double sum = 0;
		for (long sourceuserid : mentionmap.keySet()) {
			for (long targetid : mentionmap.get(sourceuserid).keySet()) {

				if (flag.contains(sourceuserid + "\t" + targetid)
						|| flag.contains(targetid + "\t" + sourceuserid)) {
					continue;
				}
				double value2 = 0;
				double value1 = mentionmap.get(sourceuserid).get(targetid);
				if (mentionmap.containsKey(targetid)
						&& mentionmap.get(targetid).containsKey(sourceuserid)) {
					value2 = mentionmap.get(targetid).get(sourceuserid);
				}

				if (value1 < 200 && value2 < 200) {
					int x = (int) (value1);
					int y = (int) (value2);
					grid[x][y]++;
					sum++;
				}

				flag.add(sourceuserid + "\t" + targetid);
				flag.add(targetid + "\t" + sourceuserid);
			}
		}

		WriteUtil<String> writeUtil = new WriteUtil<>();
		List<String> result = new ArrayList<>();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				double x = i;
				double y = j;
				double value = grid[i][j] / sum + 0.000001;
				result.add(x + "\t" + y + "\t" + value);
			}
		}
		writeUtil.writeList(result, path
				+ "result/mentionreciplygriddistri.txt");

	}

	public void TheTopAnalysis(String path, AvlTree<User> useravl) {
		String mentionNetworkFile = path + "result/mentionnetwork.txt";
		String retweetNetwork = path + "result/retweetroutenetwork.txt";
		ReadUtil<String> readUtil = new ReadUtil<>();
		Map<String, Integer> mentionRetweetPair = new HashMap<>();
		List<String> mention = readUtil.readFileByLine(mentionNetworkFile);
		// List<String> retweetList=readUtil.readFileByLine(retweetNetwork);
		HashMap<Double, Integer> map = new HashMap<>();
		Map<Long, Map<Long, Double>> mentionmap = new HashMap<>();
		Map<Long, Double> mentionGetMap = new HashMap<>();
		for (String oneline : mention) {
			String[] split = oneline.split("\t");
			long sourceuserid = Long.valueOf(split[0]);
			long targetid = Long.valueOf(split[1]);

			if (sourceuserid == targetid) {
				continue;
			}

			double value = Double.valueOf(split[2]);
			if (value < 1) {
				continue;
			}
			if (mentionmap.containsKey(sourceuserid)) {
				mentionmap.get(sourceuserid).put(targetid, value);
			} else {
				HashMap<Long, Double> tempHashMap = new HashMap<>();
				tempHashMap.put(targetid, value);
				mentionmap.put(sourceuserid, tempHashMap);
			}

			if (mentionGetMap.containsKey(targetid)) {
				mentionGetMap
						.put(targetid, mentionGetMap.get(targetid) + value);
			} else {
				mentionGetMap.put(targetid, value);
			}

		}

		TwoValueComparatorInLong tvm = new TwoValueComparatorInLong(
				mentionGetMap, 0);
		List<Long> useridList = new ArrayList<>(mentionGetMap.keySet());
		Collections.sort(useridList, tvm);
		for (int i = 0; i < 500; i++) {
			User user = new User();
			user.setUserId(useridList.get(i));
			user = useravl.getElement(user);
			System.out.println(useridList.get(i) + "\t"
					+ mentionGetMap.get(useridList.get(i)) + "\t"
					+ user.getUserName() + "\t" + user.getTag());
		}
		// HashMap<Integer, Integer> useMentionNumber = new HashMap<>();
		// double sum=0;
		// List<Integer> allfrinendNumber=new ArrayList<>();
		// for (long key : mentionmap.keySet()) {
		// //user mention number
		// int userNameNumber=mentionmap.get(key).keySet().size();
		// allfrinendNumber.add(userNameNumber);
		// if (useMentionNumber.containsKey(userNameNumber)) {
		// useMentionNumber.put(userNameNumber,
		// useMentionNumber.get(userNameNumber)+1);
		// }else {
		// useMentionNumber.put(userNameNumber,1);
		// }
		// sum+=userNameNumber;
		// }
		// double avg=sum/mentionmap.keySet().size();
		// System.out.println(avg);
		// Collections.sort(allfrinendNumber);
		// int medindex=allfrinendNumber.size()/2;
		// if (allfrinendNumber.size()%2==0) {
		// System.out.println(allfrinendNumber.get(medindex));
		// }else {
		// double
		// value=(allfrinendNumber.get(medindex)+allfrinendNumber.get(medindex+1))*1.0/2;
		// System.out.println(value);
		//
		// }
		//
		// WriteUtil<String> writeUtil=new WriteUtil<>();
		// writeUtil.writeMapKeyAndValuesplitbyt(useMentionNumber, path +
		// "result/distribution/retweetFriendDistribution.txt");

	}

	/**
	 * @param useravl
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @param path
	 */
	public void run(AvlTree<User> useravl,
			HashMap<String, Long> usernameAndIdMap, String sourcePath,
			String path) {
		Get_Information_from_retweetFile temp = new Get_Information_from_retweetFile();
		Set<Long> useridSet =new User_profile_txt_Source().get_all_user_id_inSet();

		// delete user from usernameAndIdMap than not in the useridset
		Set<String> deleteName = new HashSet<>();
		for (String name : usernameAndIdMap.keySet()) {
			if (!useridSet.contains(usernameAndIdMap.get(name))) {
				deleteName.add(name);
			}
		}
		for (String name : deleteName) {
			usernameAndIdMap.remove(name);
		}
		
		Map<Long, Map<Long, Double>> interaction = new HashMap<>();

		FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		List<String> fileList = fileUtil.getFileListSortByTimeASC(sourcePath,
				dateFormat);
		WriteUtil<String> writeUtil = new WriteUtil<>();
		Filter filter=new ChineseFilter();
		for (String OneFile : fileList) {
			System.out.println(OneFile);
			ReadWeibo rw = new ReadWeibo(OneFile);
			Retweetlist listRetweetlist;

			while ((listRetweetlist = rw.readRetweetList()) != null) {
				
				if (listRetweetlist.getRoot() == null||listRetweetlist.getRoot().getRetweetId()!=0)
					continue;
				
				OnePairTweet roottweet = listRetweetlist.getRoot();
				if (useridSet.contains(roottweet.getUserId())) {
					Set<String> targetNameList = filter
							.getAtName(roottweet.getContent());
					if (targetNameList != null) {
						// if mention
						interaction = addMention(roottweet.getUserId(),
								interaction, targetNameList, usernameAndIdMap,
								roottweet);
					}
				}

				List<OnePairTweet> list = listRetweetlist
						.getRetweetList();

				if (list != null) {

					for (int i = 0, size = list.size(); i < size; i++) {

						if (!useridSet.contains(list.get(i).getUserId())) {
							continue;
						}
						
							// the retweet path if from the retweet tree
							List<String> routeuserList = filter
									.getRouteName(list.get(i)
											.getContent());

							if (routeuserList != null && routeuserList.size() != 0
									&& i != 0) {
								
								//the first user
								for (String name : routeuserList) {
									if (usernameAndIdMap.containsKey(name)) {
										long anscetorid = usernameAndIdMap
												.get(name);
										if (anscetorid==list.get(i).getUserId()) {
											continue;
										}
										interaction = addretweet(anscetorid, list
												.get(i).getUserId(), interaction,
												list.get(i));
										break;
									}
								}
								
								//if no anscetor
								if (useridSet.contains(roottweet.getUserId())&&roottweet.getUserId()!=list.get(i).getUserId()) {
									interaction = addretweet(roottweet.getUserId(), list
											.get(i).getUserId(), interaction,
											list.get(i));
								}
							}else {
								if (useridSet.contains(roottweet.getUserId())) {
									interaction = addretweet(roottweet.getUserId(), list
											.get(i).getUserId(), interaction,
											list.get(i));
								}
							}

						// add mention intaction
							Set<String> targetNameList = filter
									.getAtName(list.get(i).getContent());
							if (targetNameList != null) {
								interaction = addMention(list.get(i)
										.getUserId(), interaction,
										targetNameList, usernameAndIdMap,
										list.get(i));
							}
					}
				}
			}
		}

		writeUtil.writemapkeyAndValueWhereValueinMap(interaction, path
				+ "userid/interactionCount.txt");

	}

	public static Map<Long, Map<Long, Double>> addMention(Long sourceuserid,
			Map<Long, Map<Long, Double>> mention,
			Set<String> targetNameList,
			HashMap<String, Long> usernameAndIdMap, OnePairTweet weibo) {
		List<String> result = new ArrayList<>();
		for (String name : targetNameList) {
			if (usernameAndIdMap.containsKey(name)) {

				long targetid = usernameAndIdMap.get(name);
				if (targetid==sourceuserid) {
					continue;
				}
				if (mention.containsKey(sourceuserid)) {
					if (mention.get(sourceuserid).containsKey(targetid)) {
						mention.get(sourceuserid).put(targetid,
								mention.get(sourceuserid).get(targetid) + 1);
					} else {
						mention.get(sourceuserid).put(targetid, 1.0);
					}
				} else {
					HashMap<Long, Double> tempHashMap = new HashMap<>();
					tempHashMap.put(targetid, 1.0);
					mention.put(sourceuserid, tempHashMap);
				}
				// 0 mention 1 retweet
				result.add(sourceuserid + "\t" + targetid + "\t"
						+ weibo.getCreateTime() + "\t" + 0);
			}
		}
		
		if (result.size()!=0) {
			WriteUtil<String> writeUtil = new WriteUtil<>();
			writeUtil.writeList(result, intectionFile);
		}
		
		return mention;
	}

	public Map<Long, Map<Long, Double>> addretweet(Long sourceuserid,
			long retweeterid, Map<Long, Map<Long, Double>> retweet,
			OnePairTweet weibo) {
		if (sourceuserid!=retweeterid) {
			if (retweet.containsKey(retweeterid)) {
				if (retweet.get(retweeterid).containsKey(sourceuserid)) {
					retweet.get(retweeterid).put(sourceuserid,
							retweet.get(retweeterid).get(sourceuserid) + 1);
				} else {
					retweet.get(retweeterid).put(sourceuserid, 1.0);
				}
			} else {
				HashMap<Long, Double> tempHashMap = new HashMap<>();
				tempHashMap.put(sourceuserid, 1.0);
				retweet.put(retweeterid, tempHashMap);
			}
			List<String> result = new ArrayList<>();
			result.add(retweeterid + "\t" + sourceuserid + "\t"
					+ weibo.getCreateTime() + "\t" + 1);
			WriteUtil<String> writeUtil = new WriteUtil<>();
			writeUtil.writeList(result, intectionFile);
		}
		return retweet;
	}
}
