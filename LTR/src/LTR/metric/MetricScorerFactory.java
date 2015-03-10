/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用以实例化各种评价指标
 * MetrciScorerFactory
 * @author vdang
 */
public class MetricScorerFactory {
	
	private static MetricScorer[] mFactory = new MetricScorer[]{new APScorer(), new NDCGScorer(), new DCGScorer(), new PrecisionScorer(), new ReciprocalRankScorer(), new BestAtKScorer(), new ERRScorer()};
	private static HashMap<String, MetricScorer> map = new HashMap<String, MetricScorer>();
	
	/**
	 * 构造函数，初始化各种评价方式，对于k值未给予值
	 */
	public MetricScorerFactory()
	{
		map.put("MAP", new APScorer());
		map.put("NDCG", new NDCGScorer());
		map.put("DCG", new DCGScorer());
		map.put("P", new PrecisionScorer());
		map.put("RR", new ReciprocalRankScorer());
		map.put("BEST", new BestAtKScorer());
		map.put("ERR", new ERRScorer());
	}
	/**
	 * @param metric 枚举型
	 * @return
	 *@create_time：2015年1月5日下午3:17:34
	 *@modifie_time：2015年1月5日 下午3:17:34
	  
	 */
	public MetricScorer createScorer(METRIC metric)
	{
		return mFactory[metric.ordinal() - METRIC.MAP.ordinal()].clone();
	}
	/**
	 * @param metric 枚举型 
	 * @param k
	 * @return 某个带k的评级
	 *@create_time：2015年1月5日下午3:17:37
	 *@modifie_time：2015年1月5日 下午3:17:37
	  
	 */
	public MetricScorer createScorer(METRIC metric, int k)
	{
		MetricScorer s = mFactory[metric.ordinal() - METRIC.MAP.ordinal()].clone();
		s.setK(k);
		return s;
	}
	
	/**
	 * @param metric 评价方法的字母表示
	 * @return 具体某个评价方式
	 *@create_time：2015年1月5日下午3:15:37
	 *@modifie_time：2015年1月5日 下午3:15:37
	  
	 */
	public MetricScorer createScorer(String metric)//e.g.: metric = "NDCG@5"
	{
		int k = -1;
		String m = "";
		MetricScorer s = null;
		if(metric.indexOf("@") != -1)
		{
			m = metric.substring(0, metric.indexOf("@"));
			k = Integer.parseInt(metric.substring(metric.indexOf("@")+1));
			s = map.get(m.toUpperCase()).clone();
			s.setK(k);
		}
		else
			s = map.get(metric.toUpperCase()).clone();
		return s;
	}
	
	public List<MetricScorer> createScorerList(String metricList)//e.g.: metric = "NDCG@5"
	{
		int k = -1;
		String m = "";
		List<MetricScorer> result=new ArrayList<MetricScorer>();
		String[] split=metricList.split(",");
		//
		for (String metric : split) {
			MetricScorer s = null;
			if(metric.indexOf("@") != -1)
			{
				m = metric.substring(0, metric.indexOf("@"));
				k = Integer.parseInt(metric.substring(metric.indexOf("@")+1));
				s = map.get(m.toUpperCase()).clone();
				s.setK(k);
			}
			else
				s = map.get(metric.toUpperCase()).clone();
			result.add(s);
		}
		return result;
	}
	public List<MetricScorer> createScorerList(int end)//e.g.: metric = "NDCG@5"
	{
		int k = -1;
		String m = "";
		List<MetricScorer> result=new ArrayList<MetricScorer>();
		result.add(map.get("MAP"));
		for (int i = 1; i <=end; i++) {
			//P@10,RR@10,ERR@10,NDCG@10
			MetricScorer P = null;
			P = map.get("P").clone();
			P.setK(i);
			result.add(P);
			
			MetricScorer RR = null;
			RR = map.get("RR").clone();
			RR.setK(i);
			result.add(RR);
			
			MetricScorer ERR = null;
			ERR = map.get("ERR").clone();
			ERR.setK(i);
			result.add(ERR);
			
			MetricScorer NDCG = null;
			NDCG = map.get("NDCG").clone();
			NDCG.setK(i);
			result.add(NDCG);
		}
		return result;
	}
}
