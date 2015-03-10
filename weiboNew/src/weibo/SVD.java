package weibo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import Jama.Matrix;

public class SVD {

	final static String workPath = "E:/weibo/5k";

	public static void main(String[] args) {
		// create M-by-N matrix that doesn't have full rank

		File fileU = new File(workPath + "/svd/u.txt");
		File fileS = new File(workPath + "/svd/s.txt");
		File fileResult=new File(workPath+"/SVDResult.txt");
		BufferedReader bufferedReader = null;
		Matrix U = null;
		Matrix S = null;
	
		try {
			bufferedReader = new BufferedReader(new FileReader(fileU));
			U = Matrix.read(bufferedReader);
			bufferedReader = new BufferedReader(new FileReader(fileS));
			S = Matrix.read(bufferedReader);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double AllCount = 0;
		double partOf = 0;
		for (int i = 0; i < S.getRowDimension(); i++) {
			AllCount += S.get(i, i);
		}
		int needDimension = 0;
		for (int j = 0; j < S.getRowDimension(); j++) {
			partOf += S.get(j, j);
			if (partOf / AllCount > 0.98) {
				needDimension = j;
				break;
			}
		}
		
		Matrix result = U.getMatrix(0, U.getRowDimension()-1, 0, needDimension)
				.times(S.getMatrix(0, needDimension, 0, needDimension));
		
		//result.print(12,10);
		try {
			BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(fileResult));
			for(int i=0;i<result.getRowDimension();i++){
				for (int j = 0; j < result.getColumnDimension(); j++) {
					bufferedWriter.write(String.valueOf(result.get(i, j)));
					bufferedWriter.write("\t");
				}
				bufferedWriter.write("\n");
			}
				
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * // compute the singular vallue decomposition
		 * System.out.println("A = U S V^T"); System.out.println();
		 * SingularValueDecomposition s = A.svd(); System.out.print("U = ");
		 * System.out.print("Sigma = "); S.print(9, 6);
		 * System.out.print("V = "); System.out.println("rank = " + s.rank());
		 * System.out.println("condition number = " + s.cond());
		 * System.out.println("2-norm = " + s.norm2());
		 */
		// print out singular values
		// System.out.print("singular values = ");
		// Matrix svalues = new Matrix(s.getSingularValues(), 1);
		// svalues.print(9, 6);

	}

}