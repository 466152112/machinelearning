/**
 * 
 */
package MF.CR.preprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import util.ClearElementInLimit;
import util.HashMapUtil;
import util.ReadUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name：MF   
 * @class_name：SplitTrainAndTest   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月26日 下午7:06:51   
 * @modifier：zhouge   
 * @modified_time：2014年9月26日 下午7:06:51   
 * @modified_note：   
 * @version    
 *    
 */
public class SplitTrainAndTest {
	static String path="J:/workspace/MF/data/";
	static String sourceFile=path+"movielens-100k.txt";
	
	public static void main(String[] agrs){
		SplitTrainAndTest aa=new SplitTrainAndTest();
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> sourceList=readUtil.readFileByLine(sourceFile);
		//the trainSet
		for (int i = 0; i < 3; i++) {
			int trainSize=0;
			if (i==0) {
				trainSize=10;
			}else if(i==1){
				trainSize=20;
			}else {
				trainSize=50;
			}
			int ratingLimit=trainSize+10;
			//each size run 10 replication
			for (int j = 0; j < 10; j++) {
				aa.run(trainSize, ratingLimit,j, sourceList,path+"split/");
			}
		}
	}
	

	public void run(int trainSize,int ratingLimit,int echo,List<String> source,String path) {
		
		

		Map<String, Set<String>> usermap=new HashMap();
		Map<String,  Set<String>> itemmap=new HashMap<>();
		
		for (String oneline : source) {
			String[] split=oneline.split("\t");
			String userid=split[0];
			String itemid=split[1];
			int rating=Integer.valueOf(split[2]);
			
			if(usermap.containsKey(userid)){
				usermap.get(userid).add(itemid);
			}else {
				Set<String> ratingListMap=new HashSet();
				ratingListMap.add(itemid);
				usermap.put(userid, ratingListMap);
			}
			if(itemmap.containsKey(itemid)){
				itemmap.get(itemid).add(userid);
			}else {
				Set<String> ratingListMap=new HashSet();
				ratingListMap.add(userid);
				itemmap.put(itemid, ratingListMap);
			}
		}
		
		ClearElementInLimit CE=new ClearElementInLimit(usermap, itemmap, ratingLimit, 5);
		usermap=CE.getUsermap();
		itemmap=CE.getItemmap();
		
		System.out.println(trainSize+"the left user is :"+usermap.size());
		System.out.println(trainSize+"the left item is :"+itemmap.size());
		Map<String, Map<String,Integer >> trainSet=new HashMap<>();
		
		for (String oneline : source) {
			String[] split=oneline.split("\t");
			String userid=split[0];
			String itemid=split[1];
			int rating=Integer.valueOf(split[2]);
			if(itemmap.containsKey(itemid)&&usermap.containsKey(userid)){
				if (trainSet.containsKey(userid)) {
					trainSet.get(userid).put(itemid, rating);
				}else {
					Map<String, Integer> ratingList=new HashMap<>();
					ratingList.put(itemid, rating);
					trainSet.put(userid, ratingList);
				}
			}
		}
		List<String> Trainresult=new ArrayList<>();
		List<String> Testresult=new ArrayList<>();
		Iterator<String> userIterator=trainSet.keySet().iterator();
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		while (userIterator.hasNext()) {
			String userId=userIterator.next();
			Map<String, Integer> ratingList=trainSet.get(userId);
			
			List<String> itemSet=new ArrayList<>(ratingList.keySet());
			Set<String> trainItemSet=new HashSet<>();
			
			while(true){
				int index=random.nextInt(itemSet.size());
				String itemid=itemSet.get(index);
				if(!trainItemSet.contains(itemid)){
					trainItemSet.add(itemid);
					if (trainItemSet.size()>=trainSize) {
						break;
					}
				}else {
					continue;
				}
			}
			
			for (String itemid : trainItemSet) {
				Trainresult.add(userId+"\t"+itemid+"\t"+ratingList.get(itemid));
			}
			
			for (String itemid : itemSet) {
				if (!trainItemSet.contains(itemid)) {
					Testresult.add(userId+"\t"+itemid+"\t"+ratingList.get(itemid));
				}
			}
		}
		
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(Trainresult, path+"train"+trainSize+"-"+echo+".txt");
		writeUtil.writeList(Testresult, path+"test"+trainSize+"-"+echo+".txt");
	}
	
}
