package model.BPR;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.RecursiveTask;

import bean.User;
import util.MatrixUtil;
import util.ReadUtil;
import util.SplitTrainAndTest;

@SuppressWarnings("serial")
public class BPR extends RecursiveTask<String>{
	// the limit in rating number
	final static int limit = 20;
	final  long LoopMax ;
	// the number of feature
	final int featureNumber=100;
	// the learningRatio in stochastic gradient
	final double learningRatio ;
	// the regular level to userFeature
	final double regularToUserFeature ;
	// the regular level to itemFeature
	final double regularToItemFeature ;
	//fileName
	final String path, userListFileName , userFeatureFileName, followGraphFileName;
	
	

	/**
	 * @param RatingFile
	 *            :ratingFile name and path
	 * @param featureNumber
	 *            :the feature number to learning
	 * @param learningRatio
	 *            :the learningRatio in stochastic gradient
	 * @param regularToUserFeature
	 *            :the regular level to userFeature
	 * @param regularToItemFeature
	 *            :the regular level to itemFeature
	 */
	public BPR(String path,String userListFileName,String userFeatureFileName,String followGraphFileName,long LoopMax, double learningRatio,
			double regularToUserFeature, double regularToItemFeature) {
		super();
		// the userFeature and itemFeature
		
		this.learningRatio = learningRatio;
		this.regularToItemFeature = regularToItemFeature;
		this.regularToUserFeature = regularToUserFeature;
		
		this.LoopMax=LoopMax;
		this.path=path;
		this.userListFileName=path+userListFileName;
		this.userFeatureFileName=path+userFeatureFileName; 
		this.followGraphFileName=path+followGraphFileName;
	}
	
