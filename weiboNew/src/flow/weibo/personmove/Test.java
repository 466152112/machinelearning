/**
 * 
 */
package flow.weibo.personmove;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import util.CalanderUtil;
import util.WriteUtil;
import bean.AvlTree;
import bean.User;
import bean.AvlTree.AvlNode;
import flow.MapTest;

/**
 * 
 * @progject_name锟斤拷weiboNew
 * @class_name锟斤拷Test
 * @class_describe锟斤拷
 * @creator锟斤拷zhouge
 * @create_time锟斤拷2014锟斤拷10锟斤拷29锟斤拷 锟斤拷锟斤拷4:50:01
 * @modifier锟斤拷zhouge
 * @modified_time锟斤拷2014锟斤拷10锟斤拷29锟斤拷 锟斤拷锟斤拷4:50:01
 * @modified_note锟斤拷
 * @version
 * 
 */
public class Test {
	static String path = "H:/baiduyun/百度云同步盘/资料/复杂网络可视化大赛/微博签到/";
	// static String path = "/home/zhouge/database/weibo/checkin/";
	static String userName = path + "checkin_user_location.txt";
	static String checkinName = path + "sortcheck.txt";
	static String citycodeFile = path + "city-codemap.txt";
	static String cityandProvincemapFile = path + "cityandprovicemap.txt";
	static Set<String> city = new HashSet<>();

	public static void main(String[] args) {
		Test test = new Test();
		BaseUtil beaBaseUtil = new BaseUtil();
		// AvlTree<MoveSequence>
		// moveAvlTree=beaBaseUtil.readUserAvlFromFile(userName);
		// AvlTree<MoveSequence> moveAvlTree = beaBaseUtil
		// .ReadMoveSequanceFromFile(checkinName);

		HashMap<String, String> codeandCitymap = beaBaseUtil
				.ReadCityCodeFromFile(citycodeFile);
		HashMap<String, String> CityAndprovince = beaBaseUtil
				.getCityAndprovinceFromFile(cityandProvincemapFile);
		test.statisticsMoveFromSourceToTargetCount(checkinName,
				CityAndprovince, codeandCitymap);
		System.out.println("end ");
	}

	/**
	 * @param moveAvlTree
	 * @create_time：2014年10月30日下午7:32:07
	 * @modifie_time：2014年10月30日 下午7:32:07
	 */
	public void statisticsMoveFromSourceToTargetCount(
			AvlTree<MoveSequence> moveAvlTree,
			HashMap<String, String> CityAndprovince,
			HashMap<String, String> codeandCitymap) {
		// 先序非递归遍历二叉树
		Stack<AvlNode<MoveSequence>> stack = new Stack<>();
		AvlNode<MoveSequence> puser = moveAvlTree.root;
		HashMap<String, Integer> moveCount = new HashMap<>();
		while (puser != null || !stack.isEmpty()) {
			while (puser != null) {
				List<LocationTime> locationTimes = puser.getElement()
						.getSortLocation();
				// 只有大于1的时候才有移动轨迹
				if (locationTimes.size() > 1) {
					String lastPosition = CityAndprovince.get(codeandCitymap
							.get(locationTimes.get(0).getCity()));
					if (lastPosition == null) {
						System.out.println(locationTimes.get(0).getCity());
					}

					for (int i = 1; i < locationTimes.size(); i++) {
						if (locationTimes.get(i).getCity().equals(lastPosition)) {
							continue;
						} else {
							String targetPro = CityAndprovince
									.get(codeandCitymap.get(locationTimes
											.get(i).getCity()));
							if (targetPro == null) {
								System.out.println(locationTimes.get(i)
										.getCity());
							}
							String lastPositionToNew = lastPosition + "\t"
									+ targetPro;
							if (moveCount.containsKey(lastPositionToNew)) {
								moveCount.put(lastPositionToNew,
										moveCount.get(lastPositionToNew) + 1);
							} else {
								moveCount.put(lastPositionToNew, 1);
							}
							lastPosition = targetPro;
						}
					}
				}
				stack.push(puser);
				puser = puser.left;
			}
			if (!stack.isEmpty()) {
				puser = stack.pop();
				puser = puser.right;
			}
		}
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeMapKeyAndValuesplitbyt(moveCount, path
				+ "moveFromSourceToTargetCount.txt");
	}

