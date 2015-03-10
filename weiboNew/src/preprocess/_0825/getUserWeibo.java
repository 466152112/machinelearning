/**
 * 
 */
package preprocess._0825;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @progject_name：weiboNew
 * @class_name：getUserFromDatabase
 * @class_describe�?
 * @creator：zhouge
 * @create_time�?014�?�?4�?下午10:17:19
 * @modifier：zhouge
 * @modified_time�?014�?�?4�?下午10:17:19
 * @modified_note�?
 * @version
 * 
 */
public class getUserWeibo {
	public static void main(String[] dk) {

		
		String path = "/media/zhouge/new2/data/";
		//String path="J:/workspace/weiboNew/data/";
		String sourceFile = path + "merge.txt";
		String resultFile=path+"clear.txt";
		try (BufferedReader bufferedReader = new BufferedReader(
						new FileReader(sourceFile));
				BufferedWriter writer=new BufferedWriter(new FileWriter(
						resultFile));
				) {
			String tempLine = null;
			long count = 0L;
			long weiboIdFlag=0L;
			try {
				while ((tempLine = bufferedReader.readLine()) != null) {
					tempLine = tempLine.trim();
					// System.out.println(oneLine);;
					String oneLine = tempLine;
					try {
						if (oneLine.indexOf("\t") == -1) {
							continue;
						}
						
						try {
							// 截取微博id
							long weiboId = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							if (weiboIdFlag==weiboId) {
								//System.out.println("same ");
								continue;
							}else {
								weiboIdFlag=weiboId;
							}
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
							// 截取用户id
							long user_id = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));
							
							oneLine = oneLine.substring(oneLine.indexOf("\t"))
									.trim();
						
						} catch (NumberFormatException e) {
							continue;
						}

						try {
							// 截取转发微博id
							long retweet_id = Long.valueOf(oneLine.substring(0,
									oneLine.indexOf("\t")));

						} catch (NumberFormatException e) {
							
						} catch (StringIndexOutOfBoundsException e) {
							continue;
						}
						writer.write(tempLine);
						writer.newLine();
						count++;
						if (count % 100000000 == 0) {
							System.out.println(count);
							writer.flush();
						}

					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					
				}
				writer.flush();
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
