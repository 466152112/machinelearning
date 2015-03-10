/**
 * 
 */
package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**   
 *    
 * @progject_name��weiboNew   
 * @class_name��CalanderUtil   
 * @class_describe��   
 * @creator��zhouge   
 * @create_time��2014��8��28�� ����11:55:45   
 * @modifier��zhouge   
 * @modified_time��2014��8��28�� ����11:55:45   
 * @modified_note��   
 * @version    
 *    
 */
public class CalanderUtil {
	final static SimpleDateFormat sf1 = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
	final static SimpleDateFormat sfEnglish = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	public static Date getDate(String time) {
		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static Date getDateEnglish(String time) {
		try {
			return sfEnglish.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getDate(String time,String SimpleDateFormat) {
		try {
			 SimpleDateFormat sf = new SimpleDateFormat(SimpleDateFormat, Locale.ENGLISH);
			 return sf.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static Date getDay(String time){
		
		final SimpleDateFormat sf1 = new SimpleDateFormat(
				"yyyy_MM_dd", Locale.ENGLISH);
			try {
				return sf1.parse(time.trim());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	
	
	 private static class CalendarComparator  implements Comparator<String>
	    {  
		 HashMap<String, Calendar> baseHashMap=null;
		 public CalendarComparator( HashMap<String, Calendar>  baseHashMap){
			this.baseHashMap=baseHashMap; 
		 }
 	        public int compare(String arg0,String arg1)  
	        {  
 	         return  this.baseHashMap.get(arg0).compareTo(this.baseHashMap.get(arg1));
	        }
			
	    }  
}
