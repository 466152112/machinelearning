package model.link.pageRank.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataWriter {
	File fs;
	FileWriter fileWriter;
	BufferedWriter bufferedWriter;
	
	public DataWriter(String fileName){
		fs=new File(fileName);
		try {
			fileWriter=new FileWriter(fs);
			bufferedWriter=new BufferedWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean write(String aa){
		try {
			bufferedWriter.write(aa);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected void finalize(){
		try {
			
			bufferedWriter.close();
			fileWriter.close();
			System.out.println("dddd");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("file close failure");
		}
		
	}
}
