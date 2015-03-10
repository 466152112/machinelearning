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
import util.MathCal;
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
public class BrisimTKI<T> extends RecursiveTask<String> {
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
	public BrisimTKI(String path, String userListFileName,
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
	private double compareSimInfollowAndOtherLevel(Map<Long, List<Long>> trainSet,
			Map<Long, List<Long>> testSet, Map<Long, User> userSet) {
		System.out.println("begin compareSim");
		WriteUtil WriteUtil = new WriteUtil<>();
		List<Long> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<Long> testUserListIterator = testSet.keySet().iterator();
		List<String> resultList=new ArrayList<>();
		int Count1=0;
		while (testUserListIterator.hasNext()) {
			// double[] tempresult=new double[userIdList.size()];

			// get the user id
			Long userIdP = testUserListIterator.next();

			// get user for userSet
			User preventUser = userSet.get(userIdP);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
			double[] changeTopKFeature = preventUser.getFeature();

			// the result for preventUser
			// loop for other user that no in the trainSet and the preventUser
			double KLDOfF=0,KLDOfOther=0;
			for (long useridofF : followeelist) {
				 User follow=userSet.get(useridofF);
				KLDOfF+=MathCal.KLDistance(changeTopKFeature, follow.getFeature());
			}
			KLDOfF/=followeelist.size();
			int countOfNoFollowee=0;
			for (int i = 0; i < userIdList.size(); i++) {
				long userid=userIdList.get(i);
				if(userid==userIdP||followeelist.contains(userid)||trainSet.get(userIdP).contains(userid)){
					continue;
				}
				 User follow=userSet.get(userid);
				KLDOfOther+=MathCal.KLDistance(changeTopKFeature, follow.getFeature());
				countOfNoFollowee++;
			}
			KLDOfOther/=countOfNoFollowee;
			resultList.add(Count1+"\t"+KLDOfF+"\t"+KLDOfOther);
			WriteUtil.writeOneLine(Count1+"\t"+KLDOfF+"\t"+KLDOfOther, path+"KLDInfolloweeAndOther.txt");
			Count1++;
		}
		System.out.println();
		return 0;
	}
	
	/**
	 * @param trainSet
	 * @param testSet
	 * @param userSet
	 * @return
	 *@create_time：2014年11月3日下午6:36:17
	 *@modifie_time：2014年11月3日 下午6:36:17
	  
	 */
	private double compareSimInfolloweeLevel(Map<Long, List<Long>> trainSet,
			Map<Long, List<Long>> testSet, Map<Long, User> userSet) {
		System.out.println("begin compareSimInfolloweeLevel");
		WriteUtil WriteUtil = new WriteUtil<>();
		List<Long> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<Long> testUserListIterator = testSet.keySet().iterator();
		List<String> resultList=new ArrayList<>();
		int Count1=0;
		while (testUserListIterator.hasNext()) {
			// double[] tempresult=new double[userIdList.size()];

			// get the user id
			Long userIdP = testUserListIterator.next();

			// get user for userSet
			User preventUser = userSet.get(userIdP);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
		

			// the result for preventUser
			// loop for other user that no in the trainSet and the preventUser
			double[] changeTopKFeature = preventUser.getFeature();
			for (long forecastId : testSet.get(userIdP)) {
				double KLDOfBridge=0,KLDOfOther=0;
				int bridgeCount=0;
				User forecastUser=userSet.get(forecastId);
				
				for (long useridofF : followeelist) {
					 User follow=userSet.get(useridofF);
					 if(follow.getfollowee().contains(forecastId)){
						 bridgeCount++;
						 KLDOfBridge+=MathCal.KLDistance(changeTopKFeature, follow.getFeature());
					 }else {
						 KLDOfOther+=MathCal.KLDistance(changeTopKFeature, follow.getFeature());
					}
				}
				if(bridgeCount>0){
					KLDOfBridge/=bridgeCount;
					KLDOfOther/=(followeelist.size()-bridgeCount);
					System.out.println(KLDOfBridge+"\t"+KLDOfOther+"\t"+bridgeCount+"\t"+(followeelist.size()-bridgeCount));
				}
				
			}
			
		}
		//WriteUtil.writeList(resultList, path+"KLDInfolloweeAndOther.txt");
		
		return 0;
	}

	private double compareSimInfolloweeCircle(Map<Long, List<Long>> trainSet,
			Map<Long, List<Long>> testSet, Map<Long, User> userSet) {
		System.out.println("begin compareSimInfolloweeLevel");
		WriteUtil WriteUtil = new WriteUtil<>();
		List<Long> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<Long> testUserListIterator = testSet.keySet().iterator();
		List<String> resultList=new ArrayList<>();
		int Count1=0;
		while (testUserListIterator.hasNext()) {
			// double[] tempresult=new double[userIdList.size()];

			// get the user id
			Long userIdP = testUserListIterator.next();

			// get user for userSet
			User preventUser = userSet.get(userIdP);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
		

			// the result for preventUser
			// loop for other user that no in the trainSet and the preventUser
			double[] changeTopKFeature = preventUser.getFeature();
			double KLDOfBridge=0,KLDOfOther=0;
			int bridgeCount=0;
				for (long useridofF : followeelist) {
					 User follow=userSet.get(useridofF);
					 if(Collections.disjoint(follow.getfollowee(), followeelist)){
						 bridgeCount++;
						 KLDOfBridge+=MathCal.KLDistance(changeTopKFeature, follow.getFeature());
					 }else {
						 KLDOfOther+=MathCal.KLDistance(changeTopKFeature, follow.getFeature());
					}
					 
				}
				if(bridgeCount>0){
					KLDOfBridge/=bridgeCount;
					KLDOfOther/=(followeelist.size()-bridgeCount);
					System.out.println(KLDOfBridge+"\t"+KLDOfOther+"\t"+bridgeCount+"\t"+(followeelist.size()-bridgeCount));
				}
				
			
		}
		//WriteUtil.writeList(resultList, path+"KLDInfolloweeAndOther.txt");
		
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
//			 // double[] topkfeature=new double[100];
//			 for (Long followee : followeeList) {
//			 topkfeature = MatrixUtil.vectorAdd(topkfeature,userSet.get(followee).getTopKFeature(topK));
//			 
//			 }
			oneUser.setcalTopKFeature(topkfeature);
		}
		// prediction for each user
		double result = compareSimInfollowAndOtherLevel(trainSet, testSet, userSet);

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
