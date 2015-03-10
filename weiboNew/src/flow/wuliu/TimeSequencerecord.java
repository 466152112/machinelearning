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
 * @progject_name：weiboNew
 * @class_name：Tongji
 * @class_describe�?
 * @creator：zhouge
 * @create_time�?014�?�?9�?上午11:24:24
 * @modifier：zhouge
 * @modified_time�?014�?�?9�?上午11:24:24
 * @modified_note�?
 * @version
 * 
 */
public class TimeSequencerecord {

	/**
	 * 
	 * @create_time�?014�?�?8日下�?:24:51
	 * @modifie_time�?014�?�?8�?下午7:24:51
	 */
	static String path = "/home/zhouge/database/taobao/";
	// static String path = "C:/Users/zhouge/Desktop/zike/网站/";

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
		// 购买数据
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
				// 新建再插�?
				user.action = 1;
				usertAvlTree.insert(user);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 浏览数据
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
	 * @progject_name：weiboNew
	 * @class_name：UserIdAndItemId
	 * @class_describe�?
	 * @creator：zhouge
	 * @create_time�?014�?�?3�?上午8:27:31
	 * @modifier：zhouge
	 * @modified_time�?014�?�?3�?上午8:27:31
	 * @modified_note�?
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
			// 如果时间相等 则比较用户id 和itemid
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
	 * @create_time�?014�?�?3日上�?0:16:49
	 * @modifie_time�?014�?�?3�?上午10:16:49
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
