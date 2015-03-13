/**
 * 
 */
package LTR.eval;

import java.text.DecimalFormat;

import LTR.learning.CoorAscent;
import LTR.learning.LinearRegRank;
import LTR.learning.RANKER_TYPE;
import LTR.learning.boosting.AdaRank;
import LTR.learning.boosting.RankBoost;
import LTR.learning.neuralnet.ListNet;
import LTR.learning.neuralnet.RankNet;
import LTR.learning.tree.LambdaMART;
import LTR.learning.tree.RFRanker;
import LTR.metric.ERRScorer;
import LTR.utilities.SimpleMath;

/**   
 *    
 * @progject_name：LearningRank   
 * @class_name：Notice   
 * @class_describe：   
 * @creator：zhouge   
 * @create_time：2015年1月2日 下午8:04:56   
 * @modifier：zhouge   
 * @modified_time：2015年1月2日 下午8:04:56   
 * @modified_note：   
 * @version    
 *    
 */
public class Notice {
	
	public static void printNotice(String trainMetric,String[] rType){
		System.out.println("Usage: java -jar RankLib.jar <Params>");
		System.out.println("Params:");
		System.out.println("  [+] Training (+ tuning and evaluation)");
		System.out.println("\t-train <file>\t\tTraining data");
		System.out.println("\t-ranker <type>\t\tSpecify which ranking algorithm to use");
		System.out.println("\t\t\t\t0: MART (gradient boosted regression tree)");
		System.out.println("\t\t\t\t1: RankNet");
		System.out.println("\t\t\t\t2: RankBoost");
		System.out.println("\t\t\t\t3: AdaRank");
		System.out.println("\t\t\t\t4: Coordinate Ascent");
		System.out.println("\t\t\t\t6: LambdaMART");
		System.out.println("\t\t\t\t7: ListNet");
		System.out.println("\t\t\t\t8: Random Forests");
		System.out.println("\t\t\t\t9: Linear regression (L2 regularization)");
		System.out.println("\t[ -feature <file> ]\tFeature description file: list features to be considered by the learner, each on a separate line");
		System.out.println("\t\t\t\tIf not specified, all features will be used.");
		//System.out.println("\t[ -metric2t <metric> ]\tMetric to optimize on the training data. Supported: MAP, NDCG@k, DCG@k, P@k, RR@k, BEST@k, ERR@k (default=" + trainMetric + ")");
		System.out.println("\t[ -metric2t <metric> ]\tMetric to optimize on the training data. Supported: MAP, NDCG@k, DCG@k, P@k, RR@k, ERR@k (default=" + trainMetric + ")");
		System.out.println("\t[ -gmax <label> ]\tHighest judged relevance label. It affects the calculation of ERR (default=" + (int)SimpleMath.logBase2(ERRScorer.MAX) + ", i.e. 5-point scale {0,1,2,3,4})");
		//System.out.println("\t[ -qrel <file> ]\tTREC-style relevance judgment file. It only affects MAP and NDCG (default=unspecified)");
		System.out.println("\t[ -silent ]\t\tDo not print progress messages (which are printed by default)");
		
		System.out.println("");
		//System.out.println("        Use the entire specified training data");
		System.out.println("\t[ -validate <file> ]\tSpecify if you want to tune your system on the validation data (default=unspecified)");
		System.out.println("\t\t\t\tIf specified, the final model will be the one that performs best on the validation data");
		System.out.println("\t[ -tvs <x \\in [0..1]> ]\tIf you don't have separate validation data, use this to set train-validation split to be (x)(1.0-x)");

		System.out.println("\t[ -save <model> ]\tSave the model learned (default=not-save)");
		
		System.out.println("");
		System.out.println("\t[ -test <file> ]\tSpecify if you want to evaluate the trained model on this data (default=unspecified)");
		System.out.println("\t[ -tts <x \\in [0..1]> ]\tSet train-test split to be (x)(1.0-x). -tts will override -tvs");
		System.out.println("\t[ -metric2T <metric> ]\tMetric to evaluate on the test data (default to the same as specified for -metric2t)");
		
		System.out.println("");
		System.out.println("\t[ -norm <method>]\tNormalize all feature vectors (default=no-normalization). Method can be:");
		System.out.println("\t\t\t\tsum: normalize each feature by the sum of all its values");
		System.out.println("\t\t\t\tzscore: normalize each feature by its mean/standard deviation");
		System.out.println("\t\t\t\tlinear: normalize each feature by its min/max values");
		
		//System.out.println("");
		//System.out.println("\t[ -sparse ]\t\tUse sparse representation for all feature vectors (default=dense)");
		
		System.out.println("");
		System.out.println("\t[ -kcv <k> ]\t\tSpecify if you want to perform k-fold cross validation using the specified training data (default=NoCV)");
		System.out.println("\t\t\t\t-tvs can be used to further reserve a portion of the training data in each fold for validation");
		//System.out.println("\t\t\t\tData for each fold is created from sequential partitions of the training data.");
		//System.out.println("\t\t\t\tRandomized partitioning can be done by shuffling the training data in advance.");
		//System.out.println("\t\t\t\tType \"java -cp bin/RankLib.jar ciir.umass.edu.feature.FeatureManager\" for help with shuffling.");
		
		System.out.println("\t[ -kcvmd <dir> ]\tDirectory for models trained via cross-validation (default=not-save)");
		System.out.println("\t[ -kcvmn <model> ]\tName for model learned in each fold. It will be prefix-ed with the fold-number (default=empty)");
		
		System.out.println("");
		System.out.println("    [-] RankNet-specific parameters");
		System.out.println("\t[ -epoch <T> ]\t\tThe number of epochs to train (default=" + RankNet.nIteration + ")");
		System.out.println("\t[ -layer <layer> ]\tThe number of hidden layers (default=" + RankNet.nHiddenLayer + ")");
		System.out.println("\t[ -node <node> ]\tThe number of hidden nodes per layer (default=" + RankNet.nHiddenNodePerLayer + ")");
		System.out.println("\t[ -lr <rate> ]\t\tLearning rate (default=" + (new DecimalFormat("###.########")).format(RankNet.learningRate) + ")");
		
		System.out.println("");
		System.out.println("    [-] RankBoost-specific parameters");
		System.out.println("\t[ -round <T> ]\t\tThe number of rounds to train (default=" + RankBoost.nIteration + ")");
		System.out.println("\t[ -tc <k> ]\t\tNumber of threshold candidates to search. -1 to use all feature values (default=" + RankBoost.nThreshold + ")");
		
		System.out.println("");
		System.out.println("    [-] AdaRank-specific parameters");
		System.out.println("\t[ -round <T> ]\t\tThe number of rounds to train (default=" + AdaRank.nIteration + ")");
		System.out.println("\t[ -noeq ]\t\tTrain without enqueuing too-strong features (default=unspecified)");
		System.out.println("\t[ -tolerance <t> ]\tTolerance between two consecutive rounds of learning (default=" + AdaRank.tolerance + ")");
		System.out.println("\t[ -max <times> ]\tThe maximum number of times can a feature be consecutively selected without changing performance (default=" + AdaRank.maxSelCount + ")");

		System.out.println("");
		System.out.println("    [-] Coordinate Ascent-specific parameters");
		System.out.println("\t[ -r <k> ]\t\tThe number of random restarts (default=" + CoorAscent.nRestart + ")");
		System.out.println("\t[ -i <iteration> ]\tThe number of iterations to search in each dimension (default=" + CoorAscent.nMaxIteration + ")");
		System.out.println("\t[ -tolerance <t> ]\tPerformance tolerance between two solutions (default=" + CoorAscent.tolerance + ")");
		System.out.println("\t[ -reg <slack> ]\tRegularization parameter (default=no-regularization)");

		System.out.println("");
		System.out.println("    [-] {MART, LambdaMART}-specific parameters");
		System.out.println("\t[ -tree <t> ]\t\tNumber of trees (default=" + LambdaMART.nTrees + ")");
		System.out.println("\t[ -leaf <l> ]\t\tNumber of leaves for each tree (default=" + LambdaMART.nTreeLeaves + ")");
		System.out.println("\t[ -shrinkage <factor> ]\tShrinkage, or learning rate (default=" + LambdaMART.learningRate + ")");
		System.out.println("\t[ -tc <k> ]\t\tNumber of threshold candidates for tree spliting. -1 to use all feature values (default=" + LambdaMART.nThreshold + ")");
		System.out.println("\t[ -mls <n> ]\t\tMin leaf support -- minimum % of docs each leaf has to contain (default=" + LambdaMART.minLeafSupport + ")");
		System.out.println("\t[ -estop <e> ]\t\tStop early when no improvement is observed on validaton data in e consecutive rounds (default=" + LambdaMART.nRoundToStopEarly + ")");

		System.out.println("");
		System.out.println("    [-] ListNet-specific parameters");
		System.out.println("\t[ -epoch <T> ]\t\tThe number of epochs to train (default=" + ListNet.nIteration + ")");
		System.out.println("\t[ -lr <rate> ]\t\tLearning rate (default=" + (new DecimalFormat("###.########")).format(ListNet.learningRate) + ")");

		System.out.println("");
		System.out.println("    [-] Random Forests-specific parameters");
		System.out.println("\t[ -bag <r> ]\t\tNumber of bags (default=" + RFRanker.nBag + ")");
		System.out.println("\t[ -srate <r> ]\t\tSub-sampling rate (default=" + RFRanker.subSamplingRate + ")");
		System.out.println("\t[ -frate <r> ]\t\tFeature sampling rate (default=" + RFRanker.featureSamplingRate + ")");
		int type = (RFRanker.rType.ordinal()-RANKER_TYPE.MART.ordinal());
		System.out.println("\t[ -rtype <type> ]\tRanker to bag (default=" + type + ", i.e. " + rType[type] + ")");
		System.out.println("\t[ -tree <t> ]\t\tNumber of trees in each bag (default=" + RFRanker.nTrees + ")");
		System.out.println("\t[ -leaf <l> ]\t\tNumber of leaves for each tree (default=" + RFRanker.nTreeLeaves + ")");
		System.out.println("\t[ -shrinkage <factor> ]\tShrinkage, or learning rate (default=" + RFRanker.learningRate + ")");
		System.out.println("\t[ -tc <k> ]\t\tNumber of threshold candidates for tree spliting. -1 to use all feature values (default=" + RFRanker.nThreshold + ")");
		System.out.println("\t[ -mls <n> ]\t\tMin leaf support -- minimum % of docs each leaf has to contain (default=" + RFRanker.minLeafSupport + ")");

		System.out.println("");
		System.out.println("    [-] Linear Regression-specific parameters");
		System.out.println("\t[ -L2 <reg> ]\t\tL2 regularization parameter (default=" + LinearRegRank.lambda + ")");

		System.out.println("");
		System.out.println("  [+] Testing previously saved models");
		System.out.println("\t-load <model>\t\tThe model to load");
		System.out.println("\t\t\t\tMultiple -load can be used to specify models from multiple folds (in increasing order),");
		System.out.println("\t\t\t\t  in which case the test/rank data will be partitioned accordingly.");
		System.out.println("\t-test <file>\t\tTest data to evaluate the model(s) (specify either this or -rank but not both)");
		System.out.println("\t-rank <file>\t\tRank the samples in the specified file (specify either this or -test but not both)");
		System.out.println("\t[ -metric2T <metric> ]\tMetric to evaluate on the test data (default=" + trainMetric + ")");
		System.out.println("\t[ -gmax <label> ]\tHighest judged relevance label. It affects the calculation of ERR (default=" + (int)SimpleMath.logBase2(ERRScorer.MAX) + ", i.e. 5-point scale {0,1,2,3,4})");
		System.out.println("\t[ -score <file>]\tStore ranker's score for each object being ranked (has to be used with -rank)");
		//System.out.println("\t[ -qrel <file> ]\tTREC-style relevance judgment file. It only affects MAP and NDCG (default=unspecified)");
		System.out.println("\t[ -idv <file> ]\t\tSave model performance (in test metric) on individual ranked lists (has to be used with -test)");
		System.out.println("\t[ -norm ]\t\tNormalize feature vectors (similar to -norm for training/tuning)");
		//System.out.println("\t[ -sparse ]\t\tUse sparse representation for all feature vectors (default=dense)");

		System.out.println("");
	}

}
