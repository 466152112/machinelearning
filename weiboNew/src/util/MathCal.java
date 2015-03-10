/**
 * 
 */
package util;

import java.util.List;
import java.util.Map;

/**   
*    
* @progject_name锛欱PR   
* @class_name锛歁athCal   
* @class_describe锛�  
* @creator锛歾houge   
* @create_time锛�014骞�鏈�3鏃�涓婂崍9:30:21   
* @modifier锛歾houge   
* @modified_time锛�014骞�鏈�3鏃�涓婂崍9:30:21   
* @modified_note锛�  
* @version    
*    
*/
public class MathCal {
	

	/**
	 * @param x
	 * @return
	 *@create_time锛�014骞�鏈�3鏃ヤ笂鍗�:30:28
	 *@modifie_time锛�014骞�鏈�3鏃�涓婂崍9:30:28
	  
	 */
	public static double sigmoid(double x){
		double result=(1.0/(1+Math.exp(-x)));
		return result;
		//double temp=Math.exp(x);
		//return temp/(1+temp);
	}
	

	/**
	 * @param flag
	 * @return
	 *@create_time锛�014骞�鏈�3鏃ヤ笂鍗�:30:34
	 *@modifie_time锛�014骞�鏈�3鏃�涓婂崍9:30:34
	  
	 */
	public static int indicator(boolean flag){
		return flag==true?1:0;
	}
	
	/**
	 * @param vectorP
	 * @param vectorQ
	 * @return
	 *@create_time：2014年11月3日下午6:01:55
	 *@modifie_time：2014年11月3日 下午6:01:55
	  
	 */
	public static double KLDistance(double[] vectorP,double[] vectorQ){
		
		int length=vectorP.length;
		if(vectorP.length!=vectorQ.length){
			System.out.println("error in the KLDistance function ,where the input vectors' length difference");
			System.exit(0);
		}
		double qsum=0;
		for (int i = 0; i < vectorQ.length; i++) {
			qsum+=vectorQ[i];
		}
		if (qsum>1.00001) {
			System.out.println("error in the KLDistance function ,where the vectorQ sum vectors must small 1");
			System.exit(0);
		}
		double psum=0;
		for (int i = 0; i < vectorQ.length; i++) {
			psum+=vectorP[i];
		}
		if (psum>1.00001) {
			System.out.println("error in the KLDistance function ,where the vectorP sum vectors must small 1");
			System.exit(0);
		}
		double result=0;
		for (int i = 0; i <length; i++) {
			if(vectorP[i]>1||vectorP[i]<0){
				System.out.println("error in the KLDistance function ,where the input must be [0,1]");
				System.exit(0);
			}
			if(vectorP[i]>0.001){
				result+=vectorP[i]*Math.log(vectorP[i]);
			}
		}
		for (int i = 0; i <length; i++) {
			if(vectorQ[i]>1||vectorQ[i]<0){
				System.out.println("error in the KLDistance function ,where the input must be [0,1]");
				System.exit(0);
			}
			if(vectorQ[i]>0.001){
				result-=vectorP[i]*Math.log(vectorQ[i]);
			}
		}
		if (result<0) {
			return 0.001;
//			System.out.println("psum:"+psum+" qsum"+qsum);
//			System.out.println("error in the KLDistance function ,where the output must be [0,]");
//			System.exit(0);
		}
		return result;
	}
	public static double cossim(double[] vector1,double[] vector2){
		double denominator1=0,denominator2=0, molecule=0;;
		int length=vector1.length;
		if(vector1.length!=vector2.length){
			System.out.println("error in the cossim function ,where the input vectors' length difference");
			System.exit(0);
		}
		for (int i = 0; i < length; i++) {
			denominator1+=Math.pow(vector1[i], 2);
		}
		denominator1=Math.sqrt(denominator1);
		
		for (int i = 0; i < length; i++) {
			denominator2+=Math.pow(vector2[i], 2);
		}
		denominator2=Math.sqrt(denominator2);
		
		if(denominator1==0||denominator2==0){
			System.out.println("error in the cossim function , where the denominator1 is zero");
			System.exit(0);
		}
		
		for (int i = 0; i < length; i++) {
			molecule+=vector1[i]*vector2[i];
		}
		return molecule/(denominator1*denominator2);
	}
	public static double getAverage(List<? extends Number> list) {

		double sum = 0;

		int len = list.size();
		if (list.get(0) instanceof Integer) {
			for (int i = 0; i < len; i++) {
				sum += (Integer) list.get(i);
			}
		} else {
			for (int i = 0; i < len; i++) {
				sum += (Double) list.get(i);
			}
		}
		return sum / len;
	}
	
	public static double getAverage(Map<Long, Double> list) {

		double sum = 0;
		int len = list.keySet().size();
		for (Object key : list.keySet()) {
			sum += (Double) list.get(key);
		}
		return sum / len;
	}
	
	public static double getSum(List<? extends Number> list) {

		double sum = 0;

		int len = list.size();
		if (list.get(0) instanceof Integer) {
			for (int i = 0; i < len; i++) {
				sum += (Integer) list.get(i);
			}
		} else {
			for (int i = 0; i < len; i++) {
				sum += (Double) list.get(i);
			}
		}
		return sum ;
	}

	public static double getFangCha(List<? extends Number> num) {

		double count = num.size();
		int len = num.size();
		double ff = 0.0;
		double avg = getAverage(num);

		if (num.get(0) instanceof Integer) {
			for (int i = 0; i < len; i++) {
				ff += ((Integer) num.get(i) - avg)
						* ((Integer) num.get(i) - avg);
			}
		} else {
			for (int i = 0; i < len; i++) {
				ff += ((Double) num.get(i) - avg) * ((Double) num.get(i) - avg);
			}
		}

		ff = ff / count;
		return ff;
	}

	public static int getTenBinIndex(int number) {
		int index = Integer.MAX_VALUE;
		if (number > 100000000) {
			System.out.println("the number is too big ");
		}
		if (number <=10) {
			return number;
		}

		for (int i = 8; i > 0; i--) {
			int zhengshu = (int) (number / Math.pow(10, i));
			if (zhengshu > 0) {
				index = i * 10;
				index += zhengshu;
				return index - 1;
			}
		}
		return -1;
	}
	
	public static int RecoverTenBinIndex(int index) {
		if(index<=10){
			return index;
		}
		int kk=index/10;
		int result=(int) Math.pow(10, kk);
		kk=index%10;
		result+=result*kk;
		return result;
	}

	public static double getBZFangCha(List<? extends Number> num) {
		return Math.sqrt(getFangCha(num));
	}
}
