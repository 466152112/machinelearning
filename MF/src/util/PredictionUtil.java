/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import MF.bean.Item;
import MF.bean.User;

/**   
 *    
 * @progject_name：MF   
 * @class_name：Prediction   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月9日 下午7:35:39   
 * @modifier：zhouge   
 * @modified_time：2014年10月9日 下午7:35:39   
 * @modified_note：   
 * @version    
 *    
 */
public class PredictionUtil {

	/**
	 * @param users
	 * @param items
	 * @param ratings
	 * @return
	 *@create_time：2014年10月9日下午7:42:29
	 *@modifie_time：2014年10月9日 下午7:42:29
	  
	 */
	public HashMap<Integer, HashMap<Integer, Double>> predicition(HashMap<Integer, User> users,HashMap<Integer, Item> items,HashMap<Integer, HashMap<Integer, Double>> ratings){
		HashMap<Integer, HashMap<Integer, Double>> result=new HashMap<>();
		List<Integer> userList=new ArrayList<>(ratings.keySet());
		for (Integer userid : userList) {
			HashMap<Integer, Double> ratinglist=ratings.get(userid);
			List<Integer> itemList=new ArrayList<>(ratinglist.keySet());
			HashMap<Integer, Double> tempitem=new HashMap<>();
			result.put(userid, tempitem);
			for (Integer itemid : itemList) {
				double temp=MatrixUtil.vectorMul(users.get(userid).getFeatures(), items.get(itemid).getFeatures());
				if (temp>5) {
					temp=5;
				}else if(temp<1){
					temp=1;
				}
				result.get(userid).put(itemid, temp);
			}
		}
		return result;
	}
	public double RMSE(HashMap<Integer, HashMap<Integer, Double>> preratings,HashMap<Integer, HashMap<Integer, Double>> realratings){
		HashMap<Integer, HashMap<Integer, Double>> result=new HashMap<>();
		List<Integer> userList=new ArrayList<>(realratings.keySet());
		double sum=0;
		int count=0;
		for (Integer userid : userList) {
			HashMap<Integer, Double> ratinglist=realratings.get(userid);
			List<Integer> itemList=new ArrayList<>(ratinglist.keySet());
			HashMap<Integer, Double> tempitem=new HashMap<>();
			result.put(userid, tempitem);
			for (Integer itemid : itemList) {
				double realvalue=realratings.get(userid).get(itemid);
				double prevalue=preratings.get(userid).get(itemid);
				sum+=Math.pow(prevalue-realvalue, 2);
				count++;
			}
		}
		sum/=count;
		return Math.pow(sum, 0.5);
	}
	
}
