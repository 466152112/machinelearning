package model;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.RecursiveTask;

import MF.SplitTrainAndTest.SplitTrainAndTest;
import MF.util.MathCal;
import MF.util.MatrixUtil;

/**
 * 
 * @progject_name：BPR
 * @class_name：BPR
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年7月23日 上午7:00:24
 * @modifier：zhouge
 * @modified_time：2014年7月23日 上午7:00:24
 * @modified_note：
 * @version
 * 
 */
public class BPR<T extends Object> extends RecursiveTask<String> {
	// the limit in rating number
	final static int limit = 4;
	final  long LoopMax ;
	final static double minUpdate=0.0001;
	// the number of feature
	final int featureNumber ;
	// the learningRatio in stochastic gradient
	final double learningRatio ;
	// the regular level to userFeature
	final double regularToUserFeature ;
	// the regular level to itemFeature
	final double regularToItemFeature ;
	final String RatingFile;
	
	

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
	public BPR(String RatingFile, int featureNumber,long LoopMax, double learningRatio,
			double regularToUserFeature, double regularToItemFeature) {
		super();
		// the userFeature and itemFeature
		this.featureNumber = featureNumber;
		this.learningRatio = learningRatio;
		this.regularToItemFeature = regularToItemFeature;
		this.regularToUserFeature = regularToUserFeature;
		this.RatingFile=RatingFile;
		this.LoopMax=LoopMax;
	}
	
