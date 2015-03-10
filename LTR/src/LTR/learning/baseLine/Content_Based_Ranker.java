/**
 * 
 */
package LTR.learning.baseLine;

import java.util.Random;

import LTR.db.DataPoint;
import LTR.learning.Ranker;

/**   
 *    
 * @progject_name：LTR   
 * @class_name：Content_Based_Recommendation   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月9日 下午4:52:09   
 * @modifier：zhouge   
 * @modified_time：2015年1月9日 下午4:52:09   
 * @modified_note：   
 * @version    
 *    
 */
public class Content_Based_Ranker extends Ranker {
	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#init()
	 */
	final int Content_feature_index=4;
	@Override
	public void init() {
		// TODO Auto-generated method stub
		//do nothing
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#learn()
	 */
	@Override
	public void learn() {
		// TODO Auto-generated method stub
		//do nothing
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#clone()
	 */
	@Override
	public Ranker clone() {
		// TODO Auto-generated method stub
		return new Content_Based_Ranker();
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "";
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#model()
	 */
	@Override
	public String model() {
		// TODO Auto-generated method stub
		String output = "## " + name() + "\n";
		output += toString();
		return output;
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#loadFromString(java.lang.String)
	 */
	@Override
	public void loadFromString(String fullText) {
		// TODO Auto-generated method stub
		System.out.println("Content_Based_Ranker not model");
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#name()
	 */
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "Content_Based_Ranker";
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#printParameters()
	 */
	@Override
	public void printParameters() {
		// TODO Auto-generated method stub
		
	}
	
	public double  eval(DataPoint p)
	{
		return p.getFeatureValue(Content_feature_index);
	}
}
