/**
 * 
 */
package Remind.preproccess.testInfoPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;
import Resource.data_Path;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：Convert
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月13日 下午12:26:00
 * @modifier：zhouge
 * @modified_time：2015年1月13日 下午12:26:00
 * @modified_note：
 * @version
 * 
 */
public class Convert {

	public static void main(String[] args) {
		String sourceFile = data_Path.getPath() + "follow/FollowGraph.txt";
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> tempList = readUtil.readFileByLine(sourceFile);
		Set<String> userset = new HashSet<>();
		List<String> userList = new ArrayList<>();
		for (String OneLine : tempList) {
			String[] split = OneLine.split("\t");
			String user1 = split[0].trim();
			userset.add(user1);
			for (int i = 1; i < split.length; i++) {
				String user2 = split[i].trim();
				if (user1 == user2) {
					continue;
				}
				userset.add(user2);
			}
		}
		userList.addAll(userset);
		Map<String, Integer> map=new HashMap<String, Integer>();
		for (int i = 0; i < userList.size(); i++) {
			map.put(userList.get(i),i+1);
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(userList, data_Path.getPath() + "follow/infopath/userIndex.txt");
		List<String> resultList=new ArrayList<>();
		for (String OneLine : tempList) {
			String[] split = OneLine.split("\t");
			String user1 = split[0].trim();
			int index1=map.get(user1);
			for (int i = 1; i < split.length; i++) {
				String user2 = split[i].trim();
				if (user1 == user2) {
					continue;
				}
				int index2=map.get(user2);
				resultList.add(index1+" "+index2+" "+1);
			}
		}
		writeUtil.writeList(resultList, data_Path.getPath() + "follow/infopath/edge.txt");
	}
}
