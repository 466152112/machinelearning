package MF.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class WriteUtil<T extends Object> {
	static File  file = null;
	
	public void writeOneLine(String oneLine,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			bufferedWriter.write(oneLine);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	public void writeList(List<T> list,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			for (T t : list) {
				bufferedWriter.write(String.valueOf(t));
				bufferedWriter.newLine();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	/**
	 * @param map write by the type like this ( key,value \r key,value``````)
	 * @param filename
	 *  
	 *@create_time：2014年7月22日下午7:57:33
	 *@modifie_time：2014年7月22日 下午7:57:33
	  
	 */
	public void writeMap(Map<T, List<T >> map,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			Iterator<T> keyList=map.keySet().iterator();
			while(keyList.hasNext()){
				T key=keyList.next();
				List<T> list=map.get(key);
				for (T element : list) {
					bufferedWriter.write(key+","+element);
					bufferedWriter.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	

	
	
	
}
