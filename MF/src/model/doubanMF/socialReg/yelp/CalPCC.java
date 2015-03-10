package model.doubanMF.socialReg.yelp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import util.ReadUser;
import util.ReadUtil;
import util.WriteUtil;

public class CalPCC {
	private static String path = "/home/zhouge/database/yelp/";
	private static String followGraphFileName = path + "social_network.txt";


	public static void main(String[] args) {
		CalPCC calPCC=new CalPCC();
		System.out.println("end");
		for (int i = 1; i <6; i++) {
			Map<Long, Map<Long, Double>> ratingMap=calPCC.ConvertToMatirxoneList(path+"train0.2_folder_"+i);
			calPCC.getFriendship(followGraphFileName, ratingMap,path+"socialPCC"+i);
		}
	}
	/**
	 * @param fileName
	 * @return
	 */
	public void getFriendship(
			String fileName,Map<Long, Map<Long, Double>> rating,String resultFile) {
		
		ReadUtil<String> readUtil = new ReadUtil();
		
		List<String> followGraph = readUtil.readFileByLine(fileName);
		
		Map<Long, Map<Long, Double>> allUser = new HashMap<>();
		// convert the arraylist followGraph into the hashmap
		for (String OneLine : followGraph) {
			String[] split = OneLine.split("\t");

			long friend2 = Long.valueOf(split[0].trim());
			long friend1 = Long.valueOf(split[1].trim());
			
			if (friend1 == friend2) {
				continue;
			}
			double value = PCC.getChangePCC(rating.get(friend1), rating.get(friend2));
			if (allUser.containsKey(friend1)) {
				allUser.get(friend1).put(friend2, value);
			} else {
				HashMap<Long, Double> friendlist = new HashMap();
				friendlist.put(friend2, value);
				allUser.put(friend1, friendlist);
			}

			if (allUser.containsKey(friend2)) {
				allUser.get(friend2).put(friend1, value);
			} else {
				HashMap<Long, Double> friendlist = new HashMap();
				friendlist.put(friend1, value);
				allUser.put(friend2, friendlist);
			}
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writemapkeyAndValueWhereValueinMap(allUser, resultFile);
	}
	
	/**
	 * @param fileNAme
	 * @return
	 */
	public Map<Long, Map<Long, Double>> ConvertToMatirxoneList(
			String fileNAme) {
		ReadUtil<String> readUtil = new ReadUtil();
		List<String> ratingList = readUtil.readFileByLine(fileNAme);
		Map<Long, Map<Long, Double>> ratingMatrix = new HashMap<>();
		for (String oneLine : ratingList) {
			String[] split = oneLine.split("\t");
			long UserId = Long.valueOf(split[0].trim());
			long ItemId = Long.valueOf(split[1].trim());
			double Rating = Double.valueOf(split[2].trim());
			if (ratingMatrix.containsKey(UserId)) {
				ratingMatrix.get(UserId).put(ItemId, Rating);
			} else {
				Map<Long, Double> oneratingMap = new HashMap<>();
				oneratingMap.put(ItemId, Rating);
				ratingMatrix.put(UserId, oneratingMap);
			}
		}
		return ratingMatrix;
	}
}