	/**
	 * @param userFeature
	 * @param itemFeature
	 * @param userSet
	 * @param itemSet
	 * @param trainRating
	 * @param testRating
	 * @return
	 *@create_time：2014年7月24日上午10:06:31
	 *@modifie_time：2014年7月24日 上午10:06:31
	  
	 */
	private double learningStep(double[][] userFeature,double[][] itemFeature,List<T> userSet,List<T> itemSet,Map<T, List<T>> trainRating,Map<T, List<T>> testRating ) {
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		long loopCount = 0;
		double[] convergenceFlagVector=new double[this.featureNumber]; 
		// loop until convergence
		while (loopCount < this.LoopMax) {
			loopCount++;
			// ----------------------------------------------------------------------
			// first random an sample from trainset

			// get the user relevant variable
			// random an userIndex [0,userSiz)
			int ranomUserIndex = random.nextInt(userSet.size());
			// get the relevant userId
			T randomUserId = userSet.get(ranomUserIndex);
			// get the relevant RatingList
			List<T> randomUserRatingList = trainRating
					.get(randomUserId);
			double[] RelevantUserFeature = userFeature[ranomUserIndex];

			// get the item relevant variable in traingset
			T randomTrainItemId = randomUserRatingList.get(random
					.nextInt(randomUserRatingList.size()));
			int randomTrainItemIndex = itemSet.indexOf(randomTrainItemId);
			if (randomTrainItemIndex == -1) {
				System.out.println("Error intget random item");
				System.exit(0);
			}
			// get the relevant Feature
			double[] TrainItemFeature = itemFeature[randomTrainItemIndex];

			// get the item to compare that no in the randomUserRatingList
			int randomCompareItemIndex=random.nextInt(itemSet.size());
			T randomCompareItemId = itemSet.get(randomCompareItemIndex);
			while (randomUserRatingList.contains(randomCompareItemId)) {
				randomCompareItemIndex=random.nextInt(itemSet.size());
				randomCompareItemId = itemSet.get(randomCompareItemIndex);
			}
			
			// get the relevant Feature
			double[] CompareItemFeature = itemFeature[randomCompareItemIndex];

			// -------------------------------------------------------------------
			// the second is the cal temp variable
			double ratingTrainItem = MatrixUtil.vectorMul(
					RelevantUserFeature, TrainItemFeature);
			double ratingCompareItem = MatrixUtil.vectorMul(
					RelevantUserFeature, CompareItemFeature);
			double RatingDifference = ratingTrainItem - ratingCompareItem;
			double RatingDifferenceLogistic = MathCal.sigmoid(RatingDifference);

			// -------------------------------------------------------------------
			// the third is to update the theta of itemFeature and userFeature
			// the formula is :
			// theta=theta+learning*(RatingDifferenceLogistic*derivative(Ratingdifferece)+regularToUserFeature*theta)
			// update the userFeature

			// regularToUserFeature*theta
			double[] RelevantUserFeatureUpdate = MatrixUtil
					.vectorscale(RelevantUserFeature,
							this.regularToUserFeature);
			// RatingDifferenceLogistic*derivative(Ratingdifferece)+regularToUserFeature*theta
			RelevantUserFeatureUpdate = MatrixUtil.vectorAdd(
					RelevantUserFeatureUpdate, MatrixUtil
							.vectorscale(MatrixUtil.vectorMin(
									TrainItemFeature, CompareItemFeature),
									RatingDifferenceLogistic));
			// learning*(RatingDifferenceLogistic*derivative(Ratingdifferece)+regularToUserFeature*theta)
			RelevantUserFeatureUpdate = MatrixUtil.vectorscale(
					RelevantUserFeatureUpdate, this.learningRatio);
			
			double[] RelevantTrainItemFeatureUpdate = MatrixUtil
					.vectorscale(TrainItemFeature,
							this.regularToItemFeature);

			RelevantTrainItemFeatureUpdate = MatrixUtil.vectorAdd(
					RelevantTrainItemFeatureUpdate, MatrixUtil
							.vectorscale(RelevantUserFeature,
									RatingDifferenceLogistic));

			RelevantTrainItemFeatureUpdate = MatrixUtil
					.vectorscale(RelevantTrainItemFeatureUpdate,
							this.learningRatio);
		

			double[] CompareItemFeatureUpdate = MatrixUtil
					.vectorscale(CompareItemFeature,
							this.regularToItemFeature);

			CompareItemFeatureUpdate = MatrixUtil.vectorAdd(
					CompareItemFeatureUpdate, MatrixUtil
							.vectorscale(MatrixUtil
									.vectorscale(
											RelevantUserFeature, -1),
									RatingDifferenceLogistic));

			CompareItemFeatureUpdate = MatrixUtil.vectorscale(
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
			convergenceFlagVector=MatrixUtil.vectorAdd(convergenceFlagVector, RelevantUserFeatureUpdate);
			convergenceFlagVector=MatrixUtil.vectorAdd(convergenceFlagVector, RelevantTrainItemFeatureUpdate);
			convergenceFlagVector=MatrixUtil.vectorAdd(convergenceFlagVector, CompareItemFeatureUpdate);
			
			//judge if convergence every 20 loop
			if (loopCount>1000000&&loopCount!=0) {
				if (MatrixUtil.elementAbsoluteAdd(convergenceFlagVector)<this.minUpdate) {
					System.out.println("the loop time is :" +loopCount);
					break;
				}else {
					convergenceFlagVector=new double[this.featureNumber];
				}
			}
			//System.out.println();
		}
		//System.out.println("loop is past");
		
		
		//-------------------------------
		//call the AUC
		return calAUC(userFeature, itemFeature, userSet, itemSet, trainRating, testRating);
	}

	private double calAUC(double[][] userFeature,double[][] itemFeature,List<T> userSet,List<T> itemSet,Map<T, List<T>> trainRating,Map<T, List<T>> testRating ) {
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		int countTrue = 0;
		int countTotal = 0;
		for (T userId : userSet) {
			// because is just one
			T relevantTestItemId = testRating.get(userId).get(0);
			double relevantTestRating = MatrixUtil.vectorMul(
					userFeature[userSet.indexOf(userId)],
					itemFeature[itemSet.indexOf(relevantTestItemId)]);
			// loop the get compare ItemId that not in train and test
			for (int i = 0; i < 1000;) {
				T itemId = itemSet.get(random.nextInt(itemSet
						.size()));
				if (!trainRating.get(userId).contains(itemId)
						&& itemId != relevantTestItemId) {
					i++;
					double compareRating = MatrixUtil.vectorMul(
							userFeature[userSet.indexOf(userId)],
							itemFeature[itemSet.indexOf(itemId)]);
					if (relevantTestRating > compareRating) {
						countTrue++;
					}

					countTotal++;
//					System.out.println(countTotal + "\t" + relevantTestRating
//							+ "\t" + compareRating);
				}
			}

		}
		return countTrue * 1.0 / countTotal;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveTask#compute()
	 */
	@Override
	protected  String compute() {
		double[][] userFeature;
		double[][] itemFeature;
		final List<T> itemSet ;
		final List<T> userSet ;
		Map<T, List<T>> trainRating = null;
		Map<T, List<T>> testRating = null;
		SplitTrainAndTest<T> splitTrainAndTest = new SplitTrainAndTest(RatingFile,
				this.limit);
		itemSet = (List<T>) splitTrainAndTest.getItemSet();
		userSet= (List<T>) splitTrainAndTest.getUserSet();
		trainRating = splitTrainAndTest.getTrainRating();
		testRating = splitTrainAndTest.getTestRating();
		userFeature = MatrixUtil.createdRandomMatrix(userSet.size(),
				this.featureNumber);
		itemFeature = MatrixUtil.createdRandomMatrix(itemSet.size(),
				this.featureNumber);
		double AUCValue=learningStep(userFeature,itemFeature,userSet,itemSet,trainRating,testRating);
		
		String temp=" featureNumber\t learningRatio\t loop\t AUC\n";
		temp+=temp=this.featureNumber+ "\t"+this.learningRatio+"\t"+this.LoopMax+"\t"+AUCValue;
		return temp;
	}

}
