/**
 * 
 */
package MF.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**   
*    
* @progject_name：BPR   
* @class_name：MatrixUtil   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年7月22日 下午8:00:35   
* @modifier：zhouge   
* @modified_time：2014年7月22日 下午8:00:35   
* @modified_note：   
* @version    
*    
*/
public class MatrixUtil{


	/**
	 * @param vectorA
	 * @param vectorB
	 * @return
	 *@create_time：2014年7月22日下午8:00:38
	 *@modifie_time：2014年7月22日 下午8:00:38
	  
	 */
	public static double[]  vectorAdd(double[] vectorA,double[] vectorB){
		double[] result=new double[vectorA.length];
		if (vectorA.length!=vectorB.length) {
			System.out.println("the dimension must be the same in vectorAdd");
			System.exit(1);
		}
		for (int i = 0; i < vectorB.length; i++) {
			result[i]=vectorA[i]+vectorB[i];
		}
		return result;
	}
	

	/**
	 * @param vectorA
	 * @param vectorB
	 * @return
	 *@create_time：2014年7月22日下午8:00:41
	 *@modifie_time：2014年7月22日 下午8:00:41
	  
	 */
	public static double[] vectorMin(double[] vectorA,double[] vectorB){
		double[] result=new double[vectorA.length];
		if (vectorA.length!=vectorB.length) {
			System.out.println("the dimension must be the same in vectorminus");
			System.exit(1);
		}
		for (int i = 0; i < vectorB.length; i++) {
			result[i]=vectorA[i]-vectorB[i];
		}
		return result;
	}

	/**
	 * @param vector
	 * @param fraction
	 * @return
	 *@create_time：2014年7月22日下午8:00:44
	 *@modifie_time：2014年7月22日 下午8:00:44
	  
	 */
	public static double[] vectorscale(double[] vector,double fraction){
		double[] result=new double[vector.length];
		for (int i = 0; i < vector.length; i++) {
			result[i]=vector[i]*fraction;
		}
		return result;
	}

	/**
	 * @param vectorA
	 * @param vectorB
	 * @return vectorA*vectorB
	 *@create_time：2014年7月22日下午8:00:46
	 *@modifie_time：2014年7月22日 下午8:00:46
	  
	 */
	public static double vectorMul(double[] vectorA,double[] vectorB){
		double result=0;
		if (vectorA.length!=vectorB.length) {
			System.out.println("the dimension must be the same in vectorAdd");
			System.exit(1);
		}
		for (int i = 0; i < vectorB.length; i++) {
			result+=vectorA[i]*vectorB[i];
			
		}
		return result;
	}
	

	/**
	 * @param matrix
	 * @param index
	 * @return
	 *@create_time：2014年7月22日下午8:00:48
	 *@modifie_time：2014年7月22日 下午8:00:48
	  
	 */
	public static double[] getVectorByCol(double[][] matrix,int index){
		double[] result;
		int rowNumber=0;
		int columnNumber=0;
		if (matrix==null) {
			System.out.println("the matrix is null in getVectorByColumnIndex");
			System.exit(1);
		}else if (matrix[0].length<index-1&&index<0) {
			System.out.println("out of bound in getVectorByColumnIndex");
			System.exit(1);
		}
		rowNumber=matrix.length;
		columnNumber=matrix[0].length;
		result=new double[rowNumber];
		for (int i = 0; i < rowNumber; i++) {
			for (int j = 0; j < columnNumber; j++) {
				if (j==index) {
					result[i]=matrix[i][j];
				}
			}
		}
		return result;
	}
	/**
	 * @param map
	 * @param rowList
	 * @param columList
	 * @return put the map to matrix
	 *@create_time：2014年7月22日下午11:26:30
	 *@modifie_time：2014年7月22日 下午11:26:30
	  
	 */
	public static double[][] convertFromMapToMatrix(Map<Integer, List<Integer>> map,List<Integer> rowList,List<Integer> columList){
		double[][] result=new double[rowList.size()][columList.size()];
		for (Integer oneRowIndex : rowList) {
			List<Integer> oneRow= map.get(oneRowIndex);
			int indexOfrow=rowList.indexOf(oneRowIndex);
			for (Integer columIndex : oneRow) {
				result[indexOfrow][columList.indexOf(columIndex)]=1;
			}
		}
		return result;
	}
	
	/**
	 * @param rowSize
	 * @param columSize
	 * @return created an Random matrix that element is double [0,1)
	 *@create_time：2014年7月22日下午11:31:20
	 *@modifie_time：2014年7月22日 下午11:31:20
	  
	 */
	public static double[][] createdRandomMatrix(int rowSize,int columSize) {
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		double[][] result=new double[rowSize][columSize];
		for (int i = 0; i < rowSize; i++) {
			for (int j = 0; j < columSize; j++) {
				result[i][j]=random.nextDouble();
			}
		}
		return result;
	}

	/**
	 * @param matrix
	 * @param rowIndex
	 * @return
	 *@create_time：2014年7月23日上午10:21:18
	 *@modifie_time：2014年7月23日 上午10:21:18
	  
	 */
	public static double[][] updateMatrixByRowIndex(double[][] matrix,int rowIndex,double[] updateVector) {
		int columsize=matrix[0].length;
		if (columsize!=updateVector.length) {
			System.out.println("there are error in updateMatrixByRowIndex:the columSize is not equal");
			System.exit(0);
		}
		for (int i = 0; i < columsize; i++) {
			matrix[rowIndex][i]+=updateVector[i];
		}
		return matrix;
	}
	
	/**
	 * @param vector
	 * @return elementAbsoluteAdd
	 *@create_time：2014年7月24日上午10:03:28
	 *@modifie_time：2014年7月24日 上午10:03:28
	  
	 */
	public static double elementAbsoluteAdd(double[] vector){
		int vectorLength=vector.length;
		double result=0;
		for (int i = 0; i < vectorLength; i++) {
			result+=Math.abs(vector[i]);
		}
		return result;
	}
}
