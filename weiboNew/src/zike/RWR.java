/**
 * 
 */
package zike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ReadUtil;
import util.WriteUtil;
import util.Matrix.Matrix;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：zike
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年9月1日 下午8:46:52
 * @modifier：zhouge
 * @modified_time：2014年9月1日 下午8:46:52
 * @modified_note：
 * @version
 * 
 */
public class RWR {
	private static String path = "J:/workspacedata/weiboNew/data/zike/";
	private static String sourceFile = path + "data.txt";
	private static String distanceFile = path + "distance103.txt";
	//private final static double lamdaOfKatz = 0.01;
	private final static double lamdaOfRWR = 0.85;
	
	public static void main(String[] args) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Matrix sourceGraphMatrix = readMatrix();
		Matrix distanceMatrix=readDistanceMatrix();
		RWR katzRWR=new RWR();
		//katzRWR.Katz(sourceGraphMatrix,distanceMatrix);
		katzRWR.RWR(sourceGraphMatrix,distanceMatrix);
	}

	/**
	 * @param sourceGraphMatrix 连接矩阵
	 * @param distanceMatrix	距离矩阵，表示节点之间的距离
	 *@create_time：2015年1月6日下午6:47:34
	 *@modifie_time：2015年1月6日 下午6:47:34
	  
	 */
	public  void RWR(Matrix sourceGraphMatrix,Matrix distanceMatrix) {

		int Dim = sourceGraphMatrix.getColumnDimension();
		// 计算出度
		double[] outDegree = new double[Dim];
		for (int row = 0; row < Dim; row++) {
			for (int column = 0; column < Dim; column++) {
				if (sourceGraphMatrix.get(row, column) == 1.0 && row != column)
					outDegree[row] += 1;
			}
		}
		Matrix transferMatrix = new Matrix(Dim, Dim, 0.0);
		for (int row = 0; row < Dim; row++) {
			for (int column = 0; column < Dim; column++) {
				if (outDegree[row]!=0) {
					transferMatrix.set(row, column, sourceGraphMatrix.get(row, column)/outDegree[row]);
				}else {
					transferMatrix.set(row, column, 0);
				}
			}
		}
		Matrix IMatrix = Matrix.identity(Dim, Dim);
		
		// sim = (1 - lambda) * inv(I- lambda * train') * I;
		Matrix simMatrix =IMatrix.minus((transferMatrix.transpose()).times(lamdaOfRWR));
		simMatrix=simMatrix.inverse();
		simMatrix=simMatrix.times(1-lamdaOfRWR).times(IMatrix);
		
		simMatrix = simMatrix.plus(simMatrix.transpose());
		//第一步进行预测，把前面得到的前100条边加入到 链接矩阵中
		saveRWRByloop(Dim, sourceGraphMatrix, 0.5,10, distanceMatrix, simMatrix);
	
//		
//		for (double i = 0; i < 10; i++) {
//			double paramter=i*0.001;
//			saveRWR(Dim, sourceGraphMatrix, paramter, distanceMatrix, simMatrix);
//		}
//		for (int i = 1; i < 10; i++) {
//			double paramter=i*0.01;
//			saveRWR(Dim, sourceGraphMatrix, paramter, distanceMatrix, simMatrix);
//		}
//		for (int i = 1; i <= 10; i++) {
//			double paramter=i*0.1;
//			saveRWR(Dim, sourceGraphMatrix, paramter, distanceMatrix, simMatrix);
//		}
//		for (int i = 1; i <= 10; i++) {
//			double paramter=i;
//			saveRWR(Dim, sourceGraphMatrix, paramter, distanceMatrix, simMatrix);
//		}
//		for (int i = 1; i <= 10; i++) {
//			double paramter=i*10;
//			saveRWR(Dim, sourceGraphMatrix, paramter, distanceMatrix, simMatrix);
//		}
	}
	public  void saveRWR(int Dim ,Matrix sourceGraphMatrix,double paramter,int numberofLoop,Matrix distanceMatrix,Matrix simMatrix){
		// save the result
		Map<String, Double> edgeValue = new HashMap<>();
		java.text.DecimalFormat df=new java.text.DecimalFormat("#.###");
		for (int row = 0; row < Dim; row++) {
			for (int column = 0; column < Dim; column++) {
				if (sourceGraphMatrix.get(row, column) ==0 && row != column) {
					String EdgeName = (column + 1) + "\t" + (row + 1);
					edgeValue.put(EdgeName, Math.exp(-1*paramter*distanceMatrix.get(row, column))*simMatrix.get(row, column));
				}
			}
		}

		// sortByValue
		List<String> edgeList = new ArrayList<>(edgeValue.keySet());
		TwoValueComparator tvc = new TwoValueComparator(edgeValue);
		Collections.sort(edgeList, tvc);
		List<String> sub = edgeList.subList(0, 6000);
		List<String> result = new ArrayList<>();
		for (String edgeName : sub) {
			result.add(edgeName + "\t" + edgeValue.get(edgeName));
		}
		WriteUtil<String> writeUtil = new WriteUtil<>();
		writeUtil.writeList(result, path+"RWR/"+df.format(paramter)+"/"+(10-numberofLoop)+"_"+".txt",false);
	}
	
	public  void saveRWRByloop(int Dim ,Matrix sourceGraphMatrix,double paramter,int numbeofloop,Matrix distanceMatrix,Matrix simMatrix){
		// save the result
		Map<String, Double> edgeValue = new HashMap<>();
		//java.text.DecimalFormat df=new java.text.DecimalFormat("#.###");
		for (int row = 0; row < Dim; row++) {
			for (int column = 0; column < Dim; column++) {
				if (sourceGraphMatrix.get(row, column) ==0.0 && row != column) {
					String EdgeName = row + "\t" + column;
					edgeValue.put(EdgeName, Math.exp(-1*paramter*distanceMatrix.get(row, column))*simMatrix.get(row, column));
				}
			}
		}

		// sortByValue
		List<String> edgeList = new ArrayList<>(edgeValue.keySet());
		TwoValueComparator tvc = new TwoValueComparator(edgeValue);
		Collections.sort(edgeList, tvc);
		int count=0;
		for (String edgeName : edgeList) {
			String[] split=edgeName.split("\t");
			int row=Integer.valueOf(split[0]);
			int column=Integer.valueOf(split[1]);
			if (sourceGraphMatrix.get(row, column)==0) {
				
				sourceGraphMatrix.set(row, column, 1);
				count++;
				if (count>=100) {
					break;
				}
			}
		}
		double[] outDegree = new double[Dim];
		for (int row = 0; row < Dim; row++) {
			for (int column = 0; column < Dim; column++) {
				if (sourceGraphMatrix.get(row, column) == 1.0 && row != column)
					outDegree[row] += 1;
			}
		}
		Matrix transferMatrix = new Matrix(Dim, Dim, 0.0);
		for (int row = 0; row < Dim; row++) {
			for (int column = 0; column < Dim; column++) {
				if (outDegree[row]!=0) {
					transferMatrix.set(row, column, sourceGraphMatrix.get(row, column)/outDegree[row]);
				}else {
					transferMatrix.set(row, column, 0);
				}
			}
		}
		Matrix IMatrix = Matrix.identity(Dim, Dim);
		// sim = (1 - lambda) * inv(I- lambda * train') * I;
		simMatrix =IMatrix.minus((transferMatrix.transpose()).times(lamdaOfRWR));
		simMatrix=simMatrix.inverse();
		simMatrix=simMatrix.times(1-lamdaOfRWR).times(IMatrix);
		
		simMatrix = simMatrix.plus(simMatrix.transpose());
		
		//再次进行循环
		if (numbeofloop>0) {
			numbeofloop--;
			//先保存结果
			saveRWR(Dim, sourceGraphMatrix, paramter,numbeofloop, distanceMatrix, simMatrix);
			saveRWRByloop(Dim, sourceGraphMatrix, paramter,numbeofloop, distanceMatrix, simMatrix);
		}
		
		
		
	}
	

	/**
	 * 从文件中读取矩阵。注意文件中数据是从1开始，数组和matrix 是从0开始
	 * 
	 * @return Matrix
	 * @create_time：2014年9月1日下午8:57:45
	 * @modifie_time：2014年9月1日 下午8:57:45
	 */
	public static Matrix readMatrix() {
		double[][] array = new double[103][103];
		ReadUtil<String> dataRead = new ReadUtil<>();
		List<String> dataList = dataRead.readFileByLine(sourceFile);
		for (String oneLine : dataList) {
			String[] split = oneLine.split("\t");
			int to = Integer.valueOf(split[0]) - 1;
			int from = Integer.valueOf(split[1]) - 1;
			array[from][to] = 1;
		}
		Matrix result = new Matrix(array);
		return result;
	}
	
	/**
	 * 从文件中读取矩阵。注意文件中数据是从1开始，数组和matrix 是从0开始
	 * 
	 * @return Matrix
	 * @create_time：2014年9月1日下午8:57:45
	 * @modifie_time：2014年9月1日 下午8:57:45
	 */
	public static Matrix readDistanceMatrix() {
		double[][] array = new double[103][103];
		ReadUtil<String> dataRead = new ReadUtil<>();
		List<String> dataList = dataRead.readFileByLine(distanceFile);
		for (int i = 0; i < dataList.size(); i++) {
			String[] split=dataList.get(i).split("\t");
			for (int j = 0; j < split.length; j++) {
				array[i][j]=Double.valueOf(split[j]);
				//array[i][j]=1;
			}
		}
		Matrix result = new Matrix(array);
		return result;
	}
	

 class TwoValueComparator implements Comparator<String> {

	Map<String, Double> base_map;
   //标记 0,降序 1升序
    int flag=0;

    /**
     * @param value
     * @param flag 标记 0,降序 1升序。默认是降序
     */
    public TwoValueComparator(Map<String, Double> value,int flag) {
        this.base_map = value;
        this.flag=flag;
    }
    public TwoValueComparator(Map<String, Double> base_map) {
        this.base_map = base_map;
    }
	public int compare(String arg0, String arg1) {

        if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
            return 0;
        }

        if (base_map.get(arg0)<base_map.get(arg1)) {
        	if(flag==1)
            return -1;
        	else 
				return 1;
        } else if (base_map.get(arg0)==base_map.get(arg1)) {

            return 0;
        } else {
        	if (flag==1) 
        		 return 1;
        	else 
				 return -1;
        }

    }
	
}
}
