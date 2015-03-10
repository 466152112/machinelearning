/**
 * 
 */
package model.link;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ReadUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：idmap   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月9日 上午8:53:30   
 * @modifier：zhouge   
 * @modified_time：2014年10月9日 上午8:53:30   
 * @modified_note：   
 * @version    
 *    
 */
public class idmap {
	static Map<Integer, Set<Integer>> followGraph = new HashMap();
	static Map<Integer, Set<Integer>> deleteEdge = new HashMap();
	static final double lamda = 0.01;
	static String path = "J:/workspace/twitter/data/twitter/";
	static int numberofuser=11016;
	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

		idmap ccn = new idmap();
			// String followGraphFile = path + "trainlist.txt";
			String followGraphFile = path + "followgraph.txt";
			List<String> listedsa = new ReadUtil()
					.readFileByLine(followGraphFile);
			Set<Integer> useridmapSet=new HashSet<>();
			for (String oneline : listedsa) {
				String[] split = oneline.split(",");
				int user1 = Integer.valueOf(split[0].trim());
				int user2 = Integer.valueOf(split[1].trim());
				useridmapSet.add(user1);
				useridmapSet.add(user2);
				if (user1==user2) {
					System.out.println();
					continue;
				}
				if (followGraph.containsKey(user1)) {
					followGraph.get(user1).add(user2);
				} else {
					Set<Integer> temp = new HashSet<>();
					temp.add(user2);
					followGraph.put(user1, temp);
				}
				if (!followGraph.containsKey(user2)) {
					Set<Integer> temp = new HashSet<>();
					followGraph.put(user2, temp);
				}
			}
			System.out.println(followGraph.keySet().size());
			String deleteFile = path + "deletedge.txt";
			listedsa = new ReadUtil().readFileByLine(deleteFile);
			for (String oneline : listedsa) {
				String[] split = oneline.split(",");
				int user1 = Integer.valueOf(split[0].trim());
				int user2 = Integer.valueOf(split[1].trim());
				if (user1==user2) {
					System.out.println();
					continue;
				}
				if (deleteEdge.containsKey(user1)) {
					deleteEdge.get(user1).add(user2);
				} else {
					Set<Integer> temp = new HashSet<>();
					temp.add(user2);
					deleteEdge.put(user1, temp);
				}
			}
			 Iterator<Integer> deleteuser=deleteEdge.keySet().iterator();
			 System.out.println(deleteEdge.keySet().size());
			 while (deleteuser.hasNext()) {
				int keyuser =  deleteuser.next();
				followGraph.get(keyuser).removeAll(deleteEdge.get(keyuser));
			}
			System.out.println(deleteEdge.keySet().size());
			List<Integer> userListIndex=new ArrayList<Integer>(useridmapSet);
			WriteUtil writeUtil = new WriteUtil();
			List<String> test=ccn.mapindex(userListIndex,deleteEdge);
			writeUtil.writeList(test, path+"test.txt");
			List<String> train=ccn.mapindex(userListIndex,followGraph);
			writeUtil.writeList(train, path+"train.txt");
			writeUtil.writeList(userListIndex, path+"userid.txt");
		}
	
	public List<String> mapindex(List<Integer> userListindex,Map<Integer, Set<Integer>> mapuser){
		List<String> resultList=new  ArrayList<>();
		 Iterator<Integer> iterator=mapuser.keySet().iterator();
		 while (iterator.hasNext()) {
			int keyuser =  iterator.next();
			int indexoffollower=userListindex.indexOf(keyuser);
			Set<Integer> followeelist=mapuser.get(keyuser);
			for (Integer followee : followeelist) {
				int indexoffollowee=userListindex.indexOf(followee);
				resultList.add(indexoffollower+"\t"+indexoffollowee);
			}
		}
		return resultList;
	}

	}

