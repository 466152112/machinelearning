package bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OneUserTermSet {
	// 用户词汇汇总表。
	private HashMap<String, Integer> userTermSet = new HashMap<String, Integer>();
	// 用户词汇
	private HashMap<String, Double> TF = new HashMap<String, Double>();
	private ArrayList<String> termList=new ArrayList<String>();
	// 用户总词汇个数
	private int allCount = 0;

	// 添加一组词汇的方法
	public void addTermList(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			this.addAnTerm(list.get(i));
		}
	}
	
	// 添加一个词汇的方法
	private void addAnTerm(String term) {
		// 首先判断该词汇是否已经包含了
		// 存在则加一
		if (this.userTermSet.containsKey(term)) {
			this.userTermSet.put(term, this.userTermSet.get(term) + 1);
		} else {
			this.userTermSet.put(term, 1);
		}
		this.allCount++;
	}

	// 获取用户的词汇的TF值
	public HashMap<String, Double> getTF() {
		
		// 判断是否计算每个词汇的TF值
			if (this.TF.size() == this.userTermSet.size()) {
					return this.TF;
			} else {
				// 遍历userTermSet计算TF
				Iterator<String> iterator = this.userTermSet.keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					this.TF.put(key, this.userTermSet.get(key) / (1.0 * this.allCount));
				}
				return this.TF;
			}
		
	}
	//获取词汇集方法
	public ArrayList<String> getTermList(){
		//判断是否全集
		if (this.termList.size()!=this.userTermSet.size()) {
			Iterator<String> iterator=this.userTermSet.keySet().iterator();
			this.termList=new ArrayList<String>();
			while(iterator.hasNext()){
				String key=iterator.next();
				this.termList.add(key);
			}
		}
		return this.termList;
	}
	
	public HashMap<String, Integer> getuserTermSetAndNumber(){
		return this.userTermSet;
	}

}
