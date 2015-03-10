/**
 * 
 */
package MF.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;



/**   
*    
* @progject_name：BPR   
* @class_name：RatingDocument   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年7月22日 下午3:18:45   
* @modifier：zhouge   
* @modified_time：2014年7月22日 下午3:18:45   
* @modified_note：   
* @version    
*    
*/
public class RatingDocument {
	private final List<String> lines;
	private final int itemId;
	
	RatingDocument(List<String> lines, int id) {
		this.lines = lines;
		this.itemId = id;
	}

	public List<String> getLines() {
		return this.lines;
	}

	public int getItemId() {
		return this.itemId;
	}
	public static RatingDocument fromFile(File file) throws IOException {
		List<String> lines = new LinkedList<>();
		int id;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			id = Integer.valueOf(line.substring(0, line.indexOf(':')));

			line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
		}
		return new RatingDocument(lines, id);
	}
}
