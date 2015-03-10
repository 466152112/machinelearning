package Remind.pal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import util.Filter;
import util.ReadWeiboUtil;
import bean.AvlTree;
import bean.OnePairTweet;
import bean.Retweetlist;
import bean.User;

public class ReadTweetInPal extends RecursiveTask< Map<Long, Map<Long, Double> >> {

	final String fileName;
	final AvlTree<User> useravl;
	final HashMap<String, Long> usernameAndIdMap;
	final String sourcePath, path;
	Map<Long, Map<Long, Double>> mention = new HashMap<>();
	public ReadTweetInPal(String OneFile, AvlTree<User> useravl,
			HashMap<String, Long> usernameAndIdMap, String sourcePath,
			String path) {
		fileName = OneFile;
		this.useravl = useravl;
		this.usernameAndIdMap = usernameAndIdMap;
		this.sourcePath = sourcePath;
		this.path = path;

	}

	@Override
	protected Map<Long, Map<Long, Double>> compute() {
		// TODO Auto-generated method stub
		run();
		return mention;
	}

	public void run() {
		System.out.println(fileName);
		ReadWeiboUtil rw = new ReadWeiboUtil(fileName);
		Retweetlist listRetweetlist;
		
		while ((listRetweetlist = rw.readRetweetList()) != null) {
			if (listRetweetlist.getRoot() == null)
				continue;
			OnePairTweet roottweet = listRetweetlist.getRoot();
			ArrayList<String> targetNameList = Filter
					.getNameFromString(roottweet.getContent());
			if (targetNameList != null) {
				mention = addMention(roottweet.getUserId(), mention,
						targetNameList, usernameAndIdMap);
			}

			List<OnePairTweet> list = listRetweetlist
					.getRetweetListSortByTime();
			if (list != null) {
				for (int i = 0, size = list.size(); i < size; i++) {

					targetNameList = Filter.getNameFromString(list.get(i)
							.getContent());
					if (targetNameList != null) {
						mention = addMention(list.get(i).getUserId(), mention,
								targetNameList, usernameAndIdMap);
					}
				}

			}

		}
	}

	public static Map<Long, Map<Long, Double>> addMention(Long sourceuserid,
			Map<Long, Map<Long, Double>> mention,
			ArrayList<String> targetNameList,
			HashMap<String, Long> usernameAndIdMap) {
		for (String name : targetNameList) {
			if (usernameAndIdMap.containsKey(name)) {
				long targetid = usernameAndIdMap.get(name);
				if (mention.containsKey(sourceuserid)) {
					if (mention.get(sourceuserid).containsKey(targetid)) {
						mention.get(sourceuserid).put(targetid,
								mention.get(sourceuserid).get(targetid) + 1);
					} else {
						mention.get(sourceuserid).put(targetid, 1.0);
					}
				} else {
					HashMap<Long, Double> tempHashMap = new HashMap<>();
					tempHashMap.put(targetid, 1.0);
					mention.put(sourceuserid, tempHashMap);
				}
			}
		}
		return mention;
	}

	public boolean ifInpath(long userid, List<OnePairTweet> subpath) {
		for (OnePairTweet onePairTweet : subpath) {
			if (onePairTweet.getUserId() == userid) {
				return true;
			}
		}

		return false;

	}

	public Map<Long, Map<Long, Double>> addretweet(Long sourceuserid,
			long retweeterid, Map<Long, Map<Long, Double>> retweet) {

		if (retweet.containsKey(retweeterid)) {
			if (retweet.get(retweeterid).containsKey(sourceuserid)) {
				retweet.get(retweeterid).put(sourceuserid,
						retweet.get(retweeterid).get(sourceuserid) + 1);
			} else {
				retweet.get(retweeterid).put(sourceuserid, 1.0);
			}
		} else {
			HashMap<Long, Double> tempHashMap = new HashMap<>();
			tempHashMap.put(retweeterid, 1.0);
			retweet.put(retweeterid, tempHashMap);
		}

		return retweet;
	}
}
