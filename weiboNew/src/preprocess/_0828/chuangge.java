/**
 * 
 */
package preprocess._0828;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bean.OnePairTweet;
import util.ReadWeiboUtil;
import util.WriteUtil;

/**
 * 
 * 项目名称：liuchuang 类名称：PartionWeibo 类描述： 把所有微博按照userId分为1000个文�?创建人：zhouge
 * 创建时间�?014�?�?�?上午9:34:41 修改人：zhouge 修改时间�?014�?�?�?上午9:34:41 修改备注�?
 * 
 * @version
 * 
 */
public class chuangge {
	// 主程�?
	public static void main(String[] args) {

		//String path = "/media/zhouge/new2/data/2012/";
		String path ="J:/workspace/weiboNew/data/2012/";
		//String target="";
		String target=path+"chuange.txt";
		String sourcePath=path+"content/";
		chuangge chuange = new chuangge();
		
		List<String> readerFile = chuange.getSourceFile(sourcePath);
		WriteUtil<String> writeUtil=new WriteUtil<>();
		List<String> list=new ArrayList<>();
		long id=3398852524743095L;
			for (String file : readerFile) {
				ReadWeiboUtil readWeiboUtil=new ReadWeiboUtil(file);
				
				System.out.println("enter the file:"+file);
				OnePairTweet temp=new OnePairTweet();
				//OnePairTweet temp=readWeiboUtil.findOnePairTweetByWeiboId(id);
//				if (temp!=null) {
//					System.out.println(temp.toString());
//				}
				
				while((temp=readWeiboUtil.readOnePairTweetInMen())!=null){
					list.add(temp.getWeiboId()+","+temp.getUserId()+","+temp.getRetweetId()+","+temp.getRetweetUser_id()+","+temp.getCreateTime());
					if (list.size()>100000) {
						//writeUtil.writeList(list, target);
						list=new ArrayList<>();
					}
				}
				
				System.out.println("error in the file:"+readWeiboUtil.getErrorCount());
			}
			//writeUtil.writeList(list, target);
	}

	public List<String> getSourceFile(String path) {
		File pathFile = new File(path);
		File[] Filelist = pathFile.listFiles();
		List<String> result = new ArrayList<>();
		for (int j = 0; j < Filelist.length; j++) {
			File file = Filelist[j];
			if (file.isFile()) {
				result.add(file.getPath());
			}
		}
		return result;
	}
	
	
}
