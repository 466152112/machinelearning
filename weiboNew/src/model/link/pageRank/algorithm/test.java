package model.link.pageRank.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import model.link.pageRank.tools.*;

import java.util.ArrayList;


public class test {
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
		ArrayList<String> indegreeResult=new Indegree().getTop(sourceFile);
		ArrayList<String> PRResult=new PR().getPageRank(sourceFile);
		ArrayList<String> HITSResult=new HITS().getAH(sourceFile);
		for (int i = 0; i <=topsize; i++) {
			dataWriter.write(i+" ,"+indegreeResult.get(i)+",\t"+PRResult.get(i)+",\t"+HITSResult.get(i));
		}
		System.out.println("");
	}
	
}
