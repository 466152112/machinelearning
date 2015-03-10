package weibo.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import bean.IFIDF;

public class WriteUtil {
	static File  file = null;

	public WriteUtil() {
	}

	public void writeArraylist(String userId, ArrayList<String> list,
			String filename) {
		BufferedWriter bufferedWriter = null;
		final File file1 = new File(filename);
		try {

			bufferedWriter = new BufferedWriter(new FileWriter(file1, true));
			int size = list.size();
			for (int i = 0; i < size; i++) {
				bufferedWriter.write(userId + "," + list.get(i));
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failure to close: " + filename);
			}
			System.out.println("sucess to write:"+filename);
		}
	}

	/*
	 * 构造函数
	 */
	public WriteUtil(String filename, ArrayList<String> list) {
		BufferedWriter bufferedWriter = null;
		try {
			file = new File(filename);
			bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			int size = list.size();
			for (int i = 0; i < size; i++) {
				bufferedWriter.write(list.get(i));
				bufferedWriter.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failure to close: " + filename);
			}
			// System.out.println("sucess to write:"+filename);
		}
	}
	/*
	 * 构造函数
	 */
	public WriteUtil(String filename, HashMap<String,ArrayList<String>> list) {
		BufferedWriter bufferedWriter = null;
		try {
			file = new File(filename);
			bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			Iterator<String> iterator=list.keySet().iterator();
			while(iterator.hasNext()){
				String key=iterator.next();
				ArrayList<String> temp=(ArrayList<String>)list.get(key);
				int size = temp.size();

				for (int i = 0; i < size; i++) {
					bufferedWriter.write(key+"\t"+temp.get(i));
					bufferedWriter.newLine();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failure to close: " + filename);
			}
			// System.out.println("sucess to write:"+filename);
		}
	}
	/*
	 * 构造函数
	 */
	public void WriteIntoFileByEncodingFormat(String filename,
			ArrayList<String> list, String encodingFormat) {
		try {
			file = new File(filename);
			int size = list.size();
			FileOutputStream fos = new FileOutputStream(filename, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encodingFormat);
			for (int i = 0; i < size; i++) {
				osw.write(list.get(i) + "\n");
			}
			osw.flush();
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
			e.printStackTrace();
		}
	}
	
	public static void doubleListByEncodingFormat(String filename,
			ArrayList<Double> list, String encodingFormat) {
		try {
			file = new File(filename);
			int size = list.size();
			FileOutputStream fos = new FileOutputStream(filename, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encodingFormat);
			for (int i = 0; i < size; i++) {
				osw.write(list.get(i) + "\t");
			}
			osw.write("\n");
			osw.flush();
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
			e.printStackTrace();
		}
	}

	public static void write2Array(String filename,int[][] result) {
		try {
			file = new File(filename);
			int size = result.length;
			FileOutputStream fos = new FileOutputStream(filename, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			for (int i = 0; i < size; i++) {
				for(int j=0;j<size;j++)
				osw.write(result[i][j]+ "\t");
				osw.write("\n");
			}
			
			osw.flush();
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
			e.printStackTrace();
		}
		System.out.println("sucess to write into :"+filename);
	}
	public static void IFIDFListByEncodingFormat(String filename,
			ArrayList<IFIDF> list, String encodingFormat) {
		try {
			file = new File(filename);
			int size = list.size();
			FileOutputStream fos = new FileOutputStream(filename, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encodingFormat);
			for (int i = 0; i < size; i++) {
				osw.write(list.get(i).getUserId() + "\t"+list.get(i).getTermID()+"\t"+list.get(i).getIFIDFValue());
				osw.write("\n");
			}
			
			osw.flush();
			osw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + filename);
			e.printStackTrace();
		}
	}
}
