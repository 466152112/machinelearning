/**
 * 
 */
package preprocess;

import bean.AvlTree;
import util.ReadWeiboUtil;

public class TestIfRepeat {
	//static String path = "/home/zhouge/database/weibo/20w/";
	static String path = "J:/workspace/weiboNew/data/";
	static String sourceFile = path + "userWeibo.txt";

	// 主程�?
	public static void main(String[] args) {

		TestIfRepeat SearchForRetweetWeibo = new TestIfRepeat();


		AvlTree<Long> sourceWeiboIdTree;
		ReadWeiboUtil readWeiboUtil = null;
		ReadWeiboUtil readTarget=null;
			// read the begin file
			readWeiboUtil = new ReadWeiboUtil(sourceFile);
			sourceWeiboIdTree = new AvlTree<>();
			long temp=readWeiboUtil.readWeiboId();
			while ((temp = readWeiboUtil.readWeiboId())!=0L) {
				if (!sourceWeiboIdTree.contains(temp)) {
					sourceWeiboIdTree.contains(temp);
				}else {
						System.out.println("error");
						System.exit(0);
				}
				//System.out.println(temp);
		}
			readWeiboUtil.closeReader();
	}

	
}
