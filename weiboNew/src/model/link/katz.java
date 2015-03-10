/**
 * 
 */
package model.link;

/**   
 *    
 * @progject_name��weiboNew   
 * @class_name��katz   
 * @class_describe��   
 * @creator��zhouge   
 * @create_time��2014��10��6�� ����10:12:22   
 * @modifier��zhouge   
 * @modified_time��2014��10��6�� ����10:12:22   
 * @modified_note��   
 * @version    
 *    
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import util.ReadUtil;
import util.TwoValueComparatorInInteger;
import util.WriteUtil;
import util.Matrix.Matrix;

public class katz {
	static Map<Integer, Set<Integer>> followGraph = new HashMap();
	static Map<Integer, Set<Integer>> deleteEdge = new HashMap();
	static final int followeethreshold = 1;
	static final double lamda = 0.01;
	// static String path = "/home/xiaoqiang/twitter/1w/finaldata/0/";
	// static String path = "/home/zhouge/database/twitter/twitter/";
	static String path = "J:/workspace/twitter/data/twitter/xiaoqiang/";
	static int numberofuser=11016;
	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		katz ccn = new katz();
		int[] calculatedLength = { 1, 2, 5, 10, 15, 20, 30, 40, 50, 60, 70, 80,
				90, 100 };
		for (int i = 0; i < 10; i++) {
			// String followGraphFile = path + "trainlist.txt";
			String followGraphFile = path + "train.txt";
			List<String> listedsa = new ReadUtil()
					.readFileByLine(followGraphFile);
			for (String oneline : listedsa) {
				String[] split = oneline.split("\t");
				int user1 = Integer.valueOf(split[0].trim());
				int user2 = Integer.valueOf(split[1].trim());
				
				if (user1==user2) {
					System.out.println();
					continue;
				}
				if (followGraph.containsKey(user1)) {
					followGraph.get(user1).add(user2);
				} else {
					Set<Integer> temp = new HashSet<>();
					temp.add(user2);
					followGraph.put(user1, temp);
				}
				if (!followGraph.containsKey(user2)) {
					Set<Integer> temp = new HashSet<>();
					followGraph.put(user2, temp);
				}
			}
			System.out.println(followGraph.keySet().size());
			String deleteFile = path + "test.txt";
			listedsa = new ReadUtil().readFileByLine(deleteFile);
			for (String oneline : listedsa) {
				String[] split = oneline.split("\t");
				int user1 = Integer.valueOf(split[0].trim());
				int user2 = Integer.valueOf(split[1].trim());
				if (user1==user2) {
					System.out.println();
					continue;
				}
				if (deleteEdge.containsKey(user1)) {
					deleteEdge.get(user1).add(user2);
				} else {
					Set<Integer> temp = new HashSet<>();
					temp.add(user2);
					deleteEdge.put(user1, temp);
				}
			}
			System.out.println(deleteEdge.keySet().size());
			// deleteEdge = ccn.getDeleteEdge();

			// 结果写入文件�?
			String recallPath = path + "recall/";
			String precisionPath = path + "precision/";
			String AUCPath = path + "AUC/";

			WriteUtil writeUtil = new WriteUtil();
			double[] recall = new double[calculatedLength.length];
			double[] precision = new double[calculatedLength.length];
			Map<Integer, List<Double>> Value = new HashMap<>();
			List<Integer> deleteUserList = new ArrayList<>();
			double AUC = 0;

			System.out.println("++++++++++Katz+++++++++++");
			Value = ccn.Katz();
			deleteUserList = new ArrayList<>(deleteEdge.keySet());
			AUC = 0;

			recall = new double[calculatedLength.length];
			precision = new double[calculatedLength.length];

			for (Integer userid : deleteUserList) {
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
	public List<Double> calRecallRatioValue(List<Integer> sortEdge,
			Integer userId) {
		ArrayList<Double> result = new ArrayList<>();
		int[] calculatedLength = { 1, 2, 5, 10, 15, 20, 30, 40, 50, 60, 70, 80,
				90, 100 };
		for (int i = 0; i < calculatedLength.length; i++) {
			double reconveyEdge = 0;
			// 获取�?��用户断的�?���?
			Set<Integer> OneLinedeleteEdge = deleteEdge.get(userId);
			// 截取前i条边
			ArrayList<Integer> subedge = new ArrayList<>(sortEdge.subList(0,calculatedLength[i]));
			for (Integer userIndex : OneLinedeleteEdge) {
				if (subedge.contains(userIndex)) {
					reconveyEdge++;
				}
			}
			result.add(reconveyEdge / OneLinedeleteEdge.size());
		}
		return result;
	}

	// 计算准确�?
	public List<Double> calPrecisionValue(List<Integer> sortEdge, Integer userId) {
		ArrayList<Double> result = new ArrayList<>();
		int[] calculatedLength = { 1, 2, 5, 10, 15, 20, 30, 40, 50, 60, 70, 80,
				90, 100 };
		for (int i = 0; i < calculatedLength.length; i++) {
			double reconveyEdge = 0;
			// 获取�?��用户断的�?���?
			Set<Integer> OneLinedeleteEdge = deleteEdge.get(userId);

			// 截取前i条边
			ArrayList<Integer> subedge = new ArrayList<>(sortEdge.subList(0,calculatedLength[i]));
			for (Integer userIndex : OneLinedeleteEdge) {
				if (subedge.contains(userIndex)) {
					reconveyEdge++;
				}
			}
			result.add(reconveyEdge / calculatedLength[i]);
		}
		return result;
	}

	// 计算AUC
	public double callAUCValue(Map<Integer, Double> oneValue, Integer userId) {
		double result = 0;
		int total = 0;
		int positiveTotal = 0;
		int EqualTotal = 0;
		List<Integer> userList = new ArrayList<>(followGraph.keySet());
		Set<Integer> OneUserDeleteEdge = deleteEdge.get(userId);
		Iterator<Integer> iterator = OneUserDeleteEdge.iterator();
		while (iterator.hasNext()) {
			Integer followeeId = iterator.next();
			double edgeValue = oneValue.get(followeeId);
			
			// System.out.println(edgeValue);
			// 用于比较的边
			
			for (int k = 0; k < numberofuser; k++) {
				if (OneUserDeleteEdge.contains(k)||followGraph.get(userId).contains(k)||userId==k) {
					continue;
				}
				if (edgeValue > oneValue.get(k)) {
					positiveTotal++;
				} else if (edgeValue == oneValue.get(k)) {
					EqualTotal++;
				}
				total++;
			}
		}

		if (total == 0) {
			System.out.println(OneUserDeleteEdge.size());
			System.out.println(total);
		}
		return result = ((1.0) * (positiveTotal + EqualTotal * 0.5)) / total;
	}

	
	// 放回降序排序后的用户下标
	public List<Integer> sortByValue(HashMap<Integer, Double> value,
			Integer userId) {
		TwoValueComparatorInInteger tvc = new TwoValueComparatorInInteger(value, 0);

		ArrayList<Integer> keyList = new ArrayList<>(value.keySet());
		Collections.sort(keyList, tvc);
//		for(Integer key1 : keyList) {
//           System.out.println(userId+"\t"+key1+"\t"+value.get(key1));
//           if (key1==10346) {
//        	   System.out.println(userId+"\t"+key1+"\t"+value.get(key1));
//   		}
	//	}
		
		
		return keyList;
	}

	public Map<Integer, List<Double>> Katz() {

		Map<Integer, List<Double>> CNValue = new HashMap<>();
		
		Matrix followMatrix = new Matrix(numberofuser, numberofuser, 0);

		for (int i = 0; i < numberofuser; i++) {
			Set<Integer> followSet = followGraph.get(i);
			if (followSet==null) {
				continue;
			}
			for (Integer follow : followSet) {
				followMatrix.set(i, follow, 1);
			}
		}
			
		Matrix siMatrix = Katz(followMatrix);

		Iterator<Integer> userIterator = deleteEdge.keySet().iterator();
		while (userIterator.hasNext()) {
			int userId = userIterator.next();
			HashMap<Integer, Double> temp = new HashMap();
			Set<Integer> followeetrain=followGraph.get(userId);
			for (int ii = 0; ii < numberofuser; ii++) {
//				if (followeetrain.contains(Integer.valueOf(10345))) {
//					System.out.println();
//				}
				if (!(userId==ii)&&!followeetrain.contains(ii)) {
					double value = siMatrix.get(userId, ii);
					temp.put(ii, value);
					
				} 
			}
			double auc= callAUCValue(temp, userId);
			List<Integer> sortResultList = sortByValue(temp, userId);
			List<Double> result = new ArrayList<>();
			// System.out.println(auc);
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
		int Dim = numberofuser;
		for (int i = 0; i < Dim; i++) {
			for (int ii = 0; ii < Dim; ii++) {
				if (i == ii) {
					siMatrix.set(i, ii, 1);
				} else if (siMatrix.get(i, ii)==1) {
					siMatrix.set(i, ii, -lamda);
				}
			}
		}
		 WriteUtil<String> writeUtil = new WriteUtil<>();
		 for (int i = 0; i < Dim; i++) {
		 StringBuffer kkBuffer = new StringBuffer();
		 for (int ii = 0; ii < Dim; ii++) {
		 kkBuffer.append(siMatrix.get(i, ii) + "\t");
		 }
		 String tempString = kkBuffer.toString().trim();
		 writeUtil.writeOneLine(tempString,path+ "katz.matrix");
		 }
		int count = 0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(path + "matrix0.txt")));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				String[] split = oneLine.trim().split("\t");
				for (int i = 0 ; i < numberofuser; i++) {
					siMatrix.set(count, i, Double.valueOf(split[i]));
				}
				count++;
				oneLine = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(count);
	
		// sim = sim - sparse(eye(size(train,1)));
		for (int i = 0; i < Dim; i++) {
			siMatrix.set(i, i, siMatrix.get(i, i) - 1);
		}
		return siMatrix;
	}

}
