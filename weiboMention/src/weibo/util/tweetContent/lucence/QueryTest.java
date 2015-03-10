package weibo.util.tweetContent.lucence;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class QueryTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,ParseException {
         String index="J:/workspacedata/weiboMention/luence/temp/index";
         File file=new File(index);
         if(file!=null){
        	 System.out.println(file.getCanonicalPath());
         }
         IndexReader  reader=IndexReader.open(FSDirectory.open(file));
         IndexSearcher searcher=new IndexSearcher(reader);
         ScoreDoc[] hits=null;
         String queryStr="爱情";//搜索的关键词
         Query query=null;
         Analyzer analyzer= new IKAnalyzer(true);
        try{
        	 QueryParser qp=new QueryParser(Version.LUCENE_36,"body",analyzer);
        	  query=qp.parse(queryStr);
         }catch(ParseException e){
        	 e.printStackTrace();
         }
         if(searcher!=null){
        	 TopDocs results=searcher.search(query,10);
        	 hits=results.scoreDocs;
        	 if(hits.length>0){
            	 System.out.println("找到"+hits.length+"结果");
            	 for(int i=0;i<hits.length;i++){
            	 System.out.println(searcher.doc(hits[i].doc).get("body"));
            	 }
             }
        	 searcher.close();
         }
	}
}
