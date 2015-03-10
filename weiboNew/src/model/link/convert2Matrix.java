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
 * @progject_nameÔºöweiboNew
 * @class_nameÔºöconvert2Matrix
 * @class_describeÔº?
 * @creatorÔºözhouge
 * @create_timeÔº?014Âπ?Êú?3Êó?‰∏ãÂçà5:36:16
 * @modifierÔºözhouge
 * @modified_timeÔº?014Âπ?Êú?3Êó?‰∏ãÂçà5:36:16
 * @modified_noteÔº?
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
