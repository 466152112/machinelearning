/**
 * 
 */
package preprocess._0904;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.AvlTree;
import bean.AvlTree.AvlNode;
import bean.CompareableUser;
import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：StatisticsUserFollow
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月5日 下午6:02:18
 * @modifier：zhouge
 * @modified_time：2014年9月5日 下午6:02:18
 * @modified_note：
 * @version
 * 
 */
public class StatisticsSuperUser {
	static String path = "/home/zhouge/database/weibo/2012/";
	// static String path = "J:/workspace/weiboNew/data/myself/";
	// String target="";
	static String sourceFile = path + "useridandfollowers_count.csv";
	static String userInformation = path + "userIdAndCity1kw.txt";
	static String superNumberSplitByProvince = path
			+ "superNumberSplitByProvince.txt";
	static List<String> writeResult = new ArrayList<>();
	static WriteUtil<String> writeUtil = new WriteUtil<>();
	static Map<String, Integer> ProvinceFollowMap = new HashMap<String, Integer>();
	static Map<String, Integer> superNumberMap = new HashMap<>();
	static Set<Integer> followList = new HashSet();
	static double mediaFollow = 0;
	static AvlTree<CompareableUser> userTree;
	static AvlTree<CompareableUser> userTreeNeed = new AvlTree<CompareableUser>();

	public static void main(String[] args) {
		StatisticsSuperUser statisticsUserFollow = new StatisticsSuperUser();
		userTree = statisticsUserFollow.getUserAvl(sourceFile);

		long count = 0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(userInformation)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				String[] split = oneLine.split(",");
				CompareableUser user = new CompareableUser();
				user.setUserId(Long.valueOf(split[0]));
				user = userTree.getElement(user, userTree.root);
				if (user != null) {
					followList.add(user.getFollowers_Count());
					user.setProvince(split[2]);
					userTreeNeed.insert(user);
				}
				count++;
			}
			bufferedReader.close();
			System.out.println("read user success");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(count);
		List<Integer> tempList = new ArrayList<>(followList);
		Collections.sort(tempList);
		// 计算中位数
		if (tempList.size() % 2 == 1) {
			mediaFollow = tempList.get(tempList.size() / 2);
		} else {
			mediaFollow = (tempList.get(tempList.size() / 2 - 1) + tempList
					.get(tempList.size() / 2)) * 1.0 / 2;
		}
		System.out.println("end mediaFollow" + mediaFollow);

		statisticsUserFollow.getSuperNumber(userTreeNeed.root);
		writeUtil.writemapkeyAndValueInInteger(superNumberMap,
				superNumberSplitByProvince);
		System.out.println("success");
	}

	// 中序遍历avl�?
	public void getSuperNumber(AvlNode<CompareableUser> t) {
		if (t != null) {
			getSuperNumber(t.getLeft());
			CompareableUser compareableUser = t.getElement();

			if (compareableUser.getFollowers_Count() > mediaFollow) {
				String province = compareableUser.getProvince();
				if (superNumberMap.containsKey(province)) {
					superNumberMap.put(province,
							superNumberMap.get(province) + 1);
				} else {
					superNumberMap.put(province, 1);
				}
			}
			getSuperNumber(t.getRight());
		}
	}

	/**
	 * @param userInformationFile
	 * @return
	 * @create_time：2014年9月4日上午11:04:52
	 * @modifie_time：2014年9月4日 上午11:04:52
	 */
	public AvlTree<CompareableUser> getUserAvl(String userInformationFile) {
		AvlTree<CompareableUser> userTree = new AvlTree<CompareableUser>();
		long count = 0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(userInformationFile)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				String[] split = oneLine.split(",");
				CompareableUser user = new CompareableUser();
				user.setUserId(Long.valueOf(split[0]));
				user.setFollowers_Count(Integer.valueOf(split[1]));
				userTree.insert(user);
				oneLine = bufferedReader.readLine();
				count++;
			}
			bufferedReader.close();
			System.out.println("read user success");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(count);
		return userTree;
	}

}
