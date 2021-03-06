// Copyright (C) 2014 Zhou Ge

package librec.ranking.IBPR;

import java.util.HashMap;
import java.util.Map;

import happy.coding.io.Strings;
import happy.coding.math.Randoms;
import librec.data.DenseMatrix;
import librec.data.DenseVector;
import librec.data.SparseMatrix;
import librec.data.SparseVector;
import librec.data.VectorEntry;
import librec.intf.IterativeRecommender;

/**
 * 
 * 
 * <p>
 * Related Work:
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * 
 * @author zhouge
 * 
 */
public class RandomIBPR extends IterativeRecommender {

	double basis_reg = 0.01;

	public RandomIBPR(SparseMatrix trainMatrix, SparseMatrix testMatrix,
			int fold) {
		super(trainMatrix, testMatrix, fold);

		isRankingPred = true;
		initByNorm = false;

		userBias = new DenseVector(numUsers);
		itemBias = new DenseVector(numItems);
		userBias.init(0.01);
		itemBias.init(0.01);

	}

	@Override
	protected void buildModel() throws Exception {

		for (int iter = 1; iter <= numIters; iter++) {

			// iterative in user

			loss = 0;
			errs = 0;
			userUpdate();
			itemUpdate();
			// iterative in item
			if (isConverged(iter))
				break;

		}
	}

	public void userUpdate() {

		for (int s = 0, smax = numUsers * 100; s < smax; s++) {

			int u = 0, i = 0, j = 0;

			while (true) {
				u = Randoms.uniform(trainMatrix.numRows());
				SparseVector pu = trainMatrix.row(u);

				if (pu.getCount() == 0)
					continue;

				int[] is = pu.getIndex();
				i = is[Randoms.uniform(is.length)];

				do {
					j = Randoms.uniform(numItems);
				} while (pu.contains(j));

				break;
			}

			// update parameters
			double xui = predict(u, i);
			double xuj = predict(u, j);
			double ii_basis = itemBias.get(i);
			double ji_basis = itemBias.get(j);
			double xuij = ii_basis + xui - xuj - ji_basis;

			double vals = -Math.log(g(xuij));
			loss += vals;
			errs += vals;

			double cmg = g(-xuij);

			for (int f = 0; f < numFactors; f++) {
				double puf = User_factor.get(u, f);
				double qif = Item_factor.get(i, f);
				double qjf = Item_factor.get(j, f);

				User_factor.add(u, f, lRate * (cmg * (qif - qjf) - regU * puf));
				Item_factor.add(i, f, lRate * (cmg * puf - regI * qif));
				Item_factor.add(j, f, lRate * (cmg * (-puf) - regI * qjf));

				loss += regU * puf * puf + regI * qif * qif + regI * qjf * qjf;
			}
			// basis update
			itemBias.set(i, ii_basis + lRate * (cmg - basis_reg * ii_basis));
			itemBias.set(j, ji_basis + lRate * (-cmg - basis_reg * ji_basis));
		}

	}

	public void itemUpdate() {
		for (int s = 0, smax = numItems * 100; s < smax; s++) {
			int i = 0, u1 = 0, u2 = 0;

			while (true) {
				i = Randoms.uniform(trainMatrix.numColumns());
				SparseVector pu = trainMatrix.column(i);

				if (pu.getCount() == 0)
					continue;

				int[] is = pu.getIndex();
				u1 = is[Randoms.uniform(is.length)];

				do {
					u2 = Randoms.uniform(numUsers);
				} while (pu.contains(u2));

				break;
			}
			// update parameters
			double xu1 = predict(u1, i);
			double xu2 = predict(u2, i);
			double u1_basis = userBias.get(u1);
			double u2_basis = userBias.get(u2);

			double xu12 = u1_basis + xu1 - xu2 - u2_basis;

			double vals = -Math.log(g(xu12));
			loss += vals;
			errs += vals;

			double cmg = g(-xu12);

			for (int f = 0; f < numFactors; f++) {

				double qif = Item_factor.get(i, f);
				double pu1f = User_factor.get(u1, f);
				double pu2f = User_factor.get(u2, f);

				Item_factor.add(i, f, lRate * (cmg * (pu1f - pu2f) - regI * qif));
				User_factor.add(u1, f, lRate * (cmg * qif - regU * pu1f));
				User_factor.add(u2, f, lRate * (cmg * (-qif) - regU * pu2f));

				loss += regI * qif * qif + regU * pu1f * pu1f + regI * pu2f
						* pu2f;
			}
			userBias.set(u1, u1_basis + lRate * (cmg - basis_reg * u1_basis));
			userBias.set(u2, u2_basis + lRate * (-cmg - basis_reg * u2_basis));
		}
	}

	@Override
	public String toString() {
		return Strings.toString(new Object[] { binThold, numFactors, initLRate,
				regU, regI, numIters }, ",");
	}

	@Override
	public double predict(int u, int j) {
		return userBias.get(u) + itemBias.get(j)
				+ DenseMatrix.rowMult(User_factor, u, Item_factor, j);
	}
}
