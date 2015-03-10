#-*- conding:utf-8 -*-

import numpy as np
import sys, math
import random

# Updating Strategies

# Updat(), BPR_Update()


class RMF(object):
	""" Def functions
		load_dataset, init_model, Update, Evaluate
	"""
	def __init__(self, LR, RLR, Reg, Dimension, K, train, test):
		""" arg is a list of parameters with format
		    [Learning Ratio, Regularization, Dimension, Train, Test]
		"""
		super(RMF, self).__init__()
		self.LR = LR
		self.RLR = RLR
		self.Reg = Reg 
		self.Dimension = Dimension
		self.trainpath = train
		self.testpath = test
		self.TK = K

	def exe(self):
		self.load_dataset()
		self.init_model()
		
		# Update feature matrices untile reaching the stop criterion
		self.Update()
		previous_rmse = self.RMSE(bias=False)
		StopCri = previous_rmse

		epoch = 0
		while StopCri >= 0:
			epoch += 1
			self.Update()
			current_rmse = self.RMSE(bias=False)

			StopCri = previous_rmse - current_rmse
			
			# Reach the stop criterion
			if StopCri >= 0:
				previous_rmse = current_rmse

			print "RMF Updating Epoch %d Current RMSE %f"%(epoch, previous_rmse)

		MeanAP, MeanRecall = self.Evaluate(K = self.TK)
		ndcg_at_k = self.nDCG(K = self.TK)

		return previous_rmse, MeanAP, MeanRecall, ndcg_at_k

	def BPRExe(self):
		""" Implete Bayesian Personalized Ranking optimization strategy

			Updat MAP metric
		"""
		self.load_dataset()
		self.init_model()

		# Update feature matrices untile reaching the stop criterion
		self.ExBPR_Update()
		rmse = self.RMSE()
		previous_map, previous_recall = self.Evaluate(K = self.TK)
		StopCri = previous_map

		epoch = 0
		while StopCri >= 0:
			epoch += 1
			self.ExBPR_Update()
			current_map, current_recall = self.Evaluate(K = self.TK)

			StopCri = current_map - previous_map
			
			# Reach the stop criterion
			if StopCri >= 0:
				previous_map = current_map
				previous_recall = current_recall
				rmse = self.RMSE()

			#print "BPR-RMF Updating Epoch %d Current MAP %f"%(epoch, previous_map)

		MeanAP, MeanRecall = previous_map, previous_recall
		ndcg_at_k = self.nDCG(K = self.TK)

		return rmse, MeanAP, MeanRecall, ndcg_at_k
			

	def RateBPRMF(self):
		""" Implete Bayesian Personalized Ranking optimization strategy

			Updating MAP metric
		"""
		self.load_dataset()
		self.init_model()

		# # Update feature matrices untile reaching the stop criterion
		for i in range(0, 10):
			self.Update()
			previous_rmse = self.RMSE()
			StopCri = previous_rmse

			epoch = 0
			while StopCri >= 0:
				epoch += 1
				self.Update()
				current_rmse = self.RMSE()

				StopCri = previous_rmse - current_rmse
				
				# Reach the stop criterion
				if StopCri >= 0:
					previous_rmse = current_rmse

				print "%d Run RMF Updating Epoch %d Current RMSE %f"%(i + 1, epoch, previous_rmse)

			# Update feature matrices untile reaching the stop criterion
			self.ExBPR_Update()
			rmse = self.RMSE()
			previous_map, previous_recall = self.Evaluate(K = self.TK)
			StopCri = previous_map

			epoch = 0
			while StopCri >= 0:
				epoch += 1
				self.ExBPR_Update()
				current_map, current_recall = self.Evaluate(K = self.TK)

				StopCri = current_map - previous_map
				
				# Reach the stop criterion
				if StopCri >= 0:
					previous_map = current_map
					previous_recall = current_recall
					rmse = self.RMSE()

				print "%d Run BPR-RMF Updating Epoch %d Current MAP %f"%(i+1, epoch, previous_map)

			print "%d Run BPR-RMF NDCG Equals to %f"%(i + 1, self.nDCG(K = self.TK))

		MeanAP, MeanRecall = previous_map, previous_recall
		ndcg_at_k = self.nDCG(K = self.TK)

		return rmse, MeanAP, MeanRecall, ndcg_at_k		

	def DCGPairWise(self):
		""" Integrate rating and ranking tasks, optimize ranking algorithm for nDCG metric
		"""

		self.load_dataset()
		self.init_model()

		out_previous_ndcg = self.nDCG(K = self.TK)

		out_stopCri = out_previous_ndcg

		while out_stopCri >= 0:
			
			# # Update feature matrices untile reaching the stop criterion
			self.Update()
			previous_rmse = self.RMSE()
			StopCri = previous_rmse

			epoch = 0
			while StopCri >= 0:
				epoch += 1
				self.Update()
				current_rmse = self.RMSE()

				StopCri = previous_rmse - current_rmse
				
				# Reach the stop criterion
				if StopCri >= 0:
					previous_rmse = current_rmse

				#print "%d Run RMF Updating Epoch %d Current RMSE %f"%(i + 1, epoch, previous_rmse)

			# Update feature matrices untile reaching the stop criterion
			self.ExBPR_Update()
			#self.RRBPR_Update()

			previous_ndcg = self.nDCG(K = self.TK)
			StopCri = previous_ndcg
			
			epoch = 0
			while StopCri >= 0:
				epoch += 1
				self.ExBPR_Update()
				#self.RRBPR_Update()
				current_ndcg = self.nDCG(K = self.TK)

				StopCri = current_ndcg - previous_ndcg
				
				# Reach the stop criterion
				if StopCri >= 0:
					previous_ndcg = current_ndcg
					
					rmse = self.RMSE()

				#print "Epoch %d Current NDCG %f"%(epoch, previous_ndcg)

			out_stopCri = previous_ndcg - out_previous_ndcg
			if out_stopCri >= 0:
				out_previous_ndcg = previous_ndcg

		MeanAP, MeanRecall = self.Evaluate(K = self.TK)
		rmse = self.RMSE()

		return rmse, MeanAP, MeanRecall, out_previous_ndcg			

	def DCGMLE(self):
		""" Learning ranking function with likelihood loss
			Integrate rating information with ranking model

		"""
		self.load_dataset()
		self.init_model()

		self.RRBPR_Update()
		#rmse = self.RMSE()
		previous_ndcg = self.nDCG(K = self.TK, bias = False)
		StopCri = previous_ndcg
		
		epoch = 0
		while StopCri >= 0:
			epoch += 1
			self.RRBPR_Update()
			current_ndcg = self.nDCG(K = self.TK, bias = False)

			StopCri = current_ndcg - previous_ndcg
			
			# Reach the stop criterion
			if StopCri >= 0:
				previous_ndcg = current_ndcg

			print "Epoch %d Current NDCG %f"%( epoch, previous_ndcg)
		
		MeanAP, MeanRecall = self.Evaluate(K = self.TK, bias = False)
		rmse = self.RMSE(bias = False)

		return rmse, MeanAP, MeanRecall, previous_ndcg

	def load_dataset(self, header = False):
		""" Load training and testing dataset located in the trainpath

		    Data Format [UserID, ItemID, Rate]
		"""
		if header != True:
			self.train_data_set = self.loadtxt(file_path = self.trainpath)
			self.test_data_set = self.loadtxt(file_path = self.testpath)

	def loadtxt(self, header = False, file_path = ""):
		rows = []
		with open(file_path, "r") as src:
			if header:
				src.readline()
			for line in src.readlines():
				row = line.strip().split("\t")
				rows.append(row[:3])
			src.close()

		return rows

	def init_model(self):
		""" Initialize model parameters including user and item feature matrix

		 #~ Input
		  train_data_set: training data set numpy.array object

		 #~ Return
		  usr_mat: user feature using UserID as the key, corresponding value is the feature vector
		  item_mat: the same as usr_mat
		"""
		self.usr_mat = dict()
		self.item_mat = dict()
		self.item_bais = dict()

		for row in self.train_data_set:
			uid, iid, rate = row

			self.usr_mat.setdefault(uid, {"feature": np.random.uniform( -0.02, 0.02, self.Dimension),
											"colItems": dict(),
											"bias": random.random()})
			
			self.usr_mat[uid]["colItems"].setdefault(iid, rate)
			self.item_bais[iid] = random.random()

			self.item_mat.setdefault(iid, np.random.uniform( -0.02, 0.02, self.Dimension))

		# Rebuild testing dataset
		self.testing_set = dict()
		for row in self.test_data_set:
			uid, iid, rate = row
			if uid in self.usr_mat and iid in self.item_mat:
				self.testing_set.setdefault(uid, [])
				self.testing_set[uid].append([iid, float(rate)])

