/**
 * 
 */
package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**   
 *    
 * @progject_name：twitter   
 * @class_name：FileUtil   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月16日 上午8:56:14   
 * @modifier：zhouge   
 * @modified_time：2014年9月16日 上午8:56:14   
 * @modified_note：   
 * @version    
 *    
 */
public class FileUtil {
	
	public static  List<String> getsubFile(String path) {
		File pathFile = new File(path);
		File[] Filelist = pathFile.listFiles();
		List<String> result = new ArrayList<>();
		for (int j = 0; j < Filelist.length; j++) {
			File file = Filelist[j];
			if (file.isFile()) {
				result.add(file.getPath());
			}
		}
		return result;
	}

}
