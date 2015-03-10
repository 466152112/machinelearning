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
public class shiwulianmodel {

	public static void main(String[] args) {
		shiwulianmodel aa = new shiwulianmodel();
		aa.getmatrix();
	}

	public void getmatrix() {
		String path = "H:/baiduyun/百度云同步盘/资料/复杂网络可视化大赛/淘宝物流/";
		String wuliu = path + "wuliu.csv";
		String wuliuresultName = path + "wuliumatrixmodel.txt";
//		String weibo = path + "weiboFlowSplitByProvince.csv";
//		String weiboresultName=path+"weibomatrixmodel.txt";
//		String test=path+"test.txt";
//		String testMatrix=path+"testMatrix.txt";
	//	model(path, test, testMatrix, ",");
		model(path, wuliu, wuliuresultName, ",");
		//model(path, weibo, weiboresultName, "\t");
	}
	private void model(String path,String sourceFile,String resultFile,String splitFlag){
		ReadUtil<String> readUtil = new ReadUtil<>();
		List<String> Listsource = readUtil.readfromFileByStream(sourceFile);
		// get the province
				Set<String> provinceset = new HashSet<>();
				for (String oneLine : Listsource) {
					String[] split = oneLine.split(splitFlag);
					if (split[0].equals("其他") || split[1].equals("其他")) {
						continue;
					}
					provinceset.add(split[0]);
					provinceset.add(split[1]);
				}
				List<String> provicelist=new ArrayList<>(provinceset);
				double[][] Matrix=new double[provinceset.size()][provinceset.size()+1];
				double total=0;
				for (String oneLine : Listsource) {
					String[] split = oneLine.split(splitFlag);
					if (split[0].equals("其他") || split[1].equals("其他")) {
						continue;
					}
					if (split[0].equals(split[1])) {
						continue;
					}
					total+=Double.valueOf(split[2]);
				}
				
				for (String oneLine : Listsource) {
					String[] split = oneLine.split(splitFlag);
					if (split[0].equals("其他") || split[1].equals("其他")) {
						continue;
					}
					if (split[0].equals(split[1])) {
						continue;
					}
//					String sourceId=split[0];
//					String targetId=split[1];
					String sourceId=split[1];
					String targetId=split[0];
					int number=Integer.valueOf(split[2]);
					double temp=number/total;
					
					
					Matrix[provicelist.indexOf(sourceId)][provicelist.indexOf(sourceId)]+=temp;
					Matrix[provicelist.indexOf(sourceId)][provicelist.indexOf(targetId)]-=temp;
					Matrix[provicelist.indexOf(sourceId)][provicelist.size()]+=temp;
					
					Matrix[provicelist.indexOf(targetId)][provicelist.indexOf(sourceId)]-=temp;
					Matrix[provicelist.indexOf(targetId)][provicelist.indexOf(targetId)]+=temp;
					Matrix[provicelist.indexOf(targetId)][provicelist.size()]-=temp;
				}
				
				
				WriteUtil<String> writeUti=new WriteUtil<>();
				writeUti.write2Vectorindouble(Matrix,resultFile);
				writeUti.writeList(provicelist, path+"provice.txt",false);
	}
}
