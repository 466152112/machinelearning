/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.learning.tree;

import LTR.db.DataPoint;
import LTR.db.RankList;
import LTR.learning.*;
import LTR.metric.MetricScorer;
import LTR.utilities.SimpleMath;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import super_utils.io.Configer;
public class RFRanker extends Ranker {
	//Parameters
	//[a] general bagging parameters
	public static int nBag = 300;
	public static float subSamplingRate = 1.0f;//sampling of samples (*WITH* replacement)
	public static float featureSamplingRate = 0.3f;//sampling of features (*WITHOUT* replacement)
	//[b] what to do in each bag
	public static RANKER_TYPE rType = RANKER_TYPE.MART;//which algorithm to bag
	public static int nTrees = 1;//how many trees in each bag. If nTree > 1 ==> each bag will contain an ensemble of gradient boosted trees.
	public static int nTreeLeaves = 100;
	public static float learningRate = 0.1F;//or shrinkage. *ONLY* matters if nTrees > 1.
	public static int nThreshold = 256;
	public static int minLeafSupport = 1;
	
	//Variables
	protected Ensemble[] ensembles = null;//bag of ensembles, each can be a single tree or an ensemble of gradient boosted trees
	static String[] rType1 = new String[] { "MART", "RankNet", "RankBoost",
		"AdaRank", "Coordinate Ascent", "LambdaRank", "LambdaMART",
		"ListNet", "Random Forests", "Linear Regression" };
	static RANKER_TYPE[] rType2 = new RANKER_TYPE[] { RANKER_TYPE.MART,
		RANKER_TYPE.RANKNET, RANKER_TYPE.RANKBOOST, RANKER_TYPE.ADARANK,
		RANKER_TYPE.COOR_ASCENT, RANKER_TYPE.LAMBDARANK,
		RANKER_TYPE.LAMBDAMART, RANKER_TYPE.LISTNET,
		RANKER_TYPE.RANDOM_FOREST, RANKER_TYPE.LINEAR_REGRESSION };
	public RFRanker()
	{		
	}
	public RFRanker(List<RankList> samples, int[] features, MetricScorer scorer)
	{
		super(samples, features, scorer);
		
	}

