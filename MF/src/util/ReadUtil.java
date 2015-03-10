/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * @progject_name锛欱PR
 * @class_name锛歊eadUtil
 * @class_describe锛�
 * @creator锛歾houge
 * @create_time锛�014骞�鏈�2鏃�涓嬪崍7:57:22
 * @modifier锛歾houge
 * @modified_time锛�014骞�鏈�2鏃�涓嬪崍7:57:22
 * @modified_note锛�
 * @version
 * 
 */
public class ReadUtil<T> {

	/**
	 * @param fileName
	 * @return
	 * @create_time锛�014骞�鏈�2鏃ヤ笅鍗�:57:19
	 * @modifie_time锛�014骞�鏈�2鏃�涓嬪崍7:57:19
	 */
	public List<T> readFileByLineSaveByLinkedList(String fileName) {
		List<T> resultArrayList = new LinkedList();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(fileName)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				resultArrayList.add((T) oneLine);
				oneLine = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultArrayList;
	}

	public List<T> readFileByLine(String fileName) {
		List<T> resultArrayList = new ArrayList();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(fileName)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				resultArrayList.add((T) oneLine);
				oneLine = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultArrayList;
	}

	/**
	 * @param fileName
	 * @return
	 * @create_time锛�014骞�鏈�1鏃ヤ笅鍗�:59:38
	 * @modifie_time锛�014骞�鏈�1鏃�涓嬪崍6:59:38
	 */
	public Set<String> readSetFromFile(String fileName) {
		Set<String> resultArrayList = new HashSet();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(fileName)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				resultArrayList.add(oneLine);
				oneLine = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultArrayList;
	}

	public List<String> readfromFileByStream(String path) {
		List<String> result = new ArrayList<String>();
		try (FileInputStream fis = new FileInputStream(path);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(isr);) {

			String temp = bufferedReader.readLine();
			while (temp != null) {
				result.add(temp);
				temp = bufferedReader.readLine();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("read file failure");
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * @param followGraphFileName
	 * @return
	 * @create_time：2014年8月8日下午6:03:22
	 * @modifie_time：2014年8月8日 下午6:03:22
	 */
	public Map<String, Set<String>> getFollowGraph(String followGraphFileName) {
		Map<String, Set<String>> result = new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				followGraphFileName))) {
			String OneLine;
			while ((OneLine = bufferedReader.readLine()) != null) {
				if (OneLine.equals("")) {
					continue;
				}
				String[] split = OneLine.split(",");
				String user1 = split[0].trim();
				String user2 = split[1].trim();
				if (result.containsKey(user1)) {
					result.get(user1).add(user2);
				} else {
					Set<String> temp = new HashSet<>();
					temp.add(user2);
					result.put(user1, temp);
				}
				
				if (!result.containsKey(user2)) {
					Set<String> temp = new HashSet<>();
					result.put(user2, temp);
				} 
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
}
