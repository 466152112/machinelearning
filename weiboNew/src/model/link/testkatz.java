package model.link;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import util.ReadUtil;
import util.TwoValueComparatorInString;
import util.WriteUtil;
import util.Matrix.Matrix;

public class testkatz {
	static Map<String, Set<String>> followGraph = null;
	static Map<String, Set<String>> deleteEdge = null;
	static final int followeethreshold = 1;
	static final double lamda = 0.01;
	static String path = "J:/workspace/twitter/data/twitter/";
	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		testkatz ccn = new testkatz();
		int[] calculatedLength = { 1, 2, 5, 10, 15, 20, 30, 40, 50, 60, 70, 80,
				90, 100 };
		for (int i = 0; i < 10; i++) {
			String followGraphFile = path + "followgraph.txt";
			followGraph = new ReadUtil().getFollowGraph(followGraphFile);
			System.out.println(followGraph.keySet().size());
			String deleteFile = path + "deletedge.txt";
			 //String deleteFile = path + "test.txt";
			 deleteEdge=new ReadUtil().getFollowGraph(deleteFile);
			 Iterator<String> deleteuser=deleteEdge.keySet().iterator();
			 System.out.println(deleteEdge.keySet().size());
			 while (deleteuser.hasNext()) {
				String keyuser = (String) deleteuser.next();
				followGraph.get(keyuser).removeAll(deleteEdge.get(keyuser));
			}
			 System.out.println(followGraph.keySet().size());
			//deleteEdge = ccn.getDeleteEdge();

			// 结果写入文件�?
			String recallPath = path + "recall/";
			String precisionPath = path + "precision/";
			String AUCPath = path + "AUC/";

			WriteUtil writeUtil = new WriteUtil();
			double[] recall = new double[calculatedLength.length];
			double[] precision = new double[calculatedLength.length];
			Map<String, List<Double>> Value = new HashMap<>();
			List<String> deleteUserList = new ArrayList<>();
			double AUC = 0;
			
			System.out.println("++++++++++Katz+++++++++++");
			Value = ccn.Katz();
			deleteUserList = new ArrayList<>(deleteEdge.keySet());
			AUC = 0;

			recall = new double[calculatedLength.length];
			precision = new double[calculatedLength.length];

			for (String userid : deleteUserList) {
				List<Double> temp = Value.get(userid);
				// 第一个�?为AUC
				AUC += temp.get(0);
				// 截取recall
				List<Double> subRecall = temp.subList(1,
						calculatedLength.length + 1);
				for (int j = 0; j < subRecall.size(); j++) {
					recall[j] += subRecall.get(j);
				}
				// 截取Precision
				List<Double> subPrecision = temp.subList(
						calculatedLength.length + 1, temp.size());
				for (int j = 0; j < subPrecision.size(); j++) {
					precision[j] += subPrecision.get(j);
				}

			}

			// 求平�?
			for (int j = 0; j < calculatedLength.length; j++) {
				recall[j] /= deleteUserList.size();
				precision[j] /= deleteUserList.size();
			}
			writeUtil.writeOneLine(String.valueOf(AUC / deleteUserList.size()),
					AUCPath + "Katz.txt");
			writeUtil.writeOneLine(listToString(recall), recallPath
					+ "Katz.txt");
			writeUtil.writeOneLine(listToString(precision), precisionPath
					+ "Katz.txt");

		}

	}

	public static String listToString(double[] value) {
		StringBuffer resultBuffer = new StringBuffer();
		for (double double1 : value) {
			resultBuffer.append(double1 + "\t");
		}
		return resultBuffer.toString();
	}

	// 计算召回�?
	public List<Double> calRecallRatioValue(List<String> sortEdge, String userId) {
		ArrayList<Double> result = new ArrayList<>();
		int[] calculatedLength = { 1, 2, 5, 10, 15, 20, 30, 40, 50, 60, 70, 80,
				90, 100 };
		for (int i = 0; i < calculatedLength.length; i++) {
			double reconveyEdge = 0;
			// 获取�?��用户断的�?���?
			Set<String> OneLinedeleteEdge = deleteEdge.get(userId);

			// 截取前i条边
			ArrayList<String> subedge = new ArrayList<>(sortEdge.subList(0,
					Math.min(calculatedLength[i], sortEdge.size())));
			for (String userIndex : OneLinedeleteEdge) {
				if (subedge.contains(userIndex)) {
					reconveyEdge++;
				}
			}
			result.add(reconveyEdge / OneLinedeleteEdge.size());
		}
		return result;
	}

	// 计算准确�?
	public List<Double> calPrecisionValue(List<String> sortEdge, String userId) {
		ArrayList<Double> result = new ArrayList<>();
		int[] calculatedLength = { 1, 2, 5, 10, 15, 20, 30, 40, 50, 60, 70, 80,
				90, 100 };
		for (int i = 0; i < calculatedLength.length; i++) {
			double reconveyEdge = 0;
			// 获取�?��用户断的�?���?
			Set<String> OneLinedeleteEdge = deleteEdge.get(userId);

			// 截取前i条边
			ArrayList<String> subedge = new ArrayList<>(sortEdge.subList(0,
					Math.min(calculatedLength[i], sortEdge.size())));
			for (String userIndex : OneLinedeleteEdge) {
				if (subedge.contains(userIndex)) {
					reconveyEdge++;
				}
			}
			result.add(reconveyEdge / calculatedLength[i]);
		}
		return result;
	}

	// 计算AUC
	public double callAUCValue(Map<String, Double> oneValue, String userId) {
		double result = 0;
		int total = 0;
		int positiveTotal = 0;
		int EqualTotal = 0;
		int compareNumber = 100;
		List<String> userList = new ArrayList<>(followGraph.keySet());
		Set<String> OneUserDeleteEdge = deleteEdge.get(userId);
		Iterator<String> iterator = OneUserDeleteEdge.iterator();
		while (iterator.hasNext()) {
			String followeeId = iterator.next();
			double edgeValue = oneValue.get(followeeId);
			//System.out.println(edgeValue);
			// 用于比较的边
			ArrayList<String> compareEdge = randomNegativeEdge(userList,
					followGraph.get(userId), compareNumber);
			for (int k = 0; k < compareEdge.size(); k++) {
				if (edgeValue > oneValue.get(compareEdge.get(k))) {
					positiveTotal++;
				} else if (edgeValue == oneValue.get(compareEdge.get(k))) {
					EqualTotal++;
				}
				total++;
			}
		}

		return result = ((1.0) * (positiveTotal + EqualTotal * 0.5)) / total;
	}

	// 随机生成number条负�?
	public ArrayList<String> randomNegativeEdge(List<String> userList,
			Set<String> followeeset, int number) {
		ArrayList<String> result = new ArrayList<>();
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		int count = 0;
		while (count < number) {
			int temp = random.nextInt(userList.size());
			if (!followeeset.contains(userList.get(temp))
					&& !result.contains(userList.get(temp))) {
				result.add(userList.get(temp));
				count++;
			}
		}
		return result;

	}

	// 放回降序排序后的用户下标
	public List<String> sortByValue(HashMap<String, Double> value, String userId) {
		List<String> result = new ArrayList<>();
		TwoValueComparatorInString tvc = new TwoValueComparatorInString(value, 0);

		ArrayList<String> keyList = new ArrayList<>(value.keySet());
		Collections.sort(keyList, tvc);
		// 去除存在的變
		Set<String> edgeList = followGraph.get(userId);

		keyList.removeAll(edgeList);
		keyList.remove(userId);
		result = keyList;
		return result;
	}

	/**
	 * @return 自动生成断边 创建时间�?014�?�?7日下�?:35:45
	 *         修改时间�?014�?�?7�?下午1:35:45
	 */
	private Map<String, Set<String>> getDeleteEdge() {
		// 获取删除�?
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

		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeMapValueIsSet(deleteEdge, "deleteEdge.txt");
		writeUtil.writeMapValueIsSet(followGraph, "trainEdge.txt");
		return deleteEdge;
	}


	public Map<String, List<Double>> Katz() {

		Map<String, List<Double>> CNValue = new HashMap<>();
		Iterator<String> userIterator = followGraph.keySet().iterator();
		ArrayList<String> userList = new ArrayList<>(followGraph.keySet());
		Matrix followMatrix = new Matrix(userList.size(), userList.size(), 0);
//		while (userIterator.hasNext()) {
//			String userId = userIterator.next();
//			Set<String> followSet = followGraph.get(userId);
//			int indexuser = userList.indexOf(userId);
//			for (String follow : followSet) {
//				int indexfollow = userList.indexOf(follow);
//				followMatrix.set(indexuser, indexfollow, 1);
//			}
//		}

 //save the deleteedge
//		 WriteUtil<String> writeUtil=new WriteUtil<>();
//		 writeUtil.writeMapSetElement(deleteEdge, "/home/zhouge/database/twitter/twitter/"+"deletedge.txt");

		 Matrix siMatrix = Katz(followMatrix);
		
	
		userIterator = deleteEdge.keySet().iterator();
		while (userIterator.hasNext()) {
			String userId = userIterator.next();
			int indexuser = userList.indexOf(userId);
			HashMap<String, Double> temp = new HashMap();
			for (int ii = 0; ii < followGraph.size(); ii++) {
				if (!userId.equals(userList.get(ii))) {
					double value = siMatrix.get(indexuser, ii);
					temp.put(userList.get(ii), value);
				} else {
					temp.put(userId, 0.0);
				}

			}
			List<String> sortResultList = sortByValue(temp, userId);
			List<Double> result = new ArrayList<>();
			double auc = callAUCValue(temp, userId);
			List<Double> pre = calPrecisionValue(sortResultList, userId);
			List<Double> recall = calRecallRatioValue(sortResultList, userId);
			result.add(auc);
			result.addAll(recall);
			result.addAll(pre);
			CNValue.put(userId, result);
		}

		return CNValue;

	}

	public static Matrix Katz(Matrix siMatrix) {

		// sim = inv( sparse(eye(size(train,1))) - lambda * train);
		// 相似性矩阵的计算
		// sim = sim - sparse(eye(size(train,1)));
		
		int Dim = siMatrix.getColumnDimension();
//		for (int i = 0; i < Dim; i++) {
//			for (int ii = 0; ii < Dim; ii++) {
//				siMatrix.set(i, ii, 0);
//			}
//		}
		
//		for (int i = 0; i < Dim; i++) {
//			for (int ii = 0; ii < Dim; ii++) {
//				double temp=sourceGraphMatrix.get(i, ii);
//				temp=temp*(-lamda);
//				if (i==ii) {
//					sourceGraphMatrix.set(i, ii, 1+temp);
//				}else {
//					sourceGraphMatrix.set(i, ii, temp);
//				}
//			}
//		}
//		WriteUtil<String> writeUtil=new WriteUtil<>();
//		for (int i = 0; i < Dim; i++) {
//			StringBuffer kkBuffer=new StringBuffer();
//			for (int ii = 0; ii < Dim; ii++) {
//			
//				kkBuffer.append(sourceGraphMatrix.get(i, ii)+"\t");
//			}
//			String tempString=kkBuffer.toString().trim();
//			writeUtil.writeOneLine(tempString, "J:/workspace/twitter/data/twitter/katz.matrix");
//		}
//		sourceGraphMatrix=sourceGraphMatrix.inverse();
//		for (int i = 0; i < Dim; i++) {
//			for (int ii = 0; ii < Dim; ii++) {
//				double temp=sourceGraphMatrix.get(i, ii);
//				if (i==ii) {
//					sourceGraphMatrix.set(i, ii, temp-1);
//				}
//			}
//		}
		// inv( sparse(eye(size(train,1))) - lambda * train)

	
		
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> kaztmatrxlineList=readUtil.readFileByLine("/home/xiaoqiang/twitter/1w/inv/matrix0.txt");
		
		int count=0;
		for (String oneline : kaztmatrxlineList) {
			
			String[] split=oneline.trim().split("\t");
			for (int i = 0; i < split.length; i++) {
				siMatrix.set(count, i, Double.valueOf(split[i]));
			}
			
			count++;
		}
		// sim = sim - sparse(eye(size(train,1)));
		siMatrix=siMatrix.minus(Matrix.identity(Dim, Dim));

		return siMatrix;
	}

	private int doubleUp(double kk) {
		BigDecimal kBigDecimal = new BigDecimal(kk).setScale(0,
				BigDecimal.ROUND_HALF_UP);
		// System.out.println(kk+"\t"+ kBigDecimal.intValue());
		return kBigDecimal.intValue();
	}
}
