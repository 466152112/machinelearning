/**
 * 
 */
package flow.wuliu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import bean.AvlTree;
import bean.AvlTree.AvlNode;
import util.CalanderUtil;
import util.WriteUtil;

/**
 * 
 * @progject_nameï¼šweiboNew
 * @class_nameï¼šTongji
 * @class_describeï¼?
 * @creatorï¼šzhouge
 * @create_timeï¼?014å¹?æœ?9æ—?ä¸Šåˆ11:24:24
 * @modifierï¼šzhouge
 * @modified_timeï¼?014å¹?æœ?9æ—?ä¸Šåˆ11:24:24
 * @modified_noteï¼?
 * @version
 * 
 */
public class TimeSequencerecord {

	/**
	 * 
	 * @create_timeï¼?014å¹?æœ?8æ—¥ä¸‹å?:24:51
	 * @modifie_timeï¼?014å¹?æœ?8æ—?ä¸‹åˆ7:24:51
	 */
	static String path = "/home/zhouge/database/taobao/";
	// static String path = "C:/Users/zhouge/Desktop/zike/ç½‘ç«™/";

	static String buyAction = path + "t_yf_purchase_detail_gl.txt";
	static String browseAction = path + "t_yf_browse_detail_gl.txt";
	static String resultFileName = path + "result.txt";
	List<String> result = new ArrayList<>();

	public static void main(String[] args) {
		TimeSequencerecord aa = new TimeSequencerecord();
		aa.Run();
	}

	public void Run() {
		AvlTree<UserIdAndItemId> usertAvlTree = new AvlTree();
		final String buySimpleDateFormat = "yyyy-mm-dd HH:mm:ss";
		// 2011-10-22 16:37:37

		final String browseSimpleDateFormat = "yyyymmddHHmmss";
		// 20111006225357
		// è´­ä¹°æ•°æ®
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(buyAction)));) {
			String oneLine;
			int count = 0;
			while ((oneLine = bufferedReader.readLine()) != null) {
				String[] split = oneLine.split("\t");
				UserIdAndItemId user = new UserIdAndItemId();
				user.userId = split[0];
				user.itemid = split[2];
				Date date = CalanderUtil.getDate(split[1], buySimpleDateFormat);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				user.timeCalendar=calendar;
				// æ–°å»ºå†æ’å…?
				user.action = 1;
				usertAvlTree.insert(user);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// æµè§ˆæ•°æ®
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				new File(browseAction)));) {
			String oneLine;
			int count = 0;
			while ((oneLine = bufferedReader.readLine()) != null) {
				String[] split = oneLine.split("\t");
				if (split.length != 3) {
					continue;
				}
				UserIdAndItemId user = new UserIdAndItemId();
				user.userId = split[0];
				user.itemid = split[2];
				Date date = CalanderUtil.getDate(split[1],
						browseSimpleDateFormat);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				user.action = 0;
				user.timeCalendar=calendar;
				usertAvlTree.insert(user);

				count++;
				if (count > 20302134) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		writeToFileInthis(usertAvlTree.root);
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeList(result, resultFileName);
		result = new ArrayList<>();
	}

	/**
	 * 
	 * @progject_nameï¼šweiboNew
	 * @class_nameï¼šUserIdAndItemId
	 * @class_describeï¼?
	 * @creatorï¼šzhouge
	 * @create_timeï¼?014å¹?æœ?3æ—?ä¸Šåˆ8:27:31
	 * @modifierï¼šzhouge
	 * @modified_timeï¼?014å¹?æœ?3æ—?ä¸Šåˆ8:27:31
	 * @modified_noteï¼?
	 * @version
	 * 
	 */
	private class UserIdAndItemId implements Comparable<UserIdAndItemId> {
		String userId;
		String itemid;
		Calendar timeCalendar;
		int action;

		@Override
		public int compareTo(UserIdAndItemId o) {
			// TODO Auto-generated method stub
			if (this.timeCalendar.before(o.timeCalendar)) {
				return -1;
			} else if (this.timeCalendar.after(o.timeCalendar)) {
				return 1;
			}
			// å¦‚æœæ—¶é—´ç›¸ç­‰ åˆ™æ¯”è¾ƒç”¨æˆ·id å’Œitemid
			else {
			
				if (this.userId.compareTo(o.userId) > 0) {
					return 1;
				} else if (this.userId.compareTo(o.userId) < 0) {
					return -1;
				} else {
					if (this.itemid.compareTo(o.itemid) > 0) {
						return 1;
					} else if (this.itemid.compareTo(o.itemid) < 0) {
						return -1;
					} else {
						return 0;
					}
				}

			}

		}

	}
	/**
	 * @param root
	 * @create_timeï¼?014å¹?æœ?3æ—¥ä¸Šå?0:16:49
	 * @modifie_timeï¼?014å¹?æœ?3æ—?ä¸Šåˆ10:16:49
	 */
	@SuppressWarnings("unused")
	private void writeToFileInthis(AvlNode<UserIdAndItemId> t) {
		if (t != null) {
			writeToFileInthis(t.getLeft());
			UserIdAndItemId user = t.getElement();
			result.add(user.userId + "\t" + user.itemid + "\t"
					+ user.action);
		//	System.out.println(user.timeCalendar.toString());
			if (result.size() > 1000000) {
				WriteUtil<String> writeUtil = new WriteUtil<>();
				writeUtil.writeList(result, resultFileName);
				result = new ArrayList<>();
			}
			writeToFileInthis(t.getRight());
		}

	}
}
