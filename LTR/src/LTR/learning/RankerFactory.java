/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.learning;

import LTR.db.RankList;
import LTR.learning.baseLine.Bond_Based_Ranker;
import LTR.learning.baseLine.Content_Based_Ranker;
import LTR.learning.baseLine.Influence_Based_Ranker;
import LTR.learning.baseLine.RandomGuess;
import LTR.learning.boosting.AdaRank;
import LTR.learning.boosting.RankBoost;
import LTR.learning.neuralnet.LambdaRank;
import LTR.learning.neuralnet.ListNet;
import LTR.learning.neuralnet.RankNet;
import LTR.learning.tree.LambdaMART;
import LTR.learning.tree.MART;
import LTR.learning.tree.RFRanker;
import LTR.metric.MetricScorer;
import LTR.utilities.FileUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

/**
 * @author vdang
 * 
 * This class implements the Ranker factory. All ranking algorithms implemented have to be recognized in this class. 
 */
public class RankerFactory {

	protected Ranker[] rFactory = new Ranker[]{new MART(), new RankBoost(), new RankNet(), new AdaRank(), new CoorAscent(), new LambdaRank(), new LambdaMART(), new ListNet(), new RFRanker(), new LinearRegRank(),
	//baseline
	new RandomGuess(),new Content_Based_Ranker(),new Bond_Based_Ranker(),new Influence_Based_Ranker()
	};
	protected static HashMap<String, RANKER_TYPE> map = new HashMap<String, RANKER_TYPE>();
	
	public RankerFactory()
	{
		map.put(createRanker(RANKER_TYPE.MART).name().toUpperCase(), RANKER_TYPE.MART);
		map.put(createRanker(RANKER_TYPE.RANKNET).name().toUpperCase(), RANKER_TYPE.RANKNET);
		map.put(createRanker(RANKER_TYPE.RANKBOOST).name().toUpperCase(), RANKER_TYPE.RANKBOOST);
		map.put(createRanker(RANKER_TYPE.ADARANK).name().toUpperCase(), RANKER_TYPE.ADARANK);
		map.put(createRanker(RANKER_TYPE.COOR_ASCENT).name().toUpperCase(), RANKER_TYPE.COOR_ASCENT);
		map.put(createRanker(RANKER_TYPE.LAMBDARANK).name().toUpperCase(), RANKER_TYPE.LAMBDARANK);
		map.put(createRanker(RANKER_TYPE.LAMBDAMART).name().toUpperCase(), RANKER_TYPE.LAMBDAMART);
		map.put(createRanker(RANKER_TYPE.LISTNET).name().toUpperCase(), RANKER_TYPE.LISTNET);
		map.put(createRanker(RANKER_TYPE.RANDOM_FOREST).name().toUpperCase(), RANKER_TYPE.RANDOM_FOREST);
		map.put(createRanker(RANKER_TYPE.LINEAR_REGRESSION).name().toUpperCase(), RANKER_TYPE.LINEAR_REGRESSION);
		//baseline //11:Content_Based,12:Bonds_Based,13:Influence_Based 
		map.put(createRanker(RANKER_TYPE.RANDOM).name().toUpperCase(), RANKER_TYPE.RANDOM);
		map.put(createRanker(RANKER_TYPE.Content_Based).name().toUpperCase(), RANKER_TYPE.Content_Based);
		map.put(createRanker(RANKER_TYPE.Bonds_Based).name().toUpperCase(), RANKER_TYPE.Bonds_Based);
		map.put(createRanker(RANKER_TYPE.Influence_Based).name().toUpperCase(), RANKER_TYPE.Influence_Based);
		
		
	}	
	public Ranker createRanker(RANKER_TYPE type)
	{
		Ranker r = rFactory[type.ordinal() - RANKER_TYPE.MART.ordinal()].clone();
		return r;
	}
	/**
	 * @param type
	 * @param samples
	 * @param features
	 * @param scorer
	 * @return 创建一个排序机
	 *@create_time：2015年1月5日下午4:33:13
	 *@modifie_time：2015年1月5日 下午4:33:13
	  
	 */
	public Ranker createRanker(RANKER_TYPE type, List<RankList> samples, int[] features, MetricScorer scorer)
	{
		Ranker r = createRanker(type);
		r.setTrainingSet(samples);
		r.setFeatures(features);
		r.setMetricScorer(scorer);
		return r;
	}
	@SuppressWarnings("unchecked")
	public Ranker createRanker(String className)
	{
		Ranker r = null;
		try {
			Class c = Class.forName(className);
			r = (Ranker) c.newInstance();
		}
		catch (ClassNotFoundException e) {
			System.out.println("Could find the class \"" + className + "\" you specified. Make sure the jar library is in your classpath.");
			e.printStackTrace();
			System.exit(1);
		}
		catch (InstantiationException e) {
			System.out.println("Cannot create objects from the class \"" + className + "\" you specified.");
			e.printStackTrace();
			System.exit(1);
		}
		catch (IllegalAccessException e) {
			System.out.println("The class \"" + className + "\" does not implement the Ranker interface.");
			e.printStackTrace();
			System.exit(1);
		}
		return r;
	}
	public Ranker createRanker(String className, List<RankList> samples, int[] features, MetricScorer scorer)
	{
		Ranker r = createRanker(className);
		r.setTrainingSet(samples);
		r.setFeatures(features);
		r.setMetricScorer(scorer);
		return r;
	}
	public Ranker loadRankerFromFile(String modelFile)
	{
    return loadRankerFromString(FileUtils.read(modelFile, "ASCII"));
	}
  public Ranker loadRankerFromString(String fullText)
  {
    Ranker r = null;
    try {
      BufferedReader in = new BufferedReader(new StringReader(fullText));
      String content = in.readLine();//read the first line to get the name of the ranking algorithm
      in.close();
      content = content.replace("## ", "").trim();
      System.out.println("Model:\t\t" + content);
      r = createRanker(map.get(content.toUpperCase()));
      r.loadFromString(fullText);
    }
    catch(Exception ex)
    {
      System.out.println("Error in RankerFactory.loadRanker(): " + ex.toString());
      System.exit(1);
    }
    return r;
  }
}
