/**
 * 
 */
package dataCovert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import util.WriteUtil;
/**   
 *    
 * @progject_name：MF   
 * @class_name：CiaoCovert   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年12月3日 下午5:51:50   
 * @modifier：zhouge   
 * @modified_time：2014年12月3日 下午5:51:50   
 * @modified_note：   
 * @version    
 *    
 */
public class CiaoCovert {
	private static String path = "H:/dataset/recommendation/Ciao/dataset/";

	public static void main(String[] args) {
		CiaoCovert ciaoCovert=new CiaoCovert();
		ciaoCovert.run();
	}

	public void run() {
		
		List<String> result = new ArrayList<String>();
		String temp=null;
		try (
				BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path+"rating.txt")));) {
			HashMap<String, Integer> itemNameAndId=new HashMap<>();
			//5247778::::Pyrex Oblong Roaster::::House & Garden::::40::::
			int itemCount=0;
			while ((temp= bufferedReader.readLine()) != null) {
				String[] split=temp.split("::::");
				long userid=Long.valueOf(split[0]);
				long rating;
				try {
					rating=Long.valueOf(split[3]);
				} catch (NumberFormatException e) {
					try {
						rating=Long.valueOf(split[4]);
						// TODO: handle exception
					} catch (Exception e2) {
						System.out.println(temp);
						continue;
					}
					
				}
				
				if (itemNameAndId.containsKey(split[1])) {
					result.add(userid+"\t"+itemNameAndId.get(split[1])+"\t"+rating);
				}else {
					itemNameAndId.put(split[1], itemCount);
					itemCount++;
					result.add(userid+"\t"+itemNameAndId.get(split[1])+"\t"+rating);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("read file failure");
			System.out.println(temp);
			e.printStackTrace();
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(result, path+"Ciao_rating.txt");
		}
}