	public void init()
	{
		// Random forest
		int tree = cf.getInt("tree");
		nTrees = tree;
		nBag = cf.getInt("RandomForests.bag");
		subSamplingRate = cf.getFloat("RandomForests.srate");
		featureSamplingRate = cf.getFloat("RandomForests.frate");
		int leaf = cf.getInt("leaf");
		nTreeLeaves = leaf;
		float shrinkage = cf.getFloat("shrinkage");
		learningRate = shrinkage;
		int mls = cf.getInt("mls");
		minLeafSupport = mls;
		int rt=cf.getInt("RandomForests.rtype");
		if (rt == 0 || rt == 6) {
			rType=rType2[rt];
		} else {
			System.out
					.println(cf.getString("")
							+ " cannot be bagged. Random Forests only supports MART/LambdaMART.");
			System.out.println("System will now exit.");
			System.exit(1);
		}
		PRINT("Initializing... ");
		ensembles = new Ensemble[nBag];
		//initialize parameters for the tree(s) built in each bag
		LambdaMART.nTrees = nTrees;
		LambdaMART.nTreeLeaves = nTreeLeaves;
		LambdaMART.learningRate = learningRate;
		LambdaMART.nThreshold = nThreshold;
		LambdaMART.minLeafSupport = minLeafSupport;
		LambdaMART.nRoundToStopEarly = -1;//no early-stopping since we're doing bagging
		//turn on feature sampling
		FeatureHistogram.samplingRate = featureSamplingRate;
		PRINTLN("[Done]");
	}
	public void learn()
	{
		
		RankerFactory rf = new RankerFactory();
		PRINTLN("------------------------------------");
		PRINTLN("Training starts...");
		PRINTLN("------------------------------------");
		PRINTLN(new int[]{9, 9, 11}, new String[]{"bag", scorer.name()+"-B", scorer.name()+"-OOB"});
		PRINTLN("------------------------------------");
		//start the bagging process
		for(int i=0;i<nBag;i++)
		{
			if(i % LambdaMART.gcCycle == 0)
				System.gc();
			Sampler sp = new Sampler();
			//create a "bag" of samples by random sampling from the training set
			List<RankList> bag = sp.doSampling(samples, subSamplingRate, true);
			//"out-of-bag" samples
			//List<RankList> outOfBag = sp.getRemains();
			LambdaMART r = (LambdaMART)rf.createRanker(rType, bag, features, scorer);
			//r.setValidationSet(outOfBag);
			
			boolean tmp = Ranker.verbose;
			Ranker.verbose = false;//turn of the progress messages from training this ranker
			r.init();
			r.learn();
			Ranker.verbose = tmp;
			//PRINTLN(new int[]{9, 9, 11}, new String[]{"b["+(i+1)+"]", SimpleMath.round(r.getScoreOnTrainingData(), 4)+"", SimpleMath.round(r.getScoreOnValidationData(), 4)+""});
			PRINTLN(new int[]{9, 9}, new String[]{"b["+(i+1)+"]", SimpleMath.round(r.getScoreOnTrainingData(), 4)+""});
			ensembles[i] = r.getEnsemble();
		}
		//Finishing up
		scoreOnTrainingData = scorer.score(rank(samples));
		PRINTLN("------------------------------------");
		PRINTLN("Finished sucessfully.");
		PRINTLN(scorer.name() + " on training data: " + SimpleMath.round(scoreOnTrainingData, 4));
		if(validationSamples != null)
		{
			bestScoreOnValidationData = scorer.score(rank(validationSamples));
			PRINTLN(scorer.name() + " on validation data: " + SimpleMath.round(bestScoreOnValidationData, 4));
		}
		PRINTLN("------------------------------------");
	}
	public double eval(DataPoint dp)
	{
		double s = 0;
		for(int i=0;i<ensembles.length;i++)
			s += ensembles[i].eval(dp);
		return s/ensembles.length;
	}
	public Ranker clone()
	{
		return new RFRanker();
	}
	public String toString()
	{
		String str = "";
		for(int i=0;i<nBag;i++)
			str += ensembles[i].toString() + "\n";
		return str;
	}
	public String model()
	{
		String output = "## " + name() + "\n";
		output += "## No. of bags = " + nBag + "\n";
		output += "## Sub-sampling = " + subSamplingRate + "\n";
		output += "## Feature-sampling = " + featureSamplingRate + "\n";
		output += "## No. of trees = " + nTrees + "\n";
		output += "## No. of leaves = " + nTreeLeaves + "\n";
		output += "## No. of threshold candidates = " + nThreshold + "\n";
		output += "## Learning rate = " + learningRate + "\n";
		output += "\n";
		output += toString();
		return output;
	}
  @Override
	public void loadFromString(String fullText)
	{
		try {
			String content = "";
			String model = "";
			BufferedReader in = new BufferedReader(new StringReader(fullText));
			List<Ensemble> ens = new ArrayList<Ensemble>();
			while((content = in.readLine()) != null)
			{
				content = content.trim();
				if(content.length() == 0)
					continue;
				if(content.indexOf("##")==0)
					continue;
				//actual model component
				model += content;
				if(content.indexOf("</ensemble>") != -1)
				{
					//load the ensemble
					ens.add(new Ensemble(model));
					model = "";
				}				
			}
			in.close();
			HashSet<Integer> uniqueFeatures = new HashSet<Integer>();
			ensembles = new Ensemble[ens.size()];
			for(int i=0;i<ens.size();i++)
			{
				ensembles[i] = ens.get(i);
				//obtain used features
				int[] fids = ens.get(i).getFeatures();
				for(int f=0;f<fids.length;f++)
					if(!uniqueFeatures.contains(fids[f]))
						uniqueFeatures.add(fids[f]);
			}
			int fi = 0;
			features = new int[uniqueFeatures.size()];
			for(Integer f : uniqueFeatures)
				features[fi++] = f.intValue();
		}
		catch(Exception ex)
		{
			System.out.println("Error in RFRanker::load(): " + ex.toString());
		}
	}
	public void printParameters()
	{
		PRINTLN("No. of bags: " + nBag);
		PRINTLN("Sub-sampling: " + subSamplingRate);
		PRINTLN("Feature-sampling: " + featureSamplingRate);
		PRINTLN("No. of trees: " + nTrees);
		PRINTLN("No. of leaves: " + nTreeLeaves);
		PRINTLN("No. of threshold candidates: " + nThreshold);
		PRINTLN("Learning rate: " + learningRate);
	}
	public String name()
	{
		return "Random Forests";
	}
	
	public Ensemble[] getEnsembles()
	{
		return ensembles;
	}
}
