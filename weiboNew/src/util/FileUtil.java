/**
 * 
 */
package util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：FileUtil   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月27日 上午11:05:11   
 * @modifier：zhouge   
 * @modified_time：2014年9月27日 上午11:05:11   
 * @modified_note：   
 * @version    
 *    
 */
public class FileUtil {

	/**
	 * @param path
	 * @param DateFormat
	 * @return
	 *@create_time：2014年9月27日上午11:06:37
	 *@modifie_time：2014年9月27日 上午11:06:37
	  
	 */
	public List<String> getFileListSortByTimeASC(String path,String DateFormat) {
		File pathFile = new File(path);
		File[] Filelist = pathFile.listFiles();
		HashMap<String, Calendar> fileMap = new HashMap<>();
		List<String> result = new ArrayList<>();
		for (int j = 0; j < Filelist.length; j++) {
			File file = Filelist[j];
			if (file.isFile()) {
				String dayString = file.getName().substring(0,
						file.getName().indexOf("."));
				Date date = getDay(dayString,DateFormat);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				fileMap.put(file.getPath(), calendar);
			}
		}
		List<String> fileList = new ArrayList<>(fileMap.keySet());
		CalendarComparator calendarComparator = new CalendarComparator(fileMap);
		Collections.sort(fileList, calendarComparator);
		return fileList;
	}
	
	public List<String> getFileListSortByTimeASC(String path1,String path2,String DateFormat) {
		HashMap<String, Calendar> fileMap = new HashMap<>();
		for (int i = 0; i < 2; i++) {
			File pathFile;
			if (i==1) {
				pathFile = new File(path1);
			}else {
				 pathFile = new File(path2);
			}
			File[] Filelist = pathFile.listFiles();
		
			for (int j = 0; j < Filelist.length; j++) {
				File file = Filelist[j];
				if (file.isFile()) {
					String dayString = file.getName().substring(0,
							file.getName().indexOf("."));
					Date date = getDay(dayString,DateFormat);
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(date);
					fileMap.put(file.getPath(), calendar);
				}
			}
		}
		
		List<String> fileList = new ArrayList<>(fileMap.keySet());
		CalendarComparator calendarComparator = new CalendarComparator(fileMap);
		Collections.sort(fileList, calendarComparator);
		return fileList;
	}
	/**
	 * @param time
	 * @param DateFormat
	 * @return
	 *@create_time：2014年9月27日上午11:06:26
	 *@modifie_time：2014年9月27日 上午11:06:26
	  
	 */
	public static Date getDay(String time,String DateFormat ) {

		final SimpleDateFormat sf1 = new SimpleDateFormat(DateFormat,
				Locale.ENGLISH);
		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**   
	*    
	* @progject_name：weiboNew   
	* @class_name：CalendarComparator   
	* @class_describe：   
	* @creator：zhouge   
	* @create_time：2014年9月27日 上午11:06:32   
	* @modifier：zhouge   
	* @modified_time：2014年9月27日 上午11:06:32   
	* @modified_note：   
	* @version    
	*    
	*/
	private static class CalendarComparator implements Comparator<String> {
		HashMap<String, Calendar> baseHashMap = null;

		public CalendarComparator(HashMap<String, Calendar> baseHashMap) {
			this.baseHashMap = baseHashMap;
		}

		public int compare(String arg0, String arg1) {
			return this.baseHashMap.get(arg0).compareTo(
					this.baseHashMap.get(arg1));
		}

	}
}
