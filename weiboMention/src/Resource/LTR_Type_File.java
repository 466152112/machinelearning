/**

 * 

 */

package Resource;

import Resource.superClass.superResource;



/**

 * 

 * @progject_name：weiboMention

 * @class_name：SVM_Type_File

 * @class_describe：

 * @creator：鸽

 * @create_time：2014年12月29日 下午3:25:05

 * @modifier：鸽

 * @modified_time：2014年12月29日 下午3:25:05

 * @modified_note：

 * @version

 * 

 */

public class LTR_Type_File extends superResource{

	final static String path =work_path + "LTR/";

	/* target weibo content database */
	final static String svm_type_input= path + "svm_type_input_v6_";
	final static String svm_type_input_test = svm_type_input+ "test.txt";
	final static String svm_type_input_train = svm_type_input+ "train.txt";
	
	public LTR_Type_File(){
		super();
	}
	/**

	 * @return the path

	 */

	public static String getPath() {

		return path;

	}

	/**

	 * @return the svmTypeInput

	 */

	public static String getSvmTypeInput_test() {
		return svm_type_input_test;
	}
	/**
	 * @return the svmTypeInputTest
	 */
	public static String getSvmTypeInputTest() {
		return svm_type_input_test;
	}
	/**
	 * @return the svmTypeInputTrain
	 */
	public static String getSvmTypeInputTrain() {
		return svm_type_input_train;
	}



}