# UPDATING FUNCTIONS#
	def Bias_Update(self):
		""" Update feature matrix with SGD by traversing the training set
			error = (b_u + b_i + P_u Q_i - r_ui)

		    P_u = P_u - self.LR * (error * Q_i - Regualization * P_u)
		    Q_i = Q_i - self.LR * (error * P_u - Regualization * Q_i)
		"""

		for row in self.train_data_set:
			uid, iid, rate = row
			rate = float(rate)
			pu = self.usr_mat[uid]["feature"]
			qi = self.item_mat[iid]
			b_u = self.usr_mat[uid]["bias"]
			b_i = self.item_bais[iid]

			error = np.dot(pu, qi) + b_u + b_i - rate
			self.usr_mat[uid]["feature"] = pu - self.LR * (error * qi + self.Reg * pu)
			self.item_mat[iid] = qi - self.LR * (error * pu + self.Reg * qi)
			self.usr_mat[uid]["bias"] = b_u - self.LR * (error + self.Reg * b_u)
			self.item_bais[iid] = b_i - self.LR * ( error + self.Reg * b_i)

	def Update(self):
		""" Update feature matrix with SGD by traversing the training set
			error = P_u Q_i - r_ui

		    P_u = P_u - self.LR * (error * Q_i - Regualization * P_u)
		    Q_i = Q_i - self.LR * (error * P_u - Regualization * Q_i)
		"""

		for row in self.train_data_set:
			uid, iid, rate = row
			rate = float(rate)
			pu = self.usr_mat[uid]["feature"]
			qi = self.item_mat[iid]

			error = np.dot(pu, qi) - rate
			self.usr_mat[uid]["feature"] = pu - self.LR * (error * qi + self.Reg * pu)
			self.item_mat[iid] = qi - self.LR * (error * pu + self.Reg * qi)

	# def BPR_Update(self):
	# 	""" Learn feature matrices with Bayesian Personalized Ranking algorithm
	# 	"""
	# 	item_db = self.item_mat.keys()

	# 	for row in self.train_data_set:
	# 		uid, iid, rate = row
	# 		uitems = self.usr_mat[uid]["colItems"]

	# 		# List includes uncollected items
	# 		uncol_items = [v for v in item_db if v not in uitems]
	# 		# Select an uncollected item
	# 		selected = random.choice(uncol_items)

	# 		# Calculate x_uij
	# 		usr_feature = self.usr_mat[uid]["feature"]
	# 		i_feature = self.item_mat[iid]
	# 		j_feature = self.item_mat[selected]

	# 		xuij = np.dot(usr_feature, i_feature) - np.dot(usr_feature, j_feature)

	# 		# calculate gradient term
	# 		exp_error = math.exp(-xuij) / (1 + math.exp(-xuij))

	# 		# Update corresponding parameters with Stochastic Gradient Ascent
	# 		# Update User Feature
	# 		self.usr_mat[uid]["feature"] = usr_feature + self.LR * (exp_error * ( i_feature - j_feature) - self.Reg * usr_feature)

	# 		# Update item i feature
	# 		self.item_mat[iid] = i_feature + self.LR * (exp_error * usr_feature - self.Reg * i_feature)

	# 		# Update uncollected item j feature
	# 		self.item_mat[selected] = j_feature - self.LR * (exp_error * usr_feature + self.Reg * j_feature)


	def ExBPR_Update(self):

		item_db = self.item_mat.keys()

		for uid in self.usr_mat:
			
			usr_feature = self.usr_mat[uid]["feature"]
			uitems = self.usr_mat[uid]["colItems"]

			# Construct Training Samples
			Samples = []
			items_id = uitems.keys()
			for i in range(0, len(items_id) - 1):
				tiid = items_id[i]
				rate_tiid = float(uitems[tiid])
				for j in range(i + 1, len(items_id)):
					jiid = items_id[j]
					rate_jiid = float(uitems[jiid])

					rate_diff = (rate_jiid - rate_tiid)
					if rate_diff > 0:
						Samples.append((jiid, tiid))
					elif rate_diff < 0:
						Samples.append((tiid, jiid))

			# Boostrap Sampling BPR Updating

			for iid, selected in Samples:
				i_feature = self.item_mat[iid]
				j_feature = self.item_mat[selected]

				b_i = self.item_bais[iid]
				b_j = self.item_bais[selected]

				xuij = b_i + np.dot(usr_feature, i_feature) - np.dot(usr_feature, j_feature) - b_j

				# calculate gradient term
				exp_error = math.exp(-xuij) / (1 + math.exp(-xuij))

				# Update corresponding parameters with Stochastic Gradient Ascent
				# Update User Feature
				self.usr_mat[uid]["feature"] = usr_feature + self.RLR * (exp_error * ( i_feature - j_feature) - self.Reg * usr_feature)
				
				self.item_bais[iid] = b_i + self.RLR * (exp_error - self.Reg * b_i)
				self.item_bais[selected] = b_j - self.RLR * (exp_error + self.Reg * b_j)

				# Update item i feature
				self.item_mat[iid] = i_feature + self.RLR * (exp_error * usr_feature - self.Reg * i_feature)

				# Update uncollected item j feature
				self.item_mat[selected] = j_feature - self.RLR * (exp_error * usr_feature + self.Reg * j_feature)
				
				#self.usr_mat[uid]["feature"] = usr_feature + self.RLR * (exp_error * ( i_feature - j_feature) - 2*(np.dot(usr_feature, i_feature) * i_feature) - self.Reg * usr_feature)

				# Update item i feature
				#self.item_mat[iid] = i_feature + self.RLR * (exp_error * usr_feature - 2*(np.dot(usr_feature, i_feature) * usr_feature) - self.Reg * i_feature)

				# Update uncollected item j feature
				#self.item_mat[selected] = j_feature - self.RLR * (exp_error * usr_feature + self.Reg * j_feature)

	def RRBPR_Update(self):
		""" Updating strategy for integrating rating with pairwise ranking algorithm

		"""
		item_db = self.item_mat.keys()

		margins = [0, 0, 0, 0, 0]

		for uid in self.usr_mat:

			#Samples = []
			# Construct rating constraints samples
			usr_feature = self.usr_mat[uid]["feature"]
			uitems = self.usr_mat[uid]["colItems"]

			# for iid in uitems:
			# 	rate = int(uitems[iid])
			# 	i_feature = self.item_mat[iid]

			# 	estimate = np.dot(usr_feature, i_feature)
			# 	margin = margins[rate - 1]

			# 	xu = pow(2, rate) - 1 + margin
			# 	xuij = xu - estimate

			# 	exp_error = math.exp(-xuij) / (1 + math.exp(-xuij))
				
			# 	# Update corresponding parameters with Stochastic Gradient Ascent
			# 	# Update User Feature
			# 	self.usr_mat[uid]["feature"] = usr_feature - self.RLR * (exp_error * ( i_feature) + self.Reg * usr_feature)

			# 	# Update item i feature
			# 	self.item_mat[iid] = i_feature - self.RLR * (exp_error * usr_feature + self.Reg * i_feature)	

			# usr_feature = self.usr_mat[uid]["feature"]
			

			# Construct Training Samples
			Samples = []
			items_id = uitems.keys()
			for i in range(0, len(items_id) - 1):
				tiid = items_id[i]
				rate_tiid = float(uitems[tiid])
				for j in range(i + 1, len(items_id)):
					jiid = items_id[j]
					rate_jiid = float(uitems[jiid])

					rate_diff = (rate_jiid - rate_tiid)
					if rate_diff > 0:
						Samples.append((jiid, tiid))
					elif rate_diff < 0:
						Samples.append((tiid, jiid))

			# Boostrap Sampling BPR Updating
			
			for iid, selected in Samples:
				i_feature = self.item_mat[iid]
				j_feature = self.item_mat[selected]

				xuij = np.dot(usr_feature, i_feature) - np.dot(usr_feature, j_feature)

				# calculate gradient term
				exp_error = math.exp(-xuij) / (1 + math.exp(-xuij))


				# Update corresponding parameters with Stochastic Gradient Ascent
				# Update User Feature
				self.usr_mat[uid]["feature"] = usr_feature + self.RLR * (exp_error * ( i_feature - j_feature) - self.Reg * usr_feature)

				# # Update item i feature
				self.item_mat[iid] = i_feature + self.RLR * (exp_error * usr_feature - self.Reg * i_feature)

				# # Update uncollected item j feature
				self.item_mat[selected] = j_feature - self.RLR * (exp_error * usr_feature + self.Reg * j_feature)


