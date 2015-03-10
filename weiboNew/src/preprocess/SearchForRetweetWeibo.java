/**
 * 
 */
package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import bean.AvlTree;
import bean.AvlTree.AvlNode;
import bean.OnePairTweet;
import util.ReadWeiboUtil;
import util.WriteUtil;

/**
 * 
 * 项目名称：liuchuang 类名称：PartionWeibo 类描述： 把所有微博按照userId分为1000个文�?创建人：zhouge
 * 创建时间�?014�?�?�?上午9:34:41 修改人：zhouge 修改时间�?014�?�?�?上午9:34:41 修改备注�?
 * 
 * @version
 * 
 */
public class SearchForRetweetWeibo {
	static String path = "/home/pc/weibo/2011/";
	//static String path = "J:/workspace/weiboNew/data/2012/";
	// String target="";
	static String target = path + "retweetType19.txt";
	static String sourcePath = path + "content/";
	static List<String> writeResult = new ArrayList<>();
	static WriteUtil<String> writeUtil = new WriteUtil<>();

	// 主程�?
	public static void main(String[] args) {

		SearchForRetweetWeibo SearchForRetweetWeibo = new SearchForRetweetWeibo();

		List<String> readerFile = SearchForRetweetWeibo
				.getSourceFile(sourcePath);

		AvlTree<OnePairTweet> sourceWeiboTree;
		ReadWeiboUtil readWeiboUtil = null;
		ReadWeiboUtil readTarget=null;
		// read one By One
		for (int i = 0; i < 365; i++) {
			Set<Long> sourceIdSet=new HashSet<>();
			// read the begin file
			readWeiboUtil = new ReadWeiboUtil(readerFile.get(i));
			OnePairTweet temp = new OnePairTweet();
			sourceWeiboTree = new AvlTree<>();
			while ((temp = readWeiboUtil.readOnePairTweetInMen()) != null) {
				// if the weibo is no get from other than keep in set
				if (temp.getRetweetId()==0L&&!sourceIdSet.contains(temp.getWeiboId())) {
					sourceIdSet.add(temp.getWeiboId());
					sourceWeiboTree.insert(temp);
				}else if (sourceIdSet.contains(temp.getWeiboId())) {
					//System.out.println("error");
					continue;
				}
				
				if (sourceWeiboTree.root!=null&&sourceWeiboTree.root.height==19) {

					// then read the file and the next files to find weibo which is
					// retweet from that sourceWeibo
					for (int j = i; j < readerFile.size(); j++) {
						//cal the date difference
						int dateDifference=calDateDifference(readerFile.get(j),readerFile.get(i));
						if (dateDifference>10) {
							break;
						}
						readTarget = new ReadWeiboUtil(readerFile.get(j));
						
						System.out.println("begin:"+readerFile.get(i)+"\t scan from \t"+readerFile.get(j));
						sourceWeiboTree = readTarget
								.findTweetByReWeiboIdInTree(sourceWeiboTree);
						System.out.println("end:"+readerFile.get(i)+"\t scan from \t"+readerFile.get(j));
						readTarget.closeReader();
					}
					//write each time
					writeToFile(sourceWeiboTree.root);
					sourceWeiboTree = null;
					sourceWeiboTree=new AvlTree();
				}else{
					continue;
				}
				
			}
			
			
			if (sourceWeiboTree.root!=null) {
				// then read the file and the next files to find weibo which is
				// retweet from that sourceWeibo
				for (int j = i; j < readerFile.size(); j++) {
					int dateDifference=calDateDifference(readerFile.get(j),readerFile.get(i));
					if (dateDifference>10) {
						break;
					}
					readTarget = new ReadWeiboUtil(readerFile.get(j));
					System.out.println(readerFile.get(i)+"\t scan from \t"+readerFile.get(j));
					sourceWeiboTree = readTarget
							.findTweetByReWeiboIdInTree(sourceWeiboTree);
					readTarget.closeReader();
				}
				//write each time
				writeToFile(sourceWeiboTree.root);
			}
			
			readWeiboUtil.closeReader();
			
			
		}

		// writer the last
		writeUtil.writeList(writeResult, target);

	}

	// 中序遍历avl�?
	public static void writeToFile(AvlNode<OnePairTweet> t) {
		if (t != null) {
			writeToFile(t.getLeft());
			OnePairTweet temPairTweet=t.getElement();
			List<OnePairTweet> reTweets =temPairTweet .getRetweetList();
			if (reTweets != null && reTweets.size() >0) {
				writeResult.add(temPairTweet.toString());
				for (OnePairTweet onePairTweet : reTweets) {
					writeResult.add(onePairTweet.toString());
				}
				writeResult.add("");
				if (writeResult.size() > 1000) {
					writeUtil.writeList(writeResult,target);
					writeResult = new ArrayList<>();
				}
			}
			//set the retweetList to null
			t.getElement().setRetweetList(null);
			
			writeToFile(t.getRight());
		}
	}

	public List<String> getSourceFile(String path) {
		File pathFile = new File(path);
		File[] Filelist = pathFile.listFiles();
		HashMap<String, Calendar> fileMap = new HashMap<>();
		List<String> result = new ArrayList<>();
		for (int j = 0; j < Filelist.length; j++) {
			File file = Filelist[j];
			if (file.isFile()) {
				String dayString = file.getName().substring(0,
						file.getName().indexOf("."));
				Date date = getDay(dayString);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				fileMap.put(file.getPath(), calendar);
			}
		}
		List<String> fileList = new ArrayList<>(fileMap.keySet());
		CalendarComparator calendarComparator = new CalendarComparator(fileMap);
		Collections.sort(fileList, calendarComparator);
		return fileList;
	}

	public static Date getDay(String time) {

		final SimpleDateFormat sf1 = new SimpleDateFormat("yyyy_MM_dd",
				Locale.ENGLISH);
		try {
			return sf1.parse(time.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static class CalendarComparator implements Comparator<String> {
		HashMap<String, Calendar> baseHashMap = null;

		public CalendarComparator(HashMap<String, Calendar> baseHashMap) {
			this.baseHashMap = baseHashMap;
		}

		public int compare(String arg0, String arg1) {
			return this.baseHashMap.get(arg0).compareTo(
					this.baseHashMap.get(arg1));
		}

	}
	
	/**
	 * @param fileName1 big 
	 * @param fileName2 small
	 * @return
	 *@create_time：2014年8月31日下午9:33:43
	 *@modifie_time：2014年8月31日 下午9:33:43
	  
	 */
	public static  int calDateDifference(String fileName1,String fileName2){
		int index=fileName1.lastIndexOf("/");
		if (index==-1) {
			 index=fileName1.lastIndexOf("\\");
		}
		Date date1=getDay(fileName1.substring(index+1, fileName1.length()-4));
		
		index=fileName2.lastIndexOf("/");
		if (index==-1) {
			 index=fileName2.lastIndexOf("\\");
		}
		Date date2=getDay(fileName2.substring(index+1, fileName2.length()-4));
		
		return (int) ((date1.getTime()-date2.getTime())/(24*60*60*1000));
	}
}
