/**
 * 
 */
package weibo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tool.dataStucture.AvlTree;
import bean.OnePairTweet;
import bean.Retweetlist;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：ReadWeiboUtil
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年12月15日 下午4:42:33
 * @modifier：zhouge
 * @modified_time：2014年12月15日 下午4:42:33
 * @modified_note：
 * @version
 * 
 */
public class ReadWeibo implements ReadIter {

	private final String sourceFile;

	private BufferedReader reader;
	private int errorCount = 0;
	private  int PoolSizeLimit = 1000000;
	public  int count = 0;
	private List<OnePairTweet> weiboPool = new ArrayList<>(PoolSizeLimit);

	public ReadWeibo(String sourceFile) {
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

	public String readerOneLine() {
		try {
			return this.reader.readLine();
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

	public OnePairTweet findOnePairTweetByWeiboId(long weiboId) {
		OnePairTweet result = new OnePairTweet();
		String oneLine;
		try {

			while ((oneLine = reader.readLine()) != null) {
				int flag = oneLine.indexOf(String.valueOf(weiboId));
				if (flag != -1) {
					OnePairTweet tempOnePairTweet = OnePairTweet
							.covert(oneLine);
					if (tempOnePairTweet.getWeiboId() == weiboId) {
						return tempOnePairTweet;
					} else {
						continue;
					}
				}
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}

	/**
	 * @param weiboId
	 * @return
	 * @create_time��2014��8��30������9:32:27
	 * @modifie_time��2014��8��30�� ����9:32:27
	 */
	public List<OnePairTweet> findTweetByReWeiboId(long weiboId) {
		List<OnePairTweet> result = new ArrayList();
		String oneLine;
		try {

			while ((oneLine = reader.readLine()) != null) {
				int flag = oneLine.indexOf(String.valueOf(weiboId));
				if (flag != -1) {
					OnePairTweet tempOnePairTweet = OnePairTweet
							.covert(oneLine);
					if (tempOnePairTweet.getRetweetId() == weiboId) {
						result.add(tempOnePairTweet);
						// return tempOnePairTweet;
					} else {
						continue;
					}
				}
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}

	/**
	 * @param weiboId
	 * @return find weibo by retweetId in Batch
	 * @create_time��2014��8��30������9:33:20
	 * @modifie_time��2014��8��30�� ����9:33:20
	 */
	public Map<Long, List<OnePairTweet>> findTweetByReWeiboIdInBatch(
			Set<Long> weiboIdSet) {
		Map<Long, List<OnePairTweet>> result = new HashMap<>();
		System.out.println("enter findTweetByReWeiboIdInBatch");
		String oneLine;
		try {
			// int count=0;
			while ((oneLine = reader.readLine()) != null) {
				// System.out.println(count++);

				// get retweetid from string
				long retweetId = OnePairTweet.covertRetweetId(oneLine);
				// 3509879886952174

				if (weiboIdSet.contains(retweetId)) {
					// System.out.println(count++);
					OnePairTweet tempOnePairTweet = OnePairTweet
							.covert(oneLine);
					if (result.containsKey(retweetId)) {
						result.get(retweetId).add(tempOnePairTweet);
						// System.out.println("hit in findTweetByReWeiboIdInBatch");
					} else {
						List<OnePairTweet> retweetList = new ArrayList<>();
						retweetList.add(tempOnePairTweet);
						result.put(retweetId, retweetList);
					}
				}

			}
			System.out.println("finish the  findTweetByReWeiboIdInBatch");
			return result;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * @param weiboId
	 * @return find weibo by retweetId in Batch
	 * @create_time��2014��8��30������9:33:20
	 * @modifie_time��2014��8��30�� ����9:33:20
	 */
	public AvlTree<OnePairTweet> findTweetByReWeiboIdInTree(
			AvlTree<OnePairTweet> weibotree) {
		String oneLine;
		try {
			// int count=0;
			while ((oneLine = reader.readLine()) != null) {

				// get retweetid from string
				long retweetId = OnePairTweet.covertRetweetId(oneLine);
				if (retweetId == 0L) {
					continue;
				}
				OnePairTweet tempPairTweet = new OnePairTweet();
				tempPairTweet.setWeiboId(retweetId);
				if (weibotree.contains(tempPairTweet)) {

					// System.out.println(count++);
					OnePairTweet tempOnePairTweet = OnePairTweet
							.covert(oneLine);
					weibotree.getElement(tempPairTweet, weibotree.root)
							.addOneRetweetList(tempOnePairTweet);
				}

			}

			return weibotree;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * @param weiboId
	 * @return find weibo by retweetId in Batch
	 * @create_time��2014��8��30������9:33:20
	 * @modifie_time��2014��8��30�� ����9:33:20
	 */
	public AvlTree<Long> findTweetIdThatBeRetweet(AvlTree<Long> sourceId) {

		String oneLine;
		try {
			// int count=0;
			while ((oneLine = reader.readLine()) != null) {
				// System.out.println(count++);

				// get retweetid from string
				long retweetId = OnePairTweet.covertRetweetId(oneLine);
				// 3509879886952174
				if (retweetId == 0L) {
					continue;
				} else {
					sourceId.insert(retweetId);
				}

			}

			return sourceId;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * @return
	 * @create_time��2014��8��29������10:06:07
	 * @modifie_time��2014��8��29�� ����10:06:07
	 */
	public OnePairTweet readOnePairTweet() {
		String oneLine;
		try {
			oneLine = reader.readLine();
			if (oneLine == null) {
				return null;
			}
			return OnePairTweet.covert(oneLine);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			// e.printStackTrace();
			// read the next
			errorCount++;
			return readOnePairTweet();
		}
		return null;
	}

	/**
	 * @return
	 * @create_time��2014��8��29������10:06:07
	 * @modifie_time��2014��8��29�� ����10:06:07
	 */
	public Retweetlist readRetweetList() {
		String oneLine;
		try {
			Retweetlist retweetlist = new Retweetlist();
			oneLine = reader.readLine();
			if (oneLine == null) {
				return null;
			}
			OnePairTweet root = covert(oneLine);
			List<OnePairTweet> reTweets = new ArrayList<>();
			while ((oneLine = reader.readLine()) != null
					&& !oneLine.trim().equals("")) {
				reTweets.add(covert(oneLine));
			}
			// HashMap<Long, OnePairTweet> map=new HashMap<>();
			// for (OnePairTweet onePairTweet : reTweets) {
			// map.put(onePairTweet.getWeiboId(), onePairTweet);
			// }
			// reTweets=new ArrayList<>(map.values());
			retweetlist.setRoot(root);
			retweetlist.setRetweetList(reTweets);
			return retweetlist;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			 e.printStackTrace();
			// read the next
			errorCount++;
			System.out.println("error in readRetweetList: " + errorCount);
			return readRetweetList();
		}
		return null;
	}

	/**
	 * @return
	 * @create_time��2014��8��29������10:06:07
	 * @modifie_time��2014��8��29�� ����10:06:07
	 */
	public Long readWeiboId() {
		String oneLine;
		try {
			oneLine = reader.readLine();
			if (oneLine == null) {
				return 0L;
			}
			return OnePairTweet.crawlingWeiboId(oneLine);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			// e.printStackTrace();
			// read the next
			errorCount++;
			return readWeiboId();
		}
		return 0L;
	}

	/**
	 * @return
	 * @create_time��2014��8��29������10:06:07
	 * @modifie_time��2014��8��29�� ����10:06:07
	 */
	public Long readRetweenWeiboId() {
		String oneLine;
		try {
			oneLine = reader.readLine();
			if (oneLine == null) {
				return null;
			}
			return OnePairTweet.crawlingWeiboId(oneLine);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {
			// e.printStackTrace();
			// read the next
			errorCount++;
			return readRetweenWeiboId();
		}
		return null;
	}

	/**
	 * @return
	 * @create_time��2014��8��29������12:35:47
	 * @modifie_time��2014��8��29�� ����12:35:47
	 */
	public OnePairTweet readOnePairTweetInMen() {
		OnePairTweet temPairTweet;

		// if weibopool have, then return
		if (weiboPool.size() > 0) {
			temPairTweet = weiboPool.get(0);
			weiboPool.remove(0);
			return temPairTweet;
		}

		// else read from the file ,and put full the weiboPool
		temPairTweet = this.readOnePairTweet();
		weiboPool.add(temPairTweet);
		while (temPairTweet != null && weiboPool.size() < PoolSizeLimit) {

			try {

				String oneLine = reader.readLine();
				if (oneLine == null) {
					break;
				}
				temPairTweet = OnePairTweet.covert(oneLine);
				if (temPairTweet == null) {
					continue;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (StringIndexOutOfBoundsException e) {
				// e.printStackTrace();
				// read the next
				errorCount++;
				continue;
			} catch (NumberFormatException e) {
				errorCount++;
				continue;
			}
			weiboPool.add(temPairTweet);

		}

		// if the weibo pool is filled ,then return
		if (weiboPool.size() > 0) {
			temPairTweet = weiboPool.get(0);
			weiboPool.remove(0);
			return temPairTweet;
		}
		// else return null
		else {
			return null;
		}

	}

	/**
	 * @param oneLine
	 * @return
	 */
	public OnePairTweet covert(String oneLine) {

		String tempString = oneLine;
		long userId, weiboId, retweetid = 0L, retweetUserId = 0L;
		String content, time;
		try {
			weiboId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));

			tempString = tempString.substring(tempString.indexOf("\t")).trim();

			userId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));
			if (tempString.trim().equals("")) {

			}
			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			try {
				// 查看是否能转为转发格式
				retweetid = Long.valueOf(tempString.substring(0,
						tempString.indexOf("\t")));

				tempString = tempString.substring(tempString.indexOf("\t"))
						.trim();

				retweetUserId = Long.valueOf(tempString.substring(0,
						tempString.indexOf("\t")));

				tempString = tempString.substring(tempString.indexOf("\t"))
						.trim();

				if (tempString.indexOf("\t") == -1) {
					time = tempString;
					content = "";
				} else {
					time = tempString.substring(0, tempString.indexOf("\t"));

					tempString = tempString.substring(tempString.indexOf("\t"))
							.trim();

					content = tempString.trim();
				}

			} catch (NumberFormatException e) {
				if (oneLine.indexOf("\t") == -1) {
					time = tempString;
					content = "";
				} else {
					time = tempString.substring(0, tempString.indexOf("\t"));

					tempString = tempString.substring(tempString.indexOf("\t"))
							.trim();

					content = tempString.trim();
				}

			}
			if (retweetid == 0L) {
				return new OnePairTweet(userId, weiboId, content, time);
			} else {
				return new OnePairTweet(userId, weiboId, content, retweetid,
						retweetUserId, time);
			}
		} catch (Exception e) {
			System.out.println("error in :" + oneLine);
			 e.printStackTrace();
			return null;
		}

	}

	/**
	 * @return the errorCount
	 */
	public int getErrorCount() {
		return errorCount;
	}

}
