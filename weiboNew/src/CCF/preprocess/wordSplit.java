/**
 * 
 */
package CCF.preprocess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bean.AvlTree;
import bean.AvlTree.AvlNode;
import CCF.bean.News;
import CCF.bean.NewsBrowseRecord;
import CCF.bean.User;
import util.ReadUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：wordSplit   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年9月25日 上午8:58:37   
 * @modifier：zhouge   
 * @modified_time：2014年9月25日 上午8:58:37   
 * @modified_note：   
 * @version    
 *    
 */
public class wordSplit {
	String path="H:/资料/competition/ccf/";
	String sourceFile=path+"train_data.txt";
	String newsFile=path+"news.txt";
	String userFile=path+"user.txt";
	String readRecord=path+"readrecord.txt";
	List<String> userResult=new ArrayList<>();
	List<String> newsResult=new ArrayList<>();
	List<String> recordResult=new ArrayList<>();
	public static void main(String[] agrs){
		wordSplit wordSplit=new wordSplit();
		wordSplit.run();
	}
	public void run(){
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> sourceList=readUtil.readfromFileByStream(sourceFile);
		AvlTree<User> userAvlTree=new AvlTree<User>();
		AvlTree<News> newsavltAvlTree=new AvlTree<News>();
		
		List<NewsBrowseRecord> newslist=new ArrayList<>();
		WriteUtil<String> writeUtil=new WriteUtil<>();
		
		int usercount=0;
		int newscount=0;
		
		for (String oneline : sourceList) {
			NewsBrowseRecord onerecord=NewsBrowseRecord.covert(oneline);
			if (onerecord==null) {
				continue;
			}
			newslist.add(onerecord);
			String BrowseTime=onerecord.getBrowseTime().get(Calendar.YEAR)+"/"+(onerecord.getBrowseTime().get(Calendar.MONTH)+1)+"/"+onerecord.getBrowseTime().get(Calendar.DATE)+"/"+onerecord.getBrowseTime().get(Calendar.HOUR_OF_DAY);
			recordResult.add(onerecord.getUserId()+"\t"+onerecord.getNewsId()+"\t"+BrowseTime);
			News news=News.getnew(onerecord);
			if (!newsavltAvlTree.contains(news)) {
				newsavltAvlTree.insert(news);
				newscount++;
			}else {
				newsavltAvlTree.getElement(news, newsavltAvlTree.root).addReadTime();
			}
			User user=new User();
			user.setUserId(onerecord.getUserId());
			if (!userAvlTree.contains(user)) {
				userAvlTree.insert(user);
				usercount++;
			}else {
				userAvlTree.getElement(user, userAvlTree.root).addRecordtime();
			}
		}
		inorderNew(newsavltAvlTree.root);
		inorderuser(userAvlTree.root);
		writeUtil.writeList(newsResult, newsFile);
		writeUtil.writeList(userResult, userFile);
		writeUtil.writeList(recordResult, readRecord);
	}
	
	public void inorderNew(AvlNode<News> t){
		if (t!=null) {
			inorderNew(t.left);
			News news=t.element;
			newsResult.add(news.toString());
			inorderNew(t.right);
		}
	}
	public void inorderuser(AvlNode<User> t){
		if (t!=null) {
			inorderuser(t.left);
			User user=t.element;
			userResult.add(user.getUserId()+"\t"+user.getRecordtime());
			inorderuser(t.right);
		}
	}
}
