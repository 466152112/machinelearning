#-*- conding:utf-8 -*-

import algorithms

_rmse = 0.
_map = 0.
_recall = 0.
_ndcg = 0.

folders = 6

for i in range(1, folders):
	trainpath = "../data/ml_100k/u"+ str(i) +".base"
	testpath = "../data/ml_100k/u"+str(i)+".test"

#trainpath = "../data/ml_100k_N/train10-2.txt"
#testpath = "../data/ml_100k_N/test10-2.txt"

# RMF

	rmf = algorithms.RMF(LR = 0.01,
						 RLR = 0.01,
						 Reg = 0.01,
						 Dimension = 5,
						 K = 10,
						 train = trainpath,
						 test = testpath)

	#RMSE, MAP, Recall, NDCG = rmf.BPRExe() # Bayesian Personalized Ranking MF for MAP
	#RMSE, MAP, Recall, NDCG = rmf.RateBPRMF() # Iterative Method based on BPRMF and MF
	RMSE, MAP, Recall, NDCG = rmf.exe() # Basic MF optimizing in RMSE
	#RMSE, MAP, Recall, NDCG = rmf.DCGPairWise() # CR-Pairwise
	#RMSE, MAP, Recall, NDCG = rmf.DCGMLE()

	_rmse += RMSE
	_map += MAP
	_recall += Recall
	_ndcg += NDCG

	print "RMSE %f MAP#5 %f Recall#5 %f NDCG %f"%(RMSE, MAP, Recall, NDCG)

#print
print "%d Folder Validation Results"%folders
print "RMSE %f MAP#5 %f Recall#5 %f NDCG %f"%(_rmse / folders, _map / folders, _recall / folders, _ndcg / folders)