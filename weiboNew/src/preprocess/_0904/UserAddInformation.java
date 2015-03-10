/**
 * 
 */
package preprocess._0904;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：UserAddInformation   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月4日 上午7:45:06   
 * @modifier：zhouge   
 * @modified_time：2014年9月4日 上午7:45:06   
 * @modified_note：   
 * @version    
 *    
 */
/**
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import util.WriteUtil;
import bean.AvlTree;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：getUserFromDatabase
 * @class_describe�?
 * @creator：zhouge
 * @create_time�?014�?�?4�?下午10:17:19
 * @modifier：zhouge
 * @modified_time�?014�?�?4�?下午10:17:19
 * @modified_note�?
 * @version
 * 
 */
public class UserAddInformation {

	final SimpleDateFormat sf1 = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

	public static void main(String[] dk) {

		//String path = "/media/new2/data/";
		String path="J:/workspace/weiboNew/data/myself/";
		
		String userInformation=path+"useridandcity.csv";
		String userIdFile=path+"userId.txt";
		String result=path+"userIdAndCity1kw.txt";
		String cityResult=path+"sexAndUserNumber";
		UserAddInformation compareTime=new UserAddInformation();
		AvlTree<Long> avlTree = new AvlTree<>();
		String tempLine=null;
		HashMap<String, Integer> cityAndUserNumber=new HashMap<>();
		try(BufferedReader Reader1 = new BufferedReader(
				new FileReader(userIdFile));
				BufferedReader Reader2 = new BufferedReader(
						new FileReader(userInformation));
				BufferedWriter writer1=new BufferedWriter(new FileWriter(result));
				) {
			
			while((tempLine=Reader1.readLine())!=null){
				long userId=Long.valueOf(tempLine);
				avlTree.insert(userId);
			}
			while((tempLine=Reader2.readLine())!=null){
				String[] splite=tempLine.split(",");
				long userId=Long.valueOf(splite[0]);
				if(avlTree.contains(userId)){
					
					String[] shengs=splite[2].trim().split(" ");
					String sheng=shengs[0];
					if (sheng.isEmpty()||sheng.equals("漳州市")||sheng.equals(" ")) {
						continue;
					}
					if(cityAndUserNumber.containsKey(splite[3])){
						cityAndUserNumber.put(splite[3], cityAndUserNumber.get(splite[3])+1);
					}else {
						cityAndUserNumber.put(splite[3], 1);
					}
					writer1.write(splite[0]+","+splite[1]+","+sheng+","+splite[3]);
					writer1.newLine();
				}
			}
			writer1.flush();
			WriteUtil<String> writeUtil=new WriteUtil<>();
			writeUtil.writemapkeyAndValueInInteger(cityAndUserNumber, cityResult);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}

	
}
