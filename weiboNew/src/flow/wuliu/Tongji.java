/**
 * 
 */
package flow.wuliu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：Tongji   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月19日 上午11:24:24   
 * @modifier：zhouge   
 * @modified_time：2014年9月19日 上午11:24:24   
 * @modified_note：   
 * @version    
 *    
 */
public class Tongji {

	/**
	 * 
	 * @create_time：2014年9月18日下午7:24:51
	 * @modifie_time：2014年9月18日 下午7:24:51
	 */
	// static String path =
	// "/media/new3/dataset/data/taobao_competition_2013/f_trading_rec/";
	static String path = "C:/Users/zhouge/Desktop/zike/网站/customer-product/";
	static String chuange = path + "customer-product.csv";

	public static void main(String[] args) {
		Tongji aa = new Tongji();
		aa.Run();
	}

	public void Run() {
		HashMap<String, Integer> userIdMap = new HashMap<>();
		HashMap<String, Integer> itemIdMap = new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(chuange)));) {
			String oneLine = bufferedReader.readLine();
			System.out.println(oneLine);
			long count = 0;
			while ((oneLine = bufferedReader.readLine()) != null) {
				if (oneLine.equals("")) {
					count+=1;
					System.out.println(count);
					continue;
				}
				// 12列
				String[] split = oneLine.split(",");
				//int number=Integer.valueOf(split[2]);
				if(userIdMap.containsKey(split[0])){
					userIdMap.put(split[0], userIdMap.get(split[0])+1);
				}else {
					userIdMap.put(split[0], 1);
				}
				
				if(itemIdMap.containsKey(split[1])){
					itemIdMap.put(split[1], itemIdMap.get(split[1])+1);
				}else {
					itemIdMap.put(split[1], 1);
				}
			//	count+=1;
				//System.out.println(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeMapKeyAndValuesplitbyt(userIdMap, path+"user-distribution.txt");
		writeUtil.writeMapKeyAndValuesplitbyt(itemIdMap, path+"item-distribution.txt");
		System.out.println("the user numner isL:" +userIdMap.size());
		
		System.out.println();
		System.out.println("the item number isL:" +itemIdMap.size());
		
	}
}
