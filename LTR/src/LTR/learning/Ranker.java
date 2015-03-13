/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.learning;

import LTR.db.DataPoint;
import LTR.db.RankList;
import LTR.metric.MetricScorer;
import LTR.metric.MetricScorerFactory;
import LTR.utilities.FileUtils;
import LTR.utilities.MergeSorter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import super_utils.io.Configer;
/**
 * @author vdang
 * 
 * This class implements the generic Ranker interface. Each ranking algorithm implemented has to extend this class. 
 */
public abstract class Ranker {
	public static boolean verbose = true;
	public MetricScorerFactory mFactory=new MetricScorerFactory();
	protected List<RankList> samples = new ArrayList<RankList>();//training samples
	protected int[] features = null;
	
	protected List<MetricScorer> scorers_4_see=null;
	protected MetricScorer scorer = null;
	//score on train data need to see
	protected List<Double> scores_value_4_see =new ArrayList<>();
	protected String scores_name_4_see=null;
	
	protected double scoreOnTrainingData = 0.0;
	protected double bestScoreOnValidationData = 0.0;
	protected Configer cf=null;
	protected List<RankList> validationSamples = null;
	
	protected Ranker()
	{

	}
	/**
	 * @param samples
	 * @param features 需要的特征集
	 * @param scorer 作为训练的scorer
	 */
	protected Ranker(List<RankList> samples, int[] features, MetricScorer scorer)
	{
		this.samples = samples;
		this.features = features;
		this.scorer = scorer;
	}
	
	//Utility functions
	public void setTrainingSet(List<RankList> samples)
	{
		this.samples = samples;
	
	}
	public void setFeatures(int[] features)
	{
		this.features = features;	
	}
	public void setValidationSet(List<RankList> samples)
	{
		this.validationSamples = samples;
	}
	public void setMetricScorer(MetricScorer scorer)
	{
		this.scorer = scorer;
	}
	
	public double getScoreOnTrainingData()
	{
		return scoreOnTrainingData;
	}
	public double getScoreOnValidationData()
	{
		return bestScoreOnValidationData;
	}
	public int[] getFeatures()
	{
		return features;
	}
	
	/**
	 * @param rl
	 * @return 对rl 按照预测的评分排序，返回排好序后的查询-文档集
	 *@create_time：2015年1月5日下午4:36:44
	 *@modifie_time：2015年1月5日 下午4:36:44
	  
	 */
	public RankList rank(RankList rl)
	{
		//一个查询-文档集合的预测评分
		double[] scores = new double[rl.size()];
		for(int i=0;i<rl.size();i++)
			scores[i] = eval(rl.get(i));
		//按照分数排序或的id编号
		int[] idx = MergeSorter.sort(scores, false);
		return new RankList(rl, idx);
	}
	/**
	 * @param l
	 * @return 对所有的样本重新按照预测值排序
	 *@create_time：2015年1月8日下午4:06:45
	 *@modifie_time：2015年1月8日 下午4:06:45
	  
	 */
	public List<RankList> rank(List<RankList> l)
	{
		List<RankList> ll = new ArrayList<RankList>();
		for(int i=0;i<l.size();i++)
			ll.add(rank(l.get(i)));
		return ll;
	}
	/**
	 * @param modelFile 
	 *   把模型写入文件中
	 *@create_time：2015年1月8日下午4:07:16
	 *@modifie_time：2015年1月8日 下午4:07:16
	  
	 */
	public void save(String modelFile) 
	{
		FileUtils.write(modelFile, "ASCII", model());
	}
	
	protected void PRINT(String msg)
	{
		if(verbose)
			System.out.print(msg);
	}
	protected void PRINTLN(String msg)
	{
		if(verbose)
			System.out.println(msg);
	}
	protected void PRINT(int[] len, String[] msgs)
	{
		if(verbose)
		{
			for(int i=0;i<msgs.length;i++)
			{
				String msg = msgs[i];
				if(msg.length() > len[i])
					msg = msg.substring(0, len[i]);
				else
					while(msg.length() < len[i])
						msg += " ";
				System.out.print(msg + " | ");
			}
		}
	}

