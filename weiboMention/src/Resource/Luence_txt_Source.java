/**
 * 
 */
package Resource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.store.NIOFSDirectory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import weibo.util.tweetContent.lucence.WeiboSimilarity;
import Resource.superClass.superResource;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：word2vec_txt_Source
 * @class_describe：
 * @creator：鸽
 * @create_time：2014年12月23日 下午8:31:25
 * @modifier：鸽
 * @modified_time：2014年12月23日 下午8:31:25
 * @modified_note：
 * @version
 * 
 */
public class Luence_txt_Source extends superResource {
	final static String path = work_path + "lucene/temp/";

	/* userId_word_count_file file */
	final static String doc_index_path = path + "index1/";
	static Analyzer analyzer = new IKAnalyzer(true);
	static Similarity similarity = new WeiboSimilarity();
	static IndexSearcher search = null;
	
	static {
		try {
			search = new IndexSearcher(new NIOFSDirectory(new File(
					doc_index_path)));
			search.setSimilarity(similarity);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Luence_txt_Source() {
		super();
	}

	/**
	 * @return the path
	 */
	public static String getPath() {
		return path;
	}

	/**
	 * @return the docIndexPath
	 */
	public static String getDocIndexPath() {
		return doc_index_path;
	}

	/**
	 * @param weiboContent
	 * @param userId
	 * @return the luence sim by weibo and user, if no user then return 0
	 *@create_time：2015年1月14日下午6:20:29
	 *@modifie_time：2015年1月14日 下午6:20:29
	  
	 */
	public double get_score_weibo_user(String weiboContent, long userId) {
		BooleanQuery bq = setQuery(weiboContent, userId);
		Sort sort = new Sort(new SortField[] { new SortField(null,
				SortField.SCORE, false) });
		// 先按记录的得分排序,然后再按记录的发布时间倒序
		TopFieldCollector collector = null;
		try {
			collector = TopFieldCollector.create(sort, 1, false, true, false,
					false);
			search.search(bq, collector);
			TopDocs tDocs = collector.topDocs();
			ScoreDoc sDocs[] = tDocs.scoreDocs;
			if (sDocs.length == 0) {
				System.out.println("no find user:" + userId);
			} else {
				ScoreDoc tScore = sDocs[0];
				return tScore.score;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public BooleanQuery setQuery(String query, Long userId) {
		return setQuery(query, String.valueOf(userId));
	}

	public BooleanQuery setQuery(String query, String userId) {
		String fiels[] = { "Name", "weibo_content", "Tag", "biography" };
		BooleanQuery bq = new BooleanQuery();
		TermQuery userIdquery = new TermQuery(new Term("Id", userId));
		bq.add(userIdquery, BooleanClause.Occur.MUST);
		for (int i = 0; i < fiels.length; i++) {

			IKSegmenter se = new IKSegmenter(new StringReader(query), true);
			Lexeme le = null;
			try {
				while ((le = se.next()) != null) {
					String tKeyWord = le.getLexemeText();
					String tFeild = fiels[i];
					TermQuery tq = new TermQuery(new Term(fiels[i], tKeyWord));

					if (tFeild.equals("weibo_content")) { // 在Name这一个Field需要给大的比重
						tq.setBoost(1.0f);
					} else if (tFeild.equals("Tag")
							|| tFeild.equals("biography")
							|| tFeild.equals("Name")) {
						tq.setBoost(2.0f); // 其他的不需要考滤
					}
					bq.add(tq, BooleanClause.Occur.SHOULD); // 关键字之间是 "或" 的关系
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bq;
	}
}
