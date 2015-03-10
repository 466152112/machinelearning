/**
 * 
 */
package Resource;

import Resource.superClass.superResource;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：FollowGraph   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月20日 下午2:42:08   
 * @modifier：zhouge   
 * @modified_time：2015年1月20日 下午2:42:08   
 * @modified_note：   
 * @version    
 *    
 */
public class FollowGraph_txt_Source  extends superResource{
	final static String path = work_path + "follow/";
	final static String followGraph_file = path + "followGraph.txt";
	/**
	 * @return the path
	 */
	public static String getPath() {
		return path;
	}
	/**
	 * @return the followgraphFile
	 */
	public static String getFollowgraphFile() {
		return followGraph_file;
	}
	
}
