/**
 * 
 */
package tool.MapTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @progject_name锛欱PR
 * @class_name锛欻ashMapUtil
 * @class_describe锛�
 * @creator锛歾houge
 * @create_time锛�014骞�鏈�2鏃�涓嬪�?:17:17
 * @modifier锛歾houge
 * @modified_time锛�014骞�鏈�2鏃�涓嬪�?:17:17
 * @modified_note锛�
 * @version
 * 
 */
public class HashMapUtil<T> {

	/**
	 * @param RatingMap
	 * @param limit
	 * @return the key List that need remove
	 * @create_time锛�014骞�鏈�2鏃ヤ笅鍗�?17:55
	 * @modifie_time锛�014骞�鏈�2鏃�涓嬪�?:17:55
	 */
	public List<T> GetRemoveElementFromKeyByNumberLimit(Map<T, List<T>> map,
			int limit) {
		List<T> removeKey = new ArrayList();
		Iterator<T> KeyList = map.keySet().iterator();
		while (KeyList.hasNext()) {
			T key = KeyList.next();

			if (map.get(key).size() < limit) {
				// System.out.println(map.get(key).size());
				removeKey.add(key);
			}
		}
		return removeKey;
	}
	/**
	 * @param map
	 * @param limit
	 * @return
	 *@create_time�?014�?�?7日下�?:14:59
	 *@modifie_time�?014�?�?7�?下午1:14:59
	  
	 */
	public Set<T> GetRemoveElementFromKeyByNumberLimitSaveBySet(Map<T, List<T>> map,
			int limit) {
		Set<T> removeKey = new HashSet();
		Iterator<T> KeyList = map.keySet().iterator();
		while (KeyList.hasNext()) {
			T key = KeyList.next();

			if (map.get(key).size() < limit) {
				// System.out.println(map.get(key).size());
				removeKey.add(key);
			}
		}
		return removeKey;
	}
	public List<T> GetRemoveElementByValueLimit(Map<T, Double> map,
			int limit) {
		List<T> removeKey = new LinkedList<T>();
		Iterator<T> KeyList = map.keySet().iterator();
		while (KeyList.hasNext()) {
			T key = KeyList.next();

			if (map.get(key) < limit) {
				// System.out.println(map.get(key).size());
				removeKey.add(key);
			}
		}
		return removeKey;
	}
	
	public Set<T> GetRemoveElementByValueLimitSaveBySet(Map<T, Double> map,
			int limit) {
		Set<T> removeKey = new HashSet();
		Iterator<T> KeyList = map.keySet().iterator();
		while (KeyList.hasNext()) {
			T key = KeyList.next();

			if (map.get(key) < limit) {
				// System.out.println(map.get(key).size());
				removeKey.add(key);
			}
		}
		return removeKey;
	}
	public List<T> GetRemoveSetElementFromKeyByNumberLimit(Map<T, Set<T>> map,
			int limit) {
		List<T> removeKey = new LinkedList<T>();
		Iterator<T> KeyList = map.keySet().iterator();
		while (KeyList.hasNext()) {
			T key = KeyList.next();

			if (map.get(key).size() < limit) {
				// System.out.println(map.get(key).size());
				removeKey.add(key);
			}
		}
		return removeKey;
	}

	/**
	 * @param map
	 * @param removeList
	 *            element list in value list need by remove
	 * @return the clear map
	 * @create_time锛�014骞�鏈�2鏃ヤ笅鍗�?35:59
	 * @modifie_time锛�014骞�鏈�2鏃�涓嬪�?:35:59
	 */
	public Map<T, List<T>> removeElementFromValueByList(Map<T, List<T>> map,
			List<T> removeList) {
		Iterator<T> KeyList = map.keySet().iterator();
		Map<T, List<T>> resultMap = new HashMap<T, List<T>>();
		while (KeyList.hasNext()) {
			T key = KeyList.next();
			List<T> elementList = map.get(key);
			Set<T> elementSet = new HashSet<>();
			elementSet.addAll(elementList);
			elementSet.removeAll(removeList);

			if (elementList.size() == 0) {
				continue;
			}
			Iterator<T> iterator = elementSet.iterator();
			elementList.clear();
			while (iterator.hasNext()) {
				T t = (T) iterator.next();
				elementList.add(t);
			}
			resultMap.put(key, elementList);
		}
		return map;
	}

	public Map<T, Set<T>> removeSetElementFromValueByList(Map<T, Set<T>> map,
			List<T> removeList) {
		Iterator<T> KeyList = map.keySet().iterator();
		Map<T, Set<T>> resultMap = new HashMap<T, Set<T>>();
		while (KeyList.hasNext()) {
			T key = KeyList.next();
			Set<T> elementSet = map.get(key);
			elementSet.removeAll(removeList);

			if (elementSet.size() == 0) {
				continue;
			}

			resultMap.put(key, elementSet);
		}
		return map;
	}

	/**
	 * @param map
	 * @param removeMap
	 * @return remove map element from another like minus
	 * @create_time锛�014骞�鏈�2鏃ヤ笅鍗�?22:57
	 * @modifie_time锛�014骞�鏈�2鏃�涓嬪�?:22:57
	 */
	public Map<T, List<T>> removeElementFromAnotherMapByKeyAndValue(
			Map<T, List<T>> map, Map<T, List<T>> removeMap) {
		Iterator<T> KeyList = removeMap.keySet().iterator();
		while (KeyList.hasNext()) {
			T key = KeyList.next();
			List<T> removeElementList = removeMap.get(key);
			boolean flag = map.get(key).removeAll(removeElementList);
			// if(!flag){
			// System.out.println(removeElementList);
			// System.out.println(map.get(key));
			// System.out.println("there is error in the function removeElementFromAnotherMapByKeyAndValue");
			// }

		}
		return map;
	}

	/**
	 * @param map
	 * @param removeMap
	 * @return remove map element from another like minus
	 * @create_time锛�014骞�鏈�2鏃ヤ笅鍗�?22:57
	 * @modifie_time锛�014骞�鏈�2鏃�涓嬪�?:22:57
	 */
	public Map<T, List<T>> removeElementFromKey(Map<T, List<T>> map,
			List<T> removeKeyList) {
		Iterator<T> KeyList = map.keySet().iterator();
		Map<T, List<T>> resultMap = new HashMap<T, List<T>>();
		while (KeyList.hasNext()) {
			T key = KeyList.next();
			if (removeKeyList.contains(key)) {
				continue;
			} else {
				resultMap.put(key, map.get(key));
			}
		}
		return resultMap;
	}

	public Map<T, Set<T>> removeSetElementFromKey(Map<T, Set<T>> map,
			List<T> removeKeyList) {
		Iterator<T> KeyList = map.keySet().iterator();
		Map<T, Set<T>> resultMap = new HashMap<T, Set<T>>();
		while (KeyList.hasNext()) {
			T key = KeyList.next();
			if (removeKeyList.contains(key)) {
				continue;
			} else {
				resultMap.put(key, map.get(key));
			}
		}
		return resultMap;
	}
}
