/**
 * 
 */
package weibo.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import tool.dataStucture.AvlTree;
import bean.OnePairTweet;
import bean.Retweetlist;
import bean.TweetEnglish;

/**
 * 
 * @progject_name��twitter
 * @class_name��ReadTwitterUtil
 * @class_describe��
 * @creator��zhouge
 * @create_time��2014��9��15�� ����6:53:20
 * @modifier��zhouge
 * @modified_time��2014��9��15�� ����6:53:20
 * @modified_note��
 * @version
 * 
 */
public class ReadTwitter implements ReadIter {

	String sourceFile=null;

	 BufferedReader reader=null;
	 int errorCount = 0;
	 static  int PoolSizeLimit = 1000000;
	 static int count = 0;
	 List<OnePairTweet> weiboPool = new ArrayList<>(PoolSizeLimit);


	public ReadTwitter(String sourceFile) {
		this.sourceFile = sourceFile;
		try {
			FileInputStream fis = new FileInputStream(sourceFile);
			InputStreamReader isr;
			isr = new InputStreamReader(fis, "UTF-8");
			reader = new BufferedReader(isr);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> readerThreeLine() {
		try {
			List<String> result = new ArrayList<>(3);
			String oneLine;

			while ((oneLine = reader.readLine()) != null) {
				oneLine=oneLine.trim();
				if (oneLine.equals("")) {
					result = new ArrayList<>(3);
					continue;
				}else if(oneLine.length()>0&&oneLine.subSequence(0, 1).equals("T")){
					result.add(oneLine);
				}
				else if(oneLine.length()>0&&oneLine.subSequence(0, 1).equals("U")){
					result.add(oneLine);
				}else if(oneLine.length()>0&&oneLine.subSequence(0, 1).equals("W")){
					result.add(oneLine);
				}
				else {
					result = new ArrayList<>(3);
					continue;
				}
				if (result.size() == 3) {
					return result;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void closeReader() {

		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void resetReader() {
		try {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileInputStream fis = new FileInputStream(sourceFile);
			InputStreamReader isr;
			isr = new InputStreamReader(fis, "UTF-8");
			reader = new BufferedReader(isr);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 * @create_time��2014��8��29������10:06:07
	 * @modifie_time��2014��8��29�� ����10:06:07
	 */
	@Override
	public OnePairTweet readOnePairTweet() {
		List<String> threeLine;
		while ((threeLine = readerThreeLine()) != null) {
			return TweetEnglish.covert(threeLine);
		}
		return null;
	}

	/**
	 * @return the errorCount
	 */
	@Override
	public int getErrorCount() {
		return errorCount;
	}

	/* (non-Javadoc)
	 * @see weibo.util.ReadIter#readerOneLine()
	 */
	@Override
	public String readerOneLine() {
		try {
			return this.reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



}