	/**
	 * @param userFeature W
	 * @param itemFeature H
	 * @param userSet
	 * @param trainRating followGraph
	 * @param testRating testFollowGraph
	 * @return
	 *@create_time�?014�?�?8日下�?:43:28
	 *@modifie_time�?014�?�?8�?下午5:43:28
	  
	 */
	private double learningStep(double[][] userFeature,double[][] itemFeature,Map<String, User>  userSet,Map<Long, List<Long>> trainRating,Map<T, List<T>> testRating ) {
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		long loopCount = 0;
		
		// the userId list
		List<T> userList=(List<T>) new ArrayList<>(userSet.keySet());
		// loop until convergence
		while(loopCount<LoopMax){
			Iterator<T> iteratortrain=trainRating.keySet().iterator();
			while(iteratortrain.hasNext()){
				T randomUserId=iteratortrain.next();
				int ranomUserIndex = userList.indexOf(randomUserId);
				
				// ----------------------------------------------------------------------
							// first random an sample from trainset

							
							// get the user relevant variable
							// random an userIndex [0,userSiz)
							//int ranomUserIndex = random.nextInt(userList.size());
							// get the relevant userId
							//T randomUserId = userList.get(ranomUserIndex);
							// get the relevant RatingList
							List<T> randomUserRatingList = trainRating
									.get(randomUserId);
							while (randomUserRatingList==null) {
								 ranomUserIndex = random.nextInt(userList.size());
								// get the relevant userId
								 randomUserId = userList.get(ranomUserIndex);
								// get the relevant RatingList
								 randomUserRatingList = trainRating
										.get(randomUserId);
							}
							double[] RelevantUserFeature = userFeature[ranomUserIndex];
							RelevantUserFeature=MatrixUtil.vectorElementMultiply(RelevantUserFeature, userSet.get(randomUserId).getFeature());
							
							// get the item relevant variable in traingset
							for (int i = 0; i < randomUserRatingList.size(); i++) {
								T randomTrainItemId = randomUserRatingList.get(i);
								
								int randomTrainItemIndex = userList.indexOf(randomTrainItemId);
								if (randomTrainItemIndex == -1) {
									System.out.println("Error int get random item");
									System.exit(0);
								}
								// get the relevant Feature
								double[] TrainItemFeature = itemFeature[randomTrainItemIndex];
								TrainItemFeature=MatrixUtil.vectorElementMultiply(TrainItemFeature, userSet.get(randomTrainItemId).getFeature());
								
								// get the item to compare that no in the randomUserRatingList
								int randomCompareItemIndex=random.nextInt(userList.size());
								T randomCompareItemId = userList.get(randomCompareItemIndex);
								while (randomUserRatingList.contains(randomCompareItemId)) {
									randomCompareItemIndex=random.nextInt(userList.size());
									randomCompareItemId = userList.get(randomCompareItemIndex);
								}
								
								// get the relevant Feature
								double[] CompareItemFeature = itemFeature[randomCompareItemIndex];
								CompareItemFeature=MatrixUtil.vectorElementMultiply(CompareItemFeature, userSet.get(randomCompareItemId).getFeature());
								
								

								// -------------------------------------------------------------------
								// the second is the cal temp variable
								double ratingTrainItem = MatrixUtil.vectorMultiply(RelevantUserFeature
										, TrainItemFeature);
								double ratingCompareItem =MatrixUtil.vectorMultiply(RelevantUserFeature
										, CompareItemFeature);
								double RatingDifference = ratingTrainItem - ratingCompareItem;
								double RatingDifferenceLogistic = Math.exp(-RatingDifference)/(1+Math.exp(-RatingDifference));

								// -------------------------------------------------------------------
								// the third is to update the theta of itemFeature and userFeature
								// the formula is :
								// theta=theta+learning*(RatingDifferenceLogistic*derivative(Ratingdifferece)+regularToUserFeature*theta)
								// update the userFeature

								// regularToUserFeature*theta
								double[] RelevantUserFeatureUpdate = MatrixUtil
										.vectorMultiplyByFraction(userFeature[ranomUserIndex],
												this.regularToUserFeature);
								// RatingDifferenceLogistic*derivative(Ratingdifferece)+regularToUserFeature*theta
								RelevantUserFeatureUpdate = MatrixUtil.vectorAdd(
										RelevantUserFeatureUpdate, MatrixUtil
												.vectorMultiplyByFraction(MatrixUtil.vectorminus(
														MatrixUtil.vectorElementMultiply(userSet.get(randomUserId).getFeature(), TrainItemFeature), 
														MatrixUtil.vectorElementMultiply(userSet.get(randomUserId).getFeature(), CompareItemFeature)),
														RatingDifferenceLogistic));
								// learning*(RatingDifferenceLogistic*derivative(Ratingdifferece)+regularToUserFeature*theta)
								RelevantUserFeatureUpdate = MatrixUtil.vectorMultiplyByFraction(
										RelevantUserFeatureUpdate, this.learningRatio);
								 
								double[] RelevantTrainItemFeatureUpdate = MatrixUtil
										.vectorMultiplyByFraction(itemFeature[randomTrainItemIndex],
												this.regularToItemFeature);

								RelevantTrainItemFeatureUpdate = MatrixUtil.vectorAdd(
										RelevantTrainItemFeatureUpdate, MatrixUtil
												.vectorMultiplyByFraction(MatrixUtil.vectorElementMultiply(RelevantUserFeature,userSet.get(randomTrainItemId).getFeature()),
														RatingDifferenceLogistic));

								RelevantTrainItemFeatureUpdate = MatrixUtil
										.vectorMultiplyByFraction(RelevantTrainItemFeatureUpdate,
												this.learningRatio);
							

								double[] CompareItemFeatureUpdate = MatrixUtil
										.vectorMultiplyByFraction(itemFeature[randomCompareItemIndex],
												this.regularToItemFeature);

								CompareItemFeatureUpdate = MatrixUtil.vectorAdd(
										CompareItemFeatureUpdate, MatrixUtil
												.vectorMultiplyByFraction(MatrixUtil
														.vectorMultiplyByFraction(
																MatrixUtil.vectorElementMultiply(RelevantUserFeature,userSet.get(randomCompareItemId).getFeature()), -1),
														RatingDifferenceLogistic));

								CompareItemFeatureUpdate = MatrixUtil.vectorMultiplyByFraction(
										CompareItemFeatureUpdate, this.learningRatio);
								
								
								// -------------------------------------------------------------------
								// the fourth is to update the theta of itemFeature and userFeature
								// to double[][] userFeature; double[][] itemFeature;
								userFeature = MatrixUtil
										.updateMatrixByRowIndex(userFeature, ranomUserIndex,
												RelevantUserFeatureUpdate);
								itemFeature = MatrixUtil.updateMatrixByRowIndex(
										itemFeature, randomTrainItemIndex,
										RelevantTrainItemFeatureUpdate);
								itemFeature = MatrixUtil.updateMatrixByRowIndex(
										itemFeature, randomCompareItemIndex,
										CompareItemFeatureUpdate);
								
								
							}
						
			}
			System.out.println(loopCount);
			loopCount++;
		}
		
	
		//System.out.println("loop is past");
		
		
		//-------------------------------
		//call the AUC
		return calAUC(userFeature, itemFeature, userSet, trainRating, testRating);
	}

