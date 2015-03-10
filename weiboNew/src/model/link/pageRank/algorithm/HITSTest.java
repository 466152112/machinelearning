package model.link.pageRank.algorithm;

import java.io.IOException;
import java.util.ArrayList;

import model.link.pageRank.tools.*;

public class HITSTest {
	public static void main(String[] args) throws IOException{
		/*
		 * ����Դ�����?
		 * ���������?
		 * �ѽ����ݱ��浽Result.txt��
		 */
		final String  sourceFile="data.txt";
		final String resultFile="result.txt";
		final int topsize=100;
		DataWriter dataWriter=new DataWriter(resultFile);
		
		//����Indegree�㷨
		ArrayList<String> HITSResult=new HITS().getAH(sourceFile);
		for (int i = 0; i <topsize; i++) {
			dataWriter.write(i+ "  "+HITSResult.get(i));
		}
		System.out.println("");
	}
	
}
