/**
 * 
 */
package weibo.util.tweetContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

public class EnglishSpliter implements Spliter{
	Version matchVersion = Version.LUCENE_36;
	final String path = "J:/workspace/twitter/data/";
	BufferedReader stopwordsReader ;
	static Analyzer analyzer;
	public EnglishSpliter() throws IOException{
		 stopwordsReader = new BufferedReader(new FileReader(path
				+ "stopwordInEnglist.txt"));
	 analyzer = new StandardAnalyzer(matchVersion, stopwordsReader);
	}
	
	public  String spliterResultInString(String oneline) {
		StringBuffer result=new StringBuffer();
		oneline = new EnglishFilter(oneline).getFilterResultString();
		StringReader sreader = new StringReader(oneline);
		TokenStream ts = analyzer.tokenStream("", sreader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		ts = new PorterStemFilter(ts);
		OffsetAttribute offsetAttribute = ts
				.addAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = ts
				.addAttribute(CharTermAttribute.class);
		try {
			ts.reset();
			while (ts.incrementToken()) {
				String dfa = charTermAttribute.toString();
				result.append(dfa+" ");
			}
			ts.end();
			ts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}
	public  List<String> spliterResultInList(String oneline) {
		List<String> result=new ArrayList<>();
		oneline = new EnglishFilter(oneline).getFilterResultString();
		StringReader sreader = new StringReader(oneline);
		TokenStream ts = analyzer.tokenStream("", sreader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		ts = new PorterStemFilter(ts);
		OffsetAttribute offsetAttribute = ts
				.addAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = ts
				.addAttribute(CharTermAttribute.class);
		try {
			ts.reset();
			while (ts.incrementToken()) {
				String dfa = charTermAttribute.toString();
				result.add(dfa);
			}
			ts.end();
			ts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return result;
	}
}
