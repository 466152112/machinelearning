package LTR.plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import super_utils.io.FileIO;

/**   
 *    
 * @progject_name：LTR   
 * @class_name：convertData   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月22日 下午2:27:53   
 * @modifier：zhouge   
 * @modified_time：2015年1月22日 下午2:27:53   
 * @modified_note：   
 * @version    
 *    
 */
public class convertData {
	static String path="D:/Program Files/onedrive/资料/实验/mention/alg_result/LTR/v5/result_0.8/";
	static String plotpath=path+"plotdata/";
	public static void main(String[] args) throws Exception {
		convertData convertData=new convertData();
		String[] resultName={"Full_feature.txt","No_User_Profile.txt","No_Weibo_Content.txt","No_Social_Tie.txt","No_Social_Status.txt",
				"Bonds_Based.txt","Content_Based.txt","Influence_Based.txt","random.txt"};
		List<List<String>> allresultContent=new ArrayList<>();
		for (String fileName : resultName) {
			allresultContent.add(convertData.readContent(path+fileName));
		}
		//读取map
		List<String> Mapresult=new ArrayList<>();
		for (List<String> list : allresultContent) {
			double map=convertData.CrawlingMetricValue("MAP", list);
			Mapresult.add(map+"");
		}
		FileIO.writeList(plotpath+"MAP.txt", Mapresult);
		
		String[] MetricName={"P","RR","ERR","NDCG"};
		for (String name : MetricName) {
			convertData.KValue(name,allresultContent);
		}
		
	}
	public void KValue(String MetricName,List<List<String>> allresultContent){

		//读取 AP
		List<String> Result=new ArrayList<>();
		for (int k = 1; k < 11; k++) {
			String temp=k+"\t";
			for (List<String> list : allresultContent) {
				double value=CrawlingMetricValue(MetricName+"@"+k, list);
				temp+=value+"\t";
			}
			temp.trim();
			Result.add(temp);
		}
		try {
			FileIO.writeList(plotpath+MetricName+".txt", Result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<String> readContent(String fileName){
		try {
			List<String> temp=FileIO.readAsList(fileName);
			int index=temp.indexOf("MAP");
			temp=temp.subList(index, temp.size());
			return temp;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public double CrawlingMetricValue(String MetricName,List<String> contentList){
			
			for (int i = 0; i < contentList.size(); i++) {
				if (contentList.get(i).indexOf(MetricName)!=-1) {
					int position=0;
					String[] spilt=contentList.get(i).split(" | ");
					for (int j = 0; j < spilt.length; j++) {
						if (spilt[j].trim().equals(MetricName)) {
							position=j;
						}
					}
					 spilt=contentList.get(i+1).split(" | ");
					 return Double.valueOf(spilt[position]);
				}
			}
			System.out.println("no find");
			return 0;
	}
}
