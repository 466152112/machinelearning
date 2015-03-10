/**
 * 
 */
package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import util.ReadUtil;

/**
 * 
 * 椤圭洰鍚嶇О锛歸eibo 绫诲悕绉帮細SubGraph 绫绘弿杩帮細 鑾峰彇27涓敤鎴风殑2绾у瓙鍥�鍒涘缓浜猴細zhouge 鍒涘缓鏃堕棿锛�014骞�鏈�4鏃�
 * 涓嬪崍7:02:03 淇敼浜猴細zhouge 淇敼鏃堕棿锛�014骞�鏈�4鏃�涓嬪崍7:02:03 淇敼澶囨敞锛�
 * 
 * @version
 * 
 */
public class SubGraph {
	static String path = "J:/weibo/5k/20140312/link/";

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		SubGraph subGraph = new SubGraph();
		// read 27 user from txt file
		try {
			Scanner scanner = new Scanner(new File(subGraph.path
					+ "needUserId.txt"));
			ArrayList<Integer> needUserArrayList = new ArrayList<>();
			while (scanner.hasNext()) {
				String temp = scanner.next();
				needUserArrayList.add(Integer.parseInt(temp.trim()));
			}
			ReadUtil readUtil = new ReadUtil();
			ArrayList<ArrayList<Integer>> graph = readUtil
					.readArray2(subGraph.path + "FollwGraphResult1.txt");
			for (Integer user : needUserArrayList) {
				// get the 2nd step subgraph for user
				subGraph.getSubGraph(graph, user);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// get 2nd step subgraph for user
	public void getSubGraph(ArrayList<ArrayList<Integer>> graph, int user) {
		// 鐢ㄦ埛淇濆瓨浠ヤ竴涓敤鎴蜂负鏍稿績鐨勪簩閮ㄥ浘
		HashSet<String> link = new HashSet<>();
		int userNumber = graph.size();
		// 鍏堝悜鍓嶈蛋涓ゆ
		for (int i = 0; i < userNumber; i++) {

			ArrayList<Integer> temp = graph.get(user);
			// 璧颁竴姝�
			if (temp.get(i).equals(1)) {
				link.add(user + "\t" + i);
//				// 璧扮浜岄儴
//				ArrayList<Integer> temp11 = graph.get(i);
//				for (int j = 0; j < userNumber; j++) {
//					if (temp11.get(j).equals(1)) {
//						link.add(i + "\t" + j);
//					}
//				}
			}
		}

		// 鍚戝悗璧颁袱姝�
		for (int i = 0; i < userNumber; i++) {

			ArrayList<Integer> temp = graph.get(i);
			// 鍚戝悗璧颁竴姝�

			if (temp.get(user).equals(1)) {
				link.add(i + "\t" + user);
//				// 鍚戝悗璧扮浜岄儴
//				for (int j = 0; j < userNumber; j++) {
//					ArrayList<Integer> temp1 = graph.get(j);
//					if (temp1.get(i).equals(1)) {
//						link.add(j + "\t" + i);
//					}
//				}

			}
		}
		// 鍐欏叆鏂囦欢
		String outPutFileString = this.path +"onestep/"+ user + ".txt";
		BufferedWriter bufferedWriter = null;
		final File file1 = new File(outPutFileString);
		try {

			bufferedWriter = new BufferedWriter(new FileWriter(file1, true));
			Iterator<String> iterator = link.iterator();
			while (iterator.hasNext()) {
				String onePath = iterator.next();
				bufferedWriter.write(onePath);
				bufferedWriter.newLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + outPutFileString);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failure to close: " + outPutFileString);
			}

		}
		System.out.println();

	}
}
