/**
 * 
 */
package Remind.preproccess;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



import tool.FileTool.ReadUtil;
import tool.io.FileIO;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.Filter;
import Resource.All_weibo_Source;
import Resource.Intection_txt_Source;
import Resource.User_profile_txt_Source;
import bean.OnePairTweet;
import bean.Retweetlist;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：AddweiboCotent   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月14日 下午7:04:46   
 * @modifier：zhouge   
 * @modified_time：2015年1月14日 下午7:04:46   
 * @modified_note：   
 * @version    
 *    
 */
public class AddweiboCotent {
	static Set<Long> needUserId=new HashSet<>();
	static Set<Long> needweiboId=new HashSet<>();
	static Map<String, Long> nameAndId=new ConcurrentHashMap<>();
	static FileWriter resultwriter=null;
	static{
		User_profile_txt_Source user_profile_txt_Source=	new User_profile_txt_Source();
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> listtemp=readUtil.readFileByLine(Intection_txt_Source.getPath()+"need_weibo_id.txt");
		for (String weiboid : listtemp) {
			needweiboId.add(Long.valueOf(weiboid));
		}
		
		needUserId=user_profile_txt_Source.get_all_user_id_inSet();
		//nameAndId=user_profile_txt_Source.getuserNameAndId();
		try {
			resultwriter=new FileWriter(new File(Intection_txt_Source.getPath()+"need_weibo_id_content.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		AddweiboCotent temAddweiboCotent=new AddweiboCotent();
		temAddweiboCotent.run();
	}
	public void run(){
		All_weibo_Source all_weibo_Source=new All_weibo_Source();
		List<String> fileList=all_weibo_Source.get_all_weibo_File_by_Sorted();
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<Object>> results = new ArrayList<>();
		
		for (String OneFile : fileList) {
			results.add(executor.submit(new scanRetweetFile(OneFile)));
		}
		for (Future<Object> result : results) {
			try {
				result.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}
	
	class scanRetweetFile implements Callable{
		final String oneFile;
		Filter filter=new ChineseFilter();
		public scanRetweetFile(String oneFile) {
			this.oneFile = oneFile;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.Callable#call()
		 */
		@Override
		public Boolean call() throws Exception {
			// TODO Auto-generated method stub
			ReadWeibo rw = new ReadWeibo(this.oneFile);
			Retweetlist listRetweetlist;
			List<String> result=new ArrayList<>();
			List<String> coretweetHistory=new ArrayList<>();
			
			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0)
					continue;
				OnePairTweet roottweet = listRetweetlist.getRoot();
				
				if(needweiboId.contains(roottweet.getWeiboId())){
					result.add(roottweet.toString());
				}
				List<OnePairTweet> list = listRetweetlist.getRetweetList();
				Set<Long> retweetuserid=new HashSet<>();
				if (list != null) {
					for (int i = 0, size = list.size(); i < size; i++) {
						// the retweet path if from the retweet tree
						List<String> routeuserList = filter.getRouteName(list.get(i).getContent());
						if (needweiboId.contains(list.get(i).getWeiboId())) {
							result.add(list.get(i).toString()+" "+roottweet.getContent());
						}
						if (needUserId.contains(list.get(i).getUserId())&&list.get(i).getUserId()!=roottweet.getUserId()&&!retweetuserid.contains(list.get(i).getUserId())) {
							StringBuffer tempBuffer=new StringBuffer();
							tempBuffer.append(list.get(i).getWeiboId()+"\t");
							tempBuffer.append(list.get(i).getUserId()+"\t");
							tempBuffer.append(list.get(i).getCreateTime()+"\t");
							tempBuffer.append(roottweet.getUserId()+"\t"+roottweet.getWeiboId()+"\t"+roottweet.getCreateTime());
							coretweetHistory.add(tempBuffer.toString());
							retweetuserid.add(list.get(i).getUserId());
						}
					}
				}
			}
			
			WriteResult(result);
			WritecoretweetResult(coretweetHistory);
			System.out.println("finish" + oneFile);
			return true;
		}
	}

	public synchronized void WritecoretweetResult(List<String> result){
		try {
			FileIO.writeList(Intection_txt_Source.getPath()+"need_user_retweet_history.txt", result,true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void WriteResult(List<String> result){
		try {
			FileIO.writeList(Intection_txt_Source.getPath()+"need_weibo_id_content.txt", result,true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
