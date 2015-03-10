/**
 * 
 */
package Resource;

import Resource.superClass.superResource;


/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：data_Path   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年12月24日 下午1:45:38   
 * @modifier：zhouge   
 * @modified_time：2014年12月24日 下午1:45:38   
 * @modified_note：   
 * @version    
 *    
 */
public class data_Path extends superResource{
	/**
	 * @return the file IO path: supporting windows, linux and unix
	 */
	public static String getPath() {
		return work_path;
	}
	public data_Path(){
		super();
	}
}
