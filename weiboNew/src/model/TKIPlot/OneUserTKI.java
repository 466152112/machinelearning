package model.TKIPlot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

import bean.User;
import util.MatrixUtil;
import util.ReadUtil;
import util.SplitTrainAndTest;
import util.TwoValueComparatorInLong;
import util.WriteUtil;

/**   
*    
* @progject_name：weiboNew   
* @class_name：BrisimTKI   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年10月23日 下午7:45:31   
* @modifier：zhouge   
* @modified_time：2014年10月23日 下午7:45:31   
* @modified_note：   
* @version    
*    
*/
@SuppressWarnings("serial")
public class OneUserTKI<T> extends RecursiveTask<String> {
	// the limit in rating number
	final static int limit = 5;

	// fileName
	final String path, userListFileName, userFeatureFileName,
			followGraphFileName;

	final int topK;

	/**
	 * @param path
	 * @param userListFileName
	 * @param userFeatureFileName
	 * @param followGraphFileName
	 */
	public OneUserTKI(String path, String userListFileName,
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
		WriteUtil WriteUtil=new WriteUtil<>();
		List<Long> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<Long> testUserListIterator = testSet.keySet().iterator();

		// save the result
		int count = 0;
		double[] resultAUC=new double[testSet.size()];
		while (testUserListIterator.hasNext()) {
		//	double[] tempresult=new double[userIdList.size()];
			
			// get the user id
			Long userId = testUserListIterator.next();
			
			// get user for userSet
			User preventUser = userSet.get(userId);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
			double[] changeTopKFeature = preventUser.calTopKFeature(topK);

			// the result for preventUser
			 HashMap<Long, Double> auclistsimplest = new HashMap();
			 HashMap<Long, Double> auclistbrige = new HashMap();
			// loop for other user that no in the trainSet and the preventUser
			for (int i = 0; i < userIdList.size(); i++) {
				Long ReferenceUserId = userIdList.get(i);

				User ReferenceUser = userSet.get(ReferenceUserId);
				if (ReferenceUserId.equals(userId)|| trainSet.get(userId).contains(ReferenceUserId)) {
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
//				double countThreeValue = 0;
//				for (int j = 0; j < changeTopKFeature.length; j++) {
//					if (preventUser.getTopKFeature(topK)[j] != 0
//							&& ReferenceUser.getTopKFeature(topK)[j] != 0) {
//						countThreeValue++;
//					}
//				}
//				countThreeValue /= topK;
////				
//				double result = countFirstValue * secondValue +countThreeValue;
				
				auclistsimplest.put(ReferenceUserId,  secondValue);
				auclistbrige.put(ReferenceUserId, countFirstValue *secondValue);
			}
			resultAUC[count]=aucCal(userId,  userIdList,testSet.get(userId) ,auclistsimplest,path+"plot/everyuser/simplest/"+userId+".txt");
			resultAUC[count]=aucCal(userId,  userIdList,testSet.get(userId) ,auclistbrige,path+"plot/everyuser/bridge/"+userId+".txt");
			
//			WriteUtil.writeOneLine(String.valueOf(resultAUC[count]), path+"plot/compare/bridge/auc.txt");
			count++;
		}
		return 0;

	}
	/**
	 * @param trainSet
	 * @param testSet
	 * @param userSet
	 * @return
	 * @create_time：2014年8月25日上午9:12:36
	 * @modifie_time：2014年8月25日 上午9:12:36
	 */
	private double predictionchangeFeature(Map<Long, List<Long>> trainSet,
			Map<Long, List<Long>> testSet, Map<Long, User> userSet) {
		System.out.println("begin prediction");
		WriteUtil WriteUtil=new WriteUtil<>();
		List<Long> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<Long> testUserListIterator = testSet.keySet().iterator();

		// save the result
		int count = 0;
		double[] resultAUC=new double[testSet.size()];
		while (testUserListIterator.hasNext()) {
		//	double[] tempresult=new double[userIdList.size()];
			
			// get the user id
			Long userId = testUserListIterator.next();
			
			// get user for userSet
			User preventUser = userSet.get(userId);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
			double[] changeTopKFeature = preventUser.calTopKFeature(topK);

			// the result for preventUser
			 HashMap<Long, Double> auclistsimplest = new HashMap();
			 HashMap<Long, Double> auclistbrige = new HashMap();
			// loop for other user that no in the trainSet and the preventUser
			for (int i = 0; i < userIdList.size(); i++) {
				Long ReferenceUserId = userIdList.get(i);

				User ReferenceUser = userSet.get(ReferenceUserId);
				if (ReferenceUserId.equals(userId)|| trainSet.get(userId).contains(ReferenceUserId)) {
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
////				
				double result = countFirstValue * secondValue +countThreeValue;
				
				auclistsimplest.put(ReferenceUserId,  secondValue);
				auclistbrige.put(ReferenceUserId, result);
			}
			resultAUC[count]=aucCal(userId,  userIdList,testSet.get(userId) ,auclistsimplest,path+"plot/everyuser/changefeature/"+userId+".txt");
			resultAUC[count]=aucCal(userId,  userIdList,testSet.get(userId) ,auclistbrige,path+"plot/everyuser/TKI/"+userId+".txt");
			
//			WriteUtil.writeOneLine(String.valueOf(resultAUC[count]), path+"plot/compare/bridge/auc.txt");
			count++;
		}
		return 0;

	}
	/**
	 * @param userId
	 * @param userIdList
	 * @param testFollowee
	 * @param value
	 * @return
	 *@create_time：2014年8月26日下午8:45:45
	 *@modifie_time：2014年8月26日 下午8:45:45
	  
	 */
	private double aucCal(Long userId, List<Long> userIdList, List<Long> testFollowee,Map<Long, Double> value) {
		//WriteUtil WriteUtil=new WriteUtil<>();
	//	System.out.println("enter the aucCal");
		double counttotal = 0, bigcount = 0, equalcount = 0;
		for (Long followeeId : testFollowee) {
		
			if (!value.containsKey((Long)followeeId)) {
				System.out.println(followeeId);
				continue;
			}
			double testvalue = value.get(followeeId);

			// compare with other user
			
			for (Long t2 : userIdList) {
				if (!value.containsKey(t2)||t2.equals(userId)|| testFollowee.contains(t2)) {
					continue;
				}
				double compareValue = value.get(t2);
				if (compareValue < testvalue) {
					bigcount++;
				} else if(compareValue == testvalue) {
					equalcount++;
				}
				counttotal++;
			}
			//double oneresult = (bigcount + equalcount * 0.5) / counttotal;
			
			//WriteUtil.writeOneLine(String.valueOf(oneresult), path+"/plot/top5/1355401/bridge/auc.txt");
			
		}

		double finisresult = (bigcount + equalcount * 0.5) / counttotal;
		//System.out.println("finish the aucCal"+finisresult);
		return finisresult;

	}

	private double aucCal(Long userId, List<Long> userIdList, List<Long> testFollowee,Map<Long, Double> value,String fileName) {
		WriteUtil WriteUtil=new WriteUtil<>();
	//	System.out.println("enter the aucCal");
		double counttotal = 0, bigcount = 0, equalcount = 0;
		for (Long followeeId : testFollowee) {
		
			if (!value.containsKey((Long)followeeId)) {
				System.out.println(followeeId);
				continue;
			}
			double testvalue = value.get(followeeId);

			// compare with other user
			
			for (Long t2 : userIdList) {
				if (!value.containsKey(t2)||t2.equals(userId)|| testFollowee.contains(t2)) {
					continue;
				}
				double compareValue = value.get(t2);
				if (compareValue < testvalue) {
					bigcount++;
				} else if(compareValue == testvalue) {
					equalcount++;
				}
				counttotal++;
			}
			double oneresult = (bigcount + equalcount * 0.5) / counttotal;
			
			WriteUtil.writeOneLine(String.valueOf(oneresult), fileName);
			
		}

		double finisresult = (bigcount + equalcount * 0.5) / counttotal;
		//System.out.println("finish the aucCal"+finisresult);
		return 0;

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
//		//	double[] topkfeature=new double[100];
//			for (T followee : followeeList) {
//				topkfeature = MatrixUtil.vectorAdd(topkfeature,
//						userSet.get(followee).getTopKFeature(topK));
//			}
			oneUser.setcalTopKFeature(topkfeature);
		}
		// prediction for each user
		double result = prediction(trainSet, testSet, userSet);
		Iterator<Long> KeyList1 = userSet.keySet().iterator();
		while (KeyList1.hasNext()) {
			Long keyT = KeyList1.next();
			User oneUser = userSet.get(keyT);
			Set<Long> followeeList = oneUser.getfollowee();
			
			double[] topkfeature = oneUser.getTopKFeature(topK);
		//	double[] topkfeature=new double[100];
			for (Long followee : followeeList) {
				topkfeature = MatrixUtil.vectorAdd(topkfeature,
						userSet.get(followee).getTopKFeature(topK));
			}
			oneUser.setcalTopKFeature(topkfeature);
		}
		// predictionchangeFeature for each user
		double result1 = predictionchangeFeature(trainSet, testSet, userSet);
		
		// System.out.println(result);

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
