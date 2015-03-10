/**
 * 
 */
package flow.weibo.personmove;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.CalanderUtil;
import util.MapUtil;
import bean.AvlTree;
import bean.User;

/**
 * 
 * @progject_name锟斤拷weiboNew
 * @class_name锟斤拷base
 * @class_describe锟斤拷
 * @creator锟斤拷zhouge
 * @create_time锟斤拷2014锟斤拷10锟斤拷29锟斤拷 锟斤拷锟斤拷4:10:44
 * @modifier锟斤拷zhouge
 * @modified_time锟斤拷2014锟斤拷10锟斤拷29锟斤拷 锟斤拷锟斤拷4:10:44
 * @modified_note锟斤拷
 * @version
 * 
 */
public class BaseUtil {

	public AvlTree<MoveSequence> readUserAvlFromFile(String fileName) {
		AvlTree<MoveSequence> userAvlTree = new AvlTree<>();
		try (FileInputStream fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String oneline;
			while ((oneline = reader.readLine()) != null) {
				// 2150565240 42 3 锟斤拷锟斤拷 十锟斤拷
				// 锟矫伙拷锟斤拷锟节碉拷锟斤拷息 锟街段ｏ拷锟矫伙拷ID 锟斤拷省ID,
				// 锟斤拷ID锟斤拷1000锟斤拷锟斤拷蓿锟斤拷锟绞★拷锟�
				String[] split = oneline.split("\t");
				if (split.length == 4) {

					MoveSequence user = new MoveSequence();
					try {
						user.setUserId(split[0]);
					} catch (NumberFormatException e) {
						continue;
					}
					if (split[3].indexOf(" ") != -1) {
						String[] split1 = split[3].split(" ");
						user.setLocation(split1[0]);
					} else {
						if (!split[3].equals("锟斤拷锟斤拷")) {
							user.setLocation(split[3]);
						}
					}
					userAvlTree.insert(user);

				} else {
					// System.out.println(oneline);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userAvlTree;
	}

	public AvlTree<MoveSequence> ReadMoveSequanceFromFile(String fileName,
			AvlTree<MoveSequence> userAvl) {

		// 1000301920 0020 21 锟缴伙拷 2012-10-19 22:52:31 17702
		try (FileInputStream fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String oneline;
			while ((oneline = reader.readLine()) != null) {
				String[] split = oneline.split("\t");

				if (split.length != 4) {
					LocationTime locationTime = new LocationTime();
					locationTime.setCity(split[1]);
					locationTime.setTypeLocation(split[3]);
					Calendar calendar = CalanderUtil.getCalander(split[4],
							"yyyy-MM-dd HH:mm:ss");
					locationTime.setTime(calendar);
					MoveSequence temp = new MoveSequence();
					temp.setUserId(split[0]);
					if (userAvl.contains(temp)) {
						userAvl.getElement(temp, userAvl.root)
								.getLocationSequence().add(locationTime);
					} else {
						System.out.println("the person :" + split[0]
								+ " not in the tree");
					}

				} else {
					System.out.println(oneline);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userAvl;
	}

	public AvlTree<MoveSequence> ReadMoveSequanceFromFile(String fileName) {
		AvlTree<MoveSequence> userAvl = new AvlTree<>();
		// 1000301920 0020 21 锟缴伙拷 2012-10-19 22:52:31 17702
		try (FileInputStream fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String oneline;
			while ((oneline = reader.readLine()) != null) {
				// 2150565240 42 3 锟斤拷锟斤拷 十锟斤拷
				// 锟矫伙拷锟斤拷锟节碉拷锟斤拷息 锟街段ｏ拷锟矫伙拷ID 锟斤拷省ID,
				// 锟斤拷ID锟斤拷1000锟斤拷锟斤拷蓿锟斤拷锟绞★拷锟�
				String[] split = oneline.split("\t");

				if (split.length != 4) {
					LocationTime locationTime = new LocationTime();
					locationTime.setCity(split[1]);
					locationTime.setTypeLocation(split[3]);
					Calendar calendar = CalanderUtil.getCalander(split[4],
							"yyyy-MM-dd HH:mm:ss");
					locationTime.setTime(calendar);
					MoveSequence temp = new MoveSequence();
					temp.setUserId(split[0]);
					if (userAvl.contains(temp)) {
						userAvl.getElement(temp, userAvl.root)
								.getLocationSequence().add(locationTime);
					} else {
						temp.getLocationSequence().add(locationTime);
						userAvl.insert(temp);
					}

				} else {
					System.out.println(oneline);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userAvl;
	}

	public HashMap<String, String> ReadCityCodeFromFile(String fileName) {
		HashMap<String, String> codeAndCity = new HashMap<>();
		// 1000301920 0020 21 锟缴伙拷 2012-10-19 22:52:31 17702
		try (FileInputStream fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String oneline;
			while ((oneline = reader.readLine()) != null) {
				String[] split = oneline.split(":");
				codeAndCity.put(split[1], split[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeAndCity;
	}

	
	public HashMap<String, String> getCityAndprovinceFromBoth(HashMap<String, String> codeAndCity,String checkin_user_locationFile) {
		HashMap<String, String> CityAndprovince = new HashMap<>();
		Iterator<String> iterator=codeAndCity.keySet().iterator();
		HashMap<String, String> CityAndprovincesource=getCityAndprovinceFromcheckin_user_location(checkin_user_locationFile);
		
		while(iterator.hasNext()){
			String key=iterator.next();
			String cityname= codeAndCity.get(key);
			if (CityAndprovincesource.containsKey(cityname)) {
				CityAndprovince.put(cityname, CityAndprovincesource.get(cityname));
			}else {
				String province=MapUtil.getProvince(cityname);
				System.out.println(cityname+"\t"+ province);
				CityAndprovince.put(cityname, province);
			}
			
			
			
		}
		return CityAndprovince;
	}
	public HashMap<String, String> getCityAndprovinceFromFile(String File) {
		HashMap<String, String> CityAndprovince = new HashMap<>();
		try (FileInputStream fis = new FileInputStream(File);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String oneline;
			while ((oneline = reader.readLine()) != null) {
				String[] split1=oneline.split(",");
				try {
					CityAndprovince.put(split1[0], split1[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					System.out.println(oneline);
				}
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CityAndprovince;
	}
	public HashMap<String, String> getCityAndprovinceFromBaiduMap(HashMap<String, String> codeAndCity) {
		HashMap<String, String> CityAndprovince = new HashMap<>();
		Iterator<String> iterator=codeAndCity.keySet().iterator();
		while(iterator.hasNext()){
			String key=iterator.next();
			String cityname= codeAndCity.get(key);
			String province=MapUtil.getProvince(cityname);
			CityAndprovince.put(cityname, province);
		}
		return CityAndprovince;
	}
	public HashMap<String, String> getCityAndprovinceFromcheckin_user_location(String checkin_user_locationFile) {
		HashMap<String, String> CityAndprovince = new HashMap<>();
		try (FileInputStream fis = new FileInputStream(checkin_user_locationFile);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader reader = new BufferedReader(isr)) {
			String oneline;
			while ((oneline = reader.readLine()) != null) {
				// 2150565240 42 3 锟斤拷锟斤拷 十锟斤拷
				// 锟矫伙拷锟斤拷锟节碉拷锟斤拷息 锟街段ｏ拷锟矫伙拷ID 锟斤拷省ID,
				// 锟斤拷ID锟斤拷1000锟斤拷锟斤拷蓿锟斤拷锟绞★拷锟�
				String[] split = oneline.split("\t");
				if (split.length == 4) {

					MoveSequence user = new MoveSequence();
					try {
						user.setUserId(split[0]);
					} catch (NumberFormatException e) {
						continue;
					}
					if (split[3].indexOf(" ") != -1) {
						String[] split1 = split[3].split(" ");
						//2150565240	42	3	湖北 十堰
						CityAndprovince.put(split1[1], split1[0]);
					} 

				} else {
					// System.out.println(oneline);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CityAndprovince;
	}
}
