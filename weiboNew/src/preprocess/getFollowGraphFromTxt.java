/**
 * 
 */
package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import bean.AvlTree;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：getUserFromTxt   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月13日 上午10:14:30   
 * @modifier：zhouge   
 * @modified_time：2014年10月13日 上午10:14:30   
 * @modified_note：   
 * @version    
 *    
 */
public class getFollowGraphFromTxt {

	public static void main(String[] dk) {
		String path = "/media/pc/new2/data/new/followGraph/";
		//String path="J:/workspace/weiboNew/data/myself/";
		//J:\workspace\weiboNew\data\myself
		String userIdFile="/media/pc/new2/data/new/userid/useridweiboNumber.txt";
		String followFile=path+"socialGraph.txt";
		String result=path+"followGraph.txt";
		getFollowGraphFromTxt gFollowGraphFromTxt=new getFollowGraphFromTxt();
		AvlTree<Long> avlTree =gFollowGraphFromTxt.getuserid(userIdFile);
		//AvlTree<Long> avlTree =new AvlTree<Long>();
		gFollowGraphFromTxt.run(result, followFile, avlTree);
	}

	public AvlTree<Long> getuserid(String userIdFile){
		AvlTree<Long> result=new AvlTree<Long>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				userIdFile));) {
			String onelineString="";
			while((onelineString=bufferedReader.readLine())!=null){
				String[] split=onelineString.split("\t");
				result.insert(Long.valueOf(split[0]));
			}
			return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 	result;
	}
	
	public void run(String resultfile,String resourcefile,	AvlTree<Long> avlTree ){
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(
				resourcefile));
				BufferedWriter Writer= new BufferedWriter(new FileWriter(new File(resultfile), true));
				) {
			String tempLine;
			long positive=0L;
			StringBuffer result=new StringBuffer();
			while ((tempLine = bufferedReader.readLine()) != null) {
				String[] split = tempLine.split(",");
				if (split.length>2) {
					continue;
				}
				try {
					long follower=Long.valueOf(split[0]);
					
					long followee=Long.valueOf(split[1]);
					if (followee==follower) {
						continue;
					}
					if (avlTree.contains(follower)&&avlTree.contains(followee)) {
						if (follower!=positive) {
							positive=follower;
							if (!result.toString().trim().equals("")) {
								Writer.write(result.toString().trim());
								Writer.newLine();
								Writer.flush();
							}
							result=new StringBuffer();
							result.append(follower+"\t");
						}
							result.append(followee+"\t");
						
					}
				} catch (NumberFormatException e) {
					
				}
				
			}
			Writer.write(result.toString().trim());
			Writer.flush();
			Writer.close();
//			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
