package model.link.pageRank.algorithm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import model.link.pageRank.tools.*;

public class Indegree {
	
	/* ��ȡԴ��ݣ�����ݽڵ���ȴ�С��������?
	 * @param fileName Դ�����?
	 */
	public ArrayList<String> getTop(String fileName) throws IOException{
		
		HashMap<String, HashSet<String>> nodehHashMap=new DataReader(fileName).getBackEdge();
		// ������ֵ������ ���õ��ź����nodeAndIndegree
		HashMap<String, Double> nodeAndIndegree=new HashMap<String, Double>();
		Iterator<String> iterator=nodehHashMap.keySet().iterator();
		while(iterator.hasNext()){
			String key=iterator.next();
			nodeAndIndegree.put(key, (double)nodehHashMap.get(key).size());
		}
		ByValueComparator bvc = new ByValueComparator(nodeAndIndegree);
		List<String> keys = new ArrayList<String>(
				nodeAndIndegree.keySet());
		Collections.sort(keys, bvc);
		
		
		// �õ��ź�����Ƽ��б�?recList
		ArrayList<String> recList = new ArrayList<String>();
		for (String key : keys) {
			recList.add(key+"("+nodeAndIndegree.get(key)+")");
			
			
		}
		return recList;
	}
}
