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
 * @class_name：RandomGuess   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月9日 下午4:22:43   
 * @modifier：zhouge   
 * @modified_time：2015年1月9日 下午4:22:43   
 * @modified_note：   
 * @version    
 *    
 */
public class RandomGuess  extends Ranker{
	static Random random = new Random();
	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#init()
	 */
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
		return new RandomGuess();
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
		System.out.println("randomGuess not model");
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see LTR.learning.Ranker#name()
	 */
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "RandomGuess";
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
		return random.nextFloat();
	}
}
