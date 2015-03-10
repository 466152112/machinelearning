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
public  class LDA_txt_Source extends superResource{
	final static String path=work_path+"/lda/";
	//保存用户id和对于的所有微博，每一行为一条微博， 
	final static String userId_All_Content_file=path+"userId_All_Content.txt";
	/*weibo word class*/
	final static String userId_Sequence_file=path+"userId_Sequence.txt";
	
	final static String USER_CONTENT_LDATYPE_FILE=path +"user_content_LDAtype.txt";
	public LDA_txt_Source(){
		super();
	}
	public static String getPath() {
		return path;
	}

	public static String getUseridAllContentFile() {
		return userId_All_Content_file;
	}

	public static String getUseridSequenceFile() {
		return userId_Sequence_file;
	}

	public static String getUserContentLdatypeFile() {
		return USER_CONTENT_LDATYPE_FILE;
	}
	
}
