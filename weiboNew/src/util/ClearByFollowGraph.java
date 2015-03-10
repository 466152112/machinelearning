/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.User;


/**   
*    
* @progject_name：weiboNew   
* @class_name：ClearByFollowGraph   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年8月27日 上午8:53:03   
* @modifier：zhouge   
* @modified_time：2014年8月27日 上午8:53:03   
* @modified_note：   
* @version    
*    
*/
public class ClearByFollowGraph {

	//private Map<T, Set<T>> followMap;
	private final List<Long> followGraph;
	private Map<Long, User> userMap;

	public ClearByFollowGraph(List<Long> followGraph, Map<Long, User> userMap) {
		this.followGraph = followGraph;
		this.userMap=userMap;
		this.clearUserItemByUserSetLimit();
	}

	private void clearUserItemByUserSetLimit() {

		System.out.println("begin the  clearUserItemByUserSetLimit");
		ArrayList<Long> tempArrayList=new ArrayList<>();
		for (Long t : followGraph) {
			String[] split = String.valueOf(t).split(" ");
			 long user1= Long.valueOf(split[0]);
			 long user2= Long.valueOf(split[1]);
			
			//the two user must in the userSet
			if (!userMap.containsKey(user1)||!userMap.containsKey(user2)) {
				continue;
			}
			
			userMap.get(user1).getfollowee().add(user2);
			userMap.get(user2).getfollowee().add(user1);
			tempArrayList.add(t);
		}
//		WriteUtil<String> writeUtil=new WriteUtil<>();
//		String clearFileName="userfollowgraph"+".txt";
//		writeUtil.writeList((List<String>) tempArrayList, clearFileName);

	}

	/**
	 * @return the userMap
	 */
	public Map<Long, User> getUserMap() {
		return userMap;
	}

}
