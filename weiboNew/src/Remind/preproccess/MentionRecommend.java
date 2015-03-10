/**
 * 
 */
package Remind.preproccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ReadUser;
import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：MentionRecommend   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2014年12月7日 下午2:34:36   
 * @modifier：zhouge   
 * @modified_time：2014年12月7日 下午2:34:36   
 * @modified_note：   
 * @version    
 *    
 */
public class MentionRecommend {
	// static String path = "/media/pc/new2/data/new/";
		 static String path = "/home/zhouge/database/weibo/new/";
		//static String path = "J:/workspacedata/weiboNew/data/reminder/";

		static String intectionFile = path + "userid/intectionrecord.txt";
		List<String> intectionRecord = new ArrayList<>();

		public static void main(String[] srg) {
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
			// String path = "/home/zhouge/database/weibo/new/";
			String profileFile = path + "userid/profile.txt";
			 String sourcePath = "/home/zhouge/database/weibo/new/retweet/";
			// String sourcePath = "/media/pc/new2/data/new/retweet/";
			//String sourcePath = "J:/workspacedata/weiboNew/data/reminder/retweet/";
			String useridFile = path + "userid/alluserid.txt";
//			HashMap<String, Long> usernameAndIdMap = null;
//			usernameAndIdMap = ReadUser
//					.getuserNameAndIdMapFromprofileFile(profileFile);
//			HashMap<Long, String> useridAndNameMap = null;
//			useridAndNameMap = ReadUser
//					.getuserIdAndNameMapFromprofileFile(profileFile);
			// AvlTree<User> useravl = ReadUser.getuserFromprofileFile(profileFile);
			MentionRecommend mentionRecommend=new MentionRecommend();
			//mentionRecommend.convertToUserIdAndContent();
			mentionRecommend.convertToPLDAType();
		}
		
		/**
		 * 
		 *@create_time：2014年12月7日下午2:52:40
		 *@modifie_time：2014年12月7日 下午2:52:40
		  
		 */
		public void convertToUserIdAndContent(){
			Set<Long> deleteuserIdset=new HashSet<>();
			String weiboContentKeywordId=path+"userid/weiboContentKeyWordId.txt";
			Map<Long, StringBuffer> useridAndContentMap=null;
			do {
				useridAndContentMap=new HashMap<>();
				try {
					FileInputStream fis = new FileInputStream(weiboContentKeywordId);
					InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
					BufferedReader bufferedReader = new BufferedReader(isr);
					String oneLine =null;
					while ((oneLine =bufferedReader.readLine())!= null) {
						String[] split=oneLine.split("\t");
						long userid=Long.valueOf(split[0]);
						if (deleteuserIdset.contains(userid)) {
							continue;
						}
						if (useridAndContentMap.keySet().contains(userid)) {
							useridAndContentMap.get(userid).append(" "+split[1]);	
						}else if(useridAndContentMap.keySet().size()<100000){
							StringBuffer temp=new StringBuffer();
							temp.append(split[1]);
							useridAndContentMap.put(userid, temp);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				//write to file
				writemapkeyAndValue(useridAndContentMap, path+"userid/userId_All_Content.txt");
				deleteuserIdset.addAll(useridAndContentMap.keySet());
			} while (useridAndContentMap.keySet().size()!=0);
			
		}
	
		public void convertToPLDAType(){
			String userid_contentFil=path+"userid/lda/userId_All_Content.txt";
			String TargetFile=path+"userid/lda/user_content_LDAtype.txt";
			List<String> useridList=new ArrayList<String>();
			try {
				FileInputStream fis = new FileInputStream(userid_contentFil);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(isr);
				String oneLine =null;
				
				while ((oneLine =bufferedReader.readLine())!= null) {
					String[] split=oneLine.split("\t");
					long userid=Long.valueOf(split[0]);
					String[] split1=oneLine.split(" ");
					Map<String, Integer> wordMap=new HashMap<>();
					for (String word : split1) {
						if (wordMap.containsKey(word)) {
							wordMap.put(word, wordMap.get(word)+1);
						}else {
							wordMap.put(word, 1);
						}
					}
					writeLDAType(wordMap, TargetFile);
					useridList.add(split[0]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			WriteUtil<String> writeUtil=new WriteUtil<>();
			writeUtil.writeList(useridList, path+"/userid/lda/userid_Sequence.txt");
		}
		
		/**
		 * @param wordMap
		 * @param TargetFile
		 *@create_time：2014年12月7日下午10:45:57
		 *@modifie_time：2014年12月7日 下午10:45:57
		  
		 */
		public void writeLDAType(Map<String, Integer> wordMap,String TargetFile){
			StringBuffer temp=new StringBuffer();
			for (String word : wordMap.keySet()) {
				temp.append(word+" "+wordMap.get(word)+" ");
			}
			WriteUtil<String> writeUtil =new WriteUtil<>();
			writeUtil.writeOneLine(temp.toString().trim(), TargetFile);
		}
		/**
		 * @param result
		 * @param filename
		 *@create_time：2014年12月7日下午2:52:37
		 *@modifie_time：2014年12月7日 下午2:52:37
		  
		 */
		public void writemapkeyAndValue(Map<Long, StringBuffer> result,String filename) {
			final File file1 = new File(filename);
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1,true));){
				Iterator<Long> keyiteratorIterator=result.keySet().iterator();
				while(keyiteratorIterator.hasNext()){
					Long keyT=keyiteratorIterator.next();
					bufferedWriter.write(keyT+"\t"+result.get(keyT).toString());
					bufferedWriter.newLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failure to write: " + filename);
			} 
		}
}
