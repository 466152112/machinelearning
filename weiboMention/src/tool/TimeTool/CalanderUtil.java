/**
 * 
 */
package tool.TimeTool;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import bean.OnePairTweet;

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

	public  Date getDate(String time) {
		SimpleDateFormat sf1 = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
	}

	public static Calendar getdateFromTimestamp(Long time) {
		Calendar cal = new GregorianCalendar(Locale.ENGLISH);
		Timestamp timestamp2=new Timestamp(time);
		Date date = timestamp2;
		cal.setTime(date);
		return cal;
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

	public static Date getDate(String time, String SimpleDateFormat) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat(SimpleDateFormat,
					Locale.ENGLISH);
			return sf.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Calendar getCalander(String time, String SimpleDateFormat) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat(SimpleDateFormat,
					Locale.ENGLISH);
			Date date = sf.parse(time.trim());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date getDay(String time) {

		final SimpleDateFormat sf1 = new SimpleDateFormat("yyyy_MM_dd",
				Locale.ENGLISH);
		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static class CalendarComparator implements Comparator<String> {
		HashMap<String, Calendar> baseHashMap = null;

		public CalendarComparator(HashMap<String, Calendar> baseHashMap) {
			this.baseHashMap = baseHashMap;
		}

		public int compare(String arg0, String arg1) {
			return this.baseHashMap.get(arg0).compareTo(
					this.baseHashMap.get(arg1));
		}
	}
	
	public static int getTimeDifferInHour(Calendar base,Calendar c2){
		long differ=c2.getTimeInMillis()-base.getTimeInMillis();
		differ/=(60*60*1000);
		return (int)differ;
	}
	public static int getTimeDifferInSeconds(Calendar base,Calendar c2){
		long differ=c2.getTimeInMillis()-base.getTimeInMillis();
		differ/=1000;
		return (int)differ;
	}
}
