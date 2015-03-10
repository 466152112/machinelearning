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
public class StatisticsOutFollow {
	static String path = "J:/workspace/weiboNew/data/myself/";
	static String sourceFileName = path + "各省用户follow数量统计.txt";
	static String tagetFileName = path + "followOuttop3.txt";

	public static void main(String[] args) {
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> sourceList = readUtil.readFileByLine(sourceFileName);
		Set<String> provinceSet = new HashSet<>();
		for (String oneLine : sourceList) {
			if (oneLine.indexOf("Source")!=-1)
				continue;
			String[] split = oneLine.split("\t");
			String source = split[0];
			String target = split[1];
			provinceSet.add(source);
			provinceSet.add(target);
		}
		List<String> result=new ArrayList<>();
		for (String source : provinceSet) {
			HashMap<String, Double> map = new HashMap<>();
			double sum=0;
			for (String oneLine : sourceList) {
				if (oneLine.indexOf("Source")!=-1)
					continue;
				String[] split = oneLine.split("\t");
				String target = split[1];
				if (target.equals(source)) {
					continue;
				}
				if (split[0].equals(source)) {
					double number = Double.valueOf(split[2].trim());
					map.put(target, number);
					sum+=number;
				}
			}
			List<String> sourceProvince=new ArrayList<>(map.keySet());
			TwoValueComparatorInString twc=new TwoValueComparatorInString(map);
			Collections.sort(sourceProvince, twc);
			List<String> subList=sourceProvince.subList(0, 3);
			for (String province : subList) {
				String temp=source+","+province+","+map.get(province)+","+map.get(province)/sum;
				result.add(temp);
			}
		}
		
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(result, tagetFileName);

	}
}
