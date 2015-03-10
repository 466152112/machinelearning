/**
 * 
 */
package flow.wuliu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.AvlTree;
import bean.AvlTree.AvlNode;
import util.CalanderUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：Tongji   
 * @class_describe�?  
 * @creator：zhouge   
 * @create_time�?014�?�?9�?上午11:24:24   
 * @modifier：zhouge   
 * @modified_time�?014�?�?9�?上午11:24:24   
 * @modified_note�?  
 * @version    
 *    
 */
public class Hebingpurchasebro {

	/**
	 * 
	 * @create_time�?014�?�?8日下�?:24:51
	 * @modifie_time�?014�?�?8�?下午7:24:51
	 */
	 static String path =
	 "/home/zhouge/database/taobao/";
	//static String path = "C:/Users/zhouge/Desktop/zike/网站/";
	
	static String buyAction = path + "t_yf_purchase_detail_gl.txt";
	static String browseAction = path + "t_yf_browse_detail_gl.txt";
	static String resultFileName=path+"result.txt";
	List<String> result=new ArrayList<>();
	public static void main(String[] args) {
		Hebingpurchasebro aa = new Hebingpurchasebro();
		aa.Run();
	}

	public void Run() {
		AvlTree<UserIdAndItemId> usertAvlTree=new AvlTree<UserIdAndItemId>();
		final String buySimpleDateFormat="yyyy-mm-dd HH:mm:ss";
		//2011-10-22 16:37:37
		
		final String browseSimpleDateFormat="yyyymmddHHmmss";
		//20111006225357
		//购买数据
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(buyAction)));) {
			String oneLine ;
			int count=0;
			while((oneLine=bufferedReader.readLine())!=null){
				String[] split=oneLine.split("\t");
				UserIdAndItemId user=new UserIdAndItemId();
				user.userId=split[0];
				user.itemid=split[2];
				Date date=CalanderUtil.getDate(split[1], buySimpleDateFormat);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				//如果有则直接添加
				if (usertAvlTree.contains(user)) {
					usertAvlTree.getElement(user, usertAvlTree.root).action.put(calendar, 1);
				}else {
					//新建再插�?
					user.action.put(calendar, 1);
					usertAvlTree.insert(user);
				}
			
			}
			} catch (Exception e) {
			e.printStackTrace();
		}
		//浏览数据
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(browseAction)));) {
			String oneLine ;
			int count=0;
			while((oneLine=bufferedReader.readLine())!=null){
				String[] split=oneLine.split("\t");
				if (split.length!=3) {
					continue;
				}
				UserIdAndItemId user=new UserIdAndItemId();
				user.userId=split[0];
				user.itemid=split[2];
				Date date=CalanderUtil.getDate(split[1], browseSimpleDateFormat);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				//如果有则直接添加
				if (usertAvlTree.contains(user)) {
					usertAvlTree.getElement(user, usertAvlTree.root).action.put(calendar, 0);
				}else {
					//新建再插�?
					user.action.put(calendar, 0);
					usertAvlTree.insert(user);
				}
				
				count++;
				if (count>20302134) {
					break;
				}
			}
			} catch (Exception e) {
			e.printStackTrace();
		}
		
		writeToFile(usertAvlTree.root);
		
	}
	
	
	
	/**   
	*    
	* @progject_name：weiboNew   
	* @class_name：UserIdAndItemId   
	* @class_describe�?  
	* @creator：zhouge   
	* @create_time�?014�?�?3�?上午8:27:31   
	* @modifier：zhouge   
	* @modified_time�?014�?�?3�?上午8:27:31   
	* @modified_note�?  
	* @version    
	*    
	*/
	private class UserIdAndItemId implements Comparable<UserIdAndItemId>{
		String userId;
		String itemid;
		HashMap<Calendar, Integer> action=new HashMap<>();
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(UserIdAndItemId o) {
			// TODO Auto-generated method stub
			if (this.userId.compareTo(o.userId)>0) {
				return 1;
			}else if(this.userId.compareTo(o.userId)<0){
				 return -1;
			}
			else {
				if (o.itemid.compareTo(this.itemid)>0) {
					return 1;
				} else if(o.itemid.compareTo(this.itemid)<0){
					return -1;
				}
				return 0;
			}
		}
		
	}
	

	// 中序遍历avl�?
	/**
	 * @param t
	 *@create_time�?014�?�?3日上�?:27:38
	 *@modifie_time�?014�?�?3�?上午8:27:38
	  
	 */
	@SuppressWarnings("unchecked")
	private  void writeToFile(AvlNode<UserIdAndItemId> t) {
		if (t != null) {
			writeToFile(t.getLeft());
			UserIdAndItemId user = t.getElement();
			HashMap<Calendar, Integer> action=user.action;
			if (action.size()>1) {
				List arrayList = new ArrayList(action.entrySet());  
			      
			    Collections.sort(arrayList, new Comparator()   
			    {  
			      public int compare(Object o1, Object o2)   
			     {  
			        Map.Entry obj1 = (Map.Entry) o1;  
			        Map.Entry obj2 = (Map.Entry) o2;  
			        return (((Calendar) (obj1.getKey())).compareTo((Calendar)obj2.getKey()));  
			      }
	  
			    });  
			    for (Iterator iter = arrayList.iterator(); iter.hasNext();)   
			    {  
			       Map.Entry entry = (Map.Entry)iter.next();  
			       Calendar  key = (Calendar)entry.getKey();  
			       result.add(user.userId+"\t"+user.itemid+"\t"+action.get(key));
					if (result.size()>1000000) {
						WriteUtil<String> writeUtil=new WriteUtil<>();
						writeUtil.writeList(result, resultFileName);
						result=new ArrayList<>();
					}
			    }
			    result.add("\r");
			}
			
			writeToFile(t.getRight());
		}
	}
}
