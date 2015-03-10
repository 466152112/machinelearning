/**
 * 
 */
package model.link;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：covert   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年10月9日 下午2:40:57   
 * @modifier：zhouge   
 * @modified_time：2014年10月9日 下午2:40:57   
 * @modified_note：   
 * @version    
 *    
 */

	/**
	 * 
	 */

	import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
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
	public class covert {
		static Map<Integer, Set<Integer>> deleteEdge = new HashMap();
		static String path = "J:/workspace/twitter/data/twitter/";
		public static void main(String[] args) throws FileNotFoundException {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

			covert ccn = new covert();
				// String followGraphFile = path + "trainlist.txt";
				String followGraphFile = path + "test.txt";
				List<String> listedsa = new ReadUtil()
						.readFileByLine(followGraphFile);
				Set<Integer> useridmapSet=new HashSet<>();
				for (String oneline : listedsa) {
					String[] split = oneline.split("\t");
					int user1 = Integer.valueOf(split[0].trim());
					int user2 = Integer.valueOf(split[1].trim());
					useridmapSet.add(user1);
					useridmapSet.add(user2);
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
				List<Integer> userlist=new ArrayList<>(deleteEdge.keySet());
				Collections.sort(userlist);
				List<String> resultList=new ArrayList<>();
				for (Integer userid : userlist) {
					String temp=userid+"\t";
					Set<Integer> followees=deleteEdge.get(userid);
					for (Integer followee : followees) {
						temp+=followee+"\t";
					}
					resultList.add(temp.trim());
				}
				WriteUtil writeUtil = new WriteUtil();
				writeUtil.writeList(resultList, path+"forest.txt");
			}

		}



