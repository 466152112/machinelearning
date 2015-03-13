/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.metric;

import java.util.List;

import LTR.db.RankList;

/**
 * @author vdang
 * A generic retrieval measure computation interface. 
 */
public class MetricScorer {
	
	/**
	 *默认k设置为10 
	 */
	protected int k = 10;
	
	public MetricScorer() 
	{
		
	}
	public void setK(int k)
	{
		this.k = k;
	}
	public int getK()
	{
		return k;
	}
//	public void loadExternalRelevanceJudgment(String qrelFile)
//	{
//		
//	}
	public double score(List<RankList> rl)
	{
		double score = 0.0;
		for(int i=0;i<rl.size();i++)
			score += score(rl.get(i));
		return score/rl.size();
	}
	
	/**
	 * @param rl
	 * @return 获取一个查询下相应的label集合 
	 *@create_time：2015年1月5日下午3:24:18
	 *@modifie_time：2015年1月5日 下午3:24:18
	  
	 */
	protected int[] getRelevanceLabels(RankList rl)
	{
		int[] rel = new int[rl.size()];
		for(int i=0;i<rl.size();i++)
			rel[i] = (int)rl.get(i).getLabel();
		return rel;
	}
	
	/**
	 * MUST BE OVER-RIDDEN
	 * 必须被子类重写
	 * @param rl
	 * @return
	 */
	public double score(RankList rl)
	{
		return 0.0;
	}
	public MetricScorer clone()
	{
		return null;
	}
	public String name()
	{
		return "";
	}
	public double[][] swapChange(RankList rl)
	{
		return null;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
}
