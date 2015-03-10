/**
 * 
 */
package MF.Interface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import util.MathCal;
import util.MatrixUtil;
import MF.bean.Feature;
import MF.bean.Item;
import MF.util.HashMapTool.TwoValueComparatorInLong;

/**
 * 
 * @progject_name：MF
 * @class_name：IterativeRecommender
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年12月17日 下午12:25:45
 * @modifier：zhouge
 * @modified_time：2014年12月17日 下午12:25:45
 * @modified_note：
 * @version
 * 
 */
public class IterativeRanking extends Recommendation{

	protected final int LatentDimension, userSize, itemSize, maxIter;

	protected final Map<Long, Set<Long>> ratingMatrix, testmatrix;

	protected final Map<Long, Set<Long>> item_user_Map;

	protected final double learnRatio, reg_user, reg_Item;

	protected final List<Long> userList, itemList;

	protected Map<Long, Feature> userFeaturemap;

	protected Map<Long, Feature> ItemFeaturemap;

	protected final Map<Long, Integer> userIndexmap;

	protected final Map<Integer, Long> userMap;

	protected final Map<Integer, Long> itemMap;

	protected   int iterCount = 0;
	protected Random random = new Random();

	protected boolean ranking;
	
	protected Map<Long, Double> userBasis=null;
	protected Map<Long, Double> itemBasis=null;
	
	/**
	 * @param LatentDimension
	 * @param ratingMatrix
	 * @param testMatrix
	 * @param maxIter
	 * @param learnRatio
	 * @param theta
	 */
	public IterativeRanking(int LatentDimension,
			Map<Long, Set<Long>> ratingMatrix, Map<Long, Set<Long>> testMatrix,
		 int maxIter,	double learnRatio, double theta) {
		this.testmatrix = testMatrix;
		this.LatentDimension = LatentDimension;
		this.ratingMatrix = ratingMatrix;
		random.setSeed(System.currentTimeMillis());
		if (ratingMatrix == null) {
			System.out
					.println("error in IterativeRecommender construction function: the ratingMatrix cannot be null");
			System.exit(0);
		}
		item_user_Map = new HashMap<>();
		for (Long userid : ratingMatrix.keySet()) {
			for (Long itemid : ratingMatrix.get(userid)) {
				if (!item_user_Map.containsKey(itemid)) {
					Set<Long> selectUserId = new HashSet<>();
					selectUserId.add(userid);
					item_user_Map.put(itemid, selectUserId);
				} else {
					item_user_Map.get(itemid).add(userid);
				}
			}
		}
		
		
		this.maxIter = maxIter;
		this.learnRatio = learnRatio;
		this.reg_user = theta;
		this.reg_Item = theta;
		this.userList = new ArrayList<>(ratingMatrix.keySet());
		this.userSize = userList.size();
		this.itemList = new ArrayList<>(item_user_Map.keySet());
		this.itemSize = itemList.size();
		
//		Collections.shuffle(this.userList);
//		Collections.shuffle(this.itemList);
		userIndexmap = new HashMap<>();
		for (Long userid : userList) {
			userIndexmap.put(userid, this.userList.indexOf(userid));
		}

		userMap = new HashMap<>();
		userFeaturemap = new HashMap<>();
		for (int i = 0; i < this.userList.size(); i++) {
			userMap.put(i, this.userList.get(i));
			Feature randomFeature = new Feature();
			randomFeature.setFeature(MatrixUtil.getRandomVector(
					LatentDimension, 0.01));
			userFeaturemap.put(this.userList.get(i), randomFeature);
		}

		itemMap = new HashMap<>();
		ItemFeaturemap = new HashMap<>();
		for (int i = 0; i < this.itemList.size(); i++) {
			itemMap.put(i, this.itemList.get(i));
			Feature randomFeature = new Feature();
			randomFeature.setFeature(MatrixUtil.getRandomVector(
					LatentDimension, 0.01));
			ItemFeaturemap.put(this.itemList.get(i), randomFeature);
		}
	}

	@Override
	public void run(){
		this.IterLearningBySGD();
	}
	public void calculateMeasure() {
		// 判断是否为ranking
		if (ranking) {
			// 计算MAP值
			
			double MAP = getMAP(10);
			System.out.println(iterCount+"\t"+MAP);
		} else {

		}
	}

	/**
	 * @param RecommendLength
	 * @return 测试集中所有用户的 AP
	 *@create_time：2014年12月17日下午1:05:11
	 *@modifie_time：2014年12月17日 下午1:05:11
	  
	 */
	public double getMAP(int RecommendLength) {
		List<Double> AP = new ArrayList<>();
		for (long userid : testmatrix.keySet()) {
			if (!ratingMatrix.containsKey(userid)) {
				continue;
			}
			
			HashMap<Long, Double> itemAndValue = new HashMap<>();
			for (long itemid : itemList) {
				if (!ratingMatrix.get(userid).contains(itemid)) {
					double value=0;
					if (userBasis!=null) {
						value=userBasis.get(userid)+itemBasis.get(itemid)+MatrixUtil.vectorMul(userFeaturemap
								.get(userid).copyFeature(),
								ItemFeaturemap.get(itemid).copyFeature());
					}else {
						value = MatrixUtil.vectorMul(userFeaturemap
								.get(userid).copyFeature(),
								ItemFeaturemap.get(itemid).copyFeature());
					}
					
					itemAndValue.put(itemid, value);
				}
			}
			if (itemAndValue.keySet().size() < 1) {
				continue;
			}
			TwoValueComparatorInLong tvc = new TwoValueComparatorInLong(
					itemAndValue);
			List<Long> pitemList = new ArrayList<>(itemAndValue.keySet());
			Collections.sort(pitemList, tvc);
//			for (Long long1 : itemList) {
//				System.out.println(itemAndValue.get(long1));
//			}
			
			pitemList = pitemList.size() > RecommendLength ? pitemList.subList(0,
					RecommendLength) : pitemList;
			AP.add(AP(pitemList, testmatrix.get(userid)));
		}

		return MathCal.getAverage(AP);
	}

	/**
	 * @param recomList
	 * @param testList
	 * @return 某一个用户的AP
	 * @create_time：2014年12月17日下午1:04:59
	 * @modifie_time：2014年12月17日 下午1:04:59
	 */
	public double AP(List<Long> recomList, Set<Long> testList) {
		double hit = 0;
		double scoreSum = 0;
		for (int i = 0; i < recomList.size(); i++) {
			if (testList.contains(recomList.get(i))) {
				hit++;
				scoreSum += hit / (i + 1);
			}
		}
		if (hit == 0) {
			return 0;
		}
		scoreSum /= Math.min(recomList.size(), testList.size());
		return scoreSum;
	}

	/**
	 * 
	 * @create_time：2014年12月17日下午12:30:50
	 * @modifie_time：2014年12月17日 下午12:30:50
	 */
	protected void IterLearningBySGD() {

	}
}
