/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.db;

import java.util.List;

import LTR.utilities.Sorter;

/**
 * @author vdang
 * 
 * This class implement the list of objects (each of which is a DataPoint) to be ranked. 
 */
public class RankList {

	protected DataPoint[] rl = null;
	
	public RankList(List<DataPoint> rl)
	{
		this.rl = new DataPoint[rl.size()];
		for(int i=0;i<rl.size();i++)
			this.rl[i] = rl.get(i);
	}
	public RankList(RankList rl)
	{
		this.rl = new DataPoint[rl.size()];
		for(int i=0;i<rl.size();i++)
			this.rl[i] = rl.get(i);
	}
	/**
	 * @param rl 
	 * @param idx 按照 idx 重新排序
	 */
	public RankList(RankList rl, int[] idx)
	{
		this.rl = new DataPoint[rl.size()];
		for(int i=0;i<idx.length;i++)
			this.rl[i] = rl.get(idx[i]);
	}
	public RankList(RankList rl, int[] idx, int offset)
	{
		this.rl = new DataPoint[rl.size()];
		for(int i=0;i<idx.length;i++)
			this.rl[i] = rl.get(idx[i]-offset);
	}	
	public String getID()
	{
		return get(0).getID();
	}
	public int size()
	{
		return rl.length;
	}
	public DataPoint get(int k)
	{
		return rl[k];
	}
	public void set(int k, DataPoint p)
	{
		rl[k] = p;
	}
	/**
	 * @return 按照正确label 排序
	 *@create_time：2015年1月8日下午7:52:17
	 *@modifie_time：2015年1月8日 下午7:52:17
	  
	 */
	public RankList getCorrectRanking()
	{
		double[] score = new double[rl.length];
		for(int i=0;i<rl.length;i++)
			score[i] = rl[i].getLabel();
		int[] idx = Sorter.sort(score, false); 
		return new RankList(this, idx);
	}
	
	public RankList getRanking(short fid)
	{
		double[] score = new double[rl.length];
		for(int i=0;i<rl.length;i++)
			score[i] = rl[i].getFeatureValue(fid);
		int[] idx = Sorter.sort(score, false);
		return new RankList(this, idx);
	}
}
