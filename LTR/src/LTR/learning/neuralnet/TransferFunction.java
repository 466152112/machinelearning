/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.learning.neuralnet;

/**
 * @author vdang
 * 
 * This is the abstract class for implementing transfer functions for neuralnet.
 */
public interface TransferFunction {
	
	/**
	 * @param x
	 * @return 计算函数值
	 *@create_time：2015年1月8日下午7:00:05
	 *@modifie_time：2015年1月8日 下午7:00:05
	  
	 */
	public double compute(double x);
	/**
	 * @param x
	 * @return 计算梯度值
	 *@create_time：2015年1月8日下午7:00:07
	 *@modifie_time：2015年1月8日 下午7:00:07
	  
	 */
	public double computeDerivative(double x);
}
