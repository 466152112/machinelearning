

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import weibo.util.ReadUtil;

public class XiangChu {
	//String workPathString="E:/weibo/5K/";
	String workPathString="/home/zhouge/database/weibo/5K/";
	String trainDataPath=workPathString+"result/train/";
	String testDataPath=workPathString+"result/test/";
	public static void main(String[] args) throws IOException{
		XiangChu xiangChu=new XiangChu();
		//follow锟斤拷系图
		String followGraphFile=xiangChu.workPathString+"FollwGraphResult.txt";
		//svd锟斤拷锟�5000*
		String SVDResult=xiangChu.workPathString+"SVDResult.txt";
		
		BufferedReader svdReader=new BufferedReader(new FileReader(SVDResult));
		
		
		ArrayList<ArrayList<Integer>> followGraph=new ReadUtil().readArray2(followGraphFile);
		
		ArrayList<ArrayList<Double>> svdResult=new ReadUtil().readArray2Double(SVDResult);
		
		xiangChu.splitTrainAndTestSet(followGraph, svdResult);
		System.out.println();
	}
	
	private void splitTrainAndTestSet(ArrayList<ArrayList<Integer>> followGraph,ArrayList<ArrayList<Double>> svdResult){
		Random random=new Random(System.currentTimeMillis());
		int FollowUserCount=0;
		int numberOfPeople=followGraph.size();
		//锟斤拷锟饺硷拷锟斤拷锟斤拷follow锟斤拷系锟斤拷
		for (ArrayList<Integer> OneLinefollowGraph : followGraph) {
			/*
			 * 锟斤拷锟斤拷90%锟斤拷为训锟斤拷锟斤拷锟斤拷 10%锟斤拷为锟斤拷锟皆硷拷
			 */
			//统计该用户follow多少
			int followCount=0;
			for(int i=0,size=OneLinefollowGraph.size();i<size;i++){
				if (OneLinefollowGraph.get(i)==1) {
					followCount++;
				}
			}
			int noFollowCount=followCount;
			int trainCount=0;
			int testCount=0;
			for(int i=0,size=OneLinefollowGraph.size();i<size;i++){
				
				//锟斤拷锟斤拷锟絝ollow锟斤拷系锟揭革拷锟斤拷小锟斤拷90锟斤拷写锟斤拷训锟斤拷锟斤拷,注锟斤拷锟侥硷拷锟叫达拷1锟斤拷始锟斤拷锟斤拷
				if (OneLinefollowGraph.get(i)==1) {
					if (random.nextInt(100)<90) {
						calAndSaveFile("train", FollowUserCount, trainCount, true, svdResult);
						trainCount++;
					}else {
						calAndSaveFile("test", FollowUserCount, testCount, true, svdResult);
						testCount++;
					}
				}
			}
			
			for (int i = 0; i < numberOfPeople&&noFollowCount>0; i++) {
				int randomUser2=random.nextInt(numberOfPeople);
				if (FollowUserCount!=randomUser2) {
					if (OneLinefollowGraph.get(randomUser2)==0) {
						//锟斤拷锟斤拷锟絝ollow锟斤拷系锟揭革拷锟斤拷小锟斤拷90锟斤拷写锟斤拷训锟斤拷锟斤拷,注锟斤拷锟侥硷拷锟叫达拷1锟斤拷始锟斤拷锟斤拷
						if (random.nextInt(100)<90) {
							calAndSaveFile("train", FollowUserCount, trainCount, false, svdResult);
							trainCount++;
						}
						//锟斤拷锟斤拷锟絝ollow锟斤拷系锟揭革拷锟绞达拷锟斤拷90锟斤拷写锟斤拷锟斤拷约锟�
						else{
							calAndSaveFile("test", FollowUserCount, testCount, false, svdResult);
							testCount++;
						}
						noFollowCount--;
					}
				}
			}
			
			FollowUserCount++;
		}
		
		
	}
	//锟斤拷锟姐。锟斤拷锟窖硷拷锟斤拷锟斤拷娴斤拷募锟斤拷锟斤拷锟�
	private  void calAndSaveFile(String filePath,int user1,int user2,Boolean isFollow,ArrayList<ArrayList<Double>> svdResult){
		//写锟斤拷训锟斤拷锟斤拷
		if (filePath.equals("train")) {
			
			try {
				File feature_file=new File(trainDataPath+"/"+(user1+1)+"-feature.txt");
				
				BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(feature_file,true));
				int svdSize=svdResult.get(0).size();
				for(int i=0;i<svdSize;i++){
					bufferedWriter.write((user2+1)+"\t"+(i+1)+"\t");
					bufferedWriter.write(e(svdResult.get(user1).get(i),svdResult.get(user2).get(i)) );
					bufferedWriter.newLine();
				}
				bufferedWriter.flush();
				bufferedWriter.close();
				File lable_file=new File(trainDataPath+"/"+(user1+1)+"-lable.txt");
				BufferedWriter bufferedWriter1=new BufferedWriter(new FileWriter(lable_file,true));
				if (isFollow) {
					bufferedWriter1.write("1");
				}else {
					bufferedWriter1.write("0");
				}
				
				bufferedWriter1.newLine();
				bufferedWriter1.flush();
				bufferedWriter1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//写锟斤拷锟斤拷约锟�
		else {

			try {
				File feature_file=new File(testDataPath+"/"+(user1+1)+"-feature.txt");
				
				BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(feature_file,true));
				int svdSize=svdResult.get(0).size();
				for(int i=0;i<svdSize;i++){
					bufferedWriter.write((user2+1)+"\t"+(i+1)+"\t");
					bufferedWriter.write(e(svdResult.get(user1).get(i), svdResult.get(user2).get(i)));
					bufferedWriter.newLine();
				}
				bufferedWriter.flush();
				bufferedWriter.close();
				
				File lable_file=new File(testDataPath+"/"+(user1+1)+"-lable.txt");
				BufferedWriter bufferedWriter1=new BufferedWriter(new FileWriter(lable_file,true));
				if (isFollow) {
					bufferedWriter1.write("1");
				}else {
					bufferedWriter1.write("0");
				}
				bufferedWriter1.newLine();
				bufferedWriter1.flush();
				bufferedWriter1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static String  e(double e1,double e2){
		return String.valueOf(Math.exp(e1)/Math.exp(e2));
	}
	
}
