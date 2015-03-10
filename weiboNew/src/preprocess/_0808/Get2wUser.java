/**
 * 
 */
package preprocess._0808;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.User;
import util.ReadUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name��weiboNew   
 * @class_name��Get1wUser   
 * @class_describe��   
 * @creator��zhouge   
 * @create_time��2014��8��8�� ����2:56:50   
 * @modifier��zhouge   
 * @modified_time��2014��8��8�� ����2:56:50   
 * @modified_note��   
 * @version    
 *    
 */
public class Get2wUser {
	public static void main(String[] args){
		String path="J:/workspace/weiboNew/data/";
		String userListFileName=path+"userList.txt";
		String UserFeatureFileName=path+"userFeature.txt";
		String followGraphFileName=path+"20wFollowGraph.txt";
		Map<String,User> userSet =readUser(userListFileName, UserFeatureFileName);
		ArrayList<String> userList=readfollowGraph(followGraphFileName, userSet);
		
	}
	
	/**
	 * @param userListFileName
	 * @param UserFeatureFileName
	 * @return
	 *@create_time��2014��8��8������3:19:55
	 *@modifie_time��2014��8��8�� ����3:19:55
	  
	 */
	public static Map<String,User> readUser(String userListFileName,String UserFeatureFileName){
		
		Map<String, User> userSet=new HashMap<String, User>();
		ReadUtil readUtil=new ReadUtil();
		List<String> userIdList=readUtil.readFileByLine(userListFileName);
		List<String> featureList=readUtil.readFileByLine(UserFeatureFileName);
		for (int i = 0; i < userIdList.size(); i++) {
			User oneuUser=new User();
			oneuUser.setUserId(Long.valueOf(userIdList.get(i)));
			oneuUser.setFeature(featureList.get(i));
			userSet.put(userIdList.get(i),oneuUser);
		}
		return userSet;
	}
	/**
	 * @param followGraph
	 * @param userSet
	 * @return
	 *@create_time��2014��8��8������3:27:46
	 *@modifie_time��2014��8��8�� ����3:27:46
	  
	 */
	public static ArrayList<String> readfollowGraph(String followGraph,Map<String, User> userSet){
		Set<String> userList=new HashSet();
		
		try(BufferedReader bufferedReader=new BufferedReader(new FileReader(followGraph))) {
			String OneLine=bufferedReader.readLine();
			
			while(userList.size()<1000){
				String[] split=OneLine.split(",");
				String user1=split[0];
				String user2=split[1];
				if (userSet.containsKey(user1)&&userSet.get(user1).isFlag()&&userSet.containsKey(user2)&&userSet.get(user2).isFlag()) {
					if (!userList.contains(user1)) userList.add(user1);
					if(!userList.contains(user2)) userList.add(user2);
					
				}
				OneLine=bufferedReader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		WriteUtil<String> writeUtil=new WriteUtil<>();
		ArrayList<String> temp=new ArrayList<>();
		
		try(BufferedReader bufferedReader=new BufferedReader(new FileReader(followGraph))) {
			String OneLine;
			
			while((OneLine=bufferedReader.readLine())!=null){
				String[] split=OneLine.split(",");
				String user1=split[0].trim();
				String user2=split[1].trim();
				if (userList.contains(user1)&&userList.contains(user2)) {
					temp.add(OneLine);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		writeUtil.writeList(temp, "1kfollowgraph.txt");
		
		writeUtil.writeList(new ArrayList<>(userList), "1kuserList.txt");
		
		temp=new ArrayList<>();
		for (String userId : userList) {
			User oneuser=userSet.get(userId);
			StringBuffer feature=new StringBuffer();
			for (double value : oneuser.getFeature()) {
				feature.append(value+"\t");
			}
			
			temp.add(feature.toString().trim());
		}
		
		writeUtil.writeList(temp, "1kuserfeature.txt");
		return new ArrayList<>(userList);
	}
}



