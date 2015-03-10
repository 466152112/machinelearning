/**
 * 
 */
package tool.drawplot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import tool.FileTool.ReadUtil;
import tool.FileTool.WriteUtil;

/**
 * 
 * @progject_name��weiboNew
 * @class_name��sampleEdge
 * @class_describe��
 * @creator��zhouge
 * @create_time��2014��9��23�� ����8:13:12
 * @modifier��zhouge
 * @modified_time��2014��9��23�� ����8:13:12
 * @modified_note��
 * @version
 * 
 */
public class sampleEdge {

	static String path = "J:/workspace/twitter/data/twitter/plot/top5/";
	String TKIFileName = path + "zhouge4009.txt";
	String idmapFile = path + "idmap.txt";
	String followGraph = path + "followgraph.txt";

	public static void main(String[] args) {
		sampleEdge aa = new sampleEdge();
		aa.Run();
	}

	public void Run() {
		ReadUtil<String> readUtil = new ReadUtil<>();

		// read the idmap file
		List<String> tempList = readUtil.readFileByLine(idmapFile);
		HashMap<Long, Long> idMap = new HashMap<>();
		for (String oneline : tempList) {
			String[] split = oneline.split("\t");
			idMap.put(Long.valueOf(split[1]), Long.valueOf(split[0]));
		}

		// read followmap
		tempList = readUtil.readFileByLine(followGraph);
		Map<Long, List<Long>> followMap = new HashMap<>();
		for (String oneline : tempList) {
			String[] split = oneline.split(",");
			long follower = Long.valueOf(split[0]);
			long followee = Long.valueOf(split[1]);
			if (followMap.containsKey(follower)) {
				followMap.get(follower).add(followee);
			} else {
				List<Long> followlist = new ArrayList<>();
				followlist.add(followee);
				followMap.put(follower, followlist);
			}
		}
		// read the TKI file
		tempList = readUtil.readFileByLine(TKIFileName);
		// ����ȡ20������ ��10������
		HashMap<Long, Double> noExistEdge = new HashMap<>();
		HashMap<Long, Double> existEdge = new HashMap<>();

		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		long sourceId = 4009;
		sourceId += 1;
		sourceId = idMap.get(sourceId);
		for (String oneline : tempList) {
			String[] split = oneline.split("\t");
			int label = Integer.valueOf(split[0]);

			// random the noExistEdge
			if (label == 0) {
				double tki = Double.valueOf(split[3]);
				if (noExistEdge.size() < 10) {
					if (random.nextDouble() < 0.05) {
						long id = Long.valueOf(split[2]) + 1;
						noExistEdge.put(idMap.get(id), tki);
						saveInformation(followMap, idMap.get(id), sourceId,
								false,true, tki);
					} else if (noExistEdge.size() < 10 && tki > 4000) {
						long id = Long.valueOf(split[2]) + 1;
						noExistEdge.put(idMap.get(id), tki);
						saveInformation(followMap, idMap.get(id), sourceId,
								false, false,tki);
					}
				}

			}

			// random the existEdge
			if (label == 1) {
				double tki = Double.valueOf(split[3]);
				if (existEdge.size() < 4) {
					if (random.nextDouble() < 0.15) {
						long id = Long.valueOf(split[2]) + 1;
						existEdge.put(idMap.get(id), tki);
						saveInformation(followMap, idMap.get(id), sourceId,
								true, true,tki);
					} else if (existEdge.size() < 10 && tki < 1000) {
						long id = Long.valueOf(split[2]) + 1;
						noExistEdge.put(idMap.get(id), tki);
						saveInformation(followMap, idMap.get(id), sourceId,
								true,false, tki);
					}
				}

			}

		}

	}

	public void saveInformation(Map<Long, List<Long>> followMap, Long targetid,
			long sourceId, boolean flag,boolean normal, double TKI) {

		List<String> needfollowship = new ArrayList<>();
		// ����ڵ��Լ�����Ϣ
		Map<Long, Integer> node = new HashMap<>();
		List<Long> followee = followMap.get(sourceId);
		for (Long node01 : followee) {
			// neighbor node color is 3
			node.put(node01, 3);
			needfollowship.add(sourceId + "," + node01);
			List<Long> node01followee = followMap.get(node01);
			if (node01followee == null) {
				continue;
			}
			if (node01followee.contains(sourceId)) {
				needfollowship.add(node01 + "," + sourceId);
			}
			if (node01followee.contains(targetid)) {
				needfollowship.add(node01 + "," + targetid);
			}
		}
		followee = followMap.get(targetid);
		for (Long node01 : followee) {
			node.put(node01, 3);
			needfollowship.add(targetid + "," + node01);
			List<Long> node01followee = followMap.get(node01);
			if (node01followee == null) {
				continue;
			}
			if (node01followee.contains(sourceId)) {
				needfollowship.add(node01 + "," + sourceId);
			}
			if (node01followee.contains(targetid)) {
				needfollowship.add(node01 + "," + targetid);
			}
		}
		// ȥ��
		Set<String> sort = new HashSet<>(needfollowship);
		needfollowship = new ArrayList<>(sort);
		List<String> endship = new ArrayList<>();
		endship.add("source,target");
		endship.addAll(needfollowship);

		if (flag) {
			// existEdgecolor is 1
			node.put(targetid, 1);
		} else {
			// noexistEdgecolor is 2
			node.put(targetid, 2);
		}
		// source node color is 0
		node.put(sourceId, 0);
		WriteUtil<String> writeUtil = new WriteUtil<String>();
		File file = new File(path + "/" + sourceId);
		if (!file.exists()) {
			file.mkdirs();
		}
		String fileName = path + sourceId + "/" + targetid;
		writeUtil.writeList(endship, fileName + "needfollowship.csv");
		String edgeInformation = null;
		if (flag) {
			edgeInformation = "" + TKI;
			if (normal) {
				writeUtil.writeOneLine(edgeInformation, fileName + "exist-normal.txt");
			} else {
				writeUtil.writeOneLine(edgeInformation, fileName + "exist-nonormal.txt");
			}
		} else {
			edgeInformation = "" + TKI;
			if (normal) {
				writeUtil.writeOneLine(edgeInformation, fileName + "noexist-normal.txt");
			} else {
				writeUtil.writeOneLine(edgeInformation, fileName + "noexist-nonormal.txt");
			}
		}
		edgeInformation = "Id,color";
		writeUtil.writeOneLine(edgeInformation, fileName + "node.csv");
		writeUtil.writeMapKeyAndValue(node, fileName + "node.csv");
	}

}
