/**
 * 
 */
package MF.CR.preprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import listnet.module.TrainListNetModule;
import util.ReadUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name：MF   
 * @class_name：ConvertToLSVM   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月28日 下午4:03:55   
 * @modifier：zhouge   
 * @modified_time：2014年9月28日 下午4:03:55   
 * @modified_note：   
 * @version    
 *    
 */
public class ConvertToLSVM {

	static String path="J:/workspace/MF/data/split/";
	static String resultpath=path+"lsvm/";
	
	public static void main(String[] agrs){
		ConvertToLSVM aa=new ConvertToLSVM();
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
				aa.ConvertToLSVM(trainSize, j);
			}
		}
	}
	public void ConvertToLSVM(int trainSize,int echo){
		ReadUtil<String> readUtil=new ReadUtil<>();
		WriteUtil<String> writeUtil=new WriteUtil<>();
		List<String> trainlist=readUtil.readFileByLine(path+"train"+trainSize+"-"+echo+".txt");
		List<String> testlist=readUtil.readFileByLine(path+"test"+trainSize+"-"+echo+".txt");
		
		Map<String, Map<String,Integer>> trainusermap=new HashMap();
		Map<String, Map<String,Integer>> testusermap=new HashMap();
		
		for (String oneline : trainlist) {
			String[] split=oneline.split("\t");
			String userid=split[0];
			String itemid=split[1];
			int rating=Integer.valueOf(split[2]);
			
			if(trainusermap.containsKey(userid)){
				trainusermap.get(userid).put(itemid, rating);
			}else {
				Map<String, Integer> ratingListMap=new HashMap<>();
				ratingListMap.put(itemid, rating);
				trainusermap.put(userid, ratingListMap);
			}
		}
		for (String oneline : testlist) {
			String[] split=oneline.split("\t");
			String userid=split[0];
			String itemid=split[1];
			int rating=Integer.valueOf(split[2]);
			
			if(testusermap.containsKey(userid)){
				testusermap.get(userid).put(itemid, rating);
			}else {
				Map<String, Integer> ratingListMap=new HashMap<>();
				ratingListMap.put(itemid, rating);
				testusermap.put(userid, ratingListMap);
			}
		}
		
		List<String> userList=new ArrayList<>(trainusermap.keySet());
		Collections.sort(userList);
		List<String> trainresult=new ArrayList<>();
		List<String> testresult=new ArrayList<>();
		for (String userid : userList) {
			StringBuffer tempBuffer=new StringBuffer();
			Map<String, Integer> rating=trainusermap.get(userid);
			List<String> itemlist=new ArrayList<>(rating.keySet());
			Collections.sort(itemlist);
			for (String itemid : itemlist) {
				tempBuffer.append(itemid+":"+rating.get(itemid)+"\t");
			}
			trainresult.add(tempBuffer.toString().trim());
		}
		
		for (String userid : userList) {
			StringBuffer tempBuffer=new StringBuffer();
			Map<String, Integer> rating=testusermap.get(userid);
			List<String> itemlist=new ArrayList<>(rating.keySet());
			Collections.sort(itemlist);
			for (String itemid : itemlist) {
				tempBuffer.append(itemid+":"+rating.get(itemid)+"\t");
			}
			testresult.add(tempBuffer.toString().trim());
		}
		writeUtil.writeList(trainresult, resultpath+trainSize+"-"+echo+"/train.txt");
		writeUtil.writeList(testresult,resultpath+trainSize+"-"+echo+"/test.txt");
	}
}
