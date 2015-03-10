package Remind.preproccess.testLucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class build_Index {
	static String path = data_Path.getPath();

	
	// get user all information
	static AvlTree<User> useravl = null;
	static Set<Long> historyuserId = new HashSet<>();
	final int maxSize = 999999;

	// index

	public static void main(String[] srg) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		// 用户属性文件
		// 转发格式存储目录
		String sourcePath = All_weibo_Source.get_all_weibo_Path();
		useravl = new User_profile_txt_Source().getLimitUserInTree_limit_by_all_need_file();

		build_Index main = new build_Index();
		main.scanRetweetCorpus( sourcePath);
	}

	/**
	 * @param targetusermap
	 * @throws IOException
	 *             把一批数据写入文件索引系统
	 * @create_time：2015年1月12日下午3:35:47
	 * @modifie_time：2015年1月12日 下午3:35:47
	 */
	public void WriteUser_Index(Map<Long, User> targetusermap)
			throws IOException {
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
		for (long userid : targetusermap.keySet()) {
			Document doc_0 = new Document();
			doc_0.add(new Field("Id", String.valueOf(targetusermap.get(userid)
					.getUserId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
			if(targetusermap.get(userid).getUserName()==null){
				System.out.println(userid);
				continue;
			}
			doc_0.add(new Field("Name",
					targetusermap.get(userid).getUserName(), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc_0.add(new Field("Tag", targetusermap.get(userid)
					.getTag_content().toString(), Field.Store.YES,
					Field.Index.ANALYZED));
			doc_0.add(new Field("biography", targetusermap.get(userid)
					.getBiography(), Field.Store.YES, Field.Index.ANALYZED));
			doc_0.add(new Field("weibo_content", targetusermap.get(userid)
					.getTweets_content().toString(), Field.Store.YES, Field.Index.ANALYZED));
			writer.addDocument(doc_0);
		}
		writer.commit();
		writer.close();
		System.out.println("数据索引-----------------");
	}

	/**
	 * @param usernameAndIdMap
	 * @param sourcePath
	 * @create_time：2014年12月15日下午6:02:34
	 * @modifie_time：2014年12月15日 下午6:02:34
	 */
	public void scanRetweetCorpus(
			String sourcePath) {
		
		
	}

	class scanRetweetFile  {

		final String oneFile;
		// tool for spliter and filter
		final Spliter spliter = new ChineseSpliter();
		final Filter filter = new ChineseFilter();
		final Content_Tool content_Feature_tool = new Content_Tool();
		WriteUtil<String> writeUtil = new WriteUtil<>();
		Map<Long, User> presentUserMap=new HashMap<>();
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
			User user=new User(userid);
			if(useravl.contains(user)&&!historyuserId.contains(userid)&&presentUserMap.keySet().size()<maxSize){
				user=useravl.getElement(user);
				presentUserMap.put(userid,user);
				return true;
			}
			else {
				return false;
			}
		}
		
		public Map<Long, User> run() {
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
				if (presentUserMap.containsKey(root_user_id)) {
					
					presentUserMap.get(root_user_id).getTweets_content().append(" "+filter_content);
					if (!tag_content.equals("")) {
						presentUserMap.get(root_user_id).getTag_content().append(" "+tag_content);
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
						if (presentUserMap.containsKey(userid)) {
							if (tempUserId.contains(userid)) {
								presentUserMap.get(userid).getTweets_content().append(" "+retweetWord);
							}else {
								presentUserMap.get(userid).getTweets_content().append(" "+filter_content+" "+retweetWord);
								if (!tag_content.equals("")) {
									presentUserMap.get(userid).getTag_content().append(" "+tag_content);
								}
								tempUserId.add(userid);
							}
						}
					}
				}
			}
			System.out.println("finish" + oneFile);
			return presentUserMap;
		}

	}

}
