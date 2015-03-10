/**
 * 
 */
package Resource;

import Resource.superClass.superResource;

/**   
 *    
 * @progject_name：weiboMention   
 * @class_name：word2vec_txt_Source   
 * @class_describe：   
 * @creator：鸽   
 * @create_time：2014年12月23日 下午8:31:25   
 * @modifier：鸽   
 * @modified_time：2014年12月23日 下午8:31:25   
 * @modified_note：   
 * @version    
 *    
 */
public  class word2vec_txt_Source extends superResource{
	final static String path=work_path+"/word2vec/";
	
	/*userId_word_count_file file*/
	final static String userId_word_count_file=path+"userId_word_count.txt";
	/*weibo word class*/
	final static String word_class_file=path+"weibo_classes.txt";
	
	private word2vec_txt_Source(){
		super();
	}
	/**
	 * @return the useridWordCountFile
	 */
	public static String getUseridWordCountFile() {
		return userId_word_count_file;
	}
	/**
	 * @return the wordCountFile
	 */
	public static String getWordClassFile() {
		return word_class_file;
	}
	
}
