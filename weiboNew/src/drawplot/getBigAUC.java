/**
 * 
 */
package drawplot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import flow.MapTest;
import util.MapUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：getBigAUC   
 * @class_describe�?  
 * @creator：zhouge   
 * @create_time�?014�?�?9�?下午8:33:14   
 * @modifier：zhouge   
 * @modified_time�?014�?�?9�?下午8:33:14   
 * @modified_note�?  
 * @version    
 *    
 */
public class getBigAUC {

	/**
	 * 
	 * @create_time�?014�?�?8日下�?:24:51
	 * @modifie_time�?014�?�?8�?下午7:24:51
	 */
	// static String path =
	// "/media/new3/dataset/data/taobao_competition_2013/f_trading_rec/";
	static String path = "/home/xiaoqiang/twitter/edgeauc/";
	

	public static void main(String[] args) {
		getBigAUC aa = new getBigAUC();
		aa.Run();
	}

	public void Run() {
		
		WriteUtil<String> writeUtil = new WriteUtil<>();
		long position=-1;
	
		List<String> temp=new ArrayList<>();
		//保存前五的AUC以及对于的�?
		double[] topKAUC=new double[5];
		//保存前五的AUC
		long[] topKUser=new long[5];
		
		List<List<String>> topKValue=new ArrayList<>();
		for (int i = 0; i < topKAUC.length; i++) {
			List<String> tempList=new ArrayList<>();
			topKValue.add(tempList);
		}
		int count=0;
		for (int i = 1; i <=11; i++) {
			//0	0	1	0.6666666666666666
			//0	0	2	0.6363636363636364
			String fileName=path+"edgeAndAuc_"+i+"_10000000.txt";
			
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(fileName)));) {
				String oneLine;
				while((oneLine=bufferedReader.readLine())!=null){
					String[] split=oneLine.split("\t");
					long follower=Long.valueOf(split[1]);
					
					if (follower!=position) {
						//writeUtil.writeList(temp, path+"/zhouge/zhouge"+position+".txt");
						if (count>50) {
							//找到插入�?
							double auc=getAUC(temp);
							int minAUCIndex=getMinPosition(topKAUC);
							double minAUC=topKAUC[minAUCIndex];
							if (auc>minAUC) {
								topKAUC[minAUCIndex]=auc;
								topKUser[minAUCIndex]=position;
								topKValue.set(minAUCIndex, temp);
							}
						}
						count=0;
						temp=new ArrayList<>();
						position=follower;
					}else {
						if(Integer.valueOf(split[0]).equals(1)){
							count++;	
						}
						temp.add(oneLine);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
	
		}
		//输出结果
		for (int i = 0; i < topKUser.length; i++) {
			System.out.println(topKUser[i]+"\t"+i);
			writeUtil.writeList(topKValue.get(i), path+"zhouge/zhouge"+topKUser[i]+".txt");
		}
		
	}
	
	public int getMinPosition(double[] sort){
		double min=Double.MAX_VALUE;
		int indexOfmin=0;
		for (int i=0; i < sort.length; i++) {
			if(sort[i]<min){
				min=sort[i];
				indexOfmin=i;
			}
		}
		
		return indexOfmin;
	}
	/**
	 * @param temp
	 * @return 计算对于的AUC�?
	 *@create_time�?014�?�?1日下�?:31:14
	 *@modifie_time�?014�?�?1�?下午7:31:14
	  
	 */
	public double getAUC(List<String> temp){
		Map<String, Double> IDAndValue=new HashMap<>();
		Set<String> PositiveID=new HashSet();
		for (String oneline : temp) {
			String[] split=oneline.split("\t");
			IDAndValue.put(split[2], Double.valueOf(split[3]));
			if(Integer.valueOf(split[0]).equals(1)){
				PositiveID.add(split[2]);
			}
		}
		int total = 0;
		int positiveTotal = 0;
		int EqualTotal = 0;
		List<String> userList = new ArrayList<>(IDAndValue.keySet());
		for (String positiveUser : PositiveID) {
			double value=IDAndValue.get(positiveUser);
			for (String compareID : userList) {
				if (PositiveID.contains(compareID)) {
					continue;
				}else {
					if (value > IDAndValue.get(compareID)) {
						positiveTotal++;
					} else if (value == IDAndValue.get(compareID)) {
						EqualTotal++;
					}
					total++;
				}
			}
		}
		return ((1.0) * (positiveTotal + EqualTotal * 0.5)) / total;
	}

}
