package weibo.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TxtToCSV {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		for (int i = 0; i < 8; i++) {
			ArrayList<String> result=new ArrayList<String>();
			File file=new File("/home/zhouge/database/qq/"+i);
			
		//读取文件
			try {
				BufferedReader bufferedReader=null;
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
			}
			
			//写入文件
			File file1 = new File("/home/zhouge/database/qq/result0-7.csv");
			BufferedWriter bufferedWriter=null;
			try {
				 bufferedWriter = new BufferedWriter(new FileWriter(file1, true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int size = result.size();
			for (String string : result) {
				String[] temp=string.split("\t");
				bufferedWriter.write(temp[0]+","+temp[1]);
				bufferedWriter.newLine();
			}
		}
		
	}

}
