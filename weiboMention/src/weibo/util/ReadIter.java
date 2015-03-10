/**
 * 
 */
package weibo.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tool.dataStucture.AvlTree;
import bean.OnePairTweet;
import bean.Retweetlist;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：ReadIter   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年12月22日 上午11:04:20   
 * @modifier：zhouge   
 * @modified_time：2014年12月22日 上午11:04:20   
 * @modified_note：   
 * @version    
 *    
 */
public interface ReadIter {
	
	public String readerOneLine();
	public void closeReader();
	public void resetReader();

	public OnePairTweet readOnePairTweet();

	public int getErrorCount();

}
