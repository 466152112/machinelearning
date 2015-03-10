package model.TKIPlot;

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

import bean.User;
import util.MathCal;
import util.MatrixUtil;
import util.ReadUtil;
import util.SplitTrainAndTest;
import util.TwoValueComparatorInLong;
import util.WriteUtil;

@SuppressWarnings("serial")
public class CaseTKI extends RecursiveTask<String> {
	// the limit in rating number
	final static int limit = 5;

	// fileName
	final String path, userListFileName, userFeatureFileName,
			followGraphFileName;

	final int topK;
	static int LabelN = 100;

	/**
	 * @param path
	 * @param userListFileName
	 * @param userFeatureFileName
	 * @param followGraphFileName
	 */
	public CaseTKI(String path, String userListFileName,
			String userFeatureFileName, String followGraphFileName, int topK) {
		super();
		// the userFeature and itemFeature

		this.path = path;
		this.userListFileName = path + userListFileName;
		this.userFeatureFileName = path + userFeatureFileName;
		this.followGraphFileName = path + followGraphFileName;
		this.topK = topK;
	}

	/**
	 * @param trainSet
	 * @param testSet
	 * @param userSet
	 * @return
	 * @create_time：2014年8月25日上午9:12:36
	 * @modifie_time：2014年8月25日 上午9:12:36
	 */
	private double prediction(Map<Long, List<Long>> trainSet,
			Map<Long, List<Long>> testSet, Map<Long, User> userSet) {
		System.out.println("begin prediction");
		WriteUtil WriteUtil = new WriteUtil<>();
		List<Long> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<Long> testUserListIterator = testSet.keySet().iterator();

		// save the result
		int count = 0;
		ArrayList resultMatrix = new ArrayList<>();
		
		while (testUserListIterator.hasNext()) {
			// double[] tempresult=new double[userIdList.size()];

			// get the user id
			Long userId = testUserListIterator.next();

			// get user for userSet
			User preventUser = userSet.get(userId);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
			double[] changeTopKFeature = preventUser.calTopKFeature(topK);
			HashMap<Long, Double> auclist = new HashMap();

			// the result for preventUser

			// loop for other user that no in the trainSet and the preventUser
			for (int i = 0; i < userIdList.size(); i++) {
				Long ReferenceUserId = userIdList.get(i);

				User ReferenceUser = userSet.get(ReferenceUserId);
				if (ReferenceUserId.equals(userId)
						|| trainSet.get(userId).contains(ReferenceUserId)) {
					continue;
				}

				// cal the first step
				int countFirstValue = 0;
				for (Long followeeId : followeelist) {
					User followee = userSet.get(followeeId);
					if (followee.getfollowee().contains(ReferenceUserId)) {
						countFirstValue++;
					}
				}

				// if(countFirstValue!=0)
				// System.out.println(countFirstValue );
				// cal the second step
				double secondValue = MatrixUtil.vectorMultiply(
						changeTopKFeature, ReferenceUser.getTopKFeature(topK));

				// cal the three step
				double countThreeValue = 0;
				for (int j = 0; j < changeTopKFeature.length; j++) {
					if (preventUser.getTopKFeature(topK)[j] != 0
							&& ReferenceUser.getTopKFeature(topK)[j] != 0) {
						countThreeValue++;
					}
				}
				countThreeValue /= topK;
				double result = countFirstValue * secondValue + countThreeValue;
				auclist.put(ReferenceUserId, result);
			}

			List<Long> sortId = sortByValue(auclist);
			int recommendLength = 100;
			 CaseCal( userId,followeelist,trainSet,sortId, userSet, changeTopKFeature,testSet.get(userId), recommendLength);
			count++;
			
		}
		writeToFile(resultMatrix);
		return 0;

	}

