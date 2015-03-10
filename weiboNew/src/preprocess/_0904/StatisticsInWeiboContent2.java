/**
 * 
 */
package preprocess._0904;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import preprocess._0828.SearchForRetweetWeibo;
import util.ReadUtil;
import util.ReadWeiboUtil;
import util.WriteUtil;
import bean.AvlTree;
import bean.CompareableUser;
import bean.OnePairTweet;
import bean.AvlTree.AvlNode;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：StatisticsInWeiboContent
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月4日 上午8:36:49
 * @modifier：zhouge
 * @modified_time：2014年9月4日 上午8:36:49
 * @modified_note：
 * @version
 * 
 */
public class StatisticsInWeiboContent2 {
	static String path = "/home/zhouge/database/weibo/2012/";
	// static String path = "J:/workspace/weiboNew/data/myself/";
	// String target="";
	static String target = path + "retweetType678.txt";
	static String weiboNumberSplitByProvinceAndHour = path
			+ "weiboNumberSplitByProvinceAndHour.txt";
	static String weiboFlowSplitByProvinceAndHour = path
			+ "weiboFlowSplitByProvinceAndHour.txt";
	static String userInformation = path + "userInformation.txt";
	static String sourcePath = path + "content/";
	static List<String> writeResult = new ArrayList<>();
	static WriteUtil<String> writeUtil = new WriteUtil<>();
	static Map<String, Integer> weiboNumberSplitByProvinceAndHourMap = new HashMap<>();
	static Map<String, Integer> weiboFlowSplitByProvinceAndHourMap = new HashMap<>();

	// 主程�?
	public static void main(String[] args) {

		StatisticsInWeiboContent2 statisticsInWeiboContent = new StatisticsInWeiboContent2();
		AvlTree<CompareableUser> userTree = statisticsInWeiboContent
				.getUserAvl(userInformation);
		List<String> readerFile = statisticsInWeiboContent
				.getSourceFile(sourcePath);

		AvlTree<OnePairTweet> sourceWeiboTree;
		ReadWeiboUtil readWeiboUtil = null;
		ReadWeiboUtil readTarget = null;
		// read one By One
		for (int i = 0; i < readerFile.size(); i++) {
			Set<Long> sourceIdSet = new HashSet<>();
			// read the begin file
			readWeiboUtil = new ReadWeiboUtil(readerFile.get(i));
			OnePairTweet temp = new OnePairTweet();
			sourceWeiboTree = new AvlTree<>();
			int count = 0;
			while ((temp = readWeiboUtil.readOnePairTweetInMen()) != null) {
				count++;
				CompareableUser user = new CompareableUser();
				user.setUserId(temp.getUserId());
				user = userTree.getElement(user, userTree.root);

				if (user != null) {
					String ProvinceAndHour = user.getProvince() + "\t"
							+ temp.getCreate_time().get(Calendar.HOUR_OF_DAY);
					if (weiboNumberSplitByProvinceAndHourMap
							.containsKey(ProvinceAndHour)) {
						weiboNumberSplitByProvinceAndHourMap.put(
								ProvinceAndHour,
								weiboNumberSplitByProvinceAndHourMap
										.get(ProvinceAndHour) + 1);
					} else {
						weiboNumberSplitByProvinceAndHourMap.put(
								ProvinceAndHour, 1);
					}
				}
				// 如果没有这个用户则继续
				else {
					continue;
				}
//				// 如果有转发信息则保存
//				if (temp.getRetweetId() != 0L) {
//					CompareableUser sourceUser = new CompareableUser();
//					sourceUser.setUserId(temp.getRetweetUser_id());
//					sourceUser = userTree.getElement(sourceUser, userTree.root);
//					// 如果没早到则不处理
//					if (sourceUser == null)
//						continue;
//
//					String sourceToTargetProvince = user.getProvince() + "_"
//							+ sourceUser.getProvince() + "_"
//							+ temp.getCreate_time().get(Calendar.HOUR_OF_DAY);
//					if (weiboFlowSplitByProvinceAndHourMap
//							.containsKey(sourceToTargetProvince)) {
//						weiboFlowSplitByProvinceAndHourMap.put(
//								sourceToTargetProvince,
//								weiboFlowSplitByProvinceAndHourMap
//										.get(sourceToTargetProvince) + 1);
//					} else {
//						weiboFlowSplitByProvinceAndHourMap.put(
//								sourceToTargetProvince, 1);
//					}
//				}

				if (temp.getRetweetId()==0L&&!sourceIdSet.contains(temp.getWeiboId())) {
					sourceIdSet.add(temp.getWeiboId());
					sourceWeiboTree.insert(temp);
				}else if (sourceIdSet.contains(temp.getWeiboId())) {
					//System.out.println("error");
					continue;
				}

				if (sourceWeiboTree.root != null
						&& sourceWeiboTree.root.height == 22) {

					// then read the file and the next files to find weibo which
					// is
					// retweet from that sourceWeibo
					for (int j = i; j < readerFile.size(); j++) {
						// cal the date difference
						int dateDifference = calDateDifference(
								readerFile.get(j), readerFile.get(i));
						if (dateDifference > 20) {
							break;
						}
						readTarget = new ReadWeiboUtil(readerFile.get(j));

						System.out.println("begin:" + readerFile.get(i)
								+ "\t scan from \t" + readerFile.get(j));
						sourceWeiboTree = readTarget
								.findTweetByReWeiboIdInTree(sourceWeiboTree);
						System.out.println("end:" + readerFile.get(i)
								+ "\t scan from \t" + readerFile.get(j));
						readTarget.closeReader();
					}
					// write each time
					writeToFile(sourceWeiboTree.root);
					sourceWeiboTree = null;
					sourceWeiboTree = new AvlTree();
				} else {
					continue;
				}
			}

			if (sourceWeiboTree.root != null) {
				// then read the file and the next files to find weibo which is
				// retweet from that sourceWeibo
				for (int j = i; j < readerFile.size(); j++) {
					int dateDifference = calDateDifference(readerFile.get(j),
							readerFile.get(i));
					if (dateDifference > 10) {
						break;
					}
					readTarget = new ReadWeiboUtil(readerFile.get(j));
					System.out.println(readerFile.get(i) + "\t scan from \t"
							+ readerFile.get(j));
					sourceWeiboTree = readTarget
							.findTweetByReWeiboIdInTree(sourceWeiboTree);
					readTarget.closeReader();
				}
				// write each time
				writeToFile(sourceWeiboTree.root);
			}
			readWeiboUtil.closeReader();
			System.out.println(readerFile.get(i) + " lineNumber:" + count);
		}

		// writer the last
		writeUtil.writeList(writeResult, target);
//
//		writeUtil.writemapkeyAndValueInInteger(
//				weiboNumberSplitByProvinceAndHourMap,
//				weiboNumberSplitByProvinceAndHour);
//		writeUtil.writemapkeyAndValueInInteger(
//				weiboFlowSplitByProvinceAndHourMap,
//				weiboFlowSplitByProvinceAndHour);
	}

