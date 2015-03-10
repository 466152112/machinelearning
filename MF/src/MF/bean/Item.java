/**
 * 
 */
package MF.bean;

import java.util.ArrayList;

import listnet.data.MSDocument;
import listnet.data.interf.Document;

/**
 * 
 * @progject_name：MF
 * @class_name：user
 * @class_describe：
 * @creator：zhouge
 * @create_time：2014年10月9日 下午4:02:02
 * @modifier：zhouge
 * @modified_time：2014年10月9日 下午4:02:02
 * @modified_note：
 * @version
 * 
 */
public class Item {

	/** The uid. */
	private int iid;

	/** The features. */
	private double[] features;


	/**
	 * @return the iid
	 */
	public int getIid() {
		return iid;
	}

	/**
	 * @param iid the iid to set
	 */
	public void setIid(int iid) {
		this.iid = iid;
	}

	/**
	 * @return the features
	 */
	public double[] getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(double[] features) {
		this.features = features;
	}

}