	protected void PRINT( List<Double> msgs)
	{
		if(verbose)
		{
			for(int i=0;i<msgs.size();i++)
			{
				double msg = msgs.get(i);
				
				System.out.print(msg + " | ");
			}
		}
	}
	protected void PRINT(int[] len, List<String> msgs)
	{
		if(verbose)
		{
			for(int i=0;i<msgs.size();i++)
			{
				String msg = msgs.get(i);
				if(msg.length() > len[i])
					msg = msg.substring(0, len[i]);
				else
					while(msg.length() < len[i])
						msg += " ";
				System.out.print(msg + " | ");
			}
		}
	}
	protected void PRINTLN(int[] len, List<String> msgs)
	{
		PRINT(len, msgs);
		PRINTLN("");
	}
	protected void PRINTLN(int[] len, String[] msgs)
	{
		PRINT(len, msgs);
		PRINTLN("");
	}
	/**
	 *打印当前时间 
	 *@create_time：2015年1月8日下午5:56:38
	 *@modifie_time：2015年1月8日 下午5:56:38
	  
	 */
	protected void PRINTTIME()
	{
		DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
	}
	/**
	 * 打印内存使用情况
	 *@create_time：2015年1月8日下午4:09:35
	 *@modifie_time：2015年1月8日 下午4:09:35
	  
	 */
	protected void PRINT_MEMORY_USAGE()
	{
		System.out.println("***** " + Runtime.getRuntime().freeMemory() + " / " + Runtime.getRuntime().maxMemory());
	}
	
	protected void copy(double[] source, double[] target)
	{
		for(int j=0;j<source.length;j++)
			target[j] = source[j];
	}
	
	/**
	 * HAVE TO BE OVER-RIDDEN IN SUB-CLASSES
	 */
	public abstract void init();
	public abstract void learn();
	
	/**
	 * @param p
	 * @return 计算模型对当前样本点，即文档的评分
	 *@create_time：2015年1月8日下午4:02:07
	 *@modifie_time：2015年1月8日 下午4:02:07
	  
	 */
	public double  eval(DataPoint p)
	{
		return -1.0;
	}

  
	/**
	 * @param scorerList the scorerList to set
	 */
	public void setScorerList(List<MetricScorer> scorerList) {
		this.scorers_4_see = scorerList;
	}
	/**
	 * @param cf the cf to set
	 */
	public void setCf(Configer cf) {
		this.cf = cf;
		scorers_4_see=mFactory.createScorerList(cf.getInt("model.metric2S"));
		scores_name_4_see=new String();
		verbose=cf.getString("silent").equals("true")?false:true;
		for (MetricScorer scorer : scorers_4_see) {
			scores_name_4_see+=scorer.name()+" |";
		}
	}
	public void printMetricName(){
		if (verbose) {
			PRINTLN(new int[]{7, 14,scores_name_4_see.length()+5, 9, 9}, new String[]{"#epoch", "% mis-ordered", scorer.name()+"-T",scores_name_4_see, scorer.name()+"-V"});
		}
	}
public abstract Ranker clone();
  /*
   * 模型的各组参数
   *  (non-Javadoc) 
 * @see java.lang.Object#toString()
 */
public abstract String toString();
  /**
 * @return 输出模型
 *@create_time：2015年1月8日下午6:07:29
 *@modifie_time：2015年1月8日 下午6:07:29
  
 */
public abstract String model();

  /**
   * 从文件中读取模型，并且设置模型
 * @param fullText
 *@create_time：2015年1月8日下午6:08:18
 *@modifie_time：2015年1月8日 下午6:08:18
  
 */
public abstract void loadFromString(String fullText);
  /**
 * @return 排序的名称
 *@create_time：2015年1月8日下午6:09:18
 *@modifie_time：2015年1月8日 下午6:09:18
 */
public abstract String name();
  /**
 * 打印模型设置的参数到屏幕
 *@create_time：2015年1月8日下午6:09:39
 *@modifie_time：2015年1月8日 下午6:09:39
  
 */
public abstract void printParameters();
}
