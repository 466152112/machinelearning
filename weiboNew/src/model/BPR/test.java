/**
 * 
 */
package model.BPR;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ReadUtil;
import util.SplitTrainAndTest;
import util.WriteUtil;

/**   
 *    
 * @progject_nameï¼šweiboNew   
 * @class_nameï¼štest   
 * @class_describeï¿?  
 * @creatorï¼šzhouge   
 * @create_timeï¿?014ï¿?ï¿?ï¿?ä¸‹åˆ3:33:26   
 * @modifierï¼šzhouge   
 * @modified_timeï¿?014ï¿?ï¿?ï¿?ä¸‹åˆ3:33:26   
 * @modified_noteï¿?  
 * @version    
 *    
 */
public class test {
	private static int classSize=0;
	
	/**
	 * @param args
	 *@create_timeï¼?014å¹?æœ?8æ—¥ä¸‹å?:40:15
	 *@modifie_timeï¼?014å¹?æœ?8æ—?ä¸‹åˆ3:40:15
	  
	 */
	public static void main(String[] args) {
	//	String path="J:/workspace/weiboNew/data/";
		String path="J:/workspace/weiboNew/data/2w";
		String followGraphFileName=path+"2wfollowGraph.txt";
		String userListFile=path+"2wuserList.txt";
		String wordclassMap="";
		String userWordFile=path+"";
		SplitTrainAndTest<String> splitTrainAndTest=new SplitTrainAndTest<>(followGraphFileName, 10);
		Set<String> userSet=splitTrainAndTest.getUserSet();
		List<String> userList=splitTrainAndTest.getUserList();
		Map<String, Integer> wordClassMap=readwordclass(wordclassMap);
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(userList, userListFile);
	}
	

	/**
	 * @param fileName
	 * @return
	 *@create_timeï¼?014å¹?æœ?8æ—¥ä¸‹å?:40:07
	 *@modifie_timeï¼?014å¹?æœ?8æ—?ä¸‹åˆ3:40:07
	  
	 */
	public static Map<String, Integer> readwordclass(String fileName){
		Map<String, Integer> result=new HashMap();
		ReadUtil readUtil=new ReadUtil();
		List<String> map=readUtil.readFileByLine(fileName);
		for (String oneline : map) {
			String[] split=oneline.split(" ");
			int id=Integer.valueOf(split[1]);
			result.put(split[0],id);
			if (id>classSize) {
				classSize=id;
			}
		}
		classSize+=1;
		return result;
	}
	
	/**
	 * @param wordClassMap
	 * @param userSet
	 * @param userList
	 * @param userWordFile
	 * @param userFeature
	 *@create_timeï¼?014å¹?æœ?8æ—¥ä¸‹å?:40:11
	 *@modifie_timeï¼?014å¹?æœ?8æ—?ä¸‹åˆ3:40:11
	  
	 */
	public static void mapUserToFeature(Map<String, Integer> wordClassMap,Set<String> userSet,List<String> userList,String userWordFile,String userFeature){
		Map<String, List<Integer>> result=new HashMap<>();
		long count=0;
		try (BufferedReader weiboReader = new BufferedReader(new FileReader(
				userWordFile));){
			String oneLine=null;
			while((oneLine=weiboReader.readLine())!=null){
				String[] split=oneLine.trim().split("\t");
				if (split.length==2) {
					String userId=split[0];
					if (!result.containsKey(userId)) {
						List<Integer> templist=new ArrayList<Integer>(classSize);
						for (int i = 0; i < classSize; i++) {
							templist.add(0);
						}
						result.put(userId, templist);
					}
					if (!userSet.contains(userId)) continue;
					split=split[1].split(" ");
					for (String word : split) {
						if(!wordClassMap.containsKey(word)) continue;
						int wordclass=wordClassMap.get(word);
						result.get(userId).set(wordclass, result.get(userId).get(wordclass)+1);
					}
				}
				System.out.println(count++);
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writemapList(result, userFeature, userList);
	}
}
