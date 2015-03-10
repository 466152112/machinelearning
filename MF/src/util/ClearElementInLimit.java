/**
 * 
 */
package util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**   
 *    
 * @progject_name：MF   
 * @class_name：ClearElementInLimit   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月26日 下午7:43:56   
 * @modifier：zhouge   
 * @modified_time：2014年9月26日 下午7:43:56   
 * @modified_note：   
 * @version    
 *    
 */
public class ClearElementInLimit {
	private Map<String, Set<String>> usermap;
	private Map<String,  Set<String>> itemmap;
	private int limitUser;
	private int limitItem;
	/**
	 * @param usermap
	 * @param itemmap
	 * @param limitUser
	 * @param limitItem
	 */
	public ClearElementInLimit(Map<String, Set<String>> usermap,Map<String,  Set<String>> itemmap,int limitUser,int limitItem) {
		this.usermap=usermap;
		this.itemmap=itemmap;
		this.limitItem=limitItem;
		this.limitUser=limitUser;
		loopRemove();
	}
	/**
	 * 
	 *@create_time：2014年9月26日下午7:47:30
	 *@modifie_time：2014年9月26日 下午7:47:30
	  
	 */
	public void loopRemove(){
		//when flag is false,end the cycle and break;
		HashMapUtil<String> hashMapUtil=new HashMapUtil<>();
		while(true){
			//get the removeUser
			List<String> removeListOfUser=hashMapUtil.GetRemoveElementFromKeyByNumberLimitInSet(usermap, limitUser);
			//get the removeItem
			List<String> removeListOfItem=hashMapUtil.GetRemoveElementFromKeyByNumberLimitInSet(itemmap, limitItem);
			
			if (removeListOfUser.size()==0&&removeListOfItem.size()==0) {
				break;
			}else {
				// remove from the userset 
				for (String userId : removeListOfUser) {
					usermap.remove(userId);
				}
				for (String itemid : removeListOfItem) {
					itemmap.remove(itemid);
				}
				itemmap=hashMapUtil.removeSetElementFromValueByList(itemmap, removeListOfUser);
				usermap=hashMapUtil.removeSetElementFromValueByList(usermap, removeListOfItem);
			}
		}
	}
	/**
	 * @return the usermap
	 */
	public Map<String, Set<String>> getUsermap() {
		return usermap;
	}
	/**
	 * @param usermap the usermap to set
	 */
	public void setUsermap(Map<String, Set<String>> usermap) {
		this.usermap = usermap;
	}
	/**
	 * @return the itemmap
	 */
	public Map<String, Set<String>> getItemmap() {
		return itemmap;
	}
	/**
	 * @param itemmap the itemmap to set
	 */
	public void setItemmap(Map<String, Set<String>> itemmap) {
		this.itemmap = itemmap;
	}
	
}
