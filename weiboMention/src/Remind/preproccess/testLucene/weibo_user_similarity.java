/**
 * 
 */
package Remind.preproccess.testLucene;

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
import org.apache.lucene.search.Explanation;
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

import Resource.Luence_txt_Source;
import weibo.util.tweetContent.lucence.WeiboSimilarity;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：weibo_user_similary
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月14日 上午9:28:35
 * @modifier：zhouge
 * @modified_time：2015年1月14日 上午9:28:35
 * @modified_note：
 * @version
 * 
 */
public class weibo_user_similarity {

	public static void main(String[] args) {
		weibo_user_similarity main = new weibo_user_similarity();
		try {
			main.test();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test() throws IOException {

		Analyzer analyzer = new IKAnalyzer(true);

		Similarity similarity = new WeiboSimilarity();
		IndexSearcher search = null;
		try {
			search = new IndexSearcher(new NIOFSDirectory(new File(
					Luence_txt_Source.getDocIndexPath())));
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		search.setSimilarity(similarity);
		String keyWords = "011年最后一条围脖，祝所有人新年快乐。虽然2012是传说中的世界末日，但也请大家充满信心地快乐每一天。新年里继续努力吧！";
		BooleanQuery bq = setQuery(keyWords,"1000164524");
		Sort sort = new Sort(new SortField[] { new SortField(null,SortField.SCORE, false) });
		// 先按记录的得分排序,然后再按记录的发布时间倒序
		TopFieldCollector collector = TopFieldCollector.create(sort, 1,
				false, true, false, false);

		search.search(bq, collector);
		TopDocs tDocs = collector.topDocs();

		ScoreDoc sDocs[] = tDocs.scoreDocs;
		
		int len = sDocs.length;
		for (int i = 0; i < len; i++) {
			ScoreDoc tScore = sDocs[i];
			int docId = tScore.doc;
			Document document = search.doc(docId); 
			float score = tScore.score;
			// System.out.println(exp.toString());　如果需要打印文档得分的详细信息则可以通过此方法
			System.out.println("DocId:" + docId + "\tScore:" + score
					+ "\tName:"+document.get("Name"));
		}
		search.close();
	}

	public BooleanQuery setQuery(String query,String userId) {
		String fiels[] = { "Name", "weibo_content", "Tag", "biography" };
		BooleanQuery bq = new BooleanQuery();
		TermQuery userIdquery = new TermQuery(new Term("Id", userId));
		bq.add(userIdquery,BooleanClause.Occur.MUST);
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
		System.out.println("搜索条件Query:" + bq.toString());

		System.out.println();
		return bq;
	}
}
