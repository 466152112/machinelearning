/**
 * 
 */
package preprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.FileUtil;
import util.ReadUtil;
import util.WriteUtil;

/**   
 *    
 * @progject_name：weiboNew   
 * @class_name：CalKLD   
 * @class_describe：   kl距离:以前一天为标准
 * @creator：zhouge   
 * @create_time：2014年9月27日 下午4:48:27   
 * @modifier：zhouge   
 * @modified_time：2014年9月27日 下午4:48:27   
 * @modified_note：   
 * @version    
 *    
 */
public class CalKLD {
	//String path = "/media/new2/data/wordsplit/";
	String path = "J:/workspace/weiboNew/data/topic/";
	String resultPath=path+"topicdistribution/";
	String everyDayTopic=resultPath + "everyDayTopic.txt";
	String KLD=resultPath + "KLD-overall.txt";
	public static void main(String[] args) {
		CalKLD CalKLD = new CalKLD();
		CalKLD.run();
	}

	public void run() {
		ReadUtil<String> readUtil=new ReadUtil<>();
		List<String> kldList=new ArrayList<>();
		List<String> topicList=readUtil.readFileByLine(everyDayTopic);
		List<List<Double>> distribution=new ArrayList<>();
		List<Double> all=new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			all.add(0.0);
		}
		for (String oneLine : topicList) {
			List<Double> oneday=new ArrayList<>();
			String[] split=oneLine.split(":");
			split=split[1].split(",");
			for (String onetopic : split) {
				if (!onetopic.trim().equals("")) {
					oneday.add(Double.valueOf(onetopic));
				}
			}
			distribution.add(oneday);
		}
		
		//covert to distribution
		for (int i = 0; i < distribution.size(); i++) {
			List<Double> oneday=distribution.get(i);
			List<Double> onedaydis=new ArrayList<>();
			double sum=0;
			for (int j = 0; j < oneday.size(); j++) {
				Double value=oneday.get(j);
				sum+=value;
				all.set(j, all.get(j)+value);
			}
			
			for (Double value : oneday) {
				onedaydis.add(value/sum);
			}
			distribution.set(i, onedaydis);
		}
		double sum=0;
		for (int i = 0; i < all.size(); i++) {
			sum+=all.get(i);
		}
		for (int i = 0; i < all.size(); i++) {
			all.set(i, all.get(i)/sum);
		}
		
		for (int i = 1; i < distribution.size(); i++) {
			double kld=calKL(distribution.get(i), all);
			kldList.add(String.valueOf(kld));
		}
		WriteUtil<String> writeUtil=new WriteUtil<>();
		writeUtil.writeList(kldList, KLD);
		System.out.println();
		
	}
	public double calKL(List<Double> onedis,List<Double> base){
		double sum=0;
		for (int i = 0; i < onedis.size(); i++) {
			sum+=onedis.get(i)*Math.log(onedis.get(i)/base.get(i));
		}
		return sum;
	}
}
