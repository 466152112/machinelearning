/**
 * 
 */
package tool.MyselfMath;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**   
*    
* @progject_name锛欱PR   
* @class_name锛歁atrixUtil   
* @class_describe锛�  
* @creator锛歾houge   
* @create_time锛�014骞�鏈�2鏃�涓嬪�?:00:35   
* @modifier锛歾houge   
* @modified_time锛�014骞�鏈�2鏃�涓嬪�?:00:35   
* @modified_note锛�  
* @version    
*    
*/
public class MatrixUtil{


	/**
	 * @param vectorA
	 * @param vectorB
	 * @return
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?00:38
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?:00:38
	  
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
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?00:38
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?:00:38
	  
	 */
	public static double[]  vectorElementMultiply(double[] vectorA,double[] vectorB){
		double[] result=new double[vectorA.length];
		if (vectorA.length!=vectorB.length) {
			System.out.println("the dimension must be the same in vectorAdd");
			System.exit(1);
		}
		for (int i = 0; i < vectorB.length; i++) {
			result[i]=vectorA[i]*vectorB[i];
		}
		return result;
	}
	/**
	 * @param vectorA
	 * @param vectorB
	 * @return
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?00:41
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?:00:41
	  
	 */
	public static double[] vectorminus(double[] vectorA,double[] vectorB){
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
	
	public static double[] getRandomVector(int columSize,double scope){
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		double[] result=new double[columSize];
		for (int i = 0; i < columSize; i++) {
				double randomNumber=random.nextDouble();
				while(randomNumber>scope){
					randomNumber=random.nextDouble();
				}
				result[i]=randomNumber;
		}
		return result;
	}
	/**
	 * @param vector
	 * @param fraction
	 * @return
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?00:44
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?:00:44
	  
	 */
	public static double[] vectorMultiplyByFraction(double[] vector,double fraction){
		double[] result=new double[vector.length];
		for (int i = 0; i < vector.length; i++) {
			result[i]=vector[i]*fraction;
		}
		return result;
	}

	/**
	 * @param vectorA
	 * @param vectorB
	 * @return
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?00:46
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?:00:46
	  
	 */
	public static double vectorMultiply(double[] vectorA,double[] vectorB){
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
	 * @param Matrix
	 * @param vectorB
	 * @return
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?00:46
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?:00:46
	  
	 */
	public static double[] vectorMultiplyMatrix(double[][] Matrix,double[] vectorB){
		double result[]=new double[vectorB.length];
		if (Matrix==null||Matrix[0].length!=vectorB.length) {
			System.out.println("Error in vectorMultiplyMatrix");
			System.exit(0);
		}
		for (int i = 0; i < vectorB.length; i++) {
			for (int j = 0; j < Matrix[0].length; j++) {
				result[i]+=Matrix[i][j]*vectorB[j];
				
				System.out.println(result[i]+"   "+Matrix[i][j]+"    "+vectorB[j]);
			}
		}
		return result;
	}
	
	/**
	 * @param matrix
	 * @param index
	 * @return
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?00:48
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?:00:48
	  
	 */
	public static double[] getVectorByColumnIndex(double[][] matrix,int index){
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
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?:26:30
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?1:26:30
	  
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
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�?:31:20
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪�?1:31:20
	  
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
	 * @param rowSize
	 * @param columSize
	 * @param scope 0-scope
	 * @return
	 *@create_time�?014�?�?7日上�?:43:53
	 *@modifie_time�?014�?�?7�?上午9:43:53
	  
	 */
	public static double[][] createdRandomMatrix(int rowSize,int columSize,double scope) {
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		double[][] result=new double[rowSize][columSize];
		
		for (int i = 0; i < rowSize; i++) {
			for (int j = 0; j < columSize; j++) {
				double randomNumber=random.nextDouble();
				while(randomNumber>scope){
					randomNumber=random.nextDouble();
				}
				result[i][j]=randomNumber;
			}
		}
		return result;
	}
	/**
	 * @param matrix
	 * @param rowIndex
	 * @return
	 *@create_time锛�014骞�鏈�3鏃ヤ笂鍗�?:21:18
	 *@modifie_time锛�014骞�鏈�3鏃�涓婂�?0:21:18
	  
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
	 *@create_time锛�014骞�鏈�4鏃ヤ笂鍗�?:03:28
	 *@modifie_time锛�014骞�鏈�4鏃�涓婂�?0:03:28
	  
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
