/**
 * 
 */
package preprocess._0904;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.AvlTree;
import bean.AvlTree.AvlNode;
import bean.CompareableUser;
import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：StatisticsUserFollow   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月5日 下午6:02:18   
 * @modifier：zhouge   
 * @modified_time：2014年9月5日 下午6:02:18   
 * @modified_note：   
 * @version    
 *    
 */
public class StatisticsUserFollow {
	static String path = "/home/zhouge/database/weibo/2012/";
	// static String path = "J:/workspace/weiboNew/data/myself/";
	// String target="";
	static String superNumberSplitByProvince=path+"superNumberSplitByProvince.txt";
	static String followSplitByProvince=path+"followSplitByProvince.txt";
	static String userFollow=path+"socialGraph.txt";
	static String userInformation = path + "userIdAndCity1kw.txt";
	static List<String> writeResult = new ArrayList<>();
	static WriteUtil<String> writeUtil = new WriteUtil<>();
	static Map<String, Integer> ProvinceFollowMap=new HashMap<String, Integer>();
	static Map<String, Integer> superNumberMap=new HashMap<>();
	static ArrayList<Integer> followList=new ArrayList<>();
	static double mediaFollow=0;
	static AvlTree<CompareableUser> userTree;
	public static void main(String[] args){
		StatisticsUserFollow statisticsUserFollow=new StatisticsUserFollow();
		 userTree = statisticsUserFollow
				.getUserAvl(userInformation);
		statisticsUserFollow.followGraphStatic();
		statisticsUserFollow.getFollowNumber(userTree.root);
		Collections.sort(followList);
		//计算中位数
		if (followList.size()%2==1) {
			mediaFollow=followList.get(followList.size()/2);
		}
		else {
			mediaFollow=(followList.get(followList.size()/2-1)+followList.get(followList.size()/2))*1.0/2;
		}
		System.out.println("end mediaFollow"+mediaFollow);
		statisticsUserFollow.getSuperNumber(userTree.root);
		System.out.println("end getSuperNumber");
//		writeUtil.writemapkeyAndValueInInteger(ProvinceFollowMap, followSplitByProvince);
		writeUtil.writemapkeyAndValueInInteger(superNumberMap, superNumberSplitByProvince);
		System.out.println("success");
	}
	
	public  void followGraphStatic(){
		long count=0;
		try(BufferedReader reader=new BufferedReader(new FileReader(userFollow))) {
			String temp;
			while((temp=reader.readLine())!=null){
				count++;
				String[] split=temp.split(",");
				long user=Long.valueOf(split[0]);
				long followee=Long.valueOf(split[1]);
				CompareableUser tempUser=new CompareableUser();
				tempUser.setUserId(user);
				tempUser=userTree.getElement(tempUser, userTree.root);
				if (tempUser==null) {
					continue;
				}
				CompareableUser tempFollowee=new CompareableUser();
				tempFollowee.setUserId(followee);
				tempFollowee=userTree.getElement(tempFollowee, userTree.root);
				if (tempFollowee==null) {
					continue;
				}
				
				String provinceFollow=tempUser.getProvince()+","+tempFollowee.getProvince();
				if(ProvinceFollowMap.containsKey(provinceFollow))	
					ProvinceFollowMap.put(provinceFollow,ProvinceFollowMap.get(provinceFollow)+1);
				else {
					ProvinceFollowMap.put(provinceFollow,1);
				}
				userTree.getElement(tempFollowee, userTree.root).setFollowers_Count(tempFollowee.getFollowers_Count()+1);
				if(count%100000==0){
					System.out.println(count);
				}
			}
			System.out.println("end followGraphStatic");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

	
	// 中序遍历avl�?
	public  void getFollowNumber(AvlNode<CompareableUser> t) {
		if (t != null) {
			getFollowNumber(t.getLeft());
			CompareableUser compareableUser = t.getElement();
			followList.add(compareableUser.getFollowers_Count());
			getFollowNumber(t.getRight());
		}
	}

	// 中序遍历avl�?
	public  void getSuperNumber(AvlNode<CompareableUser> t) {
		if (t != null) {
			getFollowNumber(t.getLeft());
			CompareableUser compareableUser = t.getElement();
			
			if(compareableUser.getFollowers_Count()>mediaFollow){
				String province=compareableUser.getProvince();
				if(superNumberMap.containsKey(province)){
					superNumberMap.put(province, superNumberMap.get(province)+1);
				}else {
					superNumberMap.put(province, 1);
				}
			}
			getFollowNumber(t.getRight());
		}
	}
	
	/**
	 * @param userInformationFile
	 * @return
	 *@create_time：2014年9月4日上午11:04:52
	 *@modifie_time：2014年9月4日 上午11:04:52
	  
	 */
	public AvlTree<CompareableUser> getUserAvl(String userInformationFile) {
		AvlTree<CompareableUser> userTree = new AvlTree<CompareableUser>();
		long count=0;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(userInformation)));
			String oneLine = bufferedReader.readLine();
			while (oneLine != null) {
				String[] split = oneLine.split(",");
				CompareableUser user = new CompareableUser();
				user.setUserId(Long.valueOf(split[0]));
				//user.setName(split[1]);
				user.setProvince(split[2]);
				//user.setMan(split[3] == "m" ? true : false);
				userTree.insert(user);
				oneLine = bufferedReader.readLine();
				count++;
			}
			bufferedReader.close();
			System.out.println("read user success");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(count);
		return userTree;
	}

}
