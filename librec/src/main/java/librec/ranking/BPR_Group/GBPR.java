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

package librec.ranking.BPR_Group;

import java.util.ArrayList;
import java.util.List;

import happy.coding.io.Strings;
import happy.coding.math.Randoms;
import librec.data.DenseMatrix;
import librec.data.DenseVector;
import librec.data.SparseMatrix;
import librec.data.SparseVector;
import librec.intf.IterativeRecommender;
import librec.intf.SocialRecommender;

/**
 * Pan and Chen, <strong>GBPR: Group Preference Based Bayesian Personalized Ranking for One-Class Collaborative
 * Filtering</strong>, IJCAI 2013.
 * 
 * @author guoguibing
 * 
 */
public class GBPR extends IterativeRecommender {

	private float rho;
	private int gLen;

	public GBPR(SparseMatrix trainMatrix, SparseMatrix testMatrix, int fold) {
		super(trainMatrix, testMatrix, fold);

		isRankingPred = true;
		initByNorm = false;
	}

	@Override
	protected void initModel() throws Exception {
		// initialization
		super.initModel();

		itemBias = new DenseVector(numItems);
		itemBias.init();

		rho = cf.getFloat("GBPR.rho");
		gLen = cf.getInt("GBPR.group.size");
	}

	@Override
	protected void buildModel() throws Exception {

		for (int iter = 1; iter <= numIters; iter++) {

			loss = 0;
			errs = 0;

		

			for (int s = 0, smax = numUsers * 100; s < smax; s++) {
//				DenseMatrix PS = new DenseMatrix(numUsers, numFactors);
//				DenseMatrix QS = new DenseMatrix(numItems, numFactors);
				// uniformly draw (u, i, g, j)
				int u = 0, i = 0, j = 0;

				// u
				SparseVector Ru = null; // row u
				do {
					u = Randoms.uniform(trainMatrix.numRows());
					Ru = trainMatrix.row(u);
				} while (Ru.getCount() == 0);

				// i
				int[] is = Ru.getIndex();
				i = is[Randoms.uniform(is.length)];

				// g
				SparseVector Ci = trainMatrix.column(i); // column i
				int[] ws = Ci.getIndex();
				List<Integer> g = new ArrayList<>();
				if (ws.length <= gLen) {
					for (int w : ws)
						g.add(w);
				} else {
					while (gLen > 1) {
						int[] idxes = Randoms.nextIntArray(gLen - 1, ws.length);
						boolean flag = false;
						for (int idx : idxes) {
							int w = ws[idx];
							if (w == u) {
								// make sure u is not added again
								flag = true;
								break;
							}

							g.add(w);
						}
						//是否重新选择一批用户
						if (!flag)
							break;
						g.clear(); // clear last iteration
					}

					g.add(u); // u in G
				}

				double pgui = predict(u, i, g);

				// j
				do {
					j = Randoms.uniform(numItems);
				} while (Ru.contains(j));

				double puj = predict(u, j);

				double pgij = pgui - puj;
				double vals = -Math.log(g(pgij));
//				loss += vals;
//				errs += vals;

				double cmg = g(-pgij);

				// update bi, bj
				double bi = itemBias.get(i);
				itemBias.add(i, lRate * (cmg - regB * bi));
				//loss += regB * bi * bi;

				double bj = itemBias.get(j);
				itemBias.add(j, lRate * (-cmg - regB * bj));
				//loss += regB * bj * bj;

				// update Pw
				double n = 1.0 / g.size();
				for (int w : g) {
					double delta = w == u ? 1 : 0;
					for (int f = 0; f < numFactors; f++) {
						double pwf = User_factor.get(w, f);
						double qif = Item_factor.get(i, f);
						double qjf = Item_factor.get(j, f);

						double delta_pwf = rho * n * qif + (1 - rho) * delta * qif - delta * qjf;
						//PS.add(w, f, cmg * delta_pwf - regU * pwf);
						User_factor.add(w, f, lRate * (cmg * delta_pwf - regU * pwf));
						//loss += regU * pwf * pwf;
					}
				}

				double sum_w[] = new double[numFactors];
				for (int f = 0; f < numFactors; f++) {
					for (int w : g) {
						sum_w[f] += User_factor.get(w, f);
					}
				}

				// update Qi, Qj
				for (int f = 0; f < numFactors; f++) {
					double puf = User_factor.get(u, f);
					double qif = Item_factor.get(i, f);
					double qjf = Item_factor.get(j, f);

					double delta_qif = rho * n * sum_w[f] + (1 - rho) * puf;
					//QS.add(i, f, cmg * delta_qif - regI * qif);
					
					Item_factor.add(i, f, lRate * (cmg * delta_qif - regI * qif));
					
					//loss += regI * qif * qif;

					double delta_qjf = -puf;
					//QS.add(j, f, cmg * delta_qjf - regI * qjf);
					Item_factor.add(j, f, lRate * (cmg * (delta_qjf) - regI * qjf));
					//loss += regI * qjf * qjf;

				}
//				User_factor = User_factor.add(PS.scale(lRate));
//				Item_factor = Item_factor.add(QS.scale(lRate));
			}

			if (isConverged(iter))
				break;
		}
	}

	@Override
	protected double predict(int u, int j) {
		return itemBias.get(j) + DenseMatrix.rowMult(User_factor, u, Item_factor, j);
	}

	/**
	 * @param u
	 * @param j
	 * @param g 用户u 在g 里面
	 * @return
	 *@create_time：2014年12月30日下午9:33:48
	 *@modifie_time：2014年12月30日 下午9:33:48
	  
	 */
	private double predict(int u, int j, List<Integer> g) {
		double ruj = predict(u, j);

		double sum = 0;
		DenseVector tempG=new DenseVector(numFactors);
		for (int w : g)
			tempG.add(User_factor.row(w));
		tempG=tempG.scale(1.0/g.size());
		sum=tempG.inner(Item_factor.row(j));
		double rgj = sum + itemBias.get(j);
		return rho * rgj + (1 - rho) * ruj;
	}

	@Override
	public String toString() {
		return Strings.toString(
				new Object[] { binThold, rho, gLen, numFactors, initLRate, regU, regI, regB, numIters }, ",");
	}

}
