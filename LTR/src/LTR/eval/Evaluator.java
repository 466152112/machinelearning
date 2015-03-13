/*===============================================================================
 * Copyright (c) 2010-2012 University of Massachusetts.  All Rights Reserved.
 *
 * Use of the RankLib package is subject to the terms of the software license set 
 * forth in the LICENSE file included with this software, and also available at
 * http://people.cs.umass.edu/~vdang/ranklib_license.html
 *===============================================================================
 */

package LTR.eval;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import LTR.db.RankList;
import LTR.db.features.FeatureManager;
import LTR.db.features.LinearNormalizer;
import LTR.db.features.Normalizer;
import LTR.db.features.SumNormalizor;
import LTR.db.features.ZScoreNormalizor;
import LTR.learning.CoorAscent;
import LTR.learning.LinearRegRank;
import LTR.learning.RANKER_TYPE;
import LTR.learning.Ranker;
import LTR.learning.RankerFactory;
import LTR.learning.RankerTrainer;
import LTR.learning.boosting.AdaRank;
import LTR.learning.boosting.RankBoost;
import LTR.learning.neuralnet.ListNet;
import LTR.learning.neuralnet.RankNet;
import LTR.learning.tree.LambdaMART;
import LTR.learning.tree.RFRanker;
import LTR.metric.ERRScorer;
import LTR.metric.METRIC;
import LTR.metric.MetricScorer;
import LTR.metric.MetricScorerFactory;
import LTR.utilities.FileUtils;
import LTR.utilities.MergeSorter;
import LTR.utilities.MyThreadPool;
import LTR.utilities.SimpleMath;
import super_utils.io.Configer;
import super_utils.io.Logs;
import super_utils.system.Dates;

/**
 * @author vdang
 * 
 *         This class is meant to provide the interface to run and compare
 *         different ranking algorithms. It lets users specify general
 *         parameters (e.g. what algorithm to run, training/testing/validating
 *         data, etc.) as well as algorithm-specific parameters. Type
 *         "java -jar bin/RankLib.jar" at the command-line to see all the
 *         options.
 */
public class Evaluator {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// all the method

		String trainFile = "";
		String featureDescriptionFile = "";
		float ttSplit = 0;// train-test split
		float tvSplit = 0;// train-validation split
		int foldCV = -1;
		String validationFile = "";
		String testFile = "";
		List<String> testFiles = new ArrayList<String>();
		int rankerType = 4;
		String trainMetric = "ERR@10";
		String testMetric = "";
		Evaluator.normalize = false;
		String savedModelFile = "";
		List<String> savedModelFiles = new ArrayList<String>();
		String kcvModelDir = "";
		String kcvModelFile = "";
		String rankFile = "";
		String prpFile = "";

		int nThread = -1; // nThread = #cpu-cores
		// for my personal use
		String indriRankingFile = "";
		String scoreFile = "";

		String configFile = "LTR.conf";
		cf = new Configer(configFile);

		// Notice.printNotice( trainMetric, rType);
		trainFile = cf.getString("dataset.train");
		rankerType = cf.getInt("ranker");
		featureDescriptionFile = cf.getString("dataset.feature");
		trainMetric = cf.getString("model.metric2t");
		testMetric = cf.getString("model.metric2T");
		// set configer for ranker
		setRanker(rankerType);

		ERRScorer.MAX = Math.pow(2, cf.getDouble("gmax"));
		qrelFile = cf.getString("dataset.qrelFile");

		ttSplit = cf.getFloat("dataset.tts");
		tvSplit = cf.getFloat("dataset.tvs");
		foldCV = cf.getInt("dataset.kcv");

		validationFile = cf.getString("dataset.validate");

		testFile = cf.getString("Testing.dataset");
		String[] split = testFile.split("|");
		for (String testFileName : split) {
			testFiles.add(cf.getString("Testing.dataset.dir") + testFileName);
		}

		String norm = cf.getString("norm");
		if (norm.compareTo("no-normalization") != 0) {
			Evaluator.normalize = true;

			if (norm.compareTo("sum") == 0)
				Evaluator.nml = new SumNormalizor();
			else if (norm.compareTo("zscore") == 0)
				Evaluator.nml = new ZScoreNormalizor();
			else if (norm.compareTo("linear") == 0)
				Evaluator.nml = new LinearNormalizer();
			else {
				Logs.error("Unknown normalizor: " + norm);
				Logs.error("System will now exit.");
				System.exit(1);
			}
		}
		String sparse = cf.getString("sparse");
		if (sparse.equals("false")) {
			useSparseRepresentation = false;
		} else {
			useSparseRepresentation = true;
		}
		String model_save_name = cf.getString("model.save.name");
		
		//save the model by default name if the model is learning and name's null
		if (model_save_name.equals("")&&cf.getInt("ranker")<10) {
			String destPath = super_utils.io.FileIO.makeDirectory("Results/"
					+ rType[cf.getInt("ranker")]);
			String destFile = destPath + rType[cf.getInt("ranker")] + "@"
					+ Dates.now() + ".model";
			model_save_name = destFile;
		}
		
		Evaluator.modelFile = model_save_name;
		kcvModelDir = cf.getString("model.kcvmd");
		kcvModelFile = cf.getString("model.kcvm.name");

		String silent = cf.getString("silent");

		if (silent.equals("false")) {
			Ranker.verbose = false;
		} else {
			Ranker.verbose = true;
		}

		savedModelFile = cf.getString("Testing.model.dir");

		split = savedModelFile.split(",");
		for (String name : split) {
			savedModelFiles.add(cf.getString("Testing.model.dir") + name);
		}

		prpFile = cf.getString("model.preRanklink.file");

		rankFile = cf.getString("Testing.rank");

		scoreFile = cf.getString("Testing.score");

		nThread = cf.getInt("thread");

		if (nThread == -1)
			nThread = Runtime.getRuntime().availableProcessors();
		MyThreadPool.init(nThread);
		// 默认为相同的评价方式
		if (testMetric.compareTo("") == 0)
			testMetric = trainMetric;

		Logs.info("");
		// Logs.info((keepOrigFeatures)?"Keep orig. features":"Discard orig. features");
		Logs.info("[+] General Parameters:");
		Evaluator e = new Evaluator(rType2[rankerType], trainMetric, testMetric);

