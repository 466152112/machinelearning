/**
 * 
 */
package preprocess._0808;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import bean.User;
import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name��weiboNew
 * @class_name��convertToMSType
 * @class_describe��
 * @creator��zhouge
 * @create_time��2014��8��8�� ����4:32:19
 * @modifier��zhouge
 * @modified_time��2014��8��8�� ����4:32:19
 * @modified_note��
 * @version
 * 
 */
public class convertToMSType {
	static List<String> userIdList;

	public static void main(String[] args) {
		String path = "J:/workspace/weiboNew/data/1k/";
		String userListFileName = path + "1kuserList.txt";
		String UserFeatureFileName = path + "1kuserFeature.txt";
		String followGraphFileName = path + "1kFollowGraph.txt";
		String train = path + "MSTrain.txt";
		String test = path + "MSTest.txt";
		ReadUtil readUtil=new ReadUtil();
		List<String> userIdList = readUtil.readFileByLine(userListFileName);
		Map<String, User> userSet = readUtil.readUser(userIdList, UserFeatureFileName);
		Map<String, Set<String>> followGraph = getFollowGraph(followGraphFileName);
		splitTrainAndTest(train, test, userSet, followGraph);
	}

	public static void splitTrainAndTest(String train, String test,
			Map<String, User> userSet, Map<String, Set<String>> followGraph) {
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());

		Iterator<String> userIterator = followGraph.keySet().iterator();
		int quarycount = 1;
		try (BufferedWriter trainWriter = new BufferedWriter(new FileWriter(
				train, true));
				BufferedWriter testWriter = new BufferedWriter(new FileWriter(
						test, true));) {
			while (userIterator.hasNext()) {
				String userId = userIterator.next();
				
				List<String> followers = new ArrayList<>(
						followGraph.get(userId));

				for (int i = 0; i < followers.size(); i++) {
					if (i < (int) (followers.size() * 0.8)) {
						Quary quary = new Quary(quarycount,
								userSet.get(userId), userSet.get(followers
										.get(i)), true);
						trainWriter.write(quary.toString());
						trainWriter.newLine();

					} else {
						Quary quary = new Quary(quarycount,
								userSet.get(userId), userSet.get(followers
										.get(i)), true);
						testWriter.write(quary.toString());
						testWriter.newLine();
					}
				}

				for (int i = 0; i < followers.size(); i++) {
					String randomuserId;
					while (true) {
						randomuserId = userIdList.get(random.nextInt(userIdList
								.size()));
						if (randomuserId != userId
								&& !followers.contains(randomuserId)) {
							break;
						}
					}

					if (i < (int) (followers.size() * 0.8)) {
						Quary quary = new Quary(quarycount,
								userSet.get(userId), userSet.get(randomuserId),
								false);
						trainWriter.write(quary.toString());
						trainWriter.newLine();
					} else {
						Quary quary = new Quary(quarycount,
								userSet.get(userId), userSet.get(randomuserId),
								false);
						testWriter.write(quary.toString());
						testWriter.newLine();
					}
				}
				System.out.println(quarycount);
				quarycount++;
			}

			trainWriter.flush();
			trainWriter.close();
			testWriter.flush();
			testWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param followGraphFileName
	 * @return
	 * @create_time��2014��8��8������6:03:22
	 * @modifie_time��2014��8��8�� ����6:03:22
	 */
	public static Map<String, Set<String>> getFollowGraph(
			String followGraphFileName) {
		Map<String, Set<String>> result = new HashMap<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
				followGraphFileName))) {
			String OneLine;

			while ((OneLine = bufferedReader.readLine()) != null) {
				String[] split = OneLine.split(",");
				String user1 = split[0].trim();
				String user2 = split[1].trim();
				if (result.containsKey(user1)) {
					result.get(user1).add(user2);
				} else {
					Set<String> temp = new HashSet<>();
					temp.add(user2);
					result.put(user1, temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
class Quary {
	int id;
	User user1;
	User user2;
	boolean flag;

	/**
	 * @param id
	 * @param user1
	 * @param user2
	 * @param flag
	 */
	public Quary(int id, User user1, User user2, boolean flag) {
		this.id = id;
		this.user1 = user1;
		this.user2 = user2;
		this.flag = flag;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the user1
	 */
	public User getUser1() {
		return user1;
	}

	/**
	 * @param user1
	 *            the user1 to set
	 */
	public void setUser1(User user1) {
		this.user1 = user1;
	}

	/**
	 * @return the user2
	 */
	public User getUser2() {
		return user2;
	}

	/**
	 * @param user2
	 *            the user2 to set
	 */
	public void setUser2(User user2) {
		this.user2 = user2;
	}

	/**
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String toString() {
		StringBuffer tempBuffer = new StringBuffer();
		if (isFlag())
			tempBuffer.append(1 + " ");
		else
			tempBuffer.append(0 + " ");

		tempBuffer.append("qid:" + getId() + " ");

		int featureSize = user1.getFeature().length;

		for (int i = 1; i <= featureSize; i++) {
			tempBuffer.append(i + ":" + user1.getFeature()[i-1] + " ");
		}
		for (int i = 1; i <= featureSize; i++) {
			tempBuffer.append((i + featureSize) + ":"
					+ user2.getFeature()[i-1] + " ");
		}

		return tempBuffer.toString().trim();
	}
}

