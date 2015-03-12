// Copyright (C) 2014 Guibing Guo
//
// This file is part of LibRec.
//
// LibRec is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LibRec is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LibRec. If not, see <http://www.gnu.org/licenses/>.
//

package librec.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import librec.data.DenseVector;
import librec.data.SparseMatrix;
import librec.data.SparseVector;
import librec.intf.IterativeRecommender;

/**
 * Shi et al., <strong>Climf: learning to maximize reciprocal rank with collaborative less-is-more filtering.</strong>,
 * RecSys 2012.
 * 
 * @author guoguibing
 * 
 */
public class CLiMF extends IterativeRecommender {

	public CLiMF(SparseMatrix rm, SparseMatrix tm, int fold) {
		super(rm, tm, fold);
	}

	@Override
	protected void buildModel() throws Exception {

		for (int iter = 1; iter <= numIters; iter++) {

			loss = 0;
			errs = 0;

			for (int u = 0; u < numUsers; u++) {

				// all user u's ratings
				SparseVector uv = trainMatrix.row(u);
				//compute the  predict value for all item
				SparseVector uPredict=new SparseVector(numItems);
				for (int i = 0; i < numItems; i++) {
					if (uv.contains(i)) {
						uPredict.set(i, predict(u, i));
					}
				}
				// compute sgd for user u
				double[] sgds = new double[numFactors];
				for (int f = 0; f < numFactors; f++) {

					double sgd = -regU * User_factor.get(u, f);

					for (int j : uv.getIndex()) {
						//the predict value (u j) is can be cal first
						double fuj =uPredict.get(j);
						double qjf = Item_factor.get(j, f);

						sgd += g(-fuj) * qjf;

						for (int k : uv.getIndex()) {
							if (k == j)
								continue;

							double fuk =uPredict.get(k);
							double qkf = Item_factor.get(k, f);

							double x = fuk - fuj;

							sgd += gd(x) / (1 - g(x)) * (qjf - qkf);
						}
					}

					sgds[f] = sgd;
				}
				//update user factor
				for (int f = 0; f < numFactors; f++)
					User_factor.add(u, f, lRate * sgds[f]);
				
				// compute sgds for items rated by user u
				// for (int j : items) {
				for (int j = 0; j < numItems; j++) {

					double fuj = uPredict.get(j);
					List<Double> jSgds = new ArrayList<>();
					for (int f = 0; f < numFactors; f++) {
						double puf = User_factor.get(u, f);
						double qjf = Item_factor.get(j, f);

						double yuj = uv.contains(j) ? 1.0 : 0.0;
						double sgd = yuj * g(-fuj) * puf - regI * qjf;

						for (int k : uv.getIndex()) {
							if (k == j)
								continue;

							double fuk = uPredict.get(k);
							double x = fuk - fuj;

							sgd += gd(-x) * (1.0 / (1 - g(x)) - 1.0 / (1 - g(-x))) * puf;
						}
						jSgds.add(sgd);
					}
					for (int f = 0; f < numFactors; f++)
						Item_factor.add(j, f, lRate * jSgds.get(f));
				}


				// compute loss
//				for (int j = 0; j < numItems; j++) {
//
//					if (uv.contains(j)) {
//						double fuj = predict(u, j);
//						double ruj = uv.get(j);
//
//						errs += (ruj - fuj) * (ruj - fuj);
//						loss += Math.log(g(fuj));
//
//						for (int k : uv.getIndex()) {
//							double fuk = predict(u, k);
//							loss += Math.log(1 - g(fuk - fuj));
//						}
//					}
//
//					for (int f = 0; f < numFactors; f++) {
//						double puf = P.get(u, f);
//						double qjf = Q.get(j, f);
//
//						loss += -0.5 * (regU * puf * puf + regI * qjf * qjf);
//					}
//				}

			}
		//	errs *= 0.5;

			if (isConverged(iter))
				break;

		}// end of training

	}

}
