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

import util.Matrix.Matrix;
import bean.User;

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
	public List<T> readFileByLine(File file) {
		List<T> resultArrayList = new ArrayList();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					file));
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
	public List<Long> readFileByLineInLong(String fileName) {
		List<Long> resultArrayList = new ArrayList();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(fileName)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				resultArrayList.add(Long.valueOf(oneLine) );
				oneLine = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultArrayList;
	}

	public Matrix readMatrixFromFile(String fileName){
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(fileName)));
			String oneLine = bufferedReader.readLine();
			Matrix resultMatrix=null;
			 int dim = 0;
			int count=0;
			while (oneLine != null) {
				String[] split = oneLine.trim().split("\t");
				if (dim==0) {
					dim=split.length;
					resultMatrix=new Matrix(dim, dim);
				}
				dim=split.length;
				
				for (int i = 0, len = split.length; i < len; i++) {
					resultMatrix.set(count, i, Double.valueOf(split[i]));
				}
				count++;
				oneLine = bufferedReader.readLine();
			}
			return resultMatrix;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	public Map<String,String> readMapInStringSplitFromFile(String fileName,String splitFlag) {
		Map<String,String> result = new HashMap<>();
		try {
			FileInputStream fis = new FileInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(isr);
			String oneLine =null;
			while ((oneLine =bufferedReader.readLine())!= null) {
				String[] split=oneLine.split(splitFlag);
				result.put(split[0].trim(), split[1].trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<String> readfromFileByStream(String path) {
		List<String> result = new ArrayList<String>();
		try (FileInputStream fis = new FileInputStream(path);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(isr);) {

			String temp = null;
			while ((temp  = bufferedReader.readLine())!= null) {
				result.add(temp);
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
				if (user1.equals(user2)) {
					continue;
				}
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

	/**
	 * @param userListFileName
	 * @param UserFeatureFileName
	 * @return
	 * @create_time：2014年8月18日下午4:14:14
	 * @modifie_time：2014年8月18日 下午4:14:14
	 */
	public Map<Long, User> readUser(List<Long> userIdList,
			String UserFeatureFileName) {

		Map<Long, User> userSet = new HashMap();

		List<T> featureList = this
				.readFileByLineSaveByLinkedList(UserFeatureFileName);

		for (int i = 0; i < userIdList.size(); i++) {
			User oneuUser = new User();
			oneuUser.setUserId(String.valueOf(userIdList.get(i)));
			oneuUser.setFeature((String) featureList.get(i));
			userSet.put( userIdList.get(i), oneuUser);
		}
		System.out.println("read the user and feature success");
		return userSet;
	}
}
