/**
 * 
 */
package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.ReadUtil;

/**   
 *    
 * 椤圭洰鍚嶇О锛歸eibo   
 * 绫诲悕绉帮細statisticsBysplit   
 * 绫绘弿杩帮細   
 * 鍒涘缓浜猴細zhouge   
 * 鍒涘缓鏃堕棿锛�014骞�鏈�3鏃�涓嬪崍1:54:20   
 * 淇敼浜猴細zhouge   
 * 淇敼鏃堕棿锛�014骞�鏈�3鏃�涓嬪崍1:54:20   
 * 淇敼澶囨敞锛�  
 * @version    
 *    
 */
public class statisticsBysplit {
	
	public static void main(String[] args){
		ReadUtil readUtil=new ReadUtil();
		List<String> weiboNumberArrayList=new ArrayList<>();
		try {
			String weiboNumber="J:/weibo/5k/20140312/缁熻/friend.txt";
			weiboNumberArrayList=readUtil.readFileByLine(weiboNumber);
		} catch (Exception e) {
			// TODO: handle exception
		}
		double[] result;
		final int extent=10;
		int totalWeiboNumber=0;
		int maxNumber=0; 
		for (String oneLine : weiboNumberArrayList) {
			String[] split=oneLine.split("\t");
			int Interval=Integer.parseInt(split[0]);
			int number=Integer.parseInt(split[1]);
			if (Interval>maxNumber) {
				maxNumber=Interval;
			}
			totalWeiboNumber+=number;
		}
		
		result=new double[(maxNumber/extent)+1];
		
		for (String oneLine : weiboNumberArrayList) {
			String[] split=oneLine.split("\t");
			int Interval=Integer.parseInt(split[0]);
			int number=Integer.parseInt(split[1]);
			result[Interval/extent]=result[Interval/extent]+number;
		}
		
		//璁＄畻姣斾緥
		for (int i = 0; i < result.length; i++) {
			result[i]=result[i]/totalWeiboNumber;
		}
//		
		String outPutFileString="J:/weibo/5k/20140312/缁熻/friendByNumber.dat";
		BufferedWriter bufferedWriter = null;
		final File file1 = new File(outPutFileString);
		try {

			bufferedWriter = new BufferedWriter(new FileWriter(file1, true));
			for (int i = 0; i < result.length; i++) {
				double d=result[i];
				double shuzi=i*extent+extent*0.5;
				String tempString=shuzi+"\t"+d;
				bufferedWriter.write(tempString);
				bufferedWriter.newLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + outPutFileString);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failure to close: " + outPutFileString);
			}
		
		}
		System.out.println();
	}
}
