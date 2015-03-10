/**
 * 
 */
package preprocess._0904;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.ReadUtil;
import util.TwoValueComparatorInString;
import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：StatisticsIndegree
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月5日 上午9:53:50
 * @modifier：zhouge
 * @modified_time：2014年9月5日 上午9:53:50
 * @modified_note：
 * @version
 * 
 */
public class StatisticsOutdegree {
	static String path = "J:/workspace/weiboNew/data/myself/2012/";
	static String sourceFileName = path + "weiboFlowSplitByProvince2012.txt";
	static String tagetFileName = path + "outInfluencetop3.txt";
	static String userNumberFileName=path+"各省用户数量.txt";
	public static void main(String[] args) {
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> sourceList = readUtil.readFileByLine(sourceFileName);
		Set<String> provinceSet = new HashSet<>();
		HashMap<String, Double> ProvinceRetweetNumber=new HashMap<>();
		for (String oneLine : sourceList) {
			if (oneLine.indexOf("Source")!=-1||oneLine.indexOf("其他")!=-1)
				continue;
			String[] split = oneLine.split("\t");
			String source = split[0];
			String target = split[1];
			provinceSet.add(source);
			provinceSet.add(target);
			if (source.equals(target)) {
				continue;
			}
			
			if(ProvinceRetweetNumber.containsKey(target)){
				ProvinceRetweetNumber.put(target, ProvinceRetweetNumber.get(target)+Double.valueOf(split[2]));
			}else {
				ProvinceRetweetNumber.put(target, Double.valueOf(split[2].trim()));
			}
		}
		List<String> userNumber = readUtil.readFileByLine(userNumberFileName);
		for (String oneLine : userNumber) {
			if (oneLine.indexOf("Source")!=-1||oneLine.indexOf("其他")!=-1)
				continue;
			String[] split = oneLine.split("\t");
			String source = split[0];
			//ProvinceRetweetNumber.put(province, ProvinceRetweetNumber.get(province)/Math.log(Integer.valueOf(split[1])));
		//	ProvinceRetweetNumber.put(source, Math.pow(ProvinceRetweetNumber.get(source), 0.5));
		}
		
		List<String> result=new ArrayList<>();
		result.add("Source,Target,Weight");
		for (String source : provinceSet) {
			HashMap<String, Double> map = new HashMap<>();
			for (String oneLine : sourceList) {
				if (oneLine.indexOf("Source")!=-1||oneLine.indexOf("其他")!=-1)
					continue;
				String[] split = oneLine.split("\t");
				String target = split[1];
				if (target.equals(source)) {
					continue;
				}
				
				if (split[0].equals(source)) {
					System.out.println(source+"	"+target);
					double number = Double.valueOf(split[2].trim());
					number=number/ProvinceRetweetNumber.get(target);
					map.put(target, number);
				}
			}
			List<String> sourceProvince=new ArrayList<>(map.keySet());
			TwoValueComparatorInString twc=new TwoValueComparatorInString(map);
			Collections.sort(sourceProvince, twc);
			List<String> subList=sourceProvince.subList(0, 3);
			for (String province : subList) {
				String temp=source+","+province+","+map.get(province);
				result.add(temp);
			}
		}
		
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(result, tagetFileName);

	}
}
