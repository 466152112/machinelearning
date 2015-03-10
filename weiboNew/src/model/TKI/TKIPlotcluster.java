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
import java.util.Random;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

import bean.User;
import util.MatrixUtil;
import util.ReadUtil;
import util.SplitTrainAndTest;
import util.WriteUtil;

@SuppressWarnings("serial")
public class TKIPlotcluster<T> extends RecursiveTask<String> {
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
	public TKIPlotcluster(String path, String userListFileName,
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
	private double prediction(Map<T, List<T>> trainSet,
			Map<T, List<T>> testSet, Map<T, User<T>> userSet) {
		System.out.println("begin prediction");
		List<T> userIdList = new ArrayList<>(userSet.keySet());

		// iterator in the test set
		Iterator<T> testUserListIterator = testSet.keySet().iterator();
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());

		// save the result
		while (testUserListIterator.hasNext()) {

			// get the user id
			T userId = testUserListIterator.next();
			if (!userId.equals("14380001")) {
				continue;
			}
			// get user for userSet
			User<T> preventUser = userSet.get(userId);
			// System.out.println(userId);

			// get the preventUser's followeelist
			Set<Long> followeelist = preventUser.getfollowee();

			// get the changeTopKFeature of preventUser
			double[] changeTopKFeature = preventUser.calTopKFeature(topK);

			// the result for preventUser

			// loop for other user that no in the trainSet and the preventUser
//			double maxfollowee = 0;
//			double maxsim = 0;
			double bigTKI=0;
		//	List<Double> followeenumber = new ArrayList<>();
			List<Double> simList = new ArrayList<>();
			List<Double> tkiList = new ArrayList<>();
			List<Double> labellist = new ArrayList<>();
			List<Integer> colorlist = new ArrayList<>();
			for (int i = 0; i < userIdList.size(); i++) {
				T ReferenceUserId = userIdList.get(i);

				User<T> ReferenceUser = userSet.get(ReferenceUserId);
				// || trainSet.get(userId).contains(ReferenceUserId)
				if (ReferenceUserId.equals(userId)) {
					continue;
				}

				// cal the first step
				double countFirstValue = 0;
				for (Long followeeId : followeelist) {
					User<T> followee = userSet.get(followeeId);
					if (followee.getfollowee().contains(ReferenceUserId)) {
						countFirstValue++;
					}
				}
//				if (countFirstValue > maxfollowee)
//					maxfollowee = countFirstValue;

				double secondValue = MatrixUtil.vectorMultiply(
						changeTopKFeature, ReferenceUser.getTopKFeature(topK));
//				if (secondValue > maxsim)
//					maxsim = secondValue;

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
				if (result>bigTKI) {
					bigTKI=result;
				}
				if ((trainSet.get(userId).contains(ReferenceUserId)||testSet.get(userId).contains(ReferenceUserId))) {
					double x=random.nextDouble()+1;
					while (x>1.9||x<1.05) {
						 x=random.nextDouble()+1;
					}
					labellist.add(x);
					colorlist.add(1);
				} else {
					double x=random.nextDouble();
					while (x>0.9||x<0.05) {
						 x=random.nextDouble();
					}
					labellist.add(x);
					colorlist.add(0);
				}
				tkiList.add(result);
				
				
			}
			List<T> result1=new ArrayList<>();
	
			for (int i = 0; i < labellist.size(); i++) {
				double tki = tkiList.get(i);
				tki/=bigTKI;
				tkiList.set(i, tki);
			}
			HashMap<String, Double> heatmap=new HashMap<>();
			double max=0;
			for (int i = 0; i < labellist.size(); i++) {
				double tki = tkiList.get(i);
				double x=labellist.get(i);
				int xindex=(int)(x/0.001);
				int yindex=(int)(tki/0.0001);
				String slice=xindex+"\t"+yindex;
				if(heatmap.containsKey(slice)){
					heatmap.put(slice, heatmap.get(slice)+1);
					if (heatmap.get(slice)>max) {
						max=heatmap.get(slice);
					}
				}
				else {
					heatmap.put(slice, 1.0);
				}
			}
			Iterator<String> iterator=heatmap.keySet().iterator();
			
			while(iterator.hasNext()){
				String keyString=iterator.next();
				heatmap.put(keyString, heatmap.get(keyString)/max);
			}
			WriteUtil<T> writeUtil = new WriteUtil<>();
			writeUtil.writeMapKeyAndValuesplitbyt(heatmap, path +"plot/14380001tki-heat.txt");
	
		}

		return 0;

	}

	protected String compute() {

		Map<T, List<T>> trainSet = null;
		Map<T, List<T>> testSet = null;

		// user and feature
		Map<T, User<T>> userSet;
		ReadUtil<T> readUtil = new ReadUtil();

		List<T> userIdList = readUtil.readFileByLine(this.userListFileName);

		userSet = readUtil.readUser(userIdList, this.userFeatureFileName);

		// split Train and test into 90% and 10%
		SplitTrainAndTest<T> splitTrainAndTest = new SplitTrainAndTest(
				this.followGraphFileName, userSet, this.limit, 0.1);

		testSet = splitTrainAndTest.getTestFollow();

		trainSet = splitTrainAndTest.getTrainFollow();
		userSet = splitTrainAndTest.getUserSet();
		// cal the setcalTopKFeature for each user
		Iterator<T> KeyList = userSet.keySet().iterator();
		while (KeyList.hasNext()) {
			T keyT = KeyList.next();
			User<T> oneUser = userSet.get(keyT);
			Set<Long> followeeList = oneUser.getfollowee();

			double[] topkfeature = oneUser.getTopKFeature(topK);
			// double[] topkfeature=new double[100];
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

}
