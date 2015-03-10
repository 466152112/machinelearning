package bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AllTermSet {
	//词最大的出现次数和最小的出现次数，如果出现多了就删除
	private final int maxValue=100000;
	private final int minValue=20;
	// 词汇表
	private HashMap<String, Integer> termAndAppearCount = new HashMap<>();
	// 每个词汇的IDF值
	private HashMap<String, Double> termAndIDF ;
	//词汇列表
//	private ArrayList<String> termList = new ArrayList<String>();
	private int totalDocument = 0;

	// 构造函数。传入总用户数
	public AllTermSet(int number) {
		this.totalDocument = number;
	}

	// 添加一组词汇的方法
	public void addTermList(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			this.addAnTerm(list.get(i));
		}
	}
	//添加一个词
	private void addAnTerm(String term) {
		// 首先判断该词汇是否已经包含了
		// 存在则加一
		if (this.termAndAppearCount.containsKey(term)) {
			this.termAndAppearCount.put(term,
					this.termAndAppearCount.get(term) + 1);
		} else {
			this.termAndAppearCount.put(term, 1);
		}
	}
//
//	// 获取IDF
//	public HashMap<String, Double> getIDF() {
//		//判断是否已经计算过
//		if (termAndIDF.size() == termAndAppearCount.size()) {
//			return termAndIDF;
//		} 
//		//重新计算
//		else {
//			Iterator<String> iterator = termAndAppearCount.keySet().iterator();
//			while (iterator.hasNext()) {
//				String key = (String) iterator.next();
//		
//				this.termAndIDF.put(
//						key,
//						Math.log(this.totalDocument
//								/ (1.0 * ( termAndAppearCount.get(key)))));
//				
//			}
//			return this.termAndIDF;
//		}
//	}
	// 获取IDF
	public double getIDF(String word) {
		//判断是否已经计算过
		if (this.termAndIDF==null) {
			Iterator<String> iterator = termAndAppearCount.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				int count=termAndAppearCount.get(key);
				if (maxValue>count&&count>minValue) {
					this.termAndIDF.put(
							key,
							Math.log(this.totalDocument
									/ (1.0 * ( termAndAppearCount.get(key)))));
				}
				
			}
		}
		if (this.termAndIDF.containsKey(word)) {
			return this.termAndIDF.get(word);
		}else {
			return 0;
		}
		
	}
	// 获取词汇集
//	public ArrayList<String> getTermList() {
//		// 判断是否全集
//		if (this.termList.size() != this.termAndAppearCount.size()) {
//			Iterator<String> iterator = this.termAndAppearCount.keySet()
//					.iterator();
//			this.termList = new ArrayList<String>();
//			while (iterator.hasNext()) {
//				String key = iterator.next();
//				this.termList.add(key);
//			}
//		}
//		return this.termList;
//	}
	
//	public HashMap<String, Integer> getTermIndex(){
//		HashMap<String, Integer> termIndexHashMap=new HashMap<>();
//			ArrayList<String> termArraylist1=this.getTermList();
//		for (int i = 0; i < termArraylist1.size(); i++) {
//			termIndexHashMap.put(termArraylist1.get(i), i+1);
//		}
//		return termIndexHashMap;
//	}
}
