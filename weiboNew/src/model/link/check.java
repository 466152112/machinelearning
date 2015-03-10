/**
 * 
 */
package model.link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ReadUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：check   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月9日 下午8:40:30   
 * @modifier：zhouge   
 * @modified_time：2014年10月9日 下午8:40:30   
 * @modified_note：   
 * @version    
 *    
 */
public class check {
	static String path = "J:/workspace/weiboNew/data/2w/";
	public static void main(String[] args)  {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		check ccn = new check();
			// String followGraphFile = path + "trainlist.txt";
			String zhouge = path + "followgraph.txt";
			
			try(BufferedReader Reader1 = new BufferedReader(new FileReader(
					new File(zhouge)));
					) {
				
				String oneLine1 = Reader1.readLine();
				int count=0;
				while ((oneLine1 != null)) {
						System.out.println(count);
						String[] split1=oneLine1.split(",");
							if (split1[1].equals(split1[0])) {
								System.out.println();
							}
							oneLine1 = Reader1.readLine();
					}
					
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
