/**
 * 
 */
package util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import bean.OnePairTweet;

/**   
 *    
 * 椤圭洰鍚嶇О锛歭iuchuang   
 * 绫诲悕绉帮細ChineseSpliter   
 * 绫绘弿杩帮細   
 * 鍒涘缓浜猴細zhouge   
 * 鍒涘缓鏃堕棿锛�014骞�鏈�3鏃�涓嬪崍9:45:38   
 * 淇敼浜猴細zhouge   
 * 淇敼鏃堕棿锛�014骞�鏈�3鏃�涓嬪崍9:45:38   
 * 淇敼澶囨敞锛�  
 * @version    
 *    
 */
public class ChineseSpliter {
	final static Analyzer analyzer = new IKAnalyzer(true);
	private  OnePairTweet onePairTweet;
	private  String oneLine;
	
	public ChineseSpliter(OnePairTweet onePairTweet){
		this.onePairTweet=onePairTweet;
		oneLine=onePairTweet.getContent();
	}
	public ChineseSpliter(String oneLine){
		this.oneLine=oneLine;
	}
	public ChineseSpliter(){}
	/**
	 * @param sentence
	 * @return
	 *@create_time锛�014骞�鏈�鏃ヤ笂鍗�0:15:35
	 * @throws IOException 
	 *@modifie_time锛�014骞�鏈�鏃�涓婂崍10:15:35
	  
	 */
	public static ArrayList<String> spliterChinese(String sentence) throws IOException{
		ArrayList<String> result=new ArrayList<>();
		//System.out.println(sentence);
		//杩囨护璇彞
		Filter filter=new Filter(sentence);
		sentence=filter.getFilterResultString();
		//濡傛灉杩囨护鍚庤瘝涓虹┖锛屽垯鐩存帴杩斿洖绌�
		if (sentence.length()==0) {
			return result;
		}
		//浣跨敤Ikananlyzer 鍒嗚瘝
		StringReader sreader = new StringReader(sentence);
		TokenStream ts = analyzer.tokenStream("", sreader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		// 閿熸枻鎷烽敓鏂ゆ嫹鎵ч敓鏂ゆ嫹閿熸枻鎷�
		try {
			while (ts.incrementToken()) {
				String te = term.toString();
					result.add(te);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(result);
		sreader.close();
		return result;
	}

	/**
	 * @param sentence
	 * @return
	 *@create_time锛�014骞�鏈�鏃ヤ笂鍗�0:15:32
	 * @throws IOException 
	 *@modifie_time锛�014骞�鏈�鏃�涓婂崍10:15:32
	  
	 */
	public static String spliter(String sentence) {
		StringBuffer result=new StringBuffer();
		//System.out.println(sentence);
		//杩囨护璇彞
		Filter filter=new Filter(sentence);
		sentence=filter.getFilterResultString();
		//濡傛灉杩囨护鍚庤瘝涓虹┖锛屽垯鐩存帴杩斿洖绌�
		if (sentence.length()==0) {
			return result.toString();
		}
		//浣跨敤Ikananlyzer 鍒嗚瘝
		StringReader sreader = new StringReader(sentence);
		TokenStream ts = analyzer.tokenStream("", sreader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		// 閿熸枻鎷烽敓鏂ゆ嫹鎵ч敓鏂ゆ嫹閿熸枻鎷�
		try {
			while (ts.incrementToken()) {
				String te = term.toString();
					result.append(te+" ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(result);
		sreader.close();
		return result.toString().trim();
	}
	
}
