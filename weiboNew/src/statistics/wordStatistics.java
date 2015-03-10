
/**
 * 
 */
package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import util.ReadUtil;
import util.WriteUtil;

/**   
 *    
 * 椤圭洰鍚嶇О锛歸eibo   
 * 绫诲悕绉帮細wordStatistics   
 * 绫绘弿杩帮細   缁熻璇嶇殑鏁伴噺
 * 鍒涘缓浜猴細zhouge   
 * 鍒涘缓鏃堕棿锛�014骞�鏈�3鏃�涓婂崍9:32:59   
 * 淇敼浜猴細zhouge   
 * 淇敼鏃堕棿锛�014骞�鏈�3鏃�涓婂崍9:32:59   
 * 淇敼澶囨敞锛�  
 * @version    
 *    
 */
public class wordStatistics {
	
	public static void main(String[] agrs) throws FileNotFoundException{
		
		
		String wordsetFile=" TermSet.txt";
		ReadUtil readUtil = new ReadUtil();
		HashMap<String,Integer> wordsethash = new HashMap<>();
//		ArrayList<String> wordset=new ArrayList<>();
//		int usersetSize = 0;
//		try {
//			// 浠庤瘝搴撴枃浠朵腑璇诲彇鏁版嵁
//			wordset = readUtil.readfromFileByStream(wordsetFile, "UTF-8");
//			for (String string : wordset) {
//				wordsethash.put(string, 0);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//鎵弿鏂囦欢
		Scanner scanner=new Scanner(new File("J:/weibo/5k/20140312/gibbsldaInputData.dat"));
		//杩囨护绗竴琛�
		scanner.next();
		while(scanner.hasNext()){
			//璇诲彇涓�
			String oneLine=scanner.next();
			String[] splitOne=oneLine.split("\t");
			for (String term : splitOne) {
				if (term!=null&&!term.equals(" ")) {
					if (wordsethash.containsKey(term.trim())) {
						wordsethash.put(term.trim(), wordsethash.get(term.trim())+1);
					}
					else if(!wordsethash.containsKey(term.trim())){
						wordsethash.put(term.trim(), 1);
					}
				}
			}
		}
		
		Iterator<String> wordIterator=wordsethash.keySet().iterator();
		String outPutFileString="J:/weibo/5k/20140312/缁熻/wordCount.dat";
		BufferedWriter bufferedWriter = null;
		final File file1 = new File(outPutFileString);
		try {

			bufferedWriter = new BufferedWriter(new FileWriter(file1, true));
			while(wordIterator.hasNext()){
				String keyString=wordIterator.next();
				String tempString=""+wordsethash.get(keyString);
				bufferedWriter.write(tempString);
				bufferedWriter.newLine();
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure to write: " + outPutFileString);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Failure to close: " + outPutFileString);
			}
		
		}
		
		System.out.println();
	} 
}