		// 训练集 或者是模型文件，
		if (trainFile.compareTo("") != 0) {
			// 打印各种提示信息
			trainModelPrint();

			// starting to do some work
			if (foldCV != -1) {
				if (kcvModelDir.compareTo("") != 0
						&& kcvModelFile.compareTo("") == 0)
					kcvModelFile = "default";
				e.evaluate(trainFile, featureDescriptionFile, foldCV, tvSplit,
						kcvModelDir, kcvModelFile);// models won't be saved if
													// kcvModelDir=""
			} else {
				if (ttSplit > 0.0)// we should use a held-out portion of the
									// training data for testing?
					// 把样本划分训练集和测试集
					e.evaluate(trainFile, validationFile,
							featureDescriptionFile, ttSplit);// no validation
																// will be done
																// if
																// validationFile=""
				// 把样本划分为训练集和验证集
				else if (tvSplit > 0.0)// should we use a portion of the
										// training data for validation?
					e.evaluate(trainFile, tvSplit, testFile,
							featureDescriptionFile);
				else
					// 指定验证集和测试集的情况
					e.evaluate(trainFile, validationFile, testFile,
							featureDescriptionFile);// All files except for
													// trainFile can be empty.
													// This will be handled
													// appropriately
			}
		}

		else // scenario: test a saved model
		{
			Logs.info("Model file:\t" + savedModelFile);
			Logs.info("Feature normalization: "
					+ ((Evaluator.normalize) ? Evaluator.nml.name() : "No"));
			if (rankFile.compareTo("") != 0) {
				if (scoreFile.compareTo("") != 0) {
					if (savedModelFiles.size() > 1)// models trained via
													// cross-validation
						e.score(savedModelFiles, rankFile, scoreFile);
					else
						// a single model
						e.score(savedModelFile, rankFile, scoreFile);
				} else if (indriRankingFile.compareTo("") != 0) {
					if (savedModelFiles.size() > 1)// models trained via
													// cross-validation
						e.rank(savedModelFiles, rankFile, indriRankingFile);
					else if (savedModelFiles.size() == 1)
						e.rank(savedModelFile, rankFile, indriRankingFile);
					// This is *ONLY* for my personal use. It is *NOT* exposed
					// via cmd-line
					// It will evaluate the input ranking (without being
					// re-ranked by any model) using any measure specified via
					// metric2T
					else
						e.rank(rankFile, indriRankingFile);
				} else {
					Logs.error("This function has been removed.");
					Logs.error("Consider using -score in addition to your current parameters, and do the ranking yourself based on these scores.");
					System.exit(1);
					// e.rank(savedModelFile, rankFile);
				}
			} else {
				Logs.info("Test metric:\t" + testMetric);
				if (testMetric.startsWith("ERR"))
					System.out
							.println("Highest relevance label (to compute ERR): "
									+ (int) SimpleMath.logBase2(ERRScorer.MAX));

				if (savedModelFile.compareTo("") != 0) {
					if (savedModelFiles.size() > 1)// models trained via
													// cross-validation
					{
						if (testFiles.size() > 1)
							e.test(savedModelFiles, testFiles, prpFile);
						else
							e.test(savedModelFiles, testFile, prpFile);
					} else if (savedModelFiles.size() == 1) // a single model
						e.test(savedModelFile, testFile, prpFile);
				} else if (scoreFile.compareTo("") != 0)
					e.testWithScoreFile(testFile, scoreFile);
				// It will evaluate the input ranking (without being re-ranked
				// by any model) using any measure specified via metric2T
				else
					e.test(testFile, prpFile);
			}
		}
		MyThreadPool.getInstance().shutdown();
	}

	// main settings
	public static boolean mustHaveRelDoc = false;
	public static boolean useSparseRepresentation = false;
	public static boolean normalize = false;
	public static Normalizer nml = new SumNormalizor();
	public static String modelFile = "";

	public static String qrelFile = "";// measure such as NDCG and MAP requires
										// "complete" judgment.
	// The relevance labels attached to our samples might be only a subset of
	// the entire relevance judgment set.
	// If we're working on datasets like Letor/Web10K or Yahoo! LTR, we can
	// totally ignore this parameter.
	// However, if we sample top-K documents from baseline run (e.g.
	// query-likelihood) to create training data for TREC collections,
	// there's a high chance some relevant document (the in qrel file TREC
	// provides) does not appear in our top-K list -- thus the calculation of
	// MAP and NDCG is no longer precise. If so, specify that "external"
	// relevance judgment here (via the -qrel cmd parameter)

	// tmp settings, for personal use
	public static String newFeatureFile = "";
	public static boolean keepOrigFeatures = false;
	public static int topNew = 2000;

	protected RankerFactory rFact = new RankerFactory();
	protected MetricScorerFactory mFact = new MetricScorerFactory();
	protected RankerTrainer trainer = new RankerTrainer();
	protected MetricScorer trainScorer = null;
	protected MetricScorer testScorer = null;
	protected List<MetricScorer> scorerList = null;
	protected RANKER_TYPE type = RANKER_TYPE.MART;

	static Configer cf = null;
	static String[] rType = new String[] { "MART", "RankNet", "RankBoost",
			"AdaRank", "Coordinate Ascent", "LambdaRank", "LambdaMART",
			"ListNet", "Random Forests", "Linear Regression",
			// baseLine
			"Random", "Content_Based", "Bonds_Based", "Influence_Based" };

	static RANKER_TYPE[] rType2 = new RANKER_TYPE[] { RANKER_TYPE.MART,
			RANKER_TYPE.RANKNET, RANKER_TYPE.RANKBOOST, RANKER_TYPE.ADARANK,
			RANKER_TYPE.COOR_ASCENT, RANKER_TYPE.LAMBDARANK,
			RANKER_TYPE.LAMBDAMART, RANKER_TYPE.LISTNET,
			RANKER_TYPE.RANDOM_FOREST, RANKER_TYPE.LINEAR_REGRESSION,
			// baseLine
			RANKER_TYPE.RANDOM, RANKER_TYPE.Content_Based,
			RANKER_TYPE.Bonds_Based, RANKER_TYPE.Influence_Based };

	public static void setRanker(int rankerType) {
		// Ranker-specific parameters
		// RankNet
		// 0: MART (gradient boosted regression tree)
		// 1: RankNet 2: RankBoost 3: AdaRank 4: Coordinate Ascent 6: LambdaMART
		// 7: ListNet 8: Random Forests 9 linear
		if (rankerType == 0) {

		} else if (rankerType == 1) {
			// RankNet.nIteration = cf.getInt("RankNet.epoch");
			RankNet.nHiddenLayer = cf.getInt("RankNet.layer");
			RankNet.nHiddenNodePerLayer = cf.getInt("RankNet.node");
			RankNet.learningRate = cf.getDouble("RankNet.lr");
		} else if (rankerType == 2) {
			// RankBoost
			RankBoost.nThreshold = cf.getInt("RankBoost.tc");
			// ranker-shared parameters
			int round = cf.getInt("round");
			RankBoost.nIteration = round;
		} else if (rankerType == 3) {
			// AdaRank
			String noeq = cf.getString("AdaRank.noeq");
			if (noeq.equals("true")) {
				AdaRank.trainWithEnqueue = false;
			} else {
				AdaRank.trainWithEnqueue = true;
			}
			// ranker-shared parameters
			int round = cf.getInt("round");
			AdaRank.nIteration = round;
			AdaRank.maxSelCount = cf.getInt("AdaRank.max");
		} else if (rankerType == 4) {
			// COORDINATE ASCENT
			CoorAscent.nRestart = cf.getInt("CoorAscent.r");
			CoorAscent.nMaxIteration = cf.getInt("CoorAscent.i");
			double reg = cf.getDouble("reg");
			CoorAscent.slack = reg;
			CoorAscent.regularized = true;
			double tolerance = cf.getDouble("tolerance");
			// AdaRank.tolerance = tolerance;
			CoorAscent.tolerance = tolerance;
		} else if (rankerType == 6) {
			int tree = cf.getInt("tree");
			LambdaMART.nThreshold = cf.getInt("RankBoost.tc");
			LambdaMART.nTrees = tree;
			int leaf = cf.getInt("leaf");
			LambdaMART.nTreeLeaves = leaf;
			float shrinkage = cf.getFloat("shrinkage");
			LambdaMART.learningRate = shrinkage;
			int mls = cf.getInt("minLeafSupport");
			LambdaMART.minLeafSupport = mls;
			LambdaMART.nRoundToStopEarly = cf.getInt("estop");
			LambdaMART.gcCycle = cf.getInt("gcc");
		} else if (rankerType == 7) {
			ListNet.nIteration = cf.getInt("RankNet.epoch");
			ListNet.learningRate = cf.getDouble("RankNet.lr");
		} else if (rankerType == 8) {
			// Random forest
			int tree = cf.getInt("tree");
			RFRanker.nTrees = tree;
			RFRanker.nBag = cf.getInt("RandomForests.bag");
			RFRanker.subSamplingRate = cf.getFloat("RandomForests.srate");
			RFRanker.featureSamplingRate = cf.getFloat("RandomForests.frate");
			int leaf = cf.getInt("leaf");
			RFRanker.nTreeLeaves = leaf;
			float shrinkage = cf.getFloat("shrinkage");
			RFRanker.learningRate = shrinkage;
			int mls = cf.getInt("mls");
			RFRanker.minLeafSupport = mls;
			int rt = cf.getInt("RandomForests.rtype");
			if (rt == 0 || rt == 6) {
				RFRanker.rType = rType2[rt];
			} else {
				Logs.error(rType[rt]
								+ " cannot be bagged. Random Forests only supports MART/LambdaMART.");
				Logs.error("System will now exit.");
				System.exit(1);
			}
		} else if (rankerType == 9) {
			LinearRegRank.lambda = cf.getDouble("LinearRegRank.lambda");
		}
		// baseline
		else if (rankerType == 10) {
			Logs.info("random guess");
		} else if (rankerType == 11) {
			Logs.info("Content_Based");
		} else if (rankerType == 12) {
			Logs.info("Bonds_Based");
		} else if (rankerType == 13) {
			Logs.info("Influence_Based");
		} else {
			Logs.info("no such ranker");
			System.exit(0);
		}

	}

	/**
	 * 
	 * @create_time：2015年1月5日下午7:09:45
	 * @modifie_time：2015年1月5日 下午7:09:45
	 */
	public static void trainModelPrint() {
		// 下面的都是打印出各种配置
		Logs.info("Training data:\t" + cf.getString("dataset.train"));

		// print out parameter settings 做交叉验证 设置test validation 等
		if (cf.getInt("dataset.kcv") != -1) {
			Logs.info("Cross validation: " + cf.getInt("dataset.kcv")
					+ " folds.");
			if (cf.getDouble("datasettvs") > 0)
				Logs.info("Train-Validation split: "
						+ cf.getDouble("dataset.tvs"));
		} else {// 不做交叉验证 则从train文件中划分 test集或者有test文件
			if (cf.getString("dataset.test").compareTo("") != 0)
				Logs.info("Test data:\t"
						+ cf.getString("dataset.test"));
			else if (cf.getFloat("dataset.tts") > 0)// choose to split training
													// data into train
				// and test
				Logs.info("Train-Test split: "
						+ cf.getFloat("dataset.tts"));

			if (cf.getString("dataset.validate").compareTo("") != 0)// the user
																	// has
																	// specified
				// the validation set
				Logs.info("Validation data:\t"
						+ cf.getString("dataset.validate"));
			else if (cf.getFloat("dataset.tvs") <= 0
					&& cf.getFloat("dataset.tvs") > 0)
				Logs.info("Train-Validation split: "
						+ cf.getFloat("dataset.tvs"));
		}

		Logs.info("Feature vector representation: "
				+ ((useSparseRepresentation) ? "Sparse" : "Dense") + ".");
		Logs.info("Ranking method:\t" + rType[cf.getInt("ranker")]);
		if (cf.getString("dataset.feature").compareTo("") != 0){}
			Logs.info("Feature description file:\t"
					+ cf.getString("dataset.feature"));
			System.out
					.println("Feature description file:\tUnspecified. All features will be used.");
		Logs.info("Train metric:\t" + cf.getString("model.metric2t"));
		Logs.info("Test metric:\t" + cf.getString("model.metric2T"));
		Logs.info("metric2s:\t" + cf.getString("model.metric2S"));
		if (cf.getString("model.metric2t").toUpperCase().startsWith("ERR")
				|| cf.getString("model.metric2T").toUpperCase()
						.startsWith("ERR"))
			Logs.info("Highest relevance label (to compute ERR): "
					+ (int) SimpleMath.logBase2(ERRScorer.MAX));
		if (cf.getString("dataset.qrelFile").compareTo("") != 0)
			System.out
					.println("TREC-format relevance judgment (only affects MAP and NDCG scores): "
							+ qrelFile);
		Logs.info("Feature normalization: "
				+ ((Evaluator.normalize) ? Evaluator.nml.name() : "No"));
		if (cf.getString("model.kcvmd").compareTo("") != 0)
			Logs.info("Models directory: "
					+ cf.getString("model.kcvmd"));
		if (cf.getString("model.kcvm.name").compareTo("") != 0)
			Logs.info("Models' name: "
					+ cf.getString("model.kcvm.name"));
		if (cf.getString("model.save.name").compareTo("") != 0)
			System.out
					.println("Model file: " + cf.getString("model.save.name"));
		// Logs.info("#threads:\t" + nThread);

		Logs.info("");
		// Logs.info("[+] " + rType[cf.getInt("ranker")]
		// + "'s Parameters:");
		// RankerFactory rf = new RankerFactory();
		//
		// rf.createRanker(rType2[cf.getInt("ranker")]).printParameters();
		Logs.info("");
	}

	/**
	 * @param rType
	 * @param trainMetric
	 * @param testMetric
	 */
	public Evaluator(RANKER_TYPE rType, METRIC trainMetric, METRIC testMetric) {
		this.type = rType;
		trainScorer = mFact.createScorer(trainMetric);
		testScorer = mFact.createScorer(testMetric);
		scorerList = mFact.createScorerList(cf.getInt("model.metric2S"));
	}

	/**
	 * @param rType
	 * @param trainMetric
	 * @param trainK
	 * @param testMetric
	 * @param testK
	 */
	public Evaluator(RANKER_TYPE rType, METRIC trainMetric, int trainK,
			METRIC testMetric, int testK) {
		this.type = rType;
		trainScorer = mFact.createScorer(trainMetric, trainK);
		testScorer = mFact.createScorer(testMetric, testK);
		scorerList = mFact.createScorerList(cf.getInt("model.metric2S"));
	}

	/**
	 * @param rType
	 * @param trainMetric
	 * @param testMetric
	 * @param k
	 */
	public Evaluator(RANKER_TYPE rType, METRIC trainMetric, METRIC testMetric,
			int k) {
		this.type = rType;
		trainScorer = mFact.createScorer(trainMetric, k);
		testScorer = mFact.createScorer(testMetric, k);
		scorerList = mFact.createScorerList(cf.getInt("model.metric2S"));
	}

	/**
	 * @param rType
	 * @param metric
	 * @param k
	 */
	public Evaluator(RANKER_TYPE rType, METRIC metric, int k) {
		this.type = rType;
		trainScorer = mFact.createScorer(metric, k);
		scorerList = mFact.createScorerList(cf.getInt("model.metric2S"));
		testScorer = trainScorer;
	}

	/**
	 * @param rType
	 *            排序方式
	 * @param trainMetric
	 *            选择的训练评价方法
	 * @param testMetric
	 *            选择的测试评价方法
	 */
	public Evaluator(RANKER_TYPE rType, String trainMetric, String testMetric) {
		this.type = rType;
		// create metric
		trainScorer = mFact.createScorer(trainMetric);
		testScorer = mFact.createScorer(testMetric);
		scorerList = mFact.createScorerList(cf.getInt("model.metric2S"));
		// 如果存在查询相关性文件不为空，则添加相关性

	}

	/**
	 * @param inputFile
	 * @return read the input feature file
	 * @create_time：2015年1月4日下午3:14:16
	 * @modifie_time：2015年1月4日 下午3:14:16
	 */
	public List<RankList> readInput(String inputFile) {
		return FeatureManager.readInput(inputFile, mustHaveRelDoc,
				useSparseRepresentation);
	}

	public void normalize(List<RankList> samples) {
		for (int i = 0; i < samples.size(); i++)
			nml.normalize(samples.get(i));
	}

	public void normalize(List<RankList> samples, int[] fids) {
		for (int i = 0; i < samples.size(); i++)
			nml.normalize(samples.get(i), fids);
	}

	public void normalizeAll(List<List<RankList>> samples, int[] fids) {
		for (int i = 0; i < samples.size(); i++)
			normalize(samples.get(i), fids);
	}

	public int[] readFeature(String featureDefFile) {
		if (featureDefFile.compareTo("") == 0)
			return null;
		return FeatureManager.readFeature(featureDefFile);
	}

	/**
	 * @param ranker
	 *            训练好参数的排序机
	 * @param rl
	 *            查询文档
	 * @return
	 * @create_time：2015年1月5日下午4:34:50
	 * @modifie_time：2015年1月5日 下午4:34:50
	 */
	public double evaluate(Ranker ranker, List<RankList> rl) {
		List<RankList> l = rl;
		if (ranker != null)
			l = ranker.rank(rl);
		return testScorer.score(l);
	}

	/**
	 * @param ranker
	 *            训练好参数的排序机
	 * @param rl
	 *            查询文档
	 * @return
	 * @create_time：2015年1月5日下午4:34:50
	 * @modifie_time：2015年1月5日 下午4:34:50
	 */
	public double[] evaluate_In2S(Ranker ranker, List<RankList> rl) {
		List<RankList> rankedl = rl;
		if (ranker != null)
			rankedl = ranker.rank(rl);
		//MAP,P@10,RR@10,ERR@10,NDCG@10
		//System.out.print("MAP");
		double[] result = new double[scorerList.size()];
		//run in pal
		ExecutorService executor = Executors.newFixedThreadPool(10);
		List<Future<Double>> results = new ArrayList<>();
		for (int j = 0; j < scorerList.size(); j++) {
			MetricScorer scorer = scorerList.get(j);
			metricScorerWork mk=new metricScorerWork( scorer, rankedl);
			results.add(executor.submit(mk));
		}
		for (int i = 0; i <scorerList.size(); i++) {
			try {
				result[i]=results.get(i).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executor.shutdownNow();
//		for (int j = 0; j < scorerList.size(); j++) {
//			MetricScorer scorer = scorerList.get(j);
//			result[j] = scorer.score(l);
//		}
		return result;
	}

	/**
	 * Evaluate the currently selected ranking algorithm using <training data,
	 * validation data, testing data and the defined features>.
	 * 
	 * @param trainFile
	 * @param validationFile
	 * @param testFile
	 * @param featureDefFile
	 */
	public void evaluate(String trainFile, String validationFile,
			String testFile, String featureDefFile) {
		List<RankList> train = readInput(trainFile);// read input

		List<RankList> validation = null;
		if (validationFile.compareTo("") != 0)
			validation = readInput(validationFile);

		List<RankList> test = null;
		if (testFile.compareTo("") != 0)
			test = readInput(testFile);

		int[] features = readFeature(featureDefFile);// read features
		if (features == null)// no features specified ==> use all features in
								// the training file
			features = FeatureManager.getFeatureFromSampleVector(train);

		if (normalize) {
			normalize(train, features);
			if (validation != null)
				normalize(validation, features);
			if (test != null)
				normalize(test, features);
		}

		RankerTrainer trainer = new RankerTrainer();
		Ranker ranker = trainer.train(type, train, validation, features,
				trainScorer, cf);

		if (test != null) {
			double rankScore = evaluate(ranker, test);
			Logs.info(testScorer.name() + " on test data: "
					+ SimpleMath.round(rankScore, 4));
		}
		if (modelFile.compareTo("") != 0) {
			Logs.info("");
			ranker.save(modelFile);
			Logs.info("Model saved to: " + modelFile);
		}
	}

	/**
	 * 处理划分训练集和测试集的情形 Evaluate the currently selected ranking algorithm using
	 * percenTrain% of the samples for training the rest for testing.
	 * 
	 * @param sampleFile
	 * @param validationFile
	 *            Empty string for "no validation data"
	 * @param featureDefFile
	 * @param percentTrain
	 */
	public void evaluate(String sampleFile, String validationFile,
			String featureDefFile, double percentTrain) {
		List<RankList> trainingData = new ArrayList<RankList>();
		List<RankList> testData = new ArrayList<RankList>();
		int[] features = prepareSplit(sampleFile, featureDefFile, percentTrain,
				normalize, trainingData, testData);
		List<RankList> validation = null;
		if (validationFile.compareTo("") != 0
				&& validationFile.compareTo("-1") != 0) {
			validation = readInput(validationFile);
			if (normalize)
				normalize(validation, features);
		}

		Ranker ranker = trainer.train(type, trainingData, validation, features,
				trainScorer, cf);

		double[] rankScore = evaluate_In2S(ranker, testData);
		int echoOfMetric=cf.getInt("model.metric2S");
		System.out.println(scorerList.get(0).name());
		System.out.println(SimpleMath.round(rankScore[0], 4));
		System.out.println("");	
		for (int round = 0; round <echoOfMetric; round++) {
			System.out.println(round+1);
			for (int i = 1; i < 5; i++) {
				System.out.print(scorerList.get(round*4+i).name() + " | ");
			}
			System.out.println("");	
			for (int i = 1; i < 5; i++) {
				System.out.print(SimpleMath.round(rankScore[round*4+i], 4)+ " | ");
			}
			System.out.println("");	
			System.out.println("");	
		}
		
		if (modelFile.compareTo("") != 0) {
			Logs.info("");
			ranker.save(modelFile);
			Logs.info("Model saved to: " + modelFile);
		}
	}

	/**
	 * 处理划分训练集和验证集的情形 Evaluate the currently selected ranking algorithm using
	 * percenTrain% of the training samples for training the rest as validation
	 * data. Test data is specified separately.
	 * 
	 * @param trainFile
	 * @param percentTrain
	 * @param testFile
	 *            Empty string for "no test data"
	 * @param featureDefFile
	 */
	public void evaluate(String trainFile, double percentTrain,
			String testFile, String featureDefFile) {
		List<RankList> train = new ArrayList<RankList>();
		List<RankList> validation = new ArrayList<RankList>();
		int[] features = prepareSplit(trainFile, featureDefFile, percentTrain,
				normalize, train, validation);
		List<RankList> test = null;
		if (testFile.compareTo("") != 0) {
			test = readInput(testFile);
			if (normalize)
				normalize(test, features);
		}

		RankerTrainer trainer = new RankerTrainer();
		Ranker ranker = trainer.train(type, train, validation, features,
				trainScorer, cf);

		if (test != null) {
			double rankScore = evaluate(ranker, test);
			Logs.info(testScorer.name() + " on test data: "
					+ SimpleMath.round(rankScore, 4));
		}
		if (modelFile.compareTo("") != 0) {
			Logs.info("");
			ranker.save(modelFile);
			Logs.info("Model saved to: " + modelFile);
		}
	}

	/**
	 * 处理带k-fold 但是不带验证集的情形 Evaluate the currently selected ranking algorithm
	 * using <data, defined features> with k-fold cross validation.
	 * 
	 * @param sampleFile
	 * @param featureDefFile
	 * @param nFold
	 * @param modelDir
	 * @param modelFile
	 */
	public void evaluate(String sampleFile, String featureDefFile, int nFold,
			String modelDir, String modelFile) {
		evaluate(sampleFile, featureDefFile, nFold, -1, modelDir, modelFile);
	}

	/**
	 * 带fold 的计算方式 Evaluate the currently selected ranking algorithm using
	 * <data, defined features> with k-fold cross validation.
	 * 
	 * @param sampleFile
	 *            样本文件名
	 * @param featureDefFile
	 *            特征描述文件
	 * @param nFold
	 *            交叉
	 * @param tvs
	 *            训练集和验证集的比例 Train-validation split ratio.
	 * @param modelDir
	 *            模型保存路径
	 * @param modelFile
	 *            模型保存名称
	 */
	public void evaluate(String sampleFile, String featureDefFile, int nFold,
			float tvs, String modelDir, String modelFile) {
		List<List<RankList>> trainingData = new ArrayList<List<RankList>>();
		List<List<RankList>> validationData = new ArrayList<List<RankList>>();
		List<List<RankList>> testData = new ArrayList<List<RankList>>();
		// read all samples
		List<RankList> samples = FeatureManager.readInput(sampleFile);
		// get features
		int[] features = readFeature(featureDefFile);// read features
		if (features == null)// no features specified ==> use all features in
								// the training file
			features = FeatureManager.getFeatureFromSampleVector(samples);
		FeatureManager.prepareCV(samples, nFold, tvs, trainingData,
				validationData, testData);
		// normalization
		if (normalize) {
			for (int i = 0; i < nFold; i++) {
				normalizeAll(trainingData, features);
				normalizeAll(validationData, features);
				normalizeAll(testData, features);
			}
		}

		Ranker ranker = null;
		double scoreOnTrain = 0.0;
		double scoreOnTest = 0.0;
		double totalScoreOnTest = 0.0;
		int totalTestSampleSize = 0;
		// 初始化评分值
		double[][] scores = new double[nFold][];
		for (int i = 0; i < nFold; i++)
			scores[i] = new double[] { 0.0, 0.0 };

		// 循环每个fold
		for (int i = 0; i < nFold; i++) {
			List<RankList> train = trainingData.get(i);
			List<RankList> vali = null;
			if (tvs > 0)
				vali = validationData.get(i);
			List<RankList> test = testData.get(i);
			// 训练器
			RankerTrainer trainer = new RankerTrainer();
			ranker = trainer
					.train(type, train, vali, features, trainScorer, cf);

			// 获取在测试集上的评分
			double s2 = evaluate(ranker, test);
			// 获取在训练集上的评分，相加 最后计算平均值
			scoreOnTrain += ranker.getScoreOnTrainingData();
			// 获取在测试集上的评分，相加最后计算平均值
			scoreOnTest += s2;
			totalScoreOnTest += s2 * test.size();
			totalTestSampleSize += test.size();

			// save performance in each fold
			scores[i][0] = ranker.getScoreOnTrainingData();
			scores[i][1] = s2;
			// 保存模型，如果有需要的话
			if (modelDir.compareTo("") != 0) {
				ranker.save(FileUtils.makePathStandard(modelDir) + "f"
						+ (i + 1) + "." + modelFile);
				Logs.info("Fold-" + (i + 1) + " model saved to: "
						+ modelFile);
			}
		}

		// 输出结果集
		Logs.info("Summary:");
		Logs.info(testScorer.name() + "\t|   Train\t| Test");
		Logs.info("----------------------------------");
		for (int i = 0; i < nFold; i++)
			Logs.info("Fold " + (i + 1) + "\t|   "
					+ SimpleMath.round(scores[i][0], 4) + "\t|  "
					+ SimpleMath.round(scores[i][1], 4) + "\t");
		Logs.info("----------------------------------");
		Logs.info("Avg.\t|   "
				+ SimpleMath.round(scoreOnTrain / nFold, 4) + "\t|  "
				+ SimpleMath.round(scoreOnTest / nFold, 4) + "\t");
		Logs.info("----------------------------------");
		Logs.info("Total\t|   " + "\t" + "\t|  "
				+ SimpleMath.round(totalScoreOnTest / totalTestSampleSize, 4)
				+ "\t");
	}

	/**
	 * 计算输入的测试集在metric2t上面的性能 Evaluate the performance (in -metric2T) of the
	 * input rankings
	 * 
	 * @param testFile
	 *            Input rankings
	 */
	public void test(String testFile) {
		List<RankList> test = readInput(testFile);
		double rankScore = evaluate(null, test);
		Logs.info(testScorer.name() + " on test data: "
				+ SimpleMath.round(rankScore, 4));
	}

	/**
	 * 计算输入的测试集在metric2t上面的性能，并且写人文件中
	 * 
	 * @param testFile
	 * @param prpFile
	 * @create_time：2015年1月5日下午6:49:21
	 * @modifie_time：2015年1月5日 下午6:49:21
	 */
	public void test(String testFile, String prpFile) {
		List<RankList> test = readInput(testFile);
		double rankScore = 0.0;
		List<String> ids = new ArrayList<String>();
		List<Double> scores = new ArrayList<Double>();
		for (int i = 0; i < test.size(); i++) {
			RankList l = test.get(i);
			double score = testScorer.score(l);
			ids.add(l.getID());
			scores.add(score);
			rankScore += score;
		}
		rankScore /= test.size();
		ids.add("all");
		scores.add(rankScore);
		Logs.info(testScorer.name() + " on test data: "
				+ SimpleMath.round(rankScore, 4));
		if (prpFile.compareTo("") != 0) {
			savePerRankListPerformanceFile(ids, scores, prpFile);
			Logs.info("Per-ranked list performance saved to: "
					+ prpFile);
		}
	}

	/**
	 * 计算输入的测试集在训练好的模型上的评分情形，并且可以写入文件 Evaluate the performance (in -metric2T) of
	 * a pre-trained model. Save its performance on each of the ranked list if
	 * this is specified.
	 * 
	 * @param modelFile
	 *            Pre-trained model
	 * @param testFile
	 *            Test data
	 * @param prpFile
	 *            Per-ranked list performance file: Model's performance on each
	 *            of the ranked list. These won't be saved if prpFile="".
	 */
	public void test(String modelFile, String testFile, String prpFile) {
		Ranker ranker = rFact.loadRankerFromFile(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if (normalize)
			normalize(test, features);

		double rankScore = 0.0;
		List<String> ids = new ArrayList<String>();
		List<Double> scores = new ArrayList<Double>();
		for (int i = 0; i < test.size(); i++) {
			RankList l = ranker.rank(test.get(i));
			double score = testScorer.score(l);
			ids.add(l.getID());
			scores.add(score);
			rankScore += score;
		}
		rankScore /= test.size();
		ids.add("all");
		scores.add(rankScore);
		Logs.info(testScorer.name() + " on test data: "
				+ SimpleMath.round(rankScore, 4));
		if (prpFile.compareTo("") != 0) {
			savePerRankListPerformanceFile(ids, scores, prpFile);
			Logs.info("Per-ranked list performance saved to: "
					+ prpFile);
		}
	}

	/**
	 * 计算输入的测试集在训练好的多个模型上的评分情形，并且可以写入文件 Evaluate the performance (in -metric2T)
	 * of k pre-trained models. Data in the test file will be splitted into k
	 * fold, where k=|models|. Each model will be evaluated on the data from the
	 * corresponding fold.
	 * 
	 * @param modelFiles
	 *            Pre-trained models
	 * @param testFile
	 *            Test data
	 * @param prpFile
	 *            Per-ranked list performance file: Model's performance on each
	 *            of the ranked list. These won't be saved if prpFile="".
	 */
	public void test(List<String> modelFiles, String testFile, String prpFile) {
		List<List<RankList>> trainingData = new ArrayList<List<RankList>>();
		List<List<RankList>> testData = new ArrayList<List<RankList>>();
		// read all samples
		int nFold = modelFiles.size();
		List<RankList> samples = FeatureManager.readInput(testFile);
		System.out.print("Preparing " + nFold + "-fold test data... ");
		FeatureManager.prepareCV(samples, nFold, trainingData, testData);
		Logs.info("[Done.]");
		double rankScore = 0.0;
		List<String> ids = new ArrayList<String>();
		List<Double> scores = new ArrayList<Double>();
		for (int f = 0; f < nFold; f++) {
			List<RankList> test = testData.get(f);
			Ranker ranker = rFact.loadRankerFromFile(modelFiles.get(f));
			int[] features = ranker.getFeatures();
			if (normalize)
				normalize(test, features);

			for (int i = 0; i < test.size(); i++) {
				RankList l = ranker.rank(test.get(i));
				double score = testScorer.score(l);
				ids.add(l.getID());
				scores.add(score);
				rankScore += score;
			}
		}
		rankScore = rankScore / ids.size();
		ids.add("all");
		scores.add(rankScore);
		Logs.info(testScorer.name() + " on test data: "
				+ SimpleMath.round(rankScore, 4));
		if (prpFile.compareTo("") != 0) {
			savePerRankListPerformanceFile(ids, scores, prpFile);
			Logs.info("Per-ranked list performance saved to: "
					+ prpFile);
		}
	}

	/**
	 * 计算输入的多个测试集在训练好的多个模型上的评分情形，并且可以写入文件 Similar to the above, except data has
	 * already been splitted. The k-th model will be applied on the k-th test
	 * file.
	 * 
	 * @param modelFiles
	 * @param testFiles
	 * @param prpFile
	 */
	public void test(List<String> modelFiles, List<String> testFiles,
			String prpFile) {
		int nFold = modelFiles.size();
		double rankScore = 0.0;
		List<String> ids = new ArrayList<String>();
		List<Double> scores = new ArrayList<Double>();
		for (int f = 0; f < nFold; f++) {
			List<RankList> test = FeatureManager.readInput(testFiles.get(f));
			Ranker ranker = rFact.loadRankerFromFile(modelFiles.get(f));
			int[] features = ranker.getFeatures();
			if (normalize)
				normalize(test, features);

			for (int i = 0; i < test.size(); i++) {
				RankList l = ranker.rank(test.get(i));
				double score = testScorer.score(l);
				ids.add(l.getID());
				scores.add(score);
				rankScore += score;
			}
		}
		rankScore = rankScore / ids.size();
		ids.add("all");
		scores.add(rankScore);
		Logs.info(testScorer.name() + " on test data: "
				+ SimpleMath.round(rankScore, 4));
		if (prpFile.compareTo("") != 0) {
			savePerRankListPerformanceFile(ids, scores, prpFile);
			Logs.info("Per-ranked list performance saved to: "
					+ prpFile);
		}
	}

	/**
	 * Re-order the input rankings and measure their effectiveness (in
	 * -metric2T)
	 * 
	 * @param testFile
	 *            Input rankings
	 * @param scoreFile
	 *            The model score file on each of the documents
	 */
	public void testWithScoreFile(String testFile, String scoreFile) {
		try {
			List<RankList> test = readInput(testFile);
			String content = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(scoreFile), "ASCII"));
			List<Double> scores = new ArrayList<Double>();
			while ((content = in.readLine()) != null) {
				content = content.trim();
				if (content.compareTo("") == 0)
					continue;
				scores.add(Double.parseDouble(content));
			}
			in.close();
			int k = 0;
			for (int i = 0; i < test.size(); i++) {
				RankList rl = test.get(i);
				double[] s = new double[rl.size()];
				for (int j = 0; j < rl.size(); j++)
					s[j] = scores.get(k++);
				rl = new RankList(rl, MergeSorter.sort(s, false));
				test.set(i, rl);
			}

			double rankScore = evaluate(null, test);
			Logs.info(testScorer.name() + " on test data: "
					+ SimpleMath.round(rankScore, 4));
		} catch (Exception ex) {
			Logs.info(ex.toString());
		}
	}

	/**
	 * Write the model's score for each of the documents in a test rankings.
	 * 
	 * @param modelFile
	 *            Pre-trained model
	 * @param testFile
	 *            Test data
	 * @param outputFile
	 *            Output file
	 */
	public void score(String modelFile, String testFile, String outputFile) {
		Ranker ranker = rFact.loadRankerFromFile(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if (normalize)
			normalize(test, features);

		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "ASCII"));
			for (int i = 0; i < test.size(); i++) {
				RankList l = test.get(i);
				for (int j = 0; j < l.size(); j++) {
					out.write(l.getID() + "\t" + j + "\t"
							+ ranker.eval(l.get(j)) + "");
					out.newLine();
				}
			}
			out.close();
		} catch (Exception ex) {
			Logs.info("Error in Evaluator::rank(): " + ex.toString());
		}
	}

	/**
	 * Write the models' score for each of the documents in a test rankings.
	 * These test rankings are splitted into k chunks where k=|models|. Each
	 * model is applied on the data from the corresponding fold.
	 * 
	 * @param modelFiles
	 * @param testFile
	 * @param outputFile
	 */
	public void score(List<String> modelFiles, String testFile,
			String outputFile) {
		List<List<RankList>> trainingData = new ArrayList<List<RankList>>();
		List<List<RankList>> testData = new ArrayList<List<RankList>>();
		// read all samples
		int nFold = modelFiles.size();
		List<RankList> samples = FeatureManager.readInput(testFile);
		System.out.print("Preparing " + nFold + "-fold test data... ");
		FeatureManager.prepareCV(samples, nFold, trainingData, testData);
		Logs.info("[Done.]");
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "ASCII"));
			for (int f = 0; f < nFold; f++) {
				List<RankList> test = testData.get(f);
				Ranker ranker = rFact.loadRankerFromFile(modelFiles.get(f));
				int[] features = ranker.getFeatures();
				if (normalize)
					normalize(test, features);
				for (int i = 0; i < test.size(); i++) {
					RankList l = test.get(i);
					for (int j = 0; j < l.size(); j++) {
						out.write(l.getID() + "\t" + j + "\t"
								+ ranker.eval(l.get(j)) + "");
						out.newLine();
					}
				}
			}
			out.close();
		} catch (Exception ex) {
			Logs.info("Error in Evaluator::score(): " + ex.toString());
		}
	}

	/**
	 * Similar to the above, except data has already been splitted. The k-th
	 * model will be applied on the k-th test file.
	 * 
	 * @param modelFiles
	 * @param testFiles
	 * @param outputFile
	 */
	public void score(List<String> modelFiles, List<String> testFiles,
			String outputFile) {
		int nFold = modelFiles.size();
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "ASCII"));
			for (int f = 0; f < nFold; f++) {
				List<RankList> test = FeatureManager
						.readInput(testFiles.get(f));
				Ranker ranker = rFact.loadRankerFromFile(modelFiles.get(f));
				int[] features = ranker.getFeatures();
				if (normalize)
					normalize(test, features);
				for (int i = 0; i < test.size(); i++) {
					RankList l = test.get(i);
					for (int j = 0; j < l.size(); j++) {
						out.write(l.getID() + "\t" + j + "\t"
								+ ranker.eval(l.get(j)) + "");
						out.newLine();
					}
				}
			}
			out.close();
		} catch (Exception ex) {
			Logs.info("Error in Evaluator::score(): " + ex.toString());
		}
	}

	/**
	 * Use a pre-trained model to re-rank the test rankings. Save the output
	 * ranking in indri's run format
	 * 
	 * @param modelFile
	 * @param testFile
	 * @param indriRanking
	 */
	public void rank(String modelFile, String testFile, String indriRanking) {
		Ranker ranker = rFact.loadRankerFromFile(modelFile);
		int[] features = ranker.getFeatures();
		List<RankList> test = readInput(testFile);
		if (normalize)
			normalize(test, features);
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(indriRanking), "ASCII"));
			for (int i = 0; i < test.size(); i++) {
				RankList l = test.get(i);
				double[] scores = new double[l.size()];
				for (int j = 0; j < l.size(); j++)
					scores[j] = ranker.eval(l.get(j));
				int[] idx = MergeSorter.sort(scores, false);
				for (int j = 0; j < idx.length; j++) {
					int k = idx[j];
					String str = l.getID() + " Q0 "
							+ l.get(k).getDescription().replace("#", "").trim()
							+ " " + (j + 1) + " "
							+ SimpleMath.round(scores[k], 5) + " indri";
					out.write(str);
					out.newLine();
				}
			}
			out.close();
		} catch (Exception ex) {
			Logs.info("Error in Evaluator::rank(): " + ex.toString());
		}
	}

	/**
	 * Generate a ranking in Indri's format from the input ranking
	 * 
	 * @param testFile
	 * @param indriRanking
	 */
	public void rank(String testFile, String indriRanking) {
		List<RankList> test = readInput(testFile);
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(indriRanking), "ASCII"));
			for (int i = 0; i < test.size(); i++) {
				RankList l = test.get(i);
				for (int j = 0; j < l.size(); j++) {
					String str = l.getID() + " Q0 "
							+ l.get(j).getDescription().replace("#", "").trim()
							+ " " + (j + 1) + " "
							+ SimpleMath.round(1.0 - 0.0001 * j, 5) + " indri";
					out.write(str);
					out.newLine();
				}
			}
			out.close();
		} catch (Exception ex) {
			Logs.info("Error in Evaluator::rank(): " + ex.toString());
		}
	}

	/**
	 * Use k pre-trained models to re-rank the test rankings. Test rankings will
	 * be splitted into k fold, where k=|models|. Each model will be used to
	 * rank the data from the corresponding fold. Save the output ranking in
	 * indri's run format.
	 * 
	 * @param modelFiles
	 * @param testFile
	 * @param indriRanking
	 */
	public void rank(List<String> modelFiles, String testFile,
			String indriRanking) {
		List<List<RankList>> trainingData = new ArrayList<List<RankList>>();
		List<List<RankList>> testData = new ArrayList<List<RankList>>();
		// read all samples
		int nFold = modelFiles.size();
		List<RankList> samples = FeatureManager.readInput(testFile);
		System.out.print("Preparing " + nFold + "-fold test data... ");
		FeatureManager.prepareCV(samples, nFold, trainingData, testData);
		Logs.info("[Done.]");
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(indriRanking), "ASCII"));
			for (int f = 0; f < nFold; f++) {
				List<RankList> test = testData.get(f);
				Ranker ranker = rFact.loadRankerFromFile(modelFiles.get(f));
				int[] features = ranker.getFeatures();
				if (normalize)
					normalize(test, features);

				for (int i = 0; i < test.size(); i++) {
					RankList l = test.get(i);
					double[] scores = new double[l.size()];
					for (int j = 0; j < l.size(); j++)
						scores[j] = ranker.eval(l.get(j));
					int[] idx = MergeSorter.sort(scores, false);
					for (int j = 0; j < idx.length; j++) {
						int k = idx[j];
						String str = l.getID()
								+ " Q0 "
								+ l.get(k).getDescription().replace("#", "")
										.trim() + " " + (j + 1) + " "
								+ SimpleMath.round(scores[k], 5) + " indri";
						out.write(str);
						out.newLine();
					}
				}
			}
			out.close();
		} catch (Exception ex) {
			Logs.info("Error in Evaluator::rank(): " + ex.toString());
		}
	}

	/**
	 * Similar to the above, except data has already been splitted. The k-th
	 * model will be applied on the k-th test file.
	 * 
	 * @param modelFiles
	 * @param testFiles
	 * @param indriRanking
	 */
	public void rank(List<String> modelFiles, List<String> testFiles,
			String indriRanking) {
		int nFold = modelFiles.size();
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(indriRanking), "ASCII"));
			for (int f = 0; f < nFold; f++) {
				List<RankList> test = FeatureManager
						.readInput(testFiles.get(f));
				Ranker ranker = rFact.loadRankerFromFile(modelFiles.get(f));
				int[] features = ranker.getFeatures();
				if (normalize)
					normalize(test, features);

				for (int i = 0; i < test.size(); i++) {
					RankList l = test.get(i);
					double[] scores = new double[l.size()];
					for (int j = 0; j < l.size(); j++)
						scores[j] = ranker.eval(l.get(j));
					int[] idx = MergeSorter.sort(scores, false);
					for (int j = 0; j < idx.length; j++) {
						int k = idx[j];
						String str = l.getID()
								+ " Q0 "
								+ l.get(k).getDescription().replace("#", "")
										.trim() + " " + (j + 1) + " "
								+ SimpleMath.round(scores[k], 5) + " indri";
						out.write(str);
						out.newLine();
					}
				}
			}
			out.close();
		} catch (Exception ex) {
			Logs.error("Error in Evaluator::rank(): " + ex.toString());
		}
	}

	/**
	 * Split the input file into two with respect to a specified split size.
	 * 
	 * @param sampleFile
	 *            Input data file
	 * @param featureDefFile
	 *            Feature definition file (if it's an empty string, all features
	 *            in the input file will be used)
	 * @param percentTrain
	 *            How much of the input data will be used for training? (the
	 *            remaining will be reserved for test/validation)
	 * @param normalize
	 *            Whether to do normalization.
	 * @param trainingData
	 *            [Output] Training data (after splitting)
	 * @param testData
	 *            [Output] Test (or validation) data (after splitting)
	 * @return A list of ids of the features to be used for learning.
	 */
	private int[] prepareSplit(String sampleFile, String featureDefFile,
			double percentTrain, boolean normalize,
			List<RankList> trainingData, List<RankList> testData) {
		List<RankList> data = readInput(sampleFile);// read input
		int[] features = readFeature(featureDefFile);// read features
		if (features == null)// no features specified ==> use all features in
								// the training file
			features = FeatureManager.getFeatureFromSampleVector(data);

		if (normalize)
			normalize(data, features);

		FeatureManager.prepareSplit(data, percentTrain, trainingData, testData);
		return features;
	}

	/**
	 * Save systems' performance to file
	 * 
	 * @param ids
	 *            Ranked list IDs.
	 * @param scores
	 *            Evaluation score (in whatever measure specified/calculated
	 *            upstream such as NDCG@k, ERR@k, etc.)
	 * @param prpFile
	 *            Output filename.
	 */
	public void savePerRankListPerformanceFile(List<String> ids,
			List<Double> scores, String prpFile) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(prpFile)));
			for (int i = 0; i < ids.size(); i++) {
				// out.write(testScorer.name() + "   " + ids.get(i) + "   " +
				// SimpleMath.round(scores.get(i), 4));
				out.write(testScorer.name() + "   " + ids.get(i) + "   "
						+ scores.get(i));
				out.newLine();
			}
			out.close();
		} catch (Exception ex) {
			System.out
					.println("Error in Evaluator::savePerRankListPerformanceFile(): "
							+ ex.toString());
			System.exit(1);
		}
	}
	
}

class metricScorerWork implements Callable<Double>{
	final MetricScorer scorer ;
	double[] result;
	List<RankList> rl;
	public metricScorerWork(MetricScorer metricScorer,List<RankList> rl){
		this.scorer=metricScorer;
		this.rl=rl;
	}
	/* (non-Javadoc)
	 * @see java.util.concurrent.RecursiveTask#compute()
	 */
	@Override
	public Double call() {
		// TODO Auto-generated method stub
		return scorer.score(rl);
	}
	
}