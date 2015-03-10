/**
 * 
 */
package flow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：Snippet
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月18日 下午7:24:51
 * @modifier：zhouge
 * @modified_time：2014年9月18日 下午7:24:51
 * @modified_note：
 * @version
 * 
 */
public class MapTest {
	/**
	 * 
	 * @create_time：2014年9月18日下午7:24:51
	 * @modifie_time：2014年9月18日 下午7:24:51
	 */
	// static String path =
	// "/media/new3/dataset/data/taobao_competition_2013/f_trading_rec/";
	static String path = "F:/database/";
	static String sourceName = path + "f_trading_rec.csv";
	static String resultName = path + "result.txt";
	static String provinceinfo = path + "provinceinfo1.txt";
	
	static String chuange = path + "chuange.txt";
	static String cityListFile = path + "citylist.txt";
	static Set<String> city = new HashSet<>();

	public static void main(String[] args) {
		MapTest aa = new MapTest();
		aa.Run();
	}

	public void Run() {
		HashMap<String, Integer> pathAndNumber = new HashMap<>();
		Map<String, String> cityAnP = new HashMap<>();
		List<String> result = new ArrayList<>();
		WriteUtil<String> writeUtil = new WriteUtil<>();
		ReadUtil<String> readUtil = new ReadUtil<>();
		// try (BufferedReader bufferedReader = new BufferedReader(new
		// FileReader(
		// new File(sourceName)));) {
		// String oneLine = bufferedReader.readLine();
		// System.out.println(oneLine);
		// long count = 0;
		// while ((oneLine = bufferedReader.readLine()) != null) {
		// // 12列
		//
		// String[] split = oneLine.split(",");
		// // result.add(split[2].replace("\"", "") + ","
		// // + split[4].replace("\"", "") + ","
		// // + split[5].replace("\"", ""));
		// // if (result.size() > 10000123) {
		// // writeUtil.writeList(result, chuange);
		// // result = new ArrayList<String>();
		// // System.out.println(count++);
		// // if (count > 3) {
		// // break;
		// // } else {
		// // count++;
		// // }
		// // }
		// //
		// String dest_city = split[10].replace("\"", "").trim();
		// String sent_city = split[11].replace("\"", "").trim();
		// if (dest_city.equals("") ||
		// sent_city.equals("")||dest_city.equals("null")||sent_city.equals("null")){
		// System.out.println("null");
		// continue;
		// }
		// city.add(dest_city);
		// city.add(sent_city);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// writeUtil.writeList(new LinkedList<>(city), cityListFile);
		// city=readUtil.readSetFromFile(cityListFile);
		// // 获取省份
		// for (String aa : city) {
		// String p = MapUtil.getProvince(aa);
		// if (p == null) {
		// cityAnP.put(aa, aa);
		// } else {
		// System.out.println(p);
		// cityAnP.put(aa, p);
		// }
		// }
		cityAnP = readUtil.readMapInStringSplitFromFile(path
				+ "cityandProvince.txt", ",");
		HashMap<String, Integer> nodeInformationHashMap = new HashMap<>();
		// writeUtil.writeMapKeyAndValue(cityAnP, path+"cityandProvince.txt");
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(sourceName)));) {
			String oneLine = bufferedReader.readLine();
			oneLine = bufferedReader.readLine();
			long count = 0;
			while ((oneLine = bufferedReader.readLine()) != null) {
				// System.out.println(count++);
				// 12列
				String[] split = oneLine.split(",");
				String dest_city = split[10].replace("\"", "").trim();
				String sent_city = split[11].replace("\"", "").trim();
				if (dest_city.equals("") || sent_city.equals("")
						|| dest_city.equals("null") || sent_city.equals("null")) {
					System.out.println(oneLine);
					continue;
				}
				if (cityAnP.get(sent_city) == null) {
					System.out.println(oneLine + "\t" + sent_city);
					continue;
				} else if (cityAnP.get(dest_city) == null) {
					System.out.println(oneLine + "\t" + dest_city);
					continue;
				}

				String path = cityAnP.get(sent_city) + ","
						+ cityAnP.get(dest_city);
				if (pathAndNumber.containsKey(path)) {
					pathAndNumber.put(path, pathAndNumber.get(path) + 1);
				} else {
					pathAndNumber.put(path, 1);
				}
				
				if (nodeInformationHashMap.containsKey(cityAnP.get(sent_city))) {
					nodeInformationHashMap
							.put(cityAnP.get(sent_city), nodeInformationHashMap
									.get(cityAnP.get(sent_city)) + 1);
				} else {
					nodeInformationHashMap.put(cityAnP.get(sent_city), 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		writeUtil.writemapkeyAndValueInIntegerSplitInDot(nodeInformationHashMap,
				provinceinfo, false);
//		writeUtil.writemapkeyAndValueInIntegerSplitInDot(pathAndNumber,
//				resultName, false);

	}
}
