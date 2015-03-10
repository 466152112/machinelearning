/**
 * 
 */
package model.doubanMF.pagerank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;






import model.doubanMF.socialReg.douban.Feature;
import util.MathCal;
import util.MatrixUtil;

/**
 * 
 * @progject_name��weiboNew
 * @class_name��MF
 * @class_describe�� the simple MF
 * @creator��zhouge
 * @create_time��2014��8��27�� ����10:17:15
 * @modifier��zhouge
 * @modified_time��2014��8��27�� ����10:17:15
 * @modified_note��
 * @version
 * 
 */
public class SocialMF1 extends RecursiveTask<String> {
	private final int LatentDimension, userSize, itemSize, maxIter;
	private Map<Long, Map<Long, Double>> ratingMatrix, testmatrix;
	private final double learnRatio, theta1, theta2, alpeh;
	private final List<Long> userList, itemList;
	private final HashMap<Long, HashMap<Long, Double>> friendPCC;
	private Map<Long, Feature> userFeaturemap;
	private Map<Long, Feature> ItemFeaturemap;
	private double FinalRSME = Double.MAX_VALUE;
	private final Map<Long, Integer> userIndexmap;
	private final Map<Integer, Long> userMap;
	private final Map<Integer, Long> itemMap;
	
	/**
	 * @param LatentDimension
	 * @param ratingMatrix
	 * @param maxIter
	 * @param learnRatio
	 * @param theta
	 */

	public SocialMF1(int LatentDimension,
			Map<Long, Map<Long, Double>> ratingMatrix,
			Map<Long, Map<Long, Double>> testMatrix,
			HashMap<Long, HashMap<Long, Double>> friendPCC,
			List<Long> itemList,List<Long> useridList, int maxIter, double learnRatio, double theta,
			double alpeh) {

		System.out.println("enter the SocialMF1 Class--------------");
		this.testmatrix = testMatrix;
		this.LatentDimension = LatentDimension;
		this.ratingMatrix = ratingMatrix;
		this.alpeh = alpeh;
		if (ratingMatrix == null) {
			System.out
					.println("error in SocialMF1 construction function: the ratingMatrix cannot be null");
			System.exit(0);
		}
		this.friendPCC = friendPCC;
		this.maxIter = maxIter;
		this.learnRatio = learnRatio;
		this.theta1 = theta;
		this.theta2 = theta;
		this.userList = new ArrayList<>(useridList);
		this.userSize = userList.size();
		this.itemSize = itemList.size();
		this.itemList = new ArrayList<>(itemList);
		Collections.shuffle(this.userList);
		Collections.shuffle(this.itemList);
		userIndexmap=new HashMap<>();
		for (Long userid : userList) {
			userIndexmap.put(userid, this.userList.indexOf(userid));
		}
		userMap=new HashMap<>();
		userFeaturemap=new HashMap<>();
		for (int i = 0; i < this.userList.size(); i++) {
			userMap.put(i, this.userList.get(i));
			Feature randomFeature=new Feature();
			randomFeature.setFeature(MatrixUtil.getRandomVector(LatentDimension, 0.01));
			userFeaturemap.put(this.userList.get(i), randomFeature);
		}
		itemMap=new HashMap<>();
		ItemFeaturemap=new HashMap<>();
		for (int i = 0; i < this.itemList.size(); i++) {
			itemMap.put(i, this.itemList.get(i));
			Feature randomFeature=new Feature();
			randomFeature.setFeature(MatrixUtil.getRandomVector(LatentDimension, 0.01));
			ItemFeaturemap.put(this.itemList.get(i), randomFeature);
		}
	}

