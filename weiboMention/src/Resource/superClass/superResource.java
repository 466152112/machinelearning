/**
 * 
 */
package Resource.superClass;

import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import tool.io.FileIO;
import tool.system.Systems.OS;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：base
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月14日 上午10:17:17
 * @modifier：zhouge
 * @modified_time：2015年1月14日 上午10:17:17
 * @modified_note：
 * @version
 * 
 */
public class superResource {
	protected static String work_path=tool.system.Systems.getOs()==OS.Windows?"J:/workspacedata/weiboMention/":"/home/zhouge/database/weibo/mention/";
	protected static String sourceWeibo_path=tool.system.Systems.getOs()==OS.Windows?"J:/workspacedata/weiboMention/lucene/temp/docs/":"/home/zhouge/database/weibo/2012/";
	protected ReadUtil<String> tempReadUtil = new ReadUtil();

	public superResource() {
	}
}
