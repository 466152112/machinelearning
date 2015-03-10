/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import montylingua.string;

import org.fnlp.app.keyword.AbstractExtractor;
import org.fnlp.app.keyword.WordExtract;

import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.corpus.StopWords;
import edu.fudan.util.exception.LoadModelException;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：ChineseKeyWordInFudan   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年11月8日 上午10:40:57   
 * @modifier：zhouge   
 * @modified_time：2014年11月8日 上午10:40:57   
 * @modified_note：   
 * @version    
 *    
 */
public class ChineseKeyWordInFudan {
	StopWords sw=null;
	CWSTagger seg=null;
	AbstractExtractor key=null;
	public ChineseKeyWordInFudan(){
		sw= new StopWords("models/stopwords");
		try {
			seg = new CWSTagger("models/seg.m");
		} catch (LoadModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		key= new WordExtract(seg,sw);
	}
	
	public ArrayList<String> getKeyWord(String sentence){
		
		
		ArrayList<String> resultArrayList=new ArrayList<>();
		Map<String, Integer> resmap=new HashMap<>();
		key = new WordExtract(seg,sw);
		if(sentence.length()<10){
			resmap=key.extract(sentence, 5);
		}else {
			resmap=key.extract(sentence, 10);
		}
		for (String keyword : resmap.keySet()) {
			if(resmap.get(keyword)>0){
				resultArrayList.add(keyword);
			}
		}
		return resultArrayList;
	}
public ArrayList<String> getKeyWord(String sentence,int keywordnumber){
		
		
		ArrayList<String> resultArrayList=new ArrayList<>();
		Map<String, Integer> resmap=new HashMap<>();
		key = new WordExtract(seg,sw);
		resmap=key.extract(sentence, keywordnumber);
		for (String keyword : resmap.keySet()) {
				resultArrayList.add(keyword);
		}
		return resultArrayList;
	}
	
}
