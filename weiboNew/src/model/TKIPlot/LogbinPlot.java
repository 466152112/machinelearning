/**
 * 
 */
package model.TKIPlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import util.Logbin;
import util.ReadUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：Logbinning   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年11月10日 下午9:54:01   
 * @modifier：zhouge   
 * @modified_time：2014年11月10日 下午9:54:01   
 * @modified_note：   
 * @version    
 *    
 */
public class LogbinPlot{
	public static void main(String[] args) {
		
		//String path="J:/workspacedata/twitter/data/content/";
		String path="J:/workspacedata/weiboNew/data/2w/";
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> list=readUtil.readFileByLine(path+"weiboCount.txt");
		HashMap<Double, Double> valueAndCount=new HashMap<>();
		double sum=0;
		for (String oneLine : list) {
			String[] split=oneLine.split("\t");
			sum+=Double.valueOf(split[1]);
		}
		
		for (String oneLine : list) {
			String[] split=oneLine.split("\t");
			valueAndCount.put(Double.valueOf(split[0]), Double.valueOf(split[1])/sum);
		}
		
		Logbin logbin=new Logbin();
		HashMap<Double, Double> finalResult =logbin.LogBinning(valueAndCount, 80);
		List<Double> keylist=new ArrayList<>(finalResult.keySet());
		Collections.sort(keylist);
		for (Double key: keylist) {
			System.out.println(key+"\t"+finalResult.get(key));
		}
	}	
	
}
