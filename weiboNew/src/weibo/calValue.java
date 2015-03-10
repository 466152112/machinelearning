package weibo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import weibo.util.ReadUtil;
import weibo.util.WriteUtil;

public class calValue {
	
	/*
	 * ���췽��
	 * @param workPath��ǰ����Ŀ¼
	 * @param FollwGraphResult ��Щ�û���follow��ϵ��άͼ�ļ�
	 * @param sourceFile TF_IDFֵ���ļ�
	 * @param �������Ҫ��������ݼ�
	 */
	public calValue(String workPath, String FollwGraphResult,String sourceFile,DBCollection dbCollection) throws IOException {

		ReadUtil readUtil = new ReadUtil();
		ArrayList<String> sourceArrayList = null;
		ArrayList<String> follwArrayList=null;
		try {
			sourceArrayList = readUtil.readfromFile(sourceFile);
			follwArrayList=readUtil.readfromFile(FollwGraphResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ��ȡsourceFile
		double[][] TF_IDF = new double[sourceArrayList.size()][];
		
		int size = sourceArrayList.size();
		int termSize;
		
		for (int i = 0; i < size; i++) {
			String temp = sourceArrayList.get(i);
			String[] tempSplit = temp.split("\t");
			
			TF_IDF[i] = new double[tempSplit.length];
			for (int j = 0; j < tempSplit.length; j++) {

				TF_IDF[i][j] = Double.parseDouble(tempSplit[j]);
			}
		}
		termSize=TF_IDF[0].length;
		//��ȡfollwGraphResult
		int[][] follwGraph=new int[size][size];
		for(int i=0;i<size;i++){
			String temp=follwArrayList.get(i);
			String[] tempSplit=temp.split("\t");
			for(int j=0;j<size;j++){
				follwGraph[i][j]=Integer.parseInt(tempSplit[j]);
				
			}
		}
		Random random=new Random();
		random.setSeed(System.currentTimeMillis());
		
		int count=140000;
		File file=new File(workPath+"/cal.txt");
		BufferedWriter bufferedWriter=null;
		 bufferedWriter=new BufferedWriter(new FileWriter(file));
		for(int i=0;i<size;i++){
			for (int j = 0; j <size; j++) {
				if(i!=j){
					//����û�i follow �û�j ��������ǵ�ֵ �����������20%
					if ((follwGraph[i][j]==1)&&count>0) {
						count--;
						String temp="";
						temp=i+"\t"+j+"\t"+1;
						bufferedWriter.write(temp);
						for(int k=0;k<termSize;k++){
							bufferedWriter.write("\t"+this.getCalValue(TF_IDF[j][k], TF_IDF[i][k]));;
						}
					
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
				}
				
				
			}
		}
		for(int i=0;i<size;i++){
			for (int j = 0; j <size; j++) {
				if(i!=j){
					//����û�i follow �û�j ��������ǵ�ֵ �����������20%
					if ((follwGraph[i][j]!=1&&random.nextInt(100)>92)&&count>0) {
						count--;
						String temp="";
						temp=i+"\t"+j+"\t"+0;
						bufferedWriter.write(temp);
						for(int k=0;k<termSize;k++){
							bufferedWriter.write("\t"+this.getCalValue(TF_IDF[j][k], TF_IDF[i][k]));;
						}
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
				}
				
				
			}
		}
		
		System.out.println();
	}

	// �����ֵ
	static private double getCalValue(double x1, double x2) {
		return Math.exp(x1) / Math.exp(x2);
	}
}
