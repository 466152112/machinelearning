/**
 * 
 */
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import MF.SplitTrainAndTest.SplitTrainAndTest;
import MF.bean.Item;
import MF.bean.Rating;
import MF.bean.User;
import MF.util.MathCal;
import MF.util.MatrixUtil;

/**   
 *    
 * @progject_name：MF   
 * @class_name：PMF   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月9日 下午4:10:49   
 * @modifier：zhouge   
 * @modified_time：2014年10月9日 下午4:10:49   
 * @modified_note：   
 * @version    
 *    
 */
public class PMF {
	final  HashMap<Integer, HashMap<Integer, Double>> ratings;
	private HashMap<Integer, User> users;
	private HashMap<Integer, Item> items;
	
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
	public PMF(String RatingFile, int featureNumber,long LoopMax, double learningRatio,HashMap<Integer, User> users,
			HashMap<Integer, Item> items,HashMap<Integer, HashMap<Integer, Double>> ratings,
			double regularToUserFeature, double regularToItemFeature) {
		super();
		// the userFeature and itemFeature
		this.featureNumber = featureNumber;
		this.learningRatio = learningRatio;
		this.regularToItemFeature = regularToItemFeature;
		this.regularToUserFeature = regularToUserFeature;
		this.LoopMax=LoopMax;
		//change the ratings
		this.ratings=mapRating(ratings);
	}
	
	/**
	 * @param userFeature
	 * @param itemFeature
	 * @param userSet
	 * @param itemSet
	 * @param trainRating
	 * @param testRating
	 * @return
	 *@create_time：2014年10月9日下午4:18:09
	 *@modifie_time：2014年10月9日 下午4:18:09
	  
	 */
	private void learningStep() {
		List<Integer> userlist=new ArrayList<>(users.keySet());
		Collections.sort(userlist);
		List<Integer> itemList=new ArrayList<>(items.keySet());
		Collections.sort(itemList);
	
		long loopCount = 0;
		// loop until convergence
		while(true){
			if (loopCount < this.LoopMax) {
				loopCount++;
			}else {
				break;
			}
		}
	}
	
	
	/**
	 * @param rating
	 * @return change the ratingmap from 1-5 to 0-1
	 *@create_time：2014年10月9日下午4:33:45
	 *@modifie_time：2014年10月9日 下午4:33:45
	  
	 */
	public HashMap<Integer, HashMap<Integer, Double>> mapRating(HashMap<Integer, HashMap<Integer, Double>> rating){
		List<Integer> userList=new ArrayList<>(rating.keySet());
		for (Integer userid : userList) {
			HashMap<Integer, Double> ratinglist=rating.get(userid);
			List<Integer> itemList=new ArrayList<>(ratinglist.keySet());
			for (Integer itemid : itemList) {
				double ratingvalue=ratinglist.get(itemid);
				ratingvalue=mapRating(ratingvalue);
				rating.get(userid).put(itemid, ratingvalue);
			}
		}
		return rating;
	}
	
	/**
	 * @param rating
	 * @return  change the rating from 1-5 to 0-1 
	 *@create_time：2014年10月9日下午4:43:50
	 *@modifie_time：2014年10月9日 下午4:43:50
	  
	 */
	public double mapRating(double rating){
		double temp=(rating-1)/4;
		return temp;
	}
	/**
	 * @return the users
	 */
	public HashMap<Integer, User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(HashMap<Integer, User> users) {
		this.users = users;
	}

	/**
	 * @return the items
	 */
	public HashMap<Integer, Item> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(HashMap<Integer, Item> items) {
		this.items = items;
	}
	
	
}
