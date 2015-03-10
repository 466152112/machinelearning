/**
 * 
 */
package util;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：dsf   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年11月11日 下午3:15:27   
 * @modifier：zhouge   
 * @modified_time：2014年11月11日 下午3:15:27   
 * @modified_note：   
 * @version    
 *    
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LogBinYan {
	
	private static HashMap<Double, Double> sourceData = new HashMap<Double, Double>();
	private static HashMap<Double, Double> finalData = new HashMap<Double, Double>();
	private static ArrayList<Double> keys = new ArrayList<Double>(); 
	
	public static void LogBinning() {
		
		double max = 0;
		double min = 0;
		max = getMax();
		min = getMin();
		double interval = (max - min) / 30; 
		System.out.println(interval);
		double m = keys.get(0);
		System.out.println(m);
		double n = m + interval;
		for(int i = 0;i < 30;i++) {
			double sum = getNew(m,n);//
			double inter = Math.pow(10,n) - Math.pow(10,m);
			double newx = Math.pow(10, (interval/2 + m));
			double newy = sum/inter;
			finalData.put(newx, newy);
			m = n;
			n = m + interval;
		}
		
	}
	
	public static void getKeysAlone() {
		Iterator<Double> iterator = sourceData.keySet().iterator();
		while(iterator.hasNext()){
			Object key = iterator.next();
			keys.add(Double.parseDouble(key.toString()));
		}
	}
	
	public static double getMax() {
		Iterator<Double> iterator = sourceData.keySet().iterator();
		double max = 0.0;
		while(iterator.hasNext()){
		    Object o = iterator.next();
		    String key = o.toString();
		    if(max < Double.parseDouble(key)) {
		    	max = Double.parseDouble(key);
		    }
		    
		}
		
		return max;
	}
	
	public static double getMin() {
		Iterator<Double> iterator = sourceData.keySet().iterator();
		double min = 10000;
		while(iterator.hasNext()){
			Object o = iterator.next();
			String key = o.toString();
			if(min > Double.parseDouble(key)) {
				min = Double.parseDouble(key);
			}
		}
		return min;
	}
	
	public static double getNew(double m,double n) {//▒ж└Щ╦сие
		Iterator<Double> iterator = sourceData.keySet().iterator();
		double sum = 0.0;
		while(iterator.hasNext()) {
			Object o = iterator.next();
			String key = o.toString();
			if(Double.parseDouble(key) >= m && Double.parseDouble(key) < n){
				sum += sourceData.get(Double.parseDouble(key));
			}
				
		}
		
		return sum;
	}
	
	public static void writeDataToFile() {
		try{
		File file2 = new File("D:\\LogBindata.txt");
		FileWriter fw = new FileWriter(file2);
		BufferedWriter bw = new BufferedWriter(fw);
		Iterator<Double> iterator = finalData.keySet().iterator();
		while(iterator.hasNext()) {
			Object o = iterator.next();
			String key = o.toString();
			double[] keyvalue = new double[2];
			keyvalue[0] = Double.parseDouble(key);
			keyvalue[1] = finalData.get(keyvalue[0]);
			String myreadline = keyvalue[0] + "\t" + keyvalue[1];
			bw.write(myreadline); 
			bw.newLine();
		}
		 bw.flush(); 
         bw.close();
         fw.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
        
	}
	
	public static void readData() {
		try {
			File  file = new File("D:\\baoyu.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s =br.readLine();
			//System.out.println(s);
			while( s != null) {
				String [] str = s.split("\t");
				//System.out.println(str[1]);
				double d1 = Double.parseDouble(str[0]);
				double d2 = Math.log10(d1);
				
				//System.out.println(d2);
				sourceData.put(d2, Double.parseDouble(str[1]));
				s = br.readLine();
			}
			fr.close();
			br.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		
		readData();
		getKeysAlone();
		LogBinning();
		writeDataToFile();
	}

}