	public void writeToFile(ArrayList resultMatrix) {
		List<String> list = new ArrayList<>();
		for (Object object : resultMatrix) {
			double[] result = (double[]) object;
			String temp = result[0] + "\t" + result[1] + "\t" + result[2];
			list.add(temp);
		}
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeList(list, path + "KLDThreeColor.txt");
	}
	/**
	 * @param resultMatrix
	 *            结果
	 * @param sortId
	 *            排序后的用户id
	 * @param userSet
	 *            用户和用户信息
	 * @param changeTopKFeature
	 *            当前用户的Feature
	 * @param testUserId
	 *            目标用户集
	 * @param recommendLength
	 *            推荐长度 1 10 100 等
	 * @return
	 * @create_time：2014年11月29日下午5:11:04
	 * @modifie_time：2014年11月29日 下午5:11:04
	 */
	public void CaseCal( Long  currentId,Set<Long> followeelist ,Map<Long, List<Long>> trainSet,List<Long> sortId,
			Map<Long, User> userSet, double[] changeTopKFeature,
			List<Long> testUserId, int recommendLength) {
		if (testUserId.size()<100) {
			return;
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		// 保存所有的KL距离
		Map<Long, Double> KLMap = new HashMap<>();
		// 求最大最小值，用以归一化
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;
		for (Long userid : sortId) {
			double KLDOfOther = MathCal.KLDistance(changeTopKFeature, userSet
					.get(userid).getFeature());
			KLMap.put(userid, KLDOfOther);
			if (maxValue < KLDOfOther) {
				maxValue = KLDOfOther;
			}
			if (minValue > KLDOfOther) {
				minValue = KLDOfOther;
			}
		}
		KLMap=disperse(KLMap);
		Set<Long> userIdSet=new HashSet<Long>();userIdSet.add(currentId);
		List<Long> TopKUserId = sortId.subList(0, recommendLength);
		//
		List<String> NodeLabel=new ArrayList<String>();
		NodeLabel.add("Id\tcolor\tKL");
		NodeLabel.add(currentId+"\t"+1+"\t"+1);
		
		// 下面三个值分别为：
		// ++的比例 ，—+的比例，+-的比例
		List<String> ResultList=new ArrayList<>();
		ResultList.add("source\ttarget\tWeight\tColor");
		// -+样本
		int count=0;
		for (int i = 0; i < TopKUserId.size(); i++) {
			if (!testUserId.contains(TopKUserId.get(i))) {
				if (count++>20) {
					break;
				}
				double BrigeNumber = 0;
				for (Long followeeId : followeelist) {
					User followee = userSet.get(followeeId);
					if (followee.getfollowee().contains(TopKUserId.get(i))) {
						BrigeNumber++;
					}
				}
				BrigeNumber=(int)((BrigeNumber+10)/10);
				userIdSet.add(TopKUserId.get(i));
				String temp=currentId+"\t"+TopKUserId.get(i)+"\t"+BrigeNumber+"\t-+";
				NodeLabel.add(TopKUserId.get(i)+"\t"+2+"\t"+KLMap.get(TopKUserId.get(i)));
				ResultList.add(temp);
				
			}
		}
		
		// ++
		for (int i = 0; i < TopKUserId.size(); i++) {
			if (testUserId.contains(TopKUserId.get(i))) {
				double BrigeNumber = 0;
				for (Long followeeId : followeelist) {
					User followee = userSet.get(followeeId);
					if (followee.getfollowee().contains(TopKUserId.get(i))) {
						BrigeNumber++;
					}
				}
				BrigeNumber=(int)((BrigeNumber+10)/10);
				userIdSet.add(TopKUserId.get(i));
				String temp=currentId+"\t"+TopKUserId.get(i)+"\t"+BrigeNumber+"\t++";
				ResultList.add(temp);
				NodeLabel.add(TopKUserId.get(i)+"\t"+3+"\t"+KLMap.get(TopKUserId.get(i)));
				testUserId.remove(TopKUserId.get(i));
			}
		}
		// +- 2
		count=0;
		for (Long userid : testUserId) {
			
			double BrigeNumber = 0;
			for (Long followeeId : followeelist) {
				User followee = userSet.get(followeeId);
				if (followee.getfollowee().contains(userid)) {
					BrigeNumber++;
				}
			}
			userIdSet.add(userid);
			BrigeNumber=(int)((BrigeNumber+10)/10);
			String temp=currentId+"\t"+userid+"\t"+BrigeNumber+"\t+-";
			ResultList.add(temp);
			NodeLabel.add(userid+"\t"+4+"\t"+KLMap.get(userid));
		}
		writeUtil.writeList(ResultList, path+"case/"+currentId+"_edge.csv");
		writeUtil.writeList(NodeLabel, path+"case/"+currentId+"_node.csv");
//		//write-Ego-Network
//		for (long userid : userIdSet) {
//			
//		}
	}
	
	public Map<Long, Double> disperse(Map<Long, Double> KLMap ){
		Map<Long, Double> resultMap=new HashMap<>();
		List<Double> value=new ArrayList<Double>();
		for (long id : KLMap.keySet()) {
			value.add(KLMap.get(id));	
		}
		//排序，划分10个区间
		Collections.sort(value);
		int Interval=value.size()/10;
		for (long id : KLMap.keySet()) {
			
			for (int i = 0; i < 10; i++) {
				
				if (KLMap.get(id)>value.get(i*Interval)&&(i+1)*Interval<value.size()&&KLMap.get(id)<=value.get((i+1)*Interval)) {
					resultMap.put(id, 1/(i+1.0));	
				}
			}
		}
		
		return resultMap;
	}
	/**
	 * @param resultMatrix
	 *            结果
	 * @param sortId
	 *            排序后的用户id
	 * @param userSet
	 *            用户和用户信息
	 * @param changeTopKFeature
	 *            当前用户的Feature
	 * @param testUserId
	 *            目标用户集
	 * @param recommendLength
	 *            推荐长度 1 10 100 等
	 * @return
	 * @create_time：2014年11月29日下午5:11:04
	 * @modifie_time：2014年11月29日 下午5:11:04
	 */
	public ArrayList KL(ArrayList resultMatrix, List<Long> sortId,
			Map<Long, User> userSet, double[] changeTopKFeature,
			List<Long> testUserId, int recommendLength) {
		// 保存所有的KL距离
		Map<Long, Double> KLMap = new HashMap<>();
		// 求最大最小值，用以归一化
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;
		for (Long userid : sortId) {
			double KLDOfOther = MathCal.KLDistance(changeTopKFeature, userSet
					.get(userid).getFeature());
			KLMap.put(userid, KLDOfOther);
			if (maxValue < KLDOfOther) {
				maxValue = KLDOfOther;
			}
			if (minValue > KLDOfOther) {
				minValue = KLDOfOther;
			}
		}

		// 归一化
		// for (Long userid : sortId) {
		// double value=(KLMap.get(userid)-minValue)/(maxValue-minValue);
		// KLMap.put(userid, value);
		// }

		List<Long> TopKUserId = sortId.subList(0, recommendLength);
		List<Double> kLList = new ArrayList<>();
		double[] result = new double[3];
		
		// 下面三个值分别为：
		// ++的比例 ，—+的比例，+-的比例
		// -+样本
		for (int i = 0; i < TopKUserId.size(); i++) {
			if (!testUserId.contains(TopKUserId.get(i))) {
				kLList.add(KLMap.get(TopKUserId.get(i)));
			}
		}
		// -+ 3
		result[0] = LabelN++;
		result[1] = MathCal.getAverage(kLList);
		result[2] = 3;
		resultMatrix.add(result);
		
		
		// ++
		for (int i = 0; i < TopKUserId.size(); i++) {
			if (testUserId.contains(TopKUserId.get(i))) {
				kLList.add(KLMap.get(TopKUserId.get(i)));
				testUserId.remove(TopKUserId.get(i));
			}
		}
		if (kLList.size() == 0) {
			return resultMatrix;
		}
		// ++ 1
		result = new double[3];
		result[0] = LabelN++;
		result[1] = MathCal.getAverage(kLList);
		result[2] = 1;
		resultMatrix.add(result);

		
		// +- 2
		kLList = new ArrayList<>();

		for (Long userid : testUserId) {
			kLList.add(KLMap.get(userid));
		}
		if (kLList.size() == 0) {
			return resultMatrix;
		}
		result = new double[3];
		result[0] = LabelN++;
		result[1] = MathCal.getAverage(kLList);
		result[2] = 2;
		resultMatrix.add(result);
		LabelN+=50;
		return resultMatrix;
		
	}

	/**
	 * @param userId
	 * @param userIdList
	 * @param testFollowee
	 * @param value
	 * @return
	 * @create_time：2014年8月26日下午8:45:45
	 * @modifie_time：2014年8月26日 下午8:45:45
	 */
	private double aucCal(Long userId, List<Long> userIdList,
			List<Long> testFollowee, Map<Long, Double> value) {
		// WriteUtil WriteUtil=new WriteUtil<>();
		// System.out.println("enter the aucCal");
		double counttotal = 0, bigcount = 0, equalcount = 0;
		for (Long followeeId : testFollowee) {

			if (!value.containsKey(followeeId)) {
				System.out.println(followeeId);
				continue;
			}
			double testvalue = value.get(followeeId);

			// compare with other user

			for (Long t2 : userIdList) {
				if (!value.containsKey(t2) || t2.equals(userId)
						|| testFollowee.contains(t2)) {
					continue;
				}
				double compareValue = value.get(t2);
				if (compareValue < testvalue) {
					bigcount++;
				} else if (compareValue == testvalue) {
					equalcount++;
				}
				counttotal++;
			}
			// double oneresult = (bigcount + equalcount * 0.5) / counttotal;

			// WriteUtil.writeOneLine(String.valueOf(oneresult),
			// path+"/plot/top5/1355401/bridge/auc.txt");

		}

		double finisresult = (bigcount + equalcount * 0.5) / counttotal;
		// System.out.println("finish the aucCal"+finisresult);
		return finisresult;

	}

	protected String compute() {

		Map<Long, List<Long>> trainSet = null;
		Map<Long, List<Long>> testSet = null;

		// user and feature
		Map<Long, User> userSet;
		ReadUtil<Long> readUtil = new ReadUtil();

		List<Long> userIdList = readUtil
				.readFileByLineInLong(this.userListFileName);

		userSet = readUtil.readUser(userIdList, this.userFeatureFileName);

		// split Longrain and test into 90% and 10%
		SplitTrainAndTest splitTrainAndTest = new SplitTrainAndTest(
				this.followGraphFileName, userSet, this.limit, 0.1);

		testSet = splitTrainAndTest.getTestFollow();

		trainSet = splitTrainAndTest.getTrainFollow();
		userSet = splitTrainAndTest.getUserSet();
		// cal the setcalTopKFeature for each user
		Iterator<Long> KeyList = userSet.keySet().iterator();
		while (KeyList.hasNext()) {
			Long keyT = KeyList.next();
			User oneUser = userSet.get(keyT);
			Set<Long> followeeList = oneUser.getfollowee();

			double[] topkfeature = oneUser.getTopKFeature(topK);
			// // double[] topkfeature=new double[100];
			for (long followee : followeeList) {
				topkfeature = MatrixUtil.vectorAdd(topkfeature,
						userSet.get(followee).getTopKFeature(topK));
			}
			oneUser.setcalTopKFeature(topkfeature);
		}
		// prediction for each user
		double result = prediction(trainSet, testSet, userSet);

		String temp = topK + "\t" + result + "\n";

		return temp;
	}

	public List<Long> sortByValue(Map<Long, Double> value) {
		List<Long> result = new ArrayList<>();
		ArrayList<Long> userList = new ArrayList<>(value.keySet());
		// 降序 排序
		TwoValueComparatorInLong tvc = new TwoValueComparatorInLong(value, 0);
		ArrayList<Long> keyList = new ArrayList<>(value.keySet());
		Collections.sort(keyList, tvc);

		result.addAll((Collection<? extends Long>) keyList);
		return result;
	}

}
