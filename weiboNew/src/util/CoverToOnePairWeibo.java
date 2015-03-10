/**
 * 
 */
package util;

import java.util.concurrent.RecursiveTask;

import bean.OnePairTweet;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：CoverToOnePairWeibo
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月4日 下午2:21:27
 * @modifier：zhouge
 * @modified_time：2014年9月4日 下午2:21:27
 * @modified_note：
 * @version
 * 
 */
public class CoverToOnePairWeibo extends RecursiveTask<OnePairTweet> {
	private final String oneline;

	public CoverToOnePairWeibo(String oneline) {
		this.oneline = oneline;
	}

	private OnePairTweet covert(String oneLine) {

		String tempString = oneLine;
		OnePairTweet onePairTweet;
		long userId, weiboId, retweetid = 0L, retweetUserId = 0L;
		String content, time;
		try {
			weiboId = Long.valueOf(oneLine.substring(0, oneLine.indexOf("\t")));

			oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();

			userId = Long.valueOf(oneLine.substring(0, oneLine.indexOf("\t")));

			oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();
			try {
				// 查看是否能转为转发格式
				retweetid = Long.valueOf(oneLine.substring(0,
						oneLine.indexOf("\t")));

				oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();

				retweetUserId = Long.valueOf(oneLine.substring(0,
						oneLine.indexOf("\t")));

				oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();

				if (oneLine.indexOf("\t") == -1) {
					time = oneLine;
					content = "";
				} else {
					time = oneLine.substring(0, oneLine.indexOf("\t"));

					oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();

					content = oneLine.trim();
				}

			} catch (NumberFormatException e) {
				if (oneLine.indexOf("\t") == -1) {
					time = oneLine;
					content = "";
				} else {
					time = oneLine.substring(0, oneLine.indexOf("\t"));

					oneLine = oneLine.substring(oneLine.indexOf("\t")).trim();

					content = oneLine.trim();
				}

			}
			if (retweetid == 0L) {
				return new OnePairTweet(userId, weiboId, content, time);
			} else {
				return new OnePairTweet(userId, weiboId, content, retweetid,
						retweetUserId, time);
			}
		} catch (Exception e) {
			System.out.println(tempString);
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.RecursiveTask#compute()
	 */
	@Override
	protected OnePairTweet compute() {
		// TODO Auto-generated method stub
		return covert(this.oneline);
	}
}
