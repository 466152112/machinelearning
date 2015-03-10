/**
 * 
 */
package model.link;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：convert2Matrix
 * @class_describe�?
 * @creator：zhouge
 * @create_time�?014�?�?3�?下午5:36:16
 * @modifier：zhouge
 * @modified_time�?014�?�?3�?下午5:36:16
 * @modified_note�?
 * @version
 * 
 */
public class convert2Matrix {
	static Map<String, Set<String>> followGraph = null;

	public static void main(String[] args) throws FileNotFoundException {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		String path = "J:/workspace/weiboNew/data/1k/";
		ComputeCommonNeighborByOutdegree ccn = new ComputeCommonNeighborByOutdegree();
		String followGraphFile = path + "1kfollowgraph.txt";
		String userListFile=path+"1kuserList.txt";
		String followGraphMatrix=path+"followgraphMatrix.txt";
		followGraph = new ReadUtil().getFollowGraph(followGraphFile);
		List<String> userList=new ReadUtil().readFileByLine(userListFile);
		int[][] result=new int[userList.size()][userList.size()];
		for (String userId : userList) {
			int indexUser=userList.indexOf(userId);
			Set<String> followeeList=followGraph.get(userId);
			if(followeeList!=null){
				for (String followee : followeeList) {
					int index=userList.indexOf(followee);
					result[indexUser][index]=1;
				}
			}
		}
		WriteUtil writeUtil=new WriteUtil<>();
		writeUtil.write2Vector(result, followGraphMatrix);
	}
}
