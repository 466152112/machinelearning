package Remind.preproccess.testLucene;

import java.io.File;
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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;
import tool.FileTool.FileUtil;
import tool.FileTool.WriteUtil;
import tool.dataStucture.AvlTree;
import weibo.util.ReadUser;
import weibo.util.ReadWeibo;
import weibo.util.tweetContent.ChineseSpliter;
import weibo.util.tweetContent.ChineseFilter;
import weibo.util.tweetContent.Filter;
import weibo.util.tweetContent.Spliter;
import weibo.util.tweetContent.lucence.WeiboSimilarity;
import Remind.extractFeature.tool.Content_Tool;
import Resource.All_weibo_Source;
import Resource.Luence_txt_Source;
import Resource.User_profile_txt_Source;
import Resource.data_Path;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：Get_Information_RetweetFile
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年12月15日 下午5:59:51
 * @modifier：zhouge
 * @modified_time：2014年12月15日 下午5:59:51
 * @modified_note：
 * @version
 * 
 */
public class build_Lucene_Index {
	static String path = data_Path.getPath();

	
	// get user all information
	 AvlTree<User> useravl = null;
	 Set<Long> userIdset=null;
	 int maxSize = 999999;
	 Map<Long, StringBuffer> userId_content=new ConcurrentHashMap<>();
	 Map<Long, StringBuffer> userId_tag=new ConcurrentHashMap<>();
	 
	// index

	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		build_Lucene_Index main = new build_Lucene_Index();
		main.scanRetweetCorpus();
	}
	/**
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @create_time：2014年12月15日下午6:02:34
	 * @modifie_time：2014年12月15日 下午6:02:34
	 */
	public void scanRetweetCorpus() {
		// 用户属性文件
				// 转发格式存储目录
		User_profile_txt_Source source=new User_profile_txt_Source();
		useravl = source.getLimitUserInTree_limit_by_all_need_file();
		userIdset=source.get_all_user_id_inSet();
		
		List<String> fileList=new All_weibo_Source().get_all_weibo_File_by_Sorted();
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
		try {
			write_to_file();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param targetusermap
	 * @throws IOException
	 *             把一批数据写入文件索引系统
	 * @create_time：2015年1月12日下午3:35:47
	 * @modifie_time：2015年1月12日 下午3:35:47
	 */
	public void write_to_file()throws IOException {
		String index_path =Luence_txt_Source.getDocIndexPath();
		Analyzer analyzer = new IKAnalyzer(true);
		Similarity similarity = new WeiboSimilarity();
		File indexDir = new File(index_path);
		Directory dir = FSDirectory.open(indexDir);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);
		writer.setSimilarity(similarity); // 设置相关度
		writer.commit();
		for (long userid : userIdset) {
			if (userId_content.containsKey(userid)) {
				Document doc_0 = new Document();
				User tempuser=new User(userid);
				tempuser=useravl.getElement(tempuser);
				doc_0.add(new Field("Id", String.valueOf(tempuser.getUserId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc_0.add(new Field("Name",
						tempuser.getUserName(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc_0.add(new Field("Tag", userId_tag.get(userid).toString(), Field.Store.YES,
						Field.Index.ANALYZED));
				doc_0.add(new Field("biography", tempuser.getBiography(), Field.Store.YES, Field.Index.ANALYZED));
				doc_0.add(new Field("weibo_content", userId_content.get(userid).toString(), Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(doc_0);
			}else {
				System.out.println("error in user:"+userid);
			}
		}
		writer.commit();
		writer.close();
		System.out.println("数据索引-----------------");
	}

	

	class scanRetweetFile implements Callable {

		final String oneFile;
		// tool for spliter and filter
		final Filter filter = new ChineseFilter();
		/**
		 * @param oneFile
		 *            当前处理的文件名称
		 * @param usernameAndIdMap
		 *            用户名和id映射 ，用于获取@和路径
		 */
		public scanRetweetFile(String oneFile) {
			this.oneFile = oneFile;
		}
		public boolean check_add(long userid){
			if(userIdset.contains(userid)&&!userId_content.containsKey(userid)){
				userId_content.put(userid, new StringBuffer());
				userId_tag.put(userid, new StringBuffer());
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		public Boolean call() throws Exception{
			// TODO Auto-generated method stub
			
			ReadWeibo rw = new ReadWeibo(this.oneFile);
			Retweetlist listRetweetlist;
			while ((listRetweetlist = rw.readRetweetList()) != null) {
				if (listRetweetlist.getRoot() == null
						|| listRetweetlist.getRoot().getRetweetId() != 0)
					continue;
				
				OnePairTweet roottweet = listRetweetlist.getRoot();
				String root_content=roottweet.getContent();
				String tag_content=filter.getTag(root_content);
				String filter_content=filter.getFilterResultString(root_content);
				long root_user_id=roottweet.getUserId();
				check_add(root_user_id);
				if (userId_content.containsKey(root_user_id)) {
					userId_content.get(root_user_id).append(" "+filter_content);
					if (!tag_content.equals("")) {
						userId_tag.get(root_user_id).append(" "+tag_content);
					}
				}

				List<OnePairTweet> list = listRetweetlist.getRetweetList();
				
				if (list != null) {
					Set<Long> tempUserId=new HashSet<Long>();
					tempUserId.add(root_user_id);
					
					for (int i = 0, size = list.size(); i < size; i++) {
						long userid=list.get(i).getUserId();
						check_add(userid);
						String retweetWord=filter.getFilterResultString(list.get(i).getContent());
						//查看是否包含当前用户
						if (userId_content.containsKey(userid)) {
							if (tempUserId.contains(userid)) {
								userId_content.get(userid).append(" "+retweetWord);
							}else {
								userId_content.get(userid).append(" "+filter_content+" "+retweetWord);
								if (!tag_content.equals("")) {
									userId_tag.get(userid).append(" "+tag_content);
								}
								tempUserId.add(userid);
							}
						}
					}
				}
			}
			System.out.println("finish" + oneFile);
			return true;
		}

	}

}
