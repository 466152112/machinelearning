/**
 * 
 */
package flow.wuliu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.ReadUtil;
import util.WriteUtil;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：ConvertMatrix
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年10月24日 下午1:15:33
 * @modifier：zhouge
 * @modified_time：2014年10月24日 下午1:15:33
 * @modified_note：
 * @version
 * 
 */
public class ConvertMatrix {

	public static void main(String[] args) {
		ConvertMatrix aa = new ConvertMatrix();
		aa.getmatrix();
	}

	public void getmatrix() {
		String path = "H:/baiduyun/百度云同步盘/资料/复杂网络可视化大赛/淘宝物流/";
		String wuliu = path + "wuliu.csv";
		String weibo = path + "weiboFlowSplitByProvince.csv";
		String wuliuresultName = path + "wuliumatrix.txt";
		String weiboresultName = path + "weibomatrix.txt";
		String provinceName = path + "province.txt";

		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> wuliuList = readUtil.readfromFileByStream(wuliu);
		List<String> weiboList = readUtil.readfromFileByStream(weibo);

		// get the province
		Set<String> provinceset = new HashSet<>();
		for (String oneLine : weiboList) {
			String[] split = oneLine.split("\t");
			if (split[0].equals("其他") || split[1].equals("其他")) {
				continue;
			}
			provinceset.add(split[0]);
		}
		List<String> provicelist=new ArrayList<>(provinceset);
		double[][] wuliuMatrix=new double[provinceset.size()][provinceset.size()];
		double[][] weiboMatrix=new double[provinceset.size()][provinceset.size()];
		
		for (String oneLine : weiboList) {
			String[] split = oneLine.split("\t");
			if (split[0].equals("其他") || split[1].equals("其他")) {
				continue;
			}
			if (split[0].equals(split[1])) {
				continue;
			}
			
			int number=Integer.valueOf(split[2]);
			double temp=number;
			weiboMatrix[provicelist.indexOf(split[0])][provicelist.indexOf(split[1])]=temp;
		}
		
		
	
		for (String oneLine : wuliuList) {
			String[] split = oneLine.split(",");
			if (split[0].equals("其他") || split[1].equals("其他")) {
				continue;
			}
			if (split[0].equals(split[1])) {
				continue;
			}
			int number=Integer.valueOf(split[2]);
			double temp=number;
			wuliuMatrix[provicelist.indexOf(split[0])][provicelist.indexOf(split[1])]=temp;
			
		}
		
		WriteUtil<String> writeUti=new WriteUtil<>();
		writeUti.write2Vectorindouble(wuliuMatrix,wuliuresultName);
		writeUti.write2Vectorindouble(weiboMatrix,weiboresultName);
		
		writeUti.writeList(provicelist, provinceName,false);
	}
	
}
