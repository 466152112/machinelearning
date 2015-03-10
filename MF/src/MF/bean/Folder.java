/**
 * 
 */
package MF.bean;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**   
*    
* @progject_name：BPR   
* @class_name：Folder   
* @class_describe：   
* @creator：zhouge   
* @create_time：2014年7月22日 下午3:18:34   
* @modifier：zhouge   
* @modified_time：2014年7月22日 下午3:18:34   
* @modified_note：   
* @version    
*    
*/
public class Folder {
    private final List<Folder> subFolders;
    private final List<RatingDocument> documents;
    
    Folder(List<Folder> subFolders, List<RatingDocument> documents) {
        this.subFolders = subFolders;
        this.documents = documents;
    }
    
    public List<Folder> getSubFolders() {
        return this.subFolders;
    }
    
   public  List<RatingDocument> getDocuments() {
        return this.documents;
    }
    
    public static Folder fromDirectory(File dir) throws IOException {
        List<RatingDocument> documents = new LinkedList<>();
        List<Folder> subFolders = new LinkedList<>();
        for (File entry : dir.listFiles()) {
            if (entry.isDirectory()) {
            	//ѭ����ȥ
                subFolders.add(Folder.fromDirectory(entry));
            } else {
                documents.add(RatingDocument.fromFile(entry));
            }
        }
        return new Folder(subFolders, documents);
    }
}

