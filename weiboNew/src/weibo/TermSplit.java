package weibo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import bean.AllTermSet;
import bean.OneUserTermSet;
import bean.IFIDF;
import weibo.util.ReadUtil;
import weibo.util.WriteUtil;

public class TermSplit {
	static String randomUserWeibo,TF_IDFResultData,resultUserData,resultTermData,encoding,HTTPREGEXS_STRING,dataPath; 
	
	
	public TermSplit( String randomUserWeibo,String TF_IDFResultData,String resultUserData,String resultTermData,String encoding,String HTTPREGEXS_STRING,String dataPath){
		//璧嬪�
		this.dataPath=dataPath; 
		this.randomUserWeibo=randomUserWeibo;
		this.TF_IDFResultData=TF_IDFResultData;
		this.resultUserData=resultUserData;
		this.resultTermData=resultTermData;
		this.encoding=encoding;
		this.HTTPREGEXS_STRING=HTTPREGEXS_STRING;
		
		//璇诲拰鍐欏伐鍏风被
				ReadUtil readUtil = new ReadUtil();
				WriteUtil writeUtil = new WriteUtil();
				
				//userTermSet涓�key涓虹敤鎴风紪鍙�value 涓虹敤鎴风殑璇嶆眹闆�
				HashMap<String, OneUserTermSet> allUserTermSet = new HashMap<String, OneUserTermSet>();
				//鎸夌収鎸囧畾鐨勭紪鐮佹柟寮忚鍙栨簮鏁版嵁
				ArrayList<String> sourceList=null;
				//婧愭暟鎹殑璁板綍鏁�
				int sourceListSize=0;
				try {
					sourceList = readUtil.readfromFileByStream(this.randomUserWeibo, encoding);
					sourceListSize=sourceList.size();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//妯″紡鍖归厤銆傛妸鐭摼鎺ュ垹闄�
				Pattern pat=Pattern.compile(HTTPREGEXS_STRING);  
			
				for (int i = 0; i < sourceListSize; i++) {
					//姣忎竴琛屼互涓�釜tab閿垝鍒嗐�鏈�鍜�涓瓧绗︿覆
					String[] oneLineString = sourceList.get(i).split("\t");
					int oneLineStringLength=oneLineString.length;
					//鎺掗櫎涓嶆槸3鎴�涓瓧绗︿覆鐨勬儏鍐�
					if (oneLineStringLength == 3 || oneLineStringLength == 4) {
					
						String oneline=oneLineStringLength == 3 ? oneLineString[2]
								: oneLineString[2] + oneLineString[3];
					
						//妯″紡鍖归厤銆傛妸鐭摼鎺ュ垹闄�
						Matcher mat=pat.matcher(oneline);  
						oneline=mat.replaceAll(" ");  
						oneLineString[0]=oneLineString[0].trim();
						//鍒ゆ柇userTermSet涓槸鍚︿互鍙婂寘鍚簡姝よ鐢ㄦ埛鐨勮褰�
						if (allUserTermSet.containsKey(oneLineString[0])) {
							ArrayList<String> tempArrayList = split(oneline);
							allUserTermSet.get(oneLineString[0])
									.addTermList(tempArrayList);
							
						} 
						// 濡傛灉璇ヨ涓嶅寘鍚鍦ㄧ敤鎴风殑璁板綍鏃�
						else {
							ArrayList<String> tempArrayList =split(oneline);
							OneUserTermSet newUser = new OneUserTermSet();
							newUser.addTermList(tempArrayList);
							allUserTermSet.put(oneLineString[0], newUser);
							
						}
					}
				}
				
				
				//鏋勫缓璇嶆眹闆�
				//鍒濆鍖栨墍鏈夎瘝姹囬泦
				AllTermSet allTermSet = new AllTermSet(allUserTermSet.size());
				
				//鍒濆鍖栫敤鎴烽泦
				ArrayList<String> userIdList = new ArrayList<>();
				
				//杩唬userTermSet
				Iterator<String> iterator = allUserTermSet.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					userIdList.add(key);
					ArrayList<String> tempArrayList = allUserTermSet.get(key)
							.getTermList();
					allTermSet.addTermList(tempArrayList);
				}
				
				//杈撳嚭璇嶆眹闆�
				writeUtil.WriteIntoFileByEncodingFormat(resultTermData,
						allTermSet.getTermList(), encoding);
				// 杈撳嚭鐢ㄦ埛闆�
				writeUtil.WriteIntoFileByEncodingFormat(resultUserData, userIdList,
						encoding);
				
				
				// TFIDF鍊艰绠�
				int userListSize=userIdList.size();
				int TermListSize=allTermSet.getIDF().size();
				
				// TFIDF鍊煎瓨鍌ㄥ湪鏁版嵁鐭╅樀 鍒濆鍊间細榛樿涓�
				double[][] IFIDF = new double[userListSize][TermListSize];
				
				for (int i = 0; i < userListSize; i++) {
					//閬嶅巻姣忎竴涓敤鎴疯瘝姹囬泦
					OneUserTermSet tempUserTermSet = allUserTermSet.get(userIdList.get(i));
					
					//褰撳墠鐢ㄦ埛璇嶆眹闆嗗ぇ灏�
					int tempUserTermSetSize=tempUserTermSet.getTermList().size();
					
					long begin=System.currentTimeMillis();
					//閬嶅巻褰撳墠鐢ㄦ埛璇嶆眹闆嗕腑鐨勬瘡涓�釜璇嶆眹
					for (int j = 0; j < tempUserTermSetSize; j++) {
						String term=tempUserTermSet.getTermList().get(j);
						int index = allTermSet.getTermList().indexOf(term);
						IFIDF[i][index] = tempUserTermSet.getTF().get(term)* allTermSet.getIDF().get(term);
					}
					//閬嶅巻褰撳墠鐢ㄦ埛璇嶆眹闆嗕腑鐨勬瘡涓�釜璇嶆眹
					HashMap<String, Double> TF=tempUserTermSet.getTF();
					Iterator<String> iteratorTemp=TF.keySet().iterator();
					while(iteratorTemp.hasNext()){
						String term=iteratorTemp.next();
						int index=allTermSet.getTermList().indexOf(term);
						
						IFIDF[i][index] = TF.get(term)* allTermSet.getIDF().get(term)*10000;
					}
					System.out.println(i);
				}
				//writeUtil.IFIDFListByEncodingFormat(resultDataPath, IFIDFList, encoding);
				ArrayList<Double> iFIDFArrayList=new ArrayList<Double>();
				for(int i=0;i<userListSize;i++){
					String tempDoube=new String();
					for (int j = 0; j < TermListSize; j++) {
						iFIDFArrayList.add(IFIDF[i][j]);
					}
					writeUtil.doubleListByEncodingFormat(TF_IDFResultData, iFIDFArrayList, "UTF-8");
					iFIDFArrayList=new ArrayList<Double>();
				}
	
	}
	

	// 锟街达拷锟斤拷
	public ArrayList<String> split(String text) {

		// 锟斤拷锟斤拷锟街词讹拷锟斤拷
		ArrayList<String> cwords = new ArrayList<String>();
		Analyzer analyzer = new IKAnalyzer(true);
		StringReader sreader = new StringReader(text);
		
		// 锟街达拷
		TokenStream ts = analyzer.tokenStream("", sreader);

		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		// 锟斤拷锟斤拷执锟斤拷锟斤拷
		try {
			while (ts.incrementToken()) {
				String te = term.toString();
				cwords.add(te);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sreader.close();
		return cwords;
	}
	
	
	
}
