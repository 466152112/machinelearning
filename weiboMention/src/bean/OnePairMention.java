/**
 * 
 */
package bean;

/**
 * 
 * @progject_name：weiboMention
 * @class_name：OnePairMention
 * @class_describe：
 * @creator：zhouge
 * @create_time：2015年1月19日 下午2:49:22
 * @modifier：zhouge
 * @modified_time：2015年1月19日 下午2:49:22
 * @modified_note：
 * @version
 * 
 */
public class OnePairMention extends OnePairTweet {

	/**
	 * @param oneLine
	 * @return
	 * @create_time：2015年1月19日下午2:53:41
	 * @modifie_time：2015年1月19日 下午2:53:41
	 */
	public static OnePairTweet covert(String oneLine,boolean LoadContent) {

		String tempString = oneLine;
		OnePairTweet onePairTweet;
		long userId, weiboId, retweetid = 0L, retweetUserId = 0L;
		String content, time;
		// 3397189691149797 1768027614 Mon Jan 02 00:50:02 +0800 2012 0
		// 1644908952 @Baby-G莹 @毕星star @蠢猪啤啤 @陈晓迪迪迪 @当薯条
		try {
			weiboId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));

			tempString = tempString.substring(tempString.indexOf("\t")).trim();

			userId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));
			tempString = tempString.substring(tempString.indexOf("\t")).trim();

			time = tempString.substring(0, tempString.indexOf("\t"));
			tempString = tempString.substring(tempString.indexOf("\t")).trim();

			retweetid = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));
			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			retweetUserId = Long.valueOf(tempString.substring(0,
					tempString.indexOf("\t")));

			tempString = tempString.substring(tempString.indexOf("\t")).trim();
			if (LoadContent) {
				if (tempString.indexOf("\t") == -1) {
					time = tempString;
					content = "";
				} else {

					tempString = tempString.substring(tempString.indexOf("\t"))
							.trim();
					content = tempString.trim();
				}
				return new OnePairTweet(userId, weiboId, content, retweetid,
						retweetUserId, time);
			}else {
				return new OnePairTweet(userId, weiboId, "", retweetid,
						retweetUserId, time);
			}
			
			
		} catch (Exception e) {
			return null;
		}
	}
	
}
