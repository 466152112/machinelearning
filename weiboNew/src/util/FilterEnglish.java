/**
 * 
 */
package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FilterEnglish {
	//杩囨护缁撴灉
	/**
	 * 
	 */
	private String filterResultString;
	
	/**
	 * @param sentence 闇�杩囨护鐨勮鍙�
	 */
	public FilterEnglish(String sentence){
		
		//杩囨护鐭摼鎺�
		filterResultString=filterLink(sentence); 
		//杩囨护鐢ㄦ埛鍚�
		filterResultString=filterUserName(filterResultString);
	}
	
	/**
	 * @param sentence
	 * @return 杩囨护鐭摼鎺�
	 * 鍒涘缓鏃堕棿锛�014骞�鏈�4鏃ヤ笂鍗�0:44:36
	 * 淇敼鏃堕棿锛�014骞�鏈�4鏃�涓婂崍10:44:36
	  
	 */
	public static String filterLink(String sentence){
		int index=sentence.indexOf("http://");
		if(index!=-1){
			//检索空格
			int bankIndex=sentence.indexOf(" ", index);
			if(bankIndex!=-1){
				return sentence.substring(0, index)+" "+sentence.substring(bankIndex);
			}else {
				return sentence.substring(0, index);
			}
		}else {
			return sentence;
		}
	}
	
	/**
	 * @param sentence
	 * @return 杩囨护鐢ㄦ埛鍚�
	 * 鍒涘缓鏃堕棿锛�014骞�鏈�4鏃ヤ笅鍗�2:02:05
	 * 淇敼鏃堕棿锛�014骞�鏈�4鏃�涓嬪崍12:02:05
	  
	 */
	public static String filterUserName(String sentence){
		sentence=sentence.trim();
		//澶勭悊濡�//@涓婃捣骞垮憡濯掍綋鍦堬細 鐨勬儏褰�
		while(true){
			//鏌ユ壘锛氬惈鏈�/@鏍囪瘑绗︾殑
			int beginFlag=sentence.indexOf("//@");
			int endFlag=sentence.indexOf(':',beginFlag);
			
			if (beginFlag!=-1&&endFlag!=-1) {
				sentence=sentence.substring(0, beginFlag)+sentence.substring(endFlag);
				sentence=sentence.trim();
			}else if (beginFlag!=-1&&endFlag==-1) {
				sentence=sentence.substring(0, beginFlag);
				sentence=sentence.trim();
			}
			else {
				break;
			}
		}
		
		//澶勭悊濡�@reyalee 鐨勬儏褰�
		while(true){
			//鏌ユ壘锛氬惈鏈�/@鏍囪瘑绗︾殑
			int beginFlag=sentence.indexOf("@");
			int endFlag=sentence.indexOf(' ',beginFlag);
			
			if (beginFlag!=-1&&endFlag!=-1) {
				sentence=sentence.substring(0, beginFlag)+sentence.substring(endFlag);
				sentence=sentence.trim();
			}else if (beginFlag!=-1&&endFlag==-1) {
				sentence=sentence.substring(0, beginFlag);
				sentence=sentence.trim();
			}
			else {
				break;
			}
		}
		return sentence;
	}
	/**
	 * @return the filterResultString
	 */
	public String getFilterResultString() {
		return filterResultString;
	}
	
}
