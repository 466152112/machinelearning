/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.learning;

import java.util.List;

import LTR.db.RankList;
import LTR.metric.MetricScorer;
import LTR.utilities.SimpleMath;
import super_utils.io.Configer;
/**
 * @author vdang
 * 
 * This class is for users who want to use this library programmatically. It provides trained rankers of different types with respect to user-specified parameters.
 */
public class RankerTrainer {

	protected RankerFactory rf = new RankerFactory();
	protected double trainingTime = 0;
	
	public Ranker train(RANKER_TYPE type, List<RankList> train, int[] features, MetricScorer scorer)
	{
		Ranker ranker = rf.createRanker(type, train, features, scorer);
		long start = System.nanoTime();
		ranker.init();
		ranker.learn();
		trainingTime = System.nanoTime() - start;
		printTrainingTime();
		return ranker;
	}
	/**
	 * @param type 某种排序机
	 * @param train 训练集
	 * @param validation 验证集
	 * @param features	特征集合
	 * @param scorer 评价
	 * @return
	 *@create_time：2015年1月5日下午4:32:01
	 *@modifie_time：2015年1月5日 下午4:32:01
	  
	 */
	public Ranker train(RANKER_TYPE type, List<RankList> train, List<RankList> validation, int[] features, MetricScorer scorer,Configer cf)
	{
		Ranker ranker = rf.createRanker(type, train, features, scorer);
		ranker.setValidationSet(validation);
		ranker.setCf(cf);
		
		long start = System.nanoTime();
		ranker.init();
		ranker.learn();
		trainingTime = System.nanoTime() - start;
		//printTrainingTime();
		return ranker;
	}
	public double getTrainingTime()
	{
		return trainingTime;
	}
	public void printTrainingTime()
	{
		System.out.println("Training time: " + SimpleMath.round((trainingTime)/1e9, 2) + " seconds");			
	}
}
