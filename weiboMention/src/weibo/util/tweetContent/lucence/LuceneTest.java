package weibo.util.tweetContent.lucence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

@SuppressWarnings("deprecation")
public class LuceneTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		 /** 指明要索引文件夹的位置,这里是D:\lucene\temp\docs文件夹下  */   
			String path="J:/workspacedata/weiboMention/luence/temp/";
			
             File fileDir=new File(path+"docs");
             /**放索引文件的位置*/
             File indexDir=new File(path+"index");
             
             Analyzer luceneAnalyer=new IKAnalyzer(true);
             Directory  dir=FSDirectory.open(indexDir);
             IndexWriterConfig iwc=new IndexWriterConfig(Version.LUCENE_36,luceneAnalyer);
             iwc.setOpenMode(OpenMode.CREATE);
             IndexWriter indexWriter=new IndexWriter(dir,iwc);
             // 在索引库没有建立并且没有索引文件的时候首先要commit一下让他建立一个,索引库的版本信息 ,如果第一次没有commit就打开一个索引读取器的话就会报异常 
             indexWriter.commit();
             File[] textFiles=fileDir.listFiles();
             long startTime=new Date().getTime();
             	//增加document到索引去
             for(int i=0;i<textFiles.length;i++){
            	 if(textFiles[i].isFile() && textFiles[i].getName().endsWith(".txt")){
            		 System.out.println("File"+textFiles[i].getCanonicalPath()+"正在被索引。");
            		 String temp=FileReaderAll(textFiles[i].getCanonicalPath(),"UTF-8");
            		// System.out.println(temp);
            		 Document document=new Document();
            		 //创建一个字段
            		 Field fieldPath=new Field("path",textFiles[i].getPath(),Field.Store.YES,Field.Index.NO);
            		 Field fieldBody=new Field("body",temp,Field.Store.YES,Field.Index.ANALYZED,TermVector.WITH_POSITIONS_OFFSETS);
            		 document.add(fieldPath);
            		 document.add(fieldBody);
            		 indexWriter.addDocument(document);
            		 
            	 }
             }
             indexWriter.close();
             long endTime=new Date().getTime();
             System.out.println("这花费了"+(endTime-startTime)+"毫秒来把文档增加到索引里面去！"+fileDir.getPath());
             
	}
	public static String FileReaderAll(String filename,String charset)throws IOException{
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),charset));
		String line=new String();
		StringBuffer temp=new StringBuffer();
		while((line=reader.readLine())!=null){
			temp.append(line);
		}
		reader.close();
		return temp.toString();
	}

}
