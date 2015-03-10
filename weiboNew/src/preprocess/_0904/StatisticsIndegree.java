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
public class StatisticsIndegree {
	static String path = "J:/workspace/weiboNew/data/myself/2012/";
	static String sourceFileName = path + "weiboFlowSplitByProvince2012.txt";
	static String tagetFileName = path + "inInfluencetop3.txt";
	static String userNumberFileName=path+"各省用户数量.txt";
	static String followFileName=path+"各省用户follow数量统计.txt";
	public static void main(String[] args) {
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> sourceList = readUtil.readFileByLine(sourceFileName);
		Set<String> provinceSet = new HashSet<>();
		HashMap<String, Double> ProvinceRetweetNumber=new HashMap<>();
		HashMap<String, Double> ProvincePeopleNumber=new HashMap<>();
		HashMap<String, HashMap<String, Double>> followMap=new HashMap<>();
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
			
			if(ProvinceRetweetNumber.containsKey(source)){
				ProvinceRetweetNumber.put(source, ProvinceRetweetNumber.get(source)+Double.valueOf(split[2]));
			}else {
				ProvinceRetweetNumber.put(source, Double.valueOf(split[2]));
			}
		}
		
		List<String> userNumber = readUtil.readFileByLine(userNumberFileName);
		for (String oneLine : userNumber) {
			if (oneLine.indexOf("Source")!=-1||oneLine.indexOf("其他")!=-1)
				continue;
			String[] split = oneLine.split("\t");
			String province = split[0];
			//这种方式有效果
			//ProvinceRetweetNumber.put(province, ProvinceRetweetNumber.get(province)/Math.log(Integer.valueOf(split[1])));
			//ProvinceRetweetNumber.put(province, Math.pow(ProvinceRetweetNumber.get(province), 0.5));
			ProvincePeopleNumber.put(province, Double.valueOf(split[1]));
		}
		
		List<String> userFollow = readUtil.readFileByLine(followFileName);
		for (String oneLine : userFollow) {
			if (oneLine.indexOf("Source")!=-1||oneLine.indexOf("其他")!=-1)
				continue;
			String[] split = oneLine.split("\t");
			String source = split[0];
			String target=split[1];
			double number=Double.valueOf(split[2]);
			if(followMap.containsKey(source)){
				followMap.get(source).put(target, number);
			}else {
				HashMap<String, Double> temp=new HashMap<>();
				temp.put(target, number);
				followMap.put(source, temp);
			}
		}
		
		List<String> result=new ArrayList<>();
		result.add("Source,Target,Weight");
		for (String target : provinceSet) {
			HashMap<String, Double> map = new HashMap<>();
			for (String oneLine : sourceList) {
				if (oneLine.indexOf("Source")!=-1||oneLine.indexOf("其他")!=-1)
					continue;
				String[] split = oneLine.split("\t");
				String source = split[0];
				if (target.equals(source)) {
					continue;
				}
				if (split[1].equals(target)) {
					double number = Double.valueOf(split[2].trim());
					number=number/ProvinceRetweetNumber.get(source);
					//number=number*10000/(Math.log(ProvincePeopleNumber.get(source))*Math.log(ProvincePeopleNumber.get(target)));
					//number=number/(followMap.get(target).get(source));
					map.put(source, number);
				}
			}
			List<String> sourceProvince=new ArrayList<>(map.keySet());
			TwoValueComparatorInString twc=new TwoValueComparatorInString(map);
			Collections.sort(sourceProvince, twc);
			List<String> subList=sourceProvince.subList(0, 3);
			for (String province : subList) {
				String temp=province+","+target+","+map.get(province);
				result.add(temp);
			}
		}
		
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(result, tagetFileName);

	}
}
