/**
 * 
 */
package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**   
 *    
 * 项目名称：liuchuang   
 * 类名称：Filter   
 * 类描述：   对语句进行过滤
 * 创建人：zhouge   
 * 创建时间：2014年6月13日 下午9:45:11   
 * 修改人：zhouge   
 * 修改时间：2014年6月13日 下午9:45:11   
 * 修改备注：   
 * @version    
 *    
 */
public class Filter {
	//过滤结果
	private String filterResultString;
	// 匹配短链接
	private static Pattern pat = Pattern.compile("[http://t.cn/[a-zA-Z0-9]{6,7}]");
	/**
	 * @param sentence 需要过滤的语句
	 */
	public Filter(String sentence){
		
		//过滤短链接
		filterResultString=filterLink(sentence); 
		//过滤用户名
		filterResultString=filterUserName(filterResultString);
	}
	
	/**
	 * @param sentence
	 * @return 过滤短链接
	 * 创建时间：2014年6月14日上午10:44:36
	 * 修改时间：2014年6月14日 上午10:44:36
	  
	 */
	public static String filterLink(String sentence){
		// 模式匹配。把短链接删除
		Matcher mat = pat.matcher(sentence);
		sentence = mat.replaceAll(" ");
		return sentence;
	}
	
	/**
	 * @param sentence
	 * @return 过滤用户名
	 * 创建时间：2014年6月14日下午12:02:05
	 * 修改时间：2014年6月14日 下午12:02:05
	  
	 */
	public static String filterUserName(String sentence){
		sentence=sentence.trim();
		//处理如 //@上海广告媒体圈： 的情形
		while(true){
			//查找：含有//@标识符的
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
		
		//处理如 @reyalee 的情形
		while(true){
			//查找：含有//@标识符的
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
