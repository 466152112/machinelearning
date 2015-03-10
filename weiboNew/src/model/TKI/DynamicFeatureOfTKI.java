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
import util.TwoValueComparator;
import util.WriteUtil;

@SuppressWarnings("serial")
public class DynamicFeatureOfTKI<T> extends RecursiveTask<String> {
	// the limit in rating number
	final static int limit = 5;

	// fileName
	final String path, userFeatureFileName,
			followGraphFileName;
	final HashMap<String, Integer> wordClass;
	final int topK;

	final List<T> userIdList ;
	/**
	 * @param path
	 * @param userListFileName
	 * @param userFeatureFileName
	 * @param followGraphFileName
	 */
	public DynamicFeatureOfTKI(String path, List<T> userIdList,
			String userFeatureFileName, String followGraphFileName,HashMap<String, Integer> wordClass, int topK) {
		super();
		// the userFeature and itemFeature

		this.path = path;
		this.userIdList =userIdList;
		this.userFeatureFileName = path + userFeatureFileName;
		this.followGraphFileName = path + followGraphFileName;
		this.topK = topK;
		this.wordClass=wordClass;
	}

	/**
	 * @param trainSet
	 * @param testSet
	 * @param userSet
	 * @return
	 * @create_time：2014年8月25日上午9:12:36
	 * @modifie_time：2014年8月25日 上午9:12:36
	 */
	private double prediction(Map<T, List<T>> trainSet,
			Map<T, List<T>> testSet, Map<T, User<T>> userSet) {
		System.out.println("begin prediction");
		List<T> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<T> testUserListIterator = testSet.keySet().iterator();

		// save the result
		int count = 0;
		int[] calculatedLength={1,2,5,10,15,20,30,40,50,60,70,80,90,100};
		double[] resultAUC=new double[testSet.size()];
		List<List<Double>> resultRecall=new ArrayList<>();
		List<List<Double>> resultPrescion=new ArrayList<>();
		while (testUserListIterator.hasNext()) {
		//	double[] tempresult=new double[userIdList.size()];
			
			// get the user id
			T userId = testUserListIterator.next();
			// get user for userSet
			User<T> preventUser = userSet.get(userId);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<T> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
			double[] changeTopKFeature = preventUser.calTopKFeature(topK);

			// the result for preventUser
			 HashMap<T, Double> auclist = new HashMap();

			// loop for other user that no in the trainSet and the preventUser
			for (int i = 0; i < userIdList.size(); i++) {
				T ReferenceUserId = userIdList.get(i);

				User<T> ReferenceUser = userSet.get(ReferenceUserId);
				if (ReferenceUserId.equals(userId)|| trainSet.get(userId).contains(ReferenceUserId)) {
					continue;
				}

				// cal the first step
				int countFirstValue = 0;
				for (T followeeId : followeelist) {
					User<T> followee = userSet.get(followeeId);
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
			List<T> sortList=this.sortByValue(auclist);
			sortList=sortList.subList(0, calculatedLength[calculatedLength.length-1]);
			resultRecall.add(CalRecall(userId,testSet.get(userId) ,sortList, calculatedLength));
			resultPrescion.add(CalPrecise(userId,testSet.get(userId) ,sortList,calculatedLength));
			System.out.println(count++);
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
		
		WriteUtil.writeVector(resultRecalltemp, path+"DynamicFeatureRecall.txt");
		
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
		WriteUtil.writeVector(resultPrecisetemp, path+"DynamicFeaturePrescion.txt");
		
		return sum/testSet.size();

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
	private double aucCal(T userId, List<T> userIdList, List<T> testFollowee,Map<T, Double> value) {
		System.out.println("enter the aucCal");
		double counttotal = 0, bigcount = 0, equalcount = 0;
		for (T followeeId : testFollowee) {
			if (!value.containsKey((T)followeeId)) {
				System.out.println(followeeId);
				continue;
			}
			double testvalue = value.get(followeeId);

			// compare with other user
			
			for (T t2 : userIdList) {
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
	private List<Double> CalRecall(T userId, List<T> testFollowee,List<T> sortList,int[] calculatedLength) {
		System.out.println("enter the CalRecall");
		List<Double> result=new ArrayList<>();
	
		for (int recomLength = 0; recomLength < calculatedLength.length; recomLength++) {
			double hitNumber=0;
			List<T> recomList=sortList.subList(0, calculatedLength[recomLength]);
			for (T followeeId : testFollowee) {
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
	private List<Double> CalPrecise(T userId, List<T> testFollowee,List<T> sortList,int[] calculatedLength) {
		System.out.println("enter the CalPrecise");
		List<Double> result=new ArrayList<>();
	
		for (int recomLength = 0; recomLength < calculatedLength.length; recomLength++) {
			double hitNumber=0;
			List<T> recomList=sortList.subList(0, calculatedLength[recomLength]);
			for (T followeeId : testFollowee) {
				if (recomList.contains(followeeId)) 
					hitNumber++;
			}
			result.add(hitNumber/calculatedLength[recomLength]);
			}
		//System.out.println("finish the CalPrecise"+result);
		return result;

	}
	
	protected String compute() {

		Map<T, List<T>> trainSet = null;
		Map<T, List<T>> testSet = null;

		// user and feature
		Map<T, User<T>> userSet=new HashMap<>();
		for (T userId : userIdList) {
			User oneuUser = new User();
			oneuUser.setUserId((String) userId);
			userSet.put((T) userId, oneuUser);
		}
		
		
		ReadUtil<T> readUtil = new ReadUtil();
		// split Train and test into 90% and 10%
		SplitTrainAndTest<T> splitTrainAndTest = new SplitTrainAndTest(
				this.followGraphFileName, userSet, this.limit, 0.1);

		testSet = splitTrainAndTest.getTestFollow();

		trainSet = splitTrainAndTest.getTrainFollow();
		userSet = splitTrainAndTest.getUserSet();
		DynamicFeature dynamicFeature=new DynamicFeature<T>(testSet, userSet, userFeatureFileName,userIdList, wordClass);
		userSet=dynamicFeature.getUserSet();
		// cal the setcalTopKFeature for each user
		Iterator<T> KeyList = userSet.keySet().iterator();
		while (KeyList.hasNext()) {
			T keyT = KeyList.next();
			User<T> oneUser = userSet.get(keyT);
			Set<T> followeeList = oneUser.getfollowee();
			double[] topkfeature = oneUser.getTopKFeature(topK);
			for (T followee : followeeList) {
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
	
		public List<T> sortByValue(Map<T, Double> value) {
			List<T> result = new ArrayList<>();
			ArrayList<T> userList = new ArrayList<>(value.keySet());
			// 降序 排序
			TwoValueComparator tvc = new TwoValueComparator((HashMap<String, Double>) value, 0);
			ArrayList<String> keyList = (ArrayList<String>) new ArrayList<>(value.keySet());
			Collections.sort(keyList, tvc);
		
			result.addAll((Collection<? extends T>) keyList);
			return result;
		}

}
