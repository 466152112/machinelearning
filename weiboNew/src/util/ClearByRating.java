/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.User;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：clearByRating
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年8月26日 下午11:26:16
 * @modifier：zhouge
 * @modified_time：2014年8月26日 下午11:26:16
 * @modified_note：
 * @version
 * 
 */
public class ClearByRating<T> {
	private Set<T> Userset;
	private Set<T> Itemset;
	private List<T> userList;
	private List<T> itemList;

	private Map<T, Map<T, Double>> ratingMatrix;

	private final int userlimit, itemLimit;
	private List<T> rating;

	public ClearByRating(List<T> rating, int userlimit, int itemLimit) {
		this.rating = rating;
		this.userlimit = userlimit;
		this.itemLimit = itemLimit;
		this.clearUserItemByRatingLimit();
	}

	private void clearUserItemByRatingLimit() {

		System.out.println("begin the  clearUserAndItemByRatingLimit");

		Map<T, List<T>> UsersetAndItem = new HashMap();
		Map<T, List<T>> ItemsetAndUser = new HashMap();
		

		// new the hashmap util
		HashMapUtil<T> hashMapUtil = new HashMapUtil<>();

		// the follow step is clean user and item by cycle
		// when flag is false,end the cycle and break;
		while (true) {
			UsersetAndItem = new HashMap();
			ItemsetAndUser = new HashMap();
			for (T t : rating) {
				String[] split = String.valueOf(t).split(" ");
				T user=(T) split[0];
				T item=(T) split[1];
				if (!UsersetAndItem.containsKey((T) split[0])) {
					List<T> itemList = new ArrayList<>();
					itemList.add(item);
					UsersetAndItem.put(user, itemList);
				} else {
					UsersetAndItem.get(user).add(item);
				}

				if (!ItemsetAndUser.containsKey(item)) {
					List<T> itemList = new ArrayList<>();
					itemList.add(user);
					ItemsetAndUser.put(item, itemList);
				} else {
					ItemsetAndUser.get(item).add(user);
				}

			}
			Set<T> removeListOfUser = hashMapUtil
					.GetRemoveElementFromKeyByNumberLimitSaveBySet(UsersetAndItem,
							userlimit);
			Set<T> removeListOfItem = hashMapUtil
					.GetRemoveElementFromKeyByNumberLimitSaveBySet(ItemsetAndUser,
							itemLimit);
			System.out.println("removeListOfUser："+removeListOfUser.size());
			System.out.println("removeListOfItem："+removeListOfItem.size());
			
			if (removeListOfUser.size() == 0 && removeListOfItem.size() == 0) {
				break;
			} 
			else {
				List<T> newRating=new ArrayList<>();
				for (T t : rating) {
					String[] split = String.valueOf(t).split(" ");
					T user=(T) split[0];
					T item=(T) split[1];
					if (!removeListOfUser.contains(user)&&!removeListOfItem.contains(item)) {
						newRating.add(t);
					}
				}
				System.out.println("old rating :"+rating.size());
				rating=newRating;
				System.out.println("new rating :"+rating.size());
			}
		}

		userList = new ArrayList<>(UsersetAndItem.keySet());
		itemList = new ArrayList<>(ItemsetAndUser.keySet());
		this.Userset = new HashSet<>(userList);
		this.Itemset = new HashSet<>(itemList);

		ratingMatrix = new HashMap<>();
		for (T t : rating) {
			String[] split = String.valueOf(t).split(" ");
			T user=(T) split[0];
			T item=(T) split[1];
			if (Userset.contains(user)&& Itemset.contains(item)) {
			
				if (ratingMatrix.containsKey(user)) {
					ratingMatrix.get(user).put(item, Double.valueOf(split[2]));
				}else {
					HashMap<T,Double> ratingValue=new HashMap<>();
					ratingValue.put(item, Double.valueOf(split[2]));
					ratingMatrix.put(user,ratingValue);
				}
			}
		}
		
//		WriteUtil<String> writeUtil=new WriteUtil<>();
//		String clearFileName="userlimit"+userlimit+" itemLimit"+itemLimit+".txt";
//		writeUtil.writeList((List<String>) rating, clearFileName);
		System.out.println("finish the clearUserItemByRatingLimit");
	}

	/**
	 * @return the userset
	 */
	public Set<T> getUserset() {
		return Userset;
	}

	/**
	 * @param userset the userset to set
	 */
	public void setUserset(Set<T> userset) {
		Userset = userset;
	}

	/**
	 * @return the itemset
	 */
	public Set<T> getItemset() {
		return Itemset;
	}

	/**
	 * @param itemset the itemset to set
	 */
	public void setItemset(Set<T> itemset) {
		Itemset = itemset;
	}

	/**
	 * @return the userList
	 */
	public List<T> getUserList() {
		return userList;
	}

	/**
	 * @param userList the userList to set
	 */
	public void setUserList(List<T> userList) {
		this.userList = userList;
	}

	/**
	 * @return the itemList
	 */
	public List<T> getItemList() {
		return itemList;
	}


	/**
	 * @return the ratingMatrix
	 */
	public Map<T, Map<T, Double>> getRatingMatrix() {
		return ratingMatrix;
	}

	public int getItemSize(){
		return Itemset.size();
	}
	
	
}
