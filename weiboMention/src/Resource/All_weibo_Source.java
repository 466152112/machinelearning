/**
 * 
 */
package Resource;

import java.util.List;

import tool.FileTool.FileUtil;
import Resource.superClass.superResource;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：All_weibo_Source   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年12月24日 下午2:01:06   
 * @modifier：zhouge   
 * @modified_time：2014年12月24日 下午2:01:06   
 * @modified_note：   
 * @version    
 *    
 */
public class All_weibo_Source extends superResource{
	
	public All_weibo_Source(){
		super();
	}
	public static String get_all_weibo_Path() {
		return sourceWeibo_path;
	}
	
	public static List<String> get_all_weibo_File_by_Sorted() {
		 FileUtil fileUtil = new FileUtil();
		String dateFormat = "yyyy_MM_dd";
		return fileUtil.getFileListSortByTimeASC(sourceWeibo_path,dateFormat);
	}
}
