/**
 * 
 */
package weibo.util.tweetContent;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
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
public interface Spliter {
	final static Analyzer analyzer = new IKAnalyzer(true);
	
	public List<String> spliterResultInList(String sentence);
	public  String spliterResultInString(String sentence);
	
}
