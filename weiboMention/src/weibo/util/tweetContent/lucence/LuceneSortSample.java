/**
 * 
 */
package weibo.util.tweetContent.lucence;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：fggf   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月12日 下午1:23:32   
 * @modifier：zhouge   
 * @modified_time：2015年1月12日 下午1:23:32   
 * @modified_note：   
 * @version    
 *    
 */

import java.io.File;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneSortSample {
	public static void main(String[] args) {
		try{

			String path = "J:/workspacedata/weiboMention/luence/temp/index/";
			Analyzer analyzer = new IKAnalyzer(true);
             
			Similarity similarity = new WeiboSimilarity();
			
			boolean isIndex = false;	// true:要索引,false:表示要搜索 
			
			if(isIndex){
				@SuppressWarnings("deprecation")
				  File indexDir=new File(path);
				Directory  dir=FSDirectory.open(indexDir);
	            IndexWriterConfig iwc=new IndexWriterConfig(Version.LUCENE_36,analyzer);
	            iwc.setOpenMode(OpenMode.CREATE);
	            IndexWriter writer=new IndexWriter(dir,iwc);
				//IndexWriter writer = new IndexWriter(new NIOFSDirectory(new File(path)),analyzer,MaxFieldLength.LIMITED);
	            writer.setSimilarity(similarity);	//设置相关度
				
				Document doc_0 = new Document();
				doc_0.add(new Field("Name","java 开发人员", Field.Store.YES, Field.Index.ANALYZED));
				doc_0.add(new Field("Info","招聘 网站开发人员,要求一年或以上工作经验", Field.Store.YES, Field.Index.ANALYZED));
				doc_0.add(new Field("Time","20100201", Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc_0);
				
				
				Document doc_1 = new Document();
				doc_1.add(new Field("Name","高级开发人员(java 方向)", Field.Store.YES, Field.Index.ANALYZED));
				doc_1.add(new Field("Info","需要有四年或者以上的工作经验,有大型项目实践,java基本扎实", Field.Store.YES, Field.Index.ANALYZED));
				doc_1.add(new Field("Time","20100131", Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc_1);
				
				
				Document doc_2 = new Document();
				doc_2.add(new Field("Name","php 开发工程师", Field.Store.YES, Field.Index.ANALYZED));
				doc_2.add(new Field("Info","主要是维护公司的网站php开发,能独立完成网站的功能", Field.Store.YES, Field.Index.ANALYZED));
				doc_2.add(new Field("Time","20100201", Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc_2);
				
				
				Document doc_3 = new Document();
				doc_3.add(new Field("Name","linux 管理员", Field.Store.YES, Field.Index.ANALYZED));
				doc_3.add(new Field("Info","管理及维护公司的linux服务器,职责包括完成mysql数据备份及日常管理,apache的性能调优等", Field.Store.YES, Field.Index.ANALYZED));
				doc_3.add(new Field("Time","20100201", Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc_3);
				
				
				Document doc_4 = new Document();
				doc_4.add(new Field("Name","lucene开发工作师", Field.Store.YES, Field.Index.ANALYZED));
				doc_4.add(new Field("Info","需要两年或者以上的从事lucene java 开发工作的经验,需要对算法,排序规则等有相关经验,java水平及基础要扎实", Field.Store.YES, Field.Index.ANALYZED));
				doc_4.add(new Field("Time","20100131", Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc_4);
				
				
				Document doc_5 = new Document();
				doc_5.add(new Field("Name","php 软件工程师", Field.Store.YES, Field.Index.ANALYZED));
				doc_5.add(new Field("Info","具有大量的php开发经验,如熟悉 java 开发,数据库管理则更佳", Field.Store.YES, Field.Index.ANALYZED));
				doc_5.add(new Field("Time","20100130", Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(doc_5);
				
				writer.close();
				System.out.println("数据索引完成");
			}else{
				IndexSearcher search = new IndexSearcher(new NIOFSDirectory(new File(path)));
				search.setSimilarity(similarity);
				String keyWords = "工程师";
				
				
				String fiels[] = {"Name","Info"};
				
				BooleanQuery bq = new BooleanQuery();
				for(int i=0;i<fiels.length;i++){
					
					IKSegmenter se = new IKSegmenter(new StringReader(keyWords), true);
					Lexeme le = null;
					
					while((le=se.next())!=null){
						String tKeyWord = le.getLexemeText();
						String tFeild = fiels[i];
						TermQuery tq = new TermQuery(new Term(fiels[i], tKeyWord));
						
						if(tFeild.equals("Name")){	//在Name这一个Field需要给大的比重
							tq.setBoost(100.0f);
						}else{
							tq.setBoost(0.0f);		//其他的不需要考滤
						}
						
						bq.add(tq, BooleanClause.Occur.SHOULD);	//关键字之间是 "或" 的关系
					}
				}
				System.out.println("搜索条件Query:" + bq.toString());
				System.out.println();
				Sort sort = new Sort(new SortField[]{new SortField(null,SortField.SCORE,false),new SortField("Time", SortField.INT,true)});
				//先按记录的得分排序,然后再按记录的发布时间倒序
				TopFieldCollector collector = TopFieldCollector.create(sort , 10  ,  false , true ,  false ,  false);
				
				long l = System.currentTimeMillis();
				search.search(bq, collector);
				TopDocs tDocs = collector.topDocs();
				
				ScoreDoc sDocs[] = tDocs.scoreDocs;

				int len = sDocs.length;
				
				for(int i=0;i<len;i++){
					ScoreDoc tScore = sDocs[i];
//					tScore.score 从Lucene3.0开始已经不能通过这样来得到些文档的得分了
					int docId = tScore.doc;
					Explanation exp = search.explain(bq, docId);
					
					Document tDoc = search.doc(docId);
					String Name = tDoc.get("Name");
					String Info = tDoc.get("Info");
					String Time = tDoc.get("Time");
					
					float score = exp.getValue();
//					System.out.println(exp.toString());　如果需要打印文档得分的详细信息则可以通过此方法
					System.out.println("DocId:"+docId+"\tScore:" + score + "\tName:" + Name + "\tTime:" + Time + "\tInfo:" + Info);
				}
				l = System.currentTimeMillis() - l;
				System.out.println("搜索用时:" + l + "ms");
				search.close();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

