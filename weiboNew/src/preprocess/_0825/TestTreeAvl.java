package preprocess._0825;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import bean.AvlTree;

/**
 * 
 */

/**
 * 
 * 项目名称：liuchuang 类名称：qq 类描述： 创建人：zhouge 创建时间：2014年5月2日 下午8:53:09 修改人：zhouge
 * 修改时间：2014年5月2日 下午8:53:09 修改备注：
 * 
 * @version
 * 
 */
public class TestTreeAvl {

	/**
	 * @param args
	 *            创建时间：2014年5月2日下午8:53:10 修改时间：2014年5月2日 下午8:53:10
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// 先读取qqid和对应的number
		 String path="J:/workspace/weiboNew/data/";
		// 构造平衡二叉树
		AvlTree<Long> avlTree = new AvlTree<>();
		Scanner scanner = new Scanner(new File(path + "text.txt"));
		while(scanner.hasNext()){
			long userId=scanner.nextLong();
			avlTree.insert(userId);
			System.out.println(userId);
		}
		System.out.println(avlTree.contains(2096651957L));
		System.out.println(avlTree.contains(2091129660L));
		System.out.println(avlTree.contains(1739236662L));
		
	}

}
