/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.learning;

/**   
*    
* @progject_name：LTR   
* @class_name：RANKER_TYPE   
* @class_describe：   保存所有的排序类型，用枚举保存
* @creator：鸽   
* @create_time：2015年1月4日 下午4:38:25   
* @modifier：鸽   
* @modified_time：2015年1月4日 下午4:38:25   
* @modified_note：   
* @version    
*    
*/
public enum RANKER_TYPE {
	MART, RANKBOOST, RANKNET, ADARANK, COOR_ASCENT, LAMBDARANK, LAMBDAMART, LISTNET, RANDOM_FOREST, LINEAR_REGRESSION
	//baseline
	,RANDOM,Content_Based,Bonds_Based,Influence_Based 
}
