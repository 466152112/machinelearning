/**
 * 
 */
package model.TKIPlot;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

import model.link.ComputeCommonNeighborByOutdegree;
import bean.User;
import util.MathCal;
import util.ReadUtil;
import util.SplitTrainAndTest;
import util.TwoValueComparatorInLong;
import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：RandomWalk
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年11月18日 下午9:16:43
 * @modifier：zhouge
 * @modified_time：2014年11月18日 下午9:16:43
 * @modified_note：
 * @version
 * 
 */
@SuppressWarnings("serial")
public class RandomWalk {
	static Map<String, Set<String>> followGraph = null;
	static Map<String, Set<String>> deleteEdge = null;
	static final int followeethreshold = 1;
	 private static String path="J:/workspacedata/weiboNew/data/2w/";
	//static String path = "J:/workspacedata/twitter/data/twitter/";

	// static String path = "/home/zhouge/database/weibo/2w/";

	// static String path = "/home/zhouge/database/twitter/twitter/";

	// String path = "/home/zhouge/database/twitter/twitter/";
	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		RandomWalk ccn = new RandomWalk();
		String followGraphFile = path + "followgraph.txt";
		followGraph = new ReadUtil().getFollowGraph(followGraphFile);
		deleteEdge = ccn.getDeleteEdge();
		Map<String, Set<String>> convertMap = ccn.convertMap(followGraph);
		int count=3000;
		
		for (String targetid : deleteEdge.keySet()) {
			for (String sourceid : deleteEdge.get(targetid)) {

				Set<String> followSet = followGraph.get(targetid);
				Set<String> bridgeuserid = new HashSet<>();

				for (String userid : followSet) {
					if (followGraph.get(userid).contains(sourceid)) {
						bridgeuserid.add(userid);
					}
				}
				if (convertMap.containsKey(sourceid)&&bridgeuserid.size()>0) {
				double ratio=	ccn.getPathImportance(convertMap, sourceid, targetid,
							bridgeuserid);
				System.out.println(count+"\t"+ratio);
					count++;
				}
				
			}
		}
	}

	public double getPathImportance(Map<String, Set<String>> convertMap,
			String sourceid, String targetid, Set<String> bridgeuserid) {
		int maxstep =3;
		//三步有百分之99
		int stepnumber = 0;
		HashMap<String, Double> randomValue = new HashMap<>();
		randomValue.put(sourceid, 1.0);
		double sum = 0;
		double brigesum = 0;
		while (stepnumber < maxstep) {
			HashMap<String, Double> newrandomValue = new HashMap<>();
			// 对于每一个点上的值下走
			for (String walkid : randomValue.keySet()) {
				if (convertMap.containsKey(walkid)) {
					// 向下走
					for (String nextstepid : convertMap.get(walkid)) {
						if (nextstepid.equals(targetid)) {
							sum+=randomValue.get(walkid)
							/ convertMap.get(walkid).size();
							if (bridgeuserid.contains(walkid)) {
								brigesum+=randomValue.get(walkid)
										/ convertMap.get(walkid).size();
							}
						}
						else if (newrandomValue.containsKey(nextstepid)) {
							newrandomValue.put(nextstepid,
									newrandomValue.get(nextstepid)
											+ randomValue.get(walkid)
											/ convertMap.get(walkid).size());
						} else {
							newrandomValue.put(
									nextstepid,
									randomValue.get(walkid)
											/ convertMap.get(walkid).size());
						}
					}
				} else {
					if (newrandomValue.containsKey(walkid)) {
						newrandomValue.put(walkid, newrandomValue.get(walkid)
								+ randomValue.get(walkid));
					} else {
						newrandomValue.put(walkid, randomValue.get(walkid));
					}

				}
			}
			randomValue = newrandomValue;
			stepnumber++;
		}
		return brigesum/sum;
	}

	public Map<String, Set<String>> convertMap(
			Map<String, Set<String>> followGraph) {
		Map<String, Set<String>> covertMap = new HashMap<>();
		for (String key : followGraph.keySet()) {
			for (String element : followGraph.get(key)) {
				if (covertMap.containsKey(element)) {
					covertMap.get(element).add(key);
				} else {
					Set<String> temp = new HashSet<>();
					temp.add(key);
					covertMap.put(element, temp);
				}
			}
		}
		return covertMap;
	}

	/**
	 * @return 鑷姩鐢熸垚鏂竟 鍒涘缓鏃堕棿锟?014锟?锟?7鏃ヤ笅锟?:35:45
	 *         淇敼鏃堕棿锟?014锟?锟?7锟?涓嬪崍1:35:45
	 */
	private Map<String, Set<String>> getDeleteEdge() {
		// 鑾峰彇鍒犻櫎锟?
		Map<String, Set<String>> deleteEdge = new HashMap<String, Set<String>>();

		Random random = new Random(System.currentTimeMillis());
		Iterator<String> iteratorUserId = followGraph.keySet().iterator();
		ArrayList<String> userList = new ArrayList<>(followGraph.keySet());
		while (iteratorUserId.hasNext()) {
			String userId = iteratorUserId.next();
			Set<String> followeeSet = followGraph.get(userId);
			if (followeeSet.size() < followeethreshold) {
				continue;
			}
			ArrayList<String> followeeList = new ArrayList<>(followeeSet);

			Set<String> tempdeleteEdge = new HashSet<>();
			int followeeSetSize = followeeSet.size();
			int count = 0, threshold = doubleUp(followeeSetSize * 0.1);

			while (count < threshold) {
				int randomIndex = random.nextInt(followeeList.size());
				if (userId.equals(followeeList.get(randomIndex))
						|| !userList.contains(followeeList.get(randomIndex))) {
					continue;
				}
				tempdeleteEdge.add(followeeList.get(randomIndex));
				followeeList.remove(randomIndex);
				count++;
			}
			followGraph.get(userId).removeAll(tempdeleteEdge);
			if (tempdeleteEdge.size() == 0) {
				continue;
			}
			deleteEdge.put(userId, tempdeleteEdge);
		}
		return deleteEdge;
	}

	private int doubleUp(double kk) {
		BigDecimal kBigDecimal = new BigDecimal(kk).setScale(0,
				BigDecimal.ROUND_HALF_UP);
		// System.out.println(kk+"\t"+ kBigDecimal.intValue());
		return kBigDecimal.intValue();
	}
}
