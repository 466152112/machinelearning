/**
 * 
 */
package model.doubanMF.socialReg.yelp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.MathCal;

/**
 * 
 * @progject_name��weiboNew
 * @class_name��PCC
 * @class_describe��
 * @creator��zhouge
 * @create_time��2014��11��13�� ����2:39:07
 * @modifier��zhouge
 * @modified_time��2014��11��13�� ����2:39:07
 * @modified_note��
 * @version
 * 
 */
public class PCC {
	public static double getChangePCC(Map<Long, Double> rating1,
			Map<Long, Double> rating2) {
		double  fenmu1 = 0, fenmu2 = 0, fenzi = 0;
		if (rating1 == null || rating2 == null) {
			return 0;
		}
		double avg1 = MathCal.getAverage(rating1);
		double avg2 = MathCal.getAverage(rating2);
		List<Long> keySet1 = new ArrayList<>(rating1.keySet());
		keySet1.retainAll(rating2.keySet());
		for (Long key : keySet1) {
			fenzi += (rating1.get(key) - avg1) * (rating2.get(key) - avg2);
		}
		for (Long key : keySet1) {
			fenmu1 += Math.pow(rating1.get(key) - avg1, 2);
		}
		for (Long key : keySet1) {
			fenmu2 += Math.pow(rating2.get(key) - avg2, 2);
		}
		if (fenmu1 == 0 || fenmu2 == 0) {
			// System.out.println("error in getChangePCC");
			return 0.5;
		} else {
			fenmu1 = Math.pow(fenmu1, 0.5);
			fenmu2 = Math.pow(fenmu2, 0.5);
			double result=fenzi / (fenmu1 * fenmu2);
			return getChangePCC(result);
		}
		
		
	}

	public static double getChangePCC(double rating) {
		return (rating + 1) / 2;
	}
}
