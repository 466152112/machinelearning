/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
public class Logbin {
	
	public HashMap<Double, Double> LogBinning(List<Double> value,int IntervalNumber){
		Collections.sort(value);
		double minValue=Math.log10(value.get(0));
		double maxValue=Math.log10(value.get(value.size()-1));
		HashMap<Double, Double> valueAndCount=new HashMap<>();
		for (Double onevalue : value) {
			if (valueAndCount.containsKey(onevalue)) {
				valueAndCount.put(onevalue, valueAndCount.get(onevalue)+1);
			}else {
				valueAndCount.put(onevalue, 1.0);
			}
		}
		HashMap<Double, Double> finalData=new HashMap<>();
		
		double interval=(maxValue-minValue)/IntervalNumber;
		double m = minValue;
		double n = m + interval;
		for(int i = 0;i < IntervalNumber;i++) {
			
			double sum = getNew(m,n,valueAndCount);//
			double inter = Math.pow(10,n) - Math.pow(10,m);
			double newx = Math.pow(10, (interval/2 + m));
			double newy = sum/inter;
			finalData.put(newx, newy);
			m = n;
			n = m + interval;
		}
		return finalData;
	}
	
	public HashMap<Double, Double> LogBinning(HashMap<Double, Double> valueAndCount,int IntervalNumber){
		List<Double> value=new ArrayList<>(valueAndCount.keySet());
		Collections.sort(value);
		double minValue=Math.log10(value.get(0));
		double maxValue=Math.log10(value.get(value.size()-1));

		HashMap<Double, Double> finalData=new HashMap<>();
		
		double interval=(maxValue-minValue)/IntervalNumber;
		double m = minValue;
		double n = m + interval;
		for(int i = 0;i < IntervalNumber;i++) {
			
			double sum = getNew(m,n,valueAndCount);
			double inter = getInterNumber(m,n,valueAndCount);
			double newx = Math.pow(10, (interval/2 + m));
			double newy = 0;
			if (inter!=0) {
				 newy = sum/inter;
			}
			finalData.put(newx, newy);
			m = n;
			n = m + interval;
		}
		return finalData;
	}
	
	private static double getNew(double m,double n,HashMap<Double, Double> sourceData) {
		
		Iterator<Double> iterator = sourceData.keySet().iterator();
		double sum = 0.0;
		while(iterator.hasNext()) {
			Double key = iterator.next();
			if(Math.log10(key)>=m&&Math.log10(key)<n){
				sum += sourceData.get(key);
				
			}
		}
		return sum;
	}
	
private static double getInterNumber(double m,double n,HashMap<Double, Double> sourceData) {
		
		Iterator<Double> iterator = sourceData.keySet().iterator();
		double sum = 0.0;
		while(iterator.hasNext()) {
			Double key = iterator.next();
			if(Math.log10(key)>=m&&Math.log10(key)<n){
				sum ++;
			}
		}
		return sum;
	}
}

