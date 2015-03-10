/**
 * 
 */
package tool.drawplot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	/**
	 * @param value
	 * @param IntervalNumber bin的数量
	 * @return 将数据转换为logbin形式
	 *@create_time：2015年1月20日下午7:04:09
	 *@modifie_time：2015年1月20日 下午7:04:09
	  
	 */
	public HashMap<Double, Double> LogBinning(List<Double> value,int IntervalNumber){
		//对原始数据排序
		Collections.sort(value);
		//计算最小最大值的log值
		double minValue=Math.log10(value.get(0));
		double maxValue=Math.log10(value.get(value.size()-1));
		//统计每个值出现的次数
		HashMap<Double, Double> valueAndCount=new HashMap<>();
		for (Double onevalue : value) {
			if (valueAndCount.containsKey(onevalue)) {
				valueAndCount.put(onevalue, valueAndCount.get(onevalue)+1);
			}else {
				valueAndCount.put(onevalue, 1.0);
			}
		}
		HashMap<Double, Double> finalData=new HashMap<>();
		
		//计算划分的bin的间隔大小，记住是在log上面的间隔
		double interval=(maxValue-minValue)/IntervalNumber;
		double m = minValue;
		double n = m + interval;
		for(int i = 0;i < IntervalNumber;i++) {
			//统计出现在这个格子中的个数
			double binCount=getInterCount(m, n, valueAndCount);
			//统计出现在这个格子中的值的总和
			double sum = getSum(m,n,valueAndCount);//
			//恢复原始坐标
			double newx = Math.pow(10, (interval/2 + m));
			double newy = sum/binCount;
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
			
			double sum = getSum(m,n,valueAndCount);
			double inter = getInterCount(m,n,valueAndCount);
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
	
	public <K  extends Comparable<? super K>, V extends Comparable<? super V>> Map<Double, Double> LogBinning(Map<K, List<V>> keyAndList,int IntervalNumber){
		List<K> value=new ArrayList<>();
		for (K key : keyAndList.keySet()) {
			for (int i = 0; i < keyAndList.get(key).size(); i++) {
				value.add(key);
			}
		}
		Collections.sort(value);
		double minValue=Math.log10((Double) value.get(0));
		double maxValue=Math.log10((Double) value.get(value.size()-1));

		Map<Double, Double> finalData=new HashMap<>();
		
		double interval=(maxValue-minValue)/IntervalNumber;
		double m = minValue;
		double n = m + interval;
		for(int i = 0;i < IntervalNumber;i++) {
			double sum = getSum_List(m,n,keyAndList);
			double inter = getInterCount_List(m,n,keyAndList);
			double newx = Math.pow(10, (interval/2 + m));
			double newy = 0;
			if (inter!=0) {
				 newy = sum/inter;
				 finalData.put(newx, newy);
			}
			m = n;
			n = m + interval;
		}
		return finalData;
	}
	
	private  <K  extends Comparable<? super K>,  V extends Comparable<? super V>> double getSum_List(double m,double n,Map<K, List<V>> sourceData) {
		Iterator<K> iterator = sourceData.keySet().iterator();
		double sum = 0.0;
		while(iterator.hasNext()) {
			K key = iterator.next();
			if(Math.log10(Double.valueOf(String.valueOf(key)))>=m&&Math.log10(Double.valueOf(String.valueOf(key)))<n){
				for (V valueElement : sourceData.get(key)) {
					sum += Double.valueOf(String.valueOf(valueElement));
				}
			}
		}
		return sum;
	}
	private  <K  extends Comparable<? super K>, V extends Comparable<? super V>> double getSum(double m,double n,Map<K, V> sourceData) {
		
		Iterator<K> iterator = sourceData.keySet().iterator();
		double sum = 0.0;
		while(iterator.hasNext()) {
			K key = iterator.next();
			if(Math.log10(Double.valueOf(String.valueOf(key)))>=m&&Math.log10(Double.valueOf(String.valueOf(key)))<n){
				sum += Double.valueOf(String.valueOf(sourceData.get(key)));
			}
		}
		return sum;
	}
	
private <K  extends Comparable<? super K>, V extends Comparable<? super V>> double getInterCount_List(double m,double n,Map<K, List<V>> sourceData) {
		
		Iterator<K> iterator = sourceData.keySet().iterator();
		double sum = 0.0;
		while(iterator.hasNext()) {
			K  key = iterator.next();
			if(Math.log10(Double.valueOf(String.valueOf(key)))>=m&&Math.log10(Double.valueOf(String.valueOf(key)))<n){
				for (V valueElement : sourceData.get(key)) {
					sum ++;
				}
			}
		}
		return sum;
	}

private <K  extends Comparable<? super K>, V extends Comparable<? super V>> double getInterCount(double m,double n,Map<K, V> sourceData) {
		
		Iterator<K> iterator = sourceData.keySet().iterator();
		double sum = 0.0;
		while(iterator.hasNext()) {
			K  key = iterator.next();
			if(Math.log10(Double.valueOf(String.valueOf(key)))>=m&&Math.log10(Double.valueOf(String.valueOf(key)))<n){
				sum ++;
			}
		}
		return sum;
	}
}

