package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class WriteUtil<T extends Object> {
	static File  file = null;
	
	public void writeOneLine(String oneLine,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			bufferedWriter.write(oneLine);
			bufferedWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	public void writeList(List<T> list,String filename) {
		this.writeList(list, filename, true);
	}
	
	public void writeList(Set<T> list,String filename) {
		List<T> temp=new ArrayList<>(list);
		this.writeList(temp, filename, true);
	}
	/**
	 * @param list
	 * @param filename
	 * @param ifAdd :if add to the end
	 *@create_time：2014年9月1日下午10:55:29
	 *@modifie_time：2014年9月1日 下午10:55:29
	  
	 */
	public void writeList(List<T> list,String filename,boolean ifAdd) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, ifAdd));){
			for (T t : list) {
				bufferedWriter.write(String.valueOf(t));
				bufferedWriter.newLine();
				
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	public void writeVector(double[] list,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			for (double t : list) {
				bufferedWriter.write(String.valueOf(t));
				bufferedWriter.newLine();
				
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	/**
	 * @param map write by the type like this ( key,value \r key,value``````)
	 * @param filename
	 *  
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�:57:33
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪崍7:57:33
	  
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
	
	/**
	 * @param map
	 * @param filename 
	 * 	write map to file in follow format: key,value
	 *@create_time：2014年9月23日下午9:04:07
	 *@modifie_time：2014年9月23日 下午9:04:07
	  
	 */
	public void writeMapKeyAndValue(Map<?, ?> map,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			Iterator<?> keyList=map.keySet().iterator();
			while(keyList.hasNext()){
				 Object key=keyList.next();
					bufferedWriter.write(String.valueOf(key)+","+String.valueOf(map.get(key)));
					bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	/**
	 * @param map
	 * @param filename 
	 * 	write map to file in follow format: key \t value
	 *@create_time：2014年9月23日下午9:04:07
	 *@modifie_time：2014年9月23日 下午9:04:07
	  
	 */
	public void writeMapKeyAndValuesplitbyt(Map<?, ?> map,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			Iterator<?> keyList=map.keySet().iterator();
			while(keyList.hasNext()){
				 Object key=keyList.next();
					bufferedWriter.write(String.valueOf(key)+"\t"+String.valueOf(map.get(key)));
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
	 *@create_time锛�014骞�鏈�2鏃ヤ笅鍗�:57:33
	 *@modifie_time锛�014骞�鏈�2鏃�涓嬪崍7:57:33
	  
	 */
	public void writeMapValueIsSet(Map<T, Set<T >> map,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			Iterator<T> keyList=map.keySet().iterator();
			while(keyList.hasNext()){
				T key=keyList.next();
				Set<T> list=map.get(key);
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
	public void writeMapSetElement(Map<T, Set<T >> map,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1, true));){
			Iterator<T> keyList=map.keySet().iterator();
			while(keyList.hasNext()){
				T key=keyList.next();
				Set<T> list=map.get(key);
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
	
	/**
	 * @param vector
	 * @param filename
	 *@create_time锛�014骞�鏈�鏃ヤ笅鍗�:44:26
	 *@modifie_time锛�014骞�鏈�鏃�涓嬪崍5:44:26
	  
	 */
	public void write2Vector(T[][] vector,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));){
			for (T[] OneLine : vector) {
				for (T one : OneLine) {
					bufferedWriter.write(one+"\t");
				}
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	
	/**
	 * @param vector
	 * @param filename
	 *@create_time锛�014骞�鏈�鏃ヤ笅鍗�:44:26
	 *@modifie_time锛�014骞�鏈�鏃�涓嬪崍5:44:26
	  
	 */
	public void write2Vectorindouble(double[][] vector,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));){
			for (double[] OneLine : vector) {
				for (double one : OneLine) {
					bufferedWriter.write(one+"\t");
				}
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	public void write2Vector(int[][] vector, String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));){
			for (int[] OneLine : vector) {
				for (int one : OneLine) {
					bufferedWriter.write(one+"\t");
				}
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}

	public void writemapkeyAndValue(Map<T, Object> result,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));){
			Iterator<T> keyiteratorIterator=result.keySet().iterator();
			while(keyiteratorIterator.hasNext()){
				T keyT=keyiteratorIterator.next();
				bufferedWriter.write(keyT+"\t"+result.get(keyT));
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	public void writemapkeyAndValueInInteger(Map<T, Integer> result,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));){
			Iterator<T> keyiteratorIterator=result.keySet().iterator();
			while(keyiteratorIterator.hasNext()){
				T keyT=keyiteratorIterator.next();
				bufferedWriter.write(keyT+"\t"+result.get(keyT));
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	
	public void writemapkeyAndValueInIntegerSplitInDot(Map<T, Integer> result,String filename,boolean flag) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1,flag));){
			Iterator<T> keyiteratorIterator=result.keySet().iterator();
			while(keyiteratorIterator.hasNext()){
				T keyT=keyiteratorIterator.next();
				bufferedWriter.write(keyT+","+result.get(keyT));
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	
	public void writemapkeyAndValueWhereValueIsMap(Map<?, HashMap<String, Integer>> result,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));){
			Iterator<?> keyiteratorIterator=result.keySet().iterator();
			while(keyiteratorIterator.hasNext()){
				Object keyT=keyiteratorIterator.next();
				HashMap<String, Integer> temp= result.get(keyT);
				bufferedWriter.write(keyT+"\t");
				for (Object key : temp.keySet()) {
					bufferedWriter.write(key+":"+temp.get(key));
				}
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
	/**
	 * @param friendPCC
	 * @param filename
	 *@create_time：2014年11月13日下午7:50:00
	 *@modifie_time：2014年11月13日 下午7:50:00
	  
	 */
	public void writemapkeyAndValueWhereValueinMap(Map<Long,Map<Long,Double>> friendPCC,String filename) {
		final File file1 = new File(filename);
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file1));){
			Iterator<?> keyiteratorIterator=friendPCC.keySet().iterator();
			while(keyiteratorIterator.hasNext()){
				Object keyT=keyiteratorIterator.next();
				Map<?, ?> temp= friendPCC.get(keyT);
				for (Object key : temp.keySet()) {
					bufferedWriter.write(String.valueOf(keyT)+"\t"+String.valueOf(key)+"\t"+temp.get(key));
					bufferedWriter.newLine();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} 
	}
}