	private double calAUC(double[][] userFeature,double[][] itemFeature,Map<String, User> userSet,Map<T, List<T>> trainRating,Map<T, List<T>> testRating ) {
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		int countTrue = 0;
		int countTotal = 0;
		List<T> userList=(List<T>) new ArrayList<>(testRating.keySet());
		List<T> userSetList=(List<T>) new ArrayList<>(userSet.keySet());
		for (T userId : userList) {
			for (T followee : testRating.get(userId)) {
				
				double relevantTestRating = MatrixUtil.vectorMultiply(
						MatrixUtil.vectorElementMultiply(userFeature[userSetList.indexOf(userId)],userSet.get(userId).getFeature()),
						MatrixUtil.vectorElementMultiply(itemFeature[userSetList.indexOf(followee)],userSet.get(followee).getFeature())
						);
				// loop the get compare ItemId that not in train and test
				for (int i = 0; i < 1000;) {
					T itemId = userSetList.get(random.nextInt(userSetList
							.size()));
					if (!trainRating.get(userId).contains(itemId)
							&& itemId != followee) {
						i++;
						double compareRating = MatrixUtil.vectorMultiply(
								MatrixUtil.vectorElementMultiply(userFeature[userSetList.indexOf(userId)],userSet.get(userId).getFeature()),
								MatrixUtil.vectorElementMultiply(itemFeature[userSetList.indexOf(itemId)],userSet.get(itemId).getFeature())
							);
						if (relevantTestRating > compareRating) {
							countTrue++;
						}

						countTotal++;
//						System.out.println(countTotal + "\t" + relevantTestRating
//								+ "\t" + compareRating);
					}
				}
			}

		}
		return countTrue * 1.0 / countTotal;
	}

	
	/**
	 * @return
	 *@create_time�?014�?�?8日下�?:58:05
	 *@modifie_time�?014�?�?8�?下午3:58:05
	  
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected  String compute() {
		
		double[][] userFeature;
		double[][] itemFeature;
		
		Map<T, List<T>> trainRating = null;
		Map<T, List<T>> testRating = null;
		
		//user and feature
		Map<String, User> userSet;
		ReadUtil readUtil=new ReadUtil();
		
		List<String> userIdList = readUtil.readFileByLine(this.userListFileName);
		
	    userSet = readUtil.readUser(userIdList, this.userFeatureFileName);
	    
		//split Train and test into 90% and 10%
		SplitTrainAndTest<T> splitTrainAndTest = new SplitTrainAndTest(this.followGraphFileName,userSet,
				this.limit,0.1);
	    
		testRating = splitTrainAndTest.getTestFollow();
		
		trainRating = splitTrainAndTest.getTrainFollow();
		userSet= splitTrainAndTest.getUserSet();
		System.out.println(userSet.size());
		userFeature = MatrixUtil.createdRandomMatrix(userSet.size(),
				this.featureNumber);
		
		itemFeature = MatrixUtil.createdRandomMatrix(userSet.size(),
				this.featureNumber);
		
		double AUCValue=learningStep(userFeature,itemFeature,userSet,trainRating,testRating);
		
		String temp=" featureNumber\t learningRatio\t loop\t AUC\n";
		temp+=temp=this.featureNumber+ "\t"+this.learningRatio+"\t"+this.LoopMax+"\t"+AUCValue;
		return temp;
	}

}