	public void statisticsMoveFromSourceToTargetCount(String checkinName,
			HashMap<String, String> CityAndprovince,
			HashMap<String, String> codeandCitymap) {
		// 先序非递归遍历二叉树
		
		Calendar earlyTime=null,LastTime=null;
		HashMap<String, Integer> moveCount = new HashMap<>();
		try (FileInputStream fis = new FileInputStream(checkinName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String oneline;
			MoveSequence premove = new MoveSequence();
			while ((oneline = reader.readLine()) != null) {
				if(oneline.indexOf("null")!=-1){
					continue;
				}
				String[] split = oneline.split("\t");

				if (split.length != 4) {
					LocationTime locationTime = new LocationTime();
					locationTime.setCity(split[1]);
					locationTime.setTypeLocation(split[3]);
					Calendar calendar = CalanderUtil.getCalander(split[4],
							"yyyy-MM-dd HH:mm:ss");
					if(earlyTime==null){
						earlyTime=calendar;
						LastTime=calendar;
					}else {
						if(earlyTime.compareTo(calendar)<0)
							earlyTime=calendar;
						if(LastTime.compareTo(calendar)>0)
							LastTime=calendar;
						
					}
					locationTime.setTime(calendar);
					MoveSequence temp = new MoveSequence();
					temp.setUserId(split[0]);
					if (premove.compareTo(temp) == 0) {
						premove.getLocationSequence().add(locationTime);
					} else {
						moveCount = statisticsMoveFromSourceToTargetCount(
								premove.getLocationSequence(), CityAndprovince,
								codeandCitymap, moveCount);
						temp.getLocationSequence().add(locationTime);
						premove = temp;
					}
				} else {
					System.out.println(oneline);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(earlyTime.getTime());
		System.out.println(LastTime.getTime());
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeMapKeyAndValuesplitbyt(moveCount, path
				+ "moveFromSourceToTargetCount.txt");
	}

	public HashMap<String, Integer> statisticsMoveFromSourceToTargetCount(
			List<LocationTime> locationTimes,
			HashMap<String, String> CityAndprovince,
			HashMap<String, String> codeandCitymap,
			HashMap<String, Integer> moveCount) {
		// 只有大于1的时候才有移动轨迹
		if (locationTimes.size() > 1) {
			String lastPosition = CityAndprovince.get(codeandCitymap
					.get(locationTimes.get(0).getCity()));
			int beginIndex = 0;

			while (lastPosition == null
					&& beginIndex < locationTimes.size() - 1) {
				beginIndex++;
				lastPosition = CityAndprovince.get(codeandCitymap
						.get(locationTimes.get(beginIndex).getCity()));

				//System.out.println(locationTimes.get(0).getCity());

				if (lastPosition != null) {
					break;
				}
			}
			for (int i = beginIndex + 1; i < locationTimes.size(); i++) {
				String targetPro = CityAndprovince.get(codeandCitymap
						.get(locationTimes.get(i).getCity()));
				if (targetPro == null) {
					//System.out.println(locationTimes.get(i).getCity());
					continue;
				}
				if (targetPro.equals(lastPosition)) {
					continue;
				} else {
					String lastPositionToNew = lastPosition + "\t" + targetPro;
					if (moveCount.containsKey(lastPositionToNew)) {
						moveCount.put(lastPositionToNew,
								moveCount.get(lastPositionToNew) + 1);
					} else {
						moveCount.put(lastPositionToNew, 1);
					}
					lastPosition = targetPro;
				}
			}
		}
		return moveCount;
	}

	/**
	 * @param moveAvlTree
	 * @create_time：2014年10月30日下午7:32:07
	 * @modifie_time：2014年10月30日 下午7:32:07
	 */
	public void statisticsMoveFromSourceToTargetCount(
			AvlTree<MoveSequence> moveAvlTree) {
		// 先序非递归遍历二叉树
		Stack<AvlNode<MoveSequence>> stack = new Stack<>();
		AvlNode<MoveSequence> puser = moveAvlTree.root;
		HashMap<String, Integer> moveCount = new HashMap<>();
		while (puser != null || !stack.isEmpty()) {
			while (puser != null) {
				List<LocationTime> locationTimes = puser.getElement()
						.getSortLocation();
				// 只有大于1的时候才有移动轨迹
				if (locationTimes.size() > 1) {
					String lastPosition = locationTimes.get(0).getCity();
					for (int i = 1; i < locationTimes.size(); i++) {
						if (locationTimes.get(i).getCity().equals(lastPosition)) {
							continue;
						} else {
							String lastPositionToNew = lastPosition + "\t"
									+ locationTimes.get(i).getCity();
							if (moveCount.containsKey(lastPositionToNew)) {
								moveCount.put(lastPositionToNew,
										moveCount.get(lastPositionToNew) + 1);
							} else {
								moveCount.put(lastPositionToNew, 1);
							}
							lastPosition = locationTimes.get(i).getCity();
						}
					}
				}
				stack.push(puser);
				puser = puser.left;
			}
			if (!stack.isEmpty()) {
				puser = stack.pop();
				puser = puser.right;
			}
		}
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeMapKeyAndValuesplitbyt(moveCount, path
				+ "moveFromSourceToTargetCount.txt");
	}
}
