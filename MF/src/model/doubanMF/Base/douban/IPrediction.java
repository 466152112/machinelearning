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
 * @progject_name閿熸枻鎷穡eiboNew
 * @class_name閿熸枻鎷稭F
 * @class_describe閿熸枻鎷?the simple MF
 * @creator閿熸枻鎷穤houge
 * @create_time閿熸枻鎷?014閿熸枻鎷?閿熸枻鎷?7閿熸枻鎷?閿熸枻鎷烽敓鏂ゆ嫹10:17:15
 * @modifier閿熸枻鎷穤houge
 * @modified_time閿熸枻鎷?014閿熸枻鎷?閿熸枻鎷?7閿熸枻鎷?閿熸枻鎷烽敓鏂ゆ嫹10:17:15
 * @modified_note閿熸枻鎷?
 * @version
 * 
 */
public class IPrediction extends RecursiveTask<String> {

	private final double fullRMSE;
	private final double coldRMSE;
	private Map<Long, Map<Long, Double>> ratingMatrix, testmatrix;
	private final int trainSeq;
	private final Map<Long, Double> itemavgMap;

	/**
	 * @param LatentDimension
	 * @param ratingMatrix
	 * @param maxIter
	 * @param learnRatio
	 * @param theta
	 */

	public IPrediction(Map<Long, Map<Long, Double>> ratingMatrix,
			Map<Long, Map<Long, Double>> testMatrix, int trainSeq) {

		this.testmatrix = testMatrix;
		this.ratingMatrix = ratingMatrix;
		if (ratingMatrix == null) {
			System.out
					.println("error in IPrediction construction function: the ratingMatrix cannot be null");
			System.exit(0);
		}
		itemavgMap = getAvgItem(ratingMatrix);
		fullRMSE = FullRMSE(testMatrix);
		coldRMSE = coldStartRMSE(testMatrix);
		this.trainSeq = trainSeq;
	}

	/**
	 * @param rating
	 * @return cal the avg
	 * @create_time：2014年11月29日下午10:24:27
	 * @modifie_time：2014年11月29日 下午10:24:27
	 */
	public Map<Long, Double> getAvgItem(Map<Long, Map<Long, Double>> rating) {
		Map<Long, Double> itemAvgMap = new HashMap<>();
		Map<Long, List<Double>> itemRatingList = new HashMap<>();
		for (long userid : rating.keySet()) {
			for (long itemid : rating.get(userid).keySet()) {
				if (itemRatingList.containsKey(itemid)) {
					itemRatingList.get(itemid).add(
							rating.get(userid).get(itemid));
				} else {
					List<Double> temp = new ArrayList<>();
					temp.add(rating.get(userid).get(itemid));
					itemRatingList.put(itemid, temp);
				}
			}
		}
		for (long itemid : itemRatingList.keySet()) {
			double avg = MathCal.getAverage(itemRatingList.get(itemid));
			itemAvgMap.put(itemid, avg);
		}
		return itemAvgMap;
	}

	public double FullRMSE(Map<Long, Map<Long, Double>> testMatrix) {

		double result = 0;
		int count = 0;
		// 閿熺煫浼欐嫹閿熸枻鎷峰惊閿熸枻鎷?
		List<Long> userlisttest = new ArrayList<>(testMatrix.keySet());
		for (Long userid : userlisttest) {

			for (Long itemid : testMatrix.get(userid).keySet()) {
				if (!itemavgMap.containsKey(itemid)) {
					continue;
				}
				double realratingValue = testMatrix.get(userid).get(itemid);
				double temp = itemavgMap.get(itemid);
				result += Math.pow(realratingValue - temp, 2);
				count++;
			}
		}
		result = result / count;
		result = Math.pow(result, 0.5);
		return result;
	}

	public double coldStartRMSE(Map<Long, Map<Long, Double>> testMatrix) {
		final int coldLimit = 5;
		double result = 0;
		int count = 0;
		// 閿熺煫浼欐嫹閿熸枻鎷峰惊閿熸枻鎷?
		List<Long> userlisttest = new ArrayList<>(testMatrix.keySet());
		for (Long userid : userlisttest) {
			if (!ratingMatrix.containsKey(userid)
					|| ratingMatrix.get(userid).size() >= coldLimit) {
				continue;
			}
			for (Long itemid : testMatrix.get(userid).keySet()) {
				if (!itemavgMap.containsKey(itemid)) {
					continue;
				}
				double realratingValue = testMatrix.get(userid).get(itemid);
				double temp = itemavgMap.get(itemid);
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
		String result = "Finish "+this.trainSeq+" IBase " + " Current full RMSE " + fullRMSE
				+ " Cold-Start RMSE " + coldRMSE;
		return result;
	}
}
