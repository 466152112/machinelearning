/**
 * 
 */
package model.doubanMF.Base.douban;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

import util.MathCal;
import util.MatrixUtil;
import util.ReadUtil;
import util.WriteUtil;
import bean.User;

/**
 * 
 * @progject_name锟斤拷weiboNew
 * @class_name锟斤拷MF
 * @class_describe锟斤�?the simple MF
 * @creator锟斤拷zhouge
 * @create_time锟斤�?014锟斤�?锟斤�?7锟斤�?锟斤拷锟斤拷10:17:15
 * @modifier锟斤拷zhouge
 * @modified_time锟斤�?014锟斤�?锟斤�?7锟斤�?锟斤拷锟斤拷10:17:15
 * @modified_note锟斤�?
 * @version
 * 
 */
public class UPrediction extends RecursiveTask<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Long, Map<Long, Double>> ratingMatrix, testmatrix;
	private final int trainSeq;
	private final Map<Long, Double> useravgMap;
	private final double fullRMSE;
	private final double coldRMSE;
	/**
	 * @param LatentDimension
	 * @param ratingMatrix
	 * @param maxIter
	 * @param learnRatio
	 * @param theta
	 */

	public UPrediction(
			Map<Long, Map<Long, Double>> ratingMatrix,
			Map<Long, Map<Long, Double>> testMatrix,int trainSeq
			) {
		
		this.testmatrix = testMatrix;
		this.ratingMatrix = ratingMatrix;
		if (ratingMatrix == null) {
			System.out
					.println("error in IPrediction construction function: the ratingMatrix cannot be null");
			System.exit(0);
		}
		useravgMap=getAvgUser(ratingMatrix);
		fullRMSE=FullRMSE(testMatrix);
		coldRMSE=coldStartRMSE(testMatrix);
		this.trainSeq = trainSeq;
	}
	

	/**
	 * @param rating
	 * @return cal the avg
	 *@create_time��2014��11��29������10:24:27
	 *@modifie_time��2014��11��29�� ����10:24:27
	  
	 */
	public Map<Long, Double> getAvgUser(
			Map<Long, Map<Long, Double>> rating) {
		Map<Long, Double> userAvgMap=new HashMap<>();
		Map<Long,List<Double>> userRatingList=new HashMap<>();
		for (long userid : rating.keySet()) {
			if (!userRatingList.containsKey(userid)) {
				List<Double> tempDoubles=new ArrayList();
				userRatingList.put(userid, tempDoubles);
			}
			for (long itemid : rating.get(userid).keySet()) {
				userRatingList.get(userid).add(rating.get(userid).get(itemid));
			}
		}
		for (long userid : userRatingList.keySet()) {
			double avg=MathCal.getAverage(userRatingList.get(userid));
			userAvgMap.put(userid, avg);
		}
		return  userAvgMap;
	}

	
	
	public double FullRMSE(Map<Long, Map<Long, Double>> testMatrix) {

		double result = 0;
		int count = 0;
		// 锟矫伙拷锟斤拷循锟斤�?
		List<Long> userlisttest = new ArrayList<>(testMatrix.keySet());
		for (Long userid : userlisttest) {
			if (!useravgMap.containsKey(userid)) {
				continue;
			}
			for (Long itemid : testMatrix.get(userid).keySet()) {
				double realratingValue = testMatrix.get(userid).get(itemid);
				double temp = useravgMap.get(userid);
				result += Math.pow(realratingValue - temp, 2);
				count++;
			}
		}
		result = result / count;
		result = Math.pow(result, 0.5);
		return result;
	}
	
	public double coldStartRMSE(Map<Long, Map<Long, Double>> testMatrix) {
		final int coldLimit=5;
		double result = 0;
		int count = 0;
		// 锟矫伙拷锟斤拷循锟斤�?
		List<Long> userlisttest = new ArrayList<>(testMatrix.keySet());
		for (Long userid : userlisttest) {
			if (!useravgMap.containsKey(userid)||ratingMatrix.get(userid).size()>=coldLimit) {
				continue;
			}
			for (Long itemid : testMatrix.get(userid).keySet()) {
				double realratingValue = testMatrix.get(userid).get(itemid);
				double temp =useravgMap.get(userid);
				result += Math.pow(realratingValue - temp, 2);
				count++;
			}
		}
		result = result / count;
		result = Math.pow(result, 0.5);
		return result;
	}

	@Override
	protected String compute() {
		
		String result ="Finish "+this.trainSeq+" UBase "+ " Current full RMSE " + fullRMSE+ " Cold-Start RMSE "+coldRMSE ;
		return result;
	}
}