# EVALUMATION METRIC

	def RMSE(self, bias=True):
		""" Calculate RMSE value of RMF, Using it as the stop criterion
		"""
		rmse = 0.
		_records = 0

		for uid in self.testing_set:

			for iid, rate in self.testing_set[uid]:
				if bias:
					error = np.dot(self.usr_mat[uid]['feature'], self.item_mat[iid]) + self.usr_mat[uid]["bias"] + self.item_bais[iid] - float(rate)
				else:
					error = np.dot(self.usr_mat[uid]['feature'], self.item_mat[iid]) - float(rate)
				
				r = error**2
				rmse += r
				_records += 1

		rmse = math.sqrt(rmse / _records)
		
		return rmse

	def nDCG(self, K = 5, bias = True):
		""" nDCG is a novel ranking metric frequently used in IR area to evaluate the performance
		    of a ranking algorithm in learning the ranking sequence of a list items. Similar metrics
		    include precision, MAP, MRR. Comparing with them, nDCG is much sensitive to the relevance
		    level of items in a ranked list. The right sequence learned by a ranking algorithm can 
		    contribute to a larger value of nDCG.

		    #~ Equation for Calculating nDCG metric
		    nDCG@k = \sum {i - k} (2^(value_at_rank) -1) / log_2 {rank + 1} 
		"""

		dcg_value = []

		for uid in self.testing_set:
			testing_items = dict(self.testing_set[uid])
			testing_ids = testing_items.keys()

			usr_feature = self.usr_mat[uid]["feature"]

			# Produce prediction
			prediction = []
			for iid in testing_ids:
				item_feature = self.item_mat[iid]
				if bias:
					score = np.dot(self.usr_mat[uid]['feature'], self.item_mat[iid]) + self.usr_mat[uid]["bias"] + self.item_bais[iid]
				else:
					score = np.dot(usr_feature, item_feature)
				
				prediction.append([iid, score])


			# Select Top-K ranked items
			prediction.sort(reverse = True, key = lambda x: x[1])
			top_k = prediction[:K]

			
			selected_items = []
			for index, v in enumerate(top_k):
				iid, score = v
				selected_items.append( [iid, float(testing_items[iid]), index + 1] )

			# Calculate normalized discount accumulative gain
			person_dcg = 0.
			for iid, rate, rank in selected_items:
				person_dcg += (2**float(rate) - 1) / math.log(rank + 1., 2) 

			# Calculate idea nDCG
			idea_dcg = 0.
			testing_ranks = testing_items.items()
			testing_ranks.sort(reverse = True, key = lambda x: x[1])

			selected_items = testing_ranks[:K]

			#selected_items.sort(reverse = True, key = lambda x: x[1])
			for index, element in enumerate(selected_items):
				rank = index + 1
				iid, rate = element

				idea_dcg += (2**float(rate) - 1) / math.log(rank + 1., 2)

			dcg_value.append(person_dcg / idea_dcg)

		ndcg = sum(dcg_value) / len(dcg_value)

		return ndcg


	def Evaluate(self, K = 5, bias = True):
		""" Evaluate each algorithm with MAP@K, Recall@K metric

		    Definition of MAP (Mean Average Precision)
		    https://www.kaggle.com/wiki/MeanAveragePrecision
		    
		    $ap@k = \sum_{i=1}^{n} P(L)/ min(m,k)$
		    k denotes the number of recommendations, m denotes the number of items rated by user i

		    Some Examples To Illustrate the calculation of ap@k

		    (1) If the user follows recommended nodes #1 and #3
		        along with another node that wasn't recommend,
		        then ap@10 = (1/1 + 2/3)/3 -> 0.56

			(2) If the user follows recommended nodes #1 and #2
			    along with another node that wasn't recommend,
			    then ap@10 = (1/1 + 2/2)/3 -> 0.67

			(3) If the user follows recommended nodes #1 and #3
			    and has no other missing nodes,
			    then ap@10 = (1/1 + 2/3)/2 -> 0.83 
		"""
		ap = []
		recall = []

		for uid in self.testing_set:
			
			collected_items = self.usr_mat[uid]["colItems"]
			usr_feature = self.usr_mat[uid]["feature"]

			# Generating Recommendation List
			reclist = []
			for iid in self.item_mat:
				if iid not in collected_items:
					if bias:
						eva_rate = self.item_bais[iid] + self.usr_mat[uid]["bias"] + np.dot(usr_feature, self.item_mat[iid])
					else:
						eva_rate = np.dot(usr_feature, self.item_mat[iid])
					reclist.append([iid, eva_rate])
			reclist.sort(reverse = True, key = lambda x: x[1])
			
			# Return top-K recommendations
			top_k = [ iid for iid, score in reclist[0:K] ]

			
			in_list_index = [ (top_k.index(v[0]) + 1) for v in self.testing_set[uid] if v[0] in top_k]
			in_list_index.sort()

			# Calculate recall value
			r = float(len(in_list_index)) / len(self.testing_set)
			recall.append(r)

			# If no item is recalled in the top-K list, then continue
			if len(in_list_index) == 0:
				ap.append(0.)

			# Decide the denominator
			denominator = float(min([len(self.testing_set[uid]), K]) )
			acc_gain = 0.

			for index, rank in enumerate(in_list_index):
				_index = index + 1.
				acc_gain += _index / rank

			apk = acc_gain / denominator
			ap.append(apk)

		MeanAP = sum(ap) / len(ap)
		MeanRecall = sum(recall) / len(recall)

		return MeanAP, MeanRecall