	/**
	 * @param userInformationFile
	 * @return
	 * @create_time：2014年9月4日上午11:04:52
	 * @modifie_time：2014年9月4日 上午11:04:52
	 */
	public AvlTree<CompareableUser> getUserAvl(String userInformationFile) {
		AvlTree<CompareableUser> userTree = new AvlTree<CompareableUser>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(userInformation)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				String[] split = oneLine.split(",");
				CompareableUser user = new CompareableUser();
				user.setUserId(Long.valueOf(split[0]));
				user.setName(split[1]);
				user.setProvince(split[2]);
				user.setMan(split[3] == "m" ? true : false);
				userTree.insert(user);
				oneLine = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return userTree;
	}

	// 中序遍历avl�?
	public static void writeToFile(AvlNode<OnePairTweet> t) {
		if (t != null) {
			writeToFile(t.getLeft());
			OnePairTweet temPairTweet = t.getElement();
			List<OnePairTweet> reTweets = temPairTweet.getRetweetList();
			if (reTweets != null && reTweets.size() > 0) {
				writeResult.add(temPairTweet.toString());
				for (OnePairTweet onePairTweet : reTweets) {
					writeResult.add(onePairTweet.toString());
				}
				writeResult.add("");
				if (writeResult.size() > 1000) {
					writeUtil.writeList(writeResult, target);
					writeResult = new ArrayList<>();
				}
			}
			// set the retweetList to null
			t.getElement().setRetweetList(null);

			writeToFile(t.getRight());
		}
	}

	/**
	 * @param path
	 * @return
	 * @create_time：2014年9月4日上午11:05:00
	 * @modifie_time：2014年9月4日 上午11:05:00
	 */
	public List<String> getSourceFile(String path) {
		File pathFile = new File(path);
		File[] Filelist = pathFile.listFiles();
		HashMap<String, Calendar> fileMap = new HashMap<>();
		List<String> result = new ArrayList<>();
		for (int j = 0; j < Filelist.length; j++) {
			File file = Filelist[j];
			if (file.isFile()) {
				String dayString = file.getName().substring(0,
						file.getName().indexOf("."));
				Date date = getDay(dayString);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				fileMap.put(file.getPath(), calendar);
			}
		}
		List<String> fileList = new ArrayList<>(fileMap.keySet());
		CalendarComparator calendarComparator = new CalendarComparator(fileMap);
		Collections.sort(fileList, calendarComparator);
		return fileList;
	}

	/**
	 * @param time
	 * @return
	 * @create_time：2014年9月4日上午11:05:03
	 * @modifie_time：2014年9月4日 上午11:05:03
	 */
	public static Date getDay(String time) {

		final SimpleDateFormat sf1 = new SimpleDateFormat("yyyy_MM_dd",
				Locale.ENGLISH);
		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @progject_name：weiboNew
	 * @class_name：CalendarComparator
	 * @class_describe：
	 * @creator：zhouge
	 * @create_time：2014年9月4日 上午11:05:09
	 * @modifier：zhouge
	 * @modified_time：2014年9月4日 上午11:05:09
	 * @modified_note：
	 * @version
	 * 
	 */
	private static class CalendarComparator implements Comparator<String> {
		HashMap<String, Calendar> baseHashMap = null;

		/**
		 * @param baseHashMap
		 */
		public CalendarComparator(HashMap<String, Calendar> baseHashMap) {
			this.baseHashMap = baseHashMap;
		}

		public int compare(String arg0, String arg1) {
			return this.baseHashMap.get(arg0).compareTo(
					this.baseHashMap.get(arg1));
		}

	}

	/**
	 * @param fileName1
	 *            big
	 * @param fileName2
	 *            small
	 * @return
	 * @create_time：2014年8月31日下午9:33:43
	 * @modifie_time：2014年8月31日 下午9:33:43
	 */
	public static int calDateDifference(String fileName1, String fileName2) {
		int index = fileName1.lastIndexOf("/");
		if (index == -1) {
			index = fileName1.lastIndexOf("\\");
		}
		Date date1 = getDay(fileName1.substring(index + 1,
				fileName1.length() - 4));

		index = fileName2.lastIndexOf("/");
		if (index == -1) {
			index = fileName2.lastIndexOf("\\");
		}
		Date date2 = getDay(fileName2.substring(index + 1,
				fileName2.length() - 4));

		return (int) ((date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000));
	}
}
