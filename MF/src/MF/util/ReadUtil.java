/**
 * 
 */
package MF.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


/**   
*    
* @progject_name：BPR   
* @class_name：ReadUtil   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年7月22日 下午7:57:22   
* @modifier：zhouge   
* @modified_time：2014年7月22日 下午7:57:22   
* @modified_note：   
* @version    
*    
*/
public class ReadUtil {
	
	/**
	 * @param fileName
	 * @return
	 *@create_time：2014年7月22日下午7:57:19
	 *@modifie_time：2014年7月22日 下午7:57:19
	  
	 */
	public ArrayList<String> readFileByLine(String fileName){
		ArrayList<String> resultArrayList=new ArrayList<>();
		try {
			BufferedReader bufferedReader=new BufferedReader(new FileReader(new File(fileName)));
			String oneLine=bufferedReader.readLine();
			while(oneLine!=null){
				resultArrayList.add(oneLine);
				oneLine=bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultArrayList;
	}
}
