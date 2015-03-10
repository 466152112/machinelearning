/**
 * 
 */
package MF.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**   
 *    
 * @progject_name：BPR   
 * @class_name：HashMapUtil   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年7月22日 下午4:17:17   
 * @modifier：zhouge   
 * @modified_time：2014年7月22日 下午4:17:17   
 * @modified_note：   
 * @version    
 *    
 */
public class HashMapUtil<T,V > {
	
	/**
	 * @param RatingMap
	 * @param limit 
	 * @return the key List that need remove
	 *@create_time：2014年7月22日下午4:17:55
	 *@modifie_time：2014年7月22日 下午4:17:55
	  
	 */
	 public   List<T> GetRemoveElementFromKeyByNumberLimit(Map<T, List<V>> map,int limit) {
		List<T> removeKey=new LinkedList<T>();
		Iterator<T> KeyList=map.keySet().iterator();
		while(KeyList.hasNext()){
			T key=KeyList.next();
			
			if (map.get(key).size()<limit) {
			//	System.out.println(map.get(key).size());
				removeKey.add(key);
			}
		}
		return removeKey;
	}
	
	 /**
	 * @param map 
	 * @param removeList element list in value list need by remove
	 * @return the clear map
	 *@create_time：2014年7月22日下午4:35:59
	 *@modifie_time：2014年7月22日 下午4:35:59
	  
	 */
	public Map<T,List<V>> removeElementFromValueByList(Map<T, List<V>> map,List<T> removeList){
		Iterator<T> KeyList=map.keySet().iterator();
		Map<T, List<V>> resultMap=new HashMap<T, List<V>>();
		while(KeyList.hasNext()){
			T key=KeyList.next();
			List<V> elementList=map.get(key);
//			System.out.println(elementList.size());
//			boolean flag=elementList.removeAll(removeList);
//			System.out.println(flag);
//			System.out.println(elementList.size());
			elementList.removeAll(removeList);
			if (elementList.size()==0) {
				continue;
			}
			resultMap.put(key,elementList);
		}
		 return map;
	 } 
	
	/**
	 * @param map
	 * @param removeMap
	 * @return remove map element from another like minus
	 *@create_time：2014年7月22日下午5:22:57
	 *@modifie_time：2014年7月22日 下午5:22:57
	  
	 */
	public Map<T,List<T>> removeElementFromAnotherMapByKeyAndValue(Map<T, List<T>> map,Map<T, List<T>> removeMap){
		Iterator<T> KeyList=removeMap.keySet().iterator();
		while(KeyList.hasNext()){
			T key=KeyList.next();
			List<T> removeElementList=removeMap.get(key);
			map.get(key).remove(removeElementList);
		}
		 return map;
	 } 
	/**
	 * @param map
	 * @param removeMap
	 * @return remove map element from another like minus
	 *@create_time：2014年7月22日下午5:22:57
	 *@modifie_time：2014年7月22日 下午5:22:57
	  
	 */
	public Map<T,List<V>> removeElementFromKey(Map<T, List<V>> map,List<T> removeKeyList){
		Iterator<T> KeyList=map.keySet().iterator();
		Map<T, List<V>> resultMap=new HashMap<T, List<V>>();
		while(KeyList.hasNext()){
			T key=KeyList.next();
			if (removeKeyList.contains(key)) {
				continue;
			}else {
				resultMap.put(key, map.get(key));
			}
		}
		 return resultMap;
	 } 
}
