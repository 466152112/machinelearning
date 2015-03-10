/**
 * 
 */
package weibo.util.tweetContent;

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
 * 鍒涘缓鏃堕棿锛�014骞�鏈�3鏃�涓嬪�?:45:38   
 * 淇敼浜猴細zhouge   
 * 淇敼鏃堕棿锛�014骞�鏈�3鏃�涓嬪�?:45:38   
 * 淇敼澶囨敞锛�  
 * @version    
 *    
 */
public class ChineseSpliter implements Spliter{
	private  OnePairTweet onePairTweet;
	private  String oneLine;
	
	public ChineseSpliter(OnePairTweet onePairTweet){
		this.onePairTweet=onePairTweet;
		oneLine=onePairTweet.getContent();
	}
	public ChineseSpliter(String oneLine){
		this.oneLine=oneLine;
	}
	public ChineseSpliter(){
		
	}
	public  ArrayList<String> spliterResultInList(String sentence) {
		ArrayList<String> result=new ArrayList<>();
		//System.out.println(sentence);
		ChineseFilter chineseFilter=new ChineseFilter(sentence);
		sentence=chineseFilter.getFilterResultString();
		if (sentence.length()==0) {
			return result;
		}
		
		StringReader sreader = new StringReader(sentence);
		TokenStream ts = analyzer.tokenStream("", sreader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
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
	 * @return 划分好的字符串
	 *@create_time：2014年12月15日下午4:35:18
	 *@modifie_time：2014年12月15日 下午4:35:18
	  
	 */
	public  String spliterResultInString(String sentence) {
		StringBuffer result=new StringBuffer();
		//System.out.println(sentence);
		ChineseFilter chineseFilter=new ChineseFilter(sentence);
		sentence=chineseFilter.getFilterResultString();
		if (sentence.length()==0) {
			return result.toString();
		}
		StringReader sreader = new StringReader(sentence);
		TokenStream ts = analyzer.tokenStream("", sreader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
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
