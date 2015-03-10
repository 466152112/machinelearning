package weibo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class ReadUtil <T > {
	/*
	 * ��һ���ļ��ж�ȡ�����
	 * @param path  �ļ���� 
	 * @return ����ļ��������
	 */
	public ArrayList<String> readfromFile(String path) throws IOException{
		ArrayList<String> result=new ArrayList<String>();
		File file=new File(path);
		BufferedReader bufferedReader = null;
	
		try {
			 bufferedReader=new BufferedReader(new FileReader(file));
			 String temp=bufferedReader.readLine();
			 while(temp!=null){
				 result.add(temp);
				 temp=bufferedReader.readLine();
			 }
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("read file failure");
			e.printStackTrace();
		}finally{
			bufferedReader.close();
		}
		
		return result;
	}
	/*
	 * ��һ���ļ�����ʽ��ȡ���
	 * @param path  �ļ���� ,format ��ȡ�ļ�����
	 * @return ����ļ��������
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<Integer>> readArray2(String fileName){
		ArrayList<ArrayList<Integer>> result=new ArrayList<>();
		BufferedReader bufferedReader=null;
		
		try{
			bufferedReader=new BufferedReader(new FileReader(new File(fileName)));
			
			String oneLine=bufferedReader.readLine();
			
			while(oneLine!=null){
				String[] oneLineSplit=oneLine.split("\t");
				ArrayList<Integer> oneResult=new ArrayList<Integer>(oneLineSplit.length);
				for(int i=0,length=oneLineSplit.length;i<length;i++){
					oneResult.add(Integer.parseInt(oneLineSplit[i]));
					}
				oneLine=bufferedReader.readLine();
				result.add(oneResult);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	public ArrayList<ArrayList<Double>> readArray2Double(String fileName){
		ArrayList<ArrayList<Double>> result=new ArrayList<>();
		BufferedReader bufferedReader=null;
		
		try{
			bufferedReader=new BufferedReader(new FileReader(new File(fileName)));
			
			String oneLine=bufferedReader.readLine();
			
			while(oneLine!=null){
				String[] oneLineSplit=oneLine.split("\t");
				ArrayList<Double> oneResult=new ArrayList<Double>(oneLineSplit.length);
				for(int i=0,length=oneLineSplit.length;i<length;i++){
					
					oneResult.add(Double.parseDouble(oneLineSplit[i]));
					}
				oneLine=bufferedReader.readLine();
				result.add(oneResult);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	public ArrayList<String> readfromFileByStream(String path,String encodingFormat) throws IOException{
		ArrayList<String> result=new ArrayList<String>();
		BufferedReader bufferedReader = null;
		try {
			  FileInputStream fis = new FileInputStream(path); 
		        InputStreamReader isr = new InputStreamReader(fis, encodingFormat); 
		         bufferedReader = new BufferedReader(isr); 
		        String temp=bufferedReader.readLine();
			 while(temp!=null){
				 result.add(temp);
				 temp=bufferedReader.readLine();
			 }
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("read file failure");
			e.printStackTrace();
		}finally{
			bufferedReader.close();
		}
		
		return result;	
		
	}
	/*
	 * ��ȡk���û���΢����Ϣ
	 * @param Arraylist<string> userIdlist ��Ҫ��ȡ���û�id����
	 * @param String path ��Ҫ��ȡ���ļ���
	 * @return ���ļ����û�id���϶�Ӧ��΢��
	 */
	public ArrayList<String> getKWeiboList(ArrayList<String> userIdList,String path){
		//���صĽ�����
		ArrayList<String> result=new ArrayList<String>();
		//�м���ݽ���
		ArrayList<String>  temp=null;
		try{
			//��ȡ�ļ�ȫ�����
			temp=readfromFile(path);
			if (temp!=null) {
				int weiboNumber=temp.size();
				for(int i=0;i<weiboNumber&&temp.get(i)!=null;i++){
					//��userId�е�΢�������
					String[] stringTemp=temp.get(i).split("\t");
					if ((stringTemp.length==3||stringTemp.length==4)&&userIdList.contains(stringTemp[0])) {
						result.add(temp.get(i));
					}
				}
			}
		}catch(IOException e){
			System.out.println();
		}
		//��ȡ�û�id��΢��
		return result;
	}
}