	/**
	 * ͨ������ݶ��½���������U �� V ����ʱ�䣺2014��4��9������4:40:33
	 * �޸�ʱ�䣺2014��4��9�� ����4:40:33
	 */
	public void IterBySGD() {
		System.out.println("IterBySGD----------------");
		int iterCount = 0;
		while (iterCount < this.maxIter) {
			// �û���ѭ��
			for (int userIndex = 0; userIndex < this.userSize; userIndex++) {
				
				Long userid = userMap.get(userIndex);
				for (int itemIndex = 0; itemIndex < this.itemSize; itemIndex++) {
					Long itemid = itemMap.get(itemIndex);
					if (ratingMatrix.get(userid)==null||ratingMatrix.get(userid).get(itemid)==null) {
						continue;
					}
					double realratingValue = ratingMatrix.get(userid).get(itemid);
					double predictRating = 0;
					double differenceRating = 0;
					double[] pitemFeature =ItemFeaturemap.get(itemid).copyFeature();
					double[] puserFeature=userFeaturemap.get(userid).copyFeature();
					predictRating=MatrixUtil.vectorMultiply(pitemFeature, puserFeature);
					differenceRating = predictRating - realratingValue;
					
					double[] usergradient = userGradient(userid, userIndex, itemid, itemIndex,differenceRating,pitemFeature,puserFeature);
					for (int i = 0; i < this.LatentDimension; i++) {
						//System.out.println(userFeaturemap.get(userid).getFeature()[i]);
						userFeaturemap.get(userid).getFeature()[i] -= this.learnRatio
								* usergradient[i];
						//System.out.println(userFeaturemap.get(userid).getFeature()[i]);
					}
					double[] itemgradient = itemGradient(userid, userIndex, itemid, itemIndex,differenceRating,pitemFeature,puserFeature);
					for (int i = 0; i < this.LatentDimension; i++) {
						this.ItemFeaturemap.get(itemid).getFeature()[i] -= this.learnRatio
								* itemgradient[i];
					}
				}
			}
		
			double result = this.getCost(this.testmatrix);
			iterCount++;
			if (result < 0.680) {
				FinalRSME = result;
				break;
			} else {
				if (result < FinalRSME) {
					FinalRSME = result;
				}
				System.out.println("mf1 :"+alpeh +"\t"+ iterCount + "\tlearningRatio:"+learnRatio+"\tRMSE:" + result+"\t min RMSE:"+FinalRSME);
			}
		}
		System.out.println("finish the IterBySGD----------------");

	}

	private double[] userGradient(long userid,int userIndex,long itemid,int itemIndex,double differenceRating,double[] pitemFeature,double[] puserFeature) {
		
		double[] gradient=MatrixUtil.vectorMultiplyByFraction(pitemFeature, differenceRating);
		
		double[] threeFeature = new double[this.LatentDimension];
		double fenmu = 0;
		if (friendPCC.get(userid)!=null) {
			for (long friendId : friendPCC.get(userid).keySet()) {
				if (!ratingMatrix.containsKey(friendId)) {
					continue;
				}
				double pcc = friendPCC.get(userid).get(friendId);
				if (pcc==0) {
					continue;
				}
				
				if (ratingMatrix.get(userid).size()>ratingMatrix.get(friendId).size()) {
					continue;
				}
				
				threeFeature=MatrixUtil.vectorAdd(threeFeature,  MatrixUtil.vectorMultiplyByFraction(this.userFeaturemap.get(friendId).copyFeature(),pcc));
				fenmu += pcc;
			}
		}
		
		if (fenmu!=0) {
			threeFeature = MatrixUtil.vectorMultiplyByFraction(threeFeature, -alpeh
					/ fenmu);
			gradient = MatrixUtil.vectorAdd(gradient, threeFeature);
		}
		gradient=MatrixUtil.vectorAdd(gradient, MatrixUtil.vectorMultiplyByFraction(puserFeature, theta1+alpeh));
		return gradient;
	}

	private double[] itemGradient(long userid,int userIndex,long itemid,int itemIndex,double differenceRating,double[] pitemFeature,double[] puserFeature) {
		double[] gradient=MatrixUtil.vectorMultiplyByFraction(puserFeature, differenceRating);
		double[] itemFeature1 = MatrixUtil
				.vectorMultiplyByFraction(pitemFeature, theta2);
		gradient = MatrixUtil.vectorAdd(gradient, itemFeature1);
		return gradient;
	}

	/**
	 * @return ��ۺ���-��ʧ���� ����ʱ�䣺2014��4��9������1:37:27 �޸�ʱ�䣺2014��4��9��
	 *         ����1:37:27
	 */
	public double getCost(Map<Long, Map<Long, Double>> testMatrix) {

		double result = 0;
		int count = 0;
		// �û���ѭ��
		List<Long> userlisttest = new ArrayList<>(testMatrix.keySet());
		for (Long userid : userlisttest) {
			if (!ratingMatrix.containsKey(userid)) {
				continue;
			}
			for (Long itemid : testMatrix.get(userid).keySet()) {
				if (!ItemFeaturemap.containsKey(itemid)) {
					continue;
				}
				double realratingValue = testMatrix.get(userid).get(itemid);
				double temp = 0;
				for (int Rank = 0; Rank < this.LatentDimension; Rank++) {
					temp += this.userFeaturemap.get(userid).getFeature()[Rank]
							* this.ItemFeaturemap.get(itemid).getFeature()[Rank];
				}
				result += Math.pow(realratingValue - temp, 2);
				count++;
			}
		}
		result = result / count;
		result = Math.pow(result, 0.5);
		return result;
	}

	public double getFinalRMSE() {
		return this.FinalRSME;
	}


	@Override
	protected String compute() {
		this.IterBySGD();
		String result = "SR1.pcc\t" + getFinalRMSE();
		return result;
	}

}
