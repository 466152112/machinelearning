package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import util.ChineseSpliter;
import util.FileUtil;
import util.ReadUtil;
import util.ReadWeiboUtil;
import util.WriteUtil;
import bean.AvlTree;
import bean.OnePairTweet;


/**   
*    
* @progject_name：weiboNew   
* @class_name：userTopicCal   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年9月27日 下午3:47:21   
* @modifier：zhouge   
* @modified_time：2014年9月27日 下午3:47:21   
* @modified_note：   
* @version    
*    
*/
public class userTopicCal {

	String path = "/media/new2/data/wordsplit/";
	//String path = "J:/workspace/weiboNew/data/topic/";
	String resultPath=path+"topicdistribution/";
	String userWeiboFile2011 = path+"2011/";
	String userWeiboFile2012 =  path+"2012/";
	
	String topicFile = resultPath + "topic.txt";
	String timeFormat="yyyy_MM_dd";
	String everyDayTopic=resultPath + "everyDayTopic.txt";
	public static void main(String[] args) {
		userTopicCal termSplit = new userTopicCal();
		termSplit.run();
	}

	public void run() {
		
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> topicList=readUtil.readFileByLine(topicFile);
		Map<String, Integer> topic=new HashMap<String, Integer>();
		for (String oneLine : topicList) {
			String[] split=oneLine.split(" ");
			topic.put(split[0], Integer.valueOf(split[1]));
		}
		
		
		FileUtil fileUtil=new FileUtil();
		List<String> fileList=fileUtil.getFileListSortByTimeASC(userWeiboFile2011, userWeiboFile2012, timeFormat);
		for (String fileName : fileList) {
			split(fileName, topic);
		}
		
	}

	public void split(String sourceFile,Map<String, Integer> wordMap) {
		try (BufferedReader weiboReader=new BufferedReader(new FileReader(sourceFile));){
			String temp;
			int[] topicCal=new int[100];
			while((temp=weiboReader.readLine())!=null){
				String[] split=temp.split("\t");
				if(split.length==4){
					temp=split[3];
				}else if(split.length==6){
					temp=split[5];
				}else {
					continue;
				}
				split=temp.split(" ");
				for (String word : split) {
					if (wordMap.containsKey(word)) {
						int index=wordMap.get(word);
						topicCal[index]+=1;
					}
				}
			}
			StringBuffer buffer=new StringBuffer();
			String dayString ="";
			int index=sourceFile.lastIndexOf("2011");
			if (index==-1) {
				index=sourceFile.lastIndexOf("2012");
			}
			dayString=sourceFile.substring(index, sourceFile.indexOf("."));
			buffer.append(dayString+":");
			for (int i : topicCal) {
				buffer.append(i+",");
			}
			//writeToFile
			WriteUtil<String> writeUtil=new WriteUtil<>();
			writeUtil.writeOneLine(buffer.toString(), everyDayTopic);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
