package model.TKI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

@SuppressWarnings("serial")
public class TKI extends RecursiveTask<String> {
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
	public TKI(String path, String userListFileName,
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
		List<Long> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<Long> testUserListIterator = testSet.keySet().iterator();

		// save the result
		int count = 0;
		int[] calculatedLength={1,2,5,10,15,20,30,40,50,60,70,80,90,100};
		double[] resultAUC=new double[testSet.size()];
		List<List<Double>> resultRecall=new ArrayList<>();
		List<List<Double>> resultPrescion=new ArrayList<>();
		while (testUserListIterator.hasNext()) {
		//	double[] tempresult=new double[userIdList.size()];
			
			// get the user id
			Long userId = testUserListIterator.next();
			// get user for userSet
			User preventUser = userSet.get(userId);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeLongopKFeature of preventUser
			double[] changeTopKFeature = preventUser.calTopKFeature(topK);

			// the result for preventUser
			 HashMap<Long, Double> auclist = new HashMap();

			// loop for other user that no in the trainSet and the preventUser
			for (int i = 0; i < userIdList.size(); i++) {
				Long ReferenceUserId = userIdList.get(i);

				User ReferenceUser = userSet.get(ReferenceUserId);
				if (ReferenceUserId.equals(userId)|| trainSet.get(userId).contains(ReferenceUserId)) {
					continue;
				}

				// cal the first step
				int countFirstValue = 0;
				for (long followeeId : followeelist) {
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
			resultAUC[count]=aucCal(userId,  userIdList,testSet.get(userId) ,auclist);
			List<Long> sortList=this.sortByValue(auclist);
			sortList=sortList.subList(0, calculatedLength[calculatedLength.length-1]);
			resultRecall.add(CalRecall(userId,testSet.get(userId) ,sortList, calculatedLength));
			resultPrescion.add(CalPrecise(userId,testSet.get(userId) ,sortList,calculatedLength));
//			System.out.println();
			count++;
		}
		double sum=0;
		for (double d : resultAUC) {
			sum+=d;
		}
		
		//cal the avg recall
		double[] resultRecalltemp=new double[calculatedLength.length];
		for (List<Double> temp : resultRecall) {
			for (int i = 0; i < temp.size(); i++) {
				//first add all the value
				resultRecalltemp[i]+=temp.get(i);
			}
		}
		for (int i = 0; i < calculatedLength.length; i++) {
			resultRecalltemp[i]/=resultRecall.size();
		}
		WriteUtil WriteUtil=new WriteUtil<>();
		
		WriteUtil.writeVector(resultRecalltemp, path+"recall/TKIFS-"+topK+".txt");
		
		//cal the avg precise
		double[] resultPrecisetemp=new double[calculatedLength.length];
		for (List<Double> temp : resultPrescion) {
			for (int i = 0; i < temp.size(); i++) {
				resultPrecisetemp[i]+=temp.get(i);
			}
		}
		for (int i = 0; i < calculatedLength.length; i++) {
			resultPrecisetemp[i]/=resultPrescion.size();
		}
		WriteUtil.writeVector(resultPrecisetemp, path+"precision/TKIFS-"+topK+".txt");
		
		double result=sum/testSet.size();
		WriteUtil.writeOneLine(String.valueOf(result), path+"AUC/TKIFS-"+topK+".txt");
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
	//	System.out.println("enter the aucCal");
		double counttotal = 0, bigcount = 0, equalcount = 0;
		for (long followeeId : testFollowee) {
			if (!value.containsKey(followeeId)) {
				System.out.println(followeeId);
				continue;
			}
			double testvalue = value.get(followeeId);

			// compare with other user
			
			for (long t2 : userIdList) {
				if (!value.containsKey(t2)||t2==userId|| testFollowee.contains(t2)) {
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
		}

		double finisresult = (bigcount + equalcount * 0.5) / counttotal;
		//System.out.println("finish the aucCal"+finisresult);
		return finisresult;

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
	private List<Double> CalRecall(Long userId, List<Long> testFollowee,List<Long> sortList,int[] calculatedLength) {
		//System.out.println("enter the CalRecall");
		List<Double> result=new ArrayList<>();
	
		for (int recomLength = 0; recomLength < calculatedLength.length; recomLength++) {
			double hitNumber=0;
			List<Long> recomList=sortList.subList(0, calculatedLength[recomLength]);
			for (long followeeId : testFollowee) {
				if (recomList.contains(followeeId)) 
					hitNumber++;
			}
			result.add(hitNumber/testFollowee.size());
			}
		//System.out.println("finish the CalRecall"+result);
		return result;

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
	private List<Double> CalPrecise(Long userId, List<Long> testFollowee,List<Long> sortList,int[] calculatedLength) {
		//System.out.println("enter the CalPrecise");
		List<Double> result=new ArrayList<>();
	
		for (int recomLength = 0; recomLength < calculatedLength.length; recomLength++) {
			double hitNumber=0;
			List<Long> recomList=sortList.subList(0, calculatedLength[recomLength]);
			for (long followeeId : testFollowee) {
				if (recomList.contains(followeeId)) 
					hitNumber++;
			}
			result.add(hitNumber/calculatedLength[recomLength]);
			}
		//System.out.println("finish the CalPrecise"+result);
		return result;

	}
	
	protected String compute() {

		Map<Long, List<Long>> trainSet = null;
		Map<Long, List<Long>> testSet = null;

		// user and feature
		Map<Long, User> userSet;
		ReadUtil<Long> readUtil = new ReadUtil();

		List<Long> userIdList = readUtil
				.readFileByLine(this.userListFileName);

		userSet = readUtil.readUser(userIdList, this.userFeatureFileName);

		// split Train and test into 90% and 10%
		SplitTrainAndTest<Long> splitTrainAndTest = new SplitTrainAndTest(
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
		//	double[] topkfeature=new double[100];
			for (Long followee : followeeList) {
				topkfeature = MatrixUtil.vectorAdd(topkfeature,
						userSet.get(followee).getTopKFeature(topK));
			}
			oneUser.setcalTopKFeature(topkfeature);
		}
		// prediction for each user
		double result = prediction(trainSet, testSet, userSet);
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
