package edu.washington.cs.figer.exp;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import edu.washington.cs.figer.data.Label;
import edu.washington.cs.figer.ml.Model;

public class MultiLabelNERPerf implements Performance {
	private static final Logger logger = LoggerFactory.getLogger(MultiLabelNERPerf.class);
	public Model m = null;
	public boolean printDetails = false;

	public static String getResultString(double p1, double p2, double r1,
			double r2) {
		double prec = p1 / p2, rec = r1 / r2, f1 = 2 * prec * rec
				/ (prec + rec);
		return String.format("prec:\t%1$5.3f\trec:\t%2$5.3f\tf1:\t%3$5.3f",
				prec, rec, f1);
	}

	public MultiLabelNERPerf(Model model) {
		pSum = 0;
		pNum = 0;
		rSum = 0;
		rNum = 0;
		pSum2 = 0;
		pNum2 = 0;
		rSum2 = 0;
		rNum2 = 0;
		pSum3 = 0;
		pNum3 = 0;
		rSum3 = 0;
		rNum3 = 0;
		m = model;
	}

	@Override
	public void print() {
		StringBuilder tex = new StringBuilder();
		double prec = pSum / pNum, rec = rSum / rNum, f1 = 2 * prec * rec
				/ (prec + rec);
		tex.append(String
				.format("& %1$5.3f & %2$5.3f & %3$5.3f", prec, rec, f1));
		logger.info(String.format(
				"strict\tprec:\t%1$5.3f\trec:\t%2$5.3f\tf1:\t%3$5.3f", prec,
				rec, f1));
		logger.info(pSum + "\t" + pNum + "\t" + rSum + "\t" + rNum);
		prec = pSum2 / pNum2;
		rec = rSum2 / rNum2;
		f1 = 2 * prec * rec / (prec + rec);
		tex.append(String
				.format("& %1$5.3f & %2$5.3f & %3$5.3f", prec, rec, f1));
		logger.info(String.format(
				"loose micro\tprec:\t%1$5.3f\trec:\t%2$5.3f\tf1:\t%3$5.3f",
				prec, rec, f1));
		logger.info(pSum2 + "\t" + pNum2 + "\t" + rSum2 + "\t" + rNum2);
		prec = pSum3 / pNum3;
		rec = rSum3 / rNum3;
		f1 = 2 * prec * rec / (prec + rec);
		tex.append(String
				.format("& %1$5.3f & %2$5.3f & %3$5.3f", prec, rec, f1));
		logger.info(String.format(
				"loose macro\tprec:\t%1$5.3f\trec:\t%2$5.3f\tf1:\t%3$5.3f",
				prec, rec, f1));
		logger.info(pSum3 + "\t" + pNum3 + "\t" + rSum3 + "\t" + rNum3);
		double hloss = (double) pSum4 / pNum4;
		logger.info(String.format("hamming loss:\t%1$5.3f\t", hloss) + pSum4
				+ "\t" + pNum4);
		// logger.info("tex:\t" + tex.toString());
	}

	@Override
	public void update(Object pred, Object truth) {
		@SuppressWarnings("unchecked")
		ArrayList<Label> predLabels = (ArrayList<Label>) pred, trueLabels = (ArrayList<Label>) truth;
		computeMetric(predLabels, trueLabels);
	}

	// strict
	public double pSum = 0, pNum = 0, rSum = 0, rNum = 0;
	// loose micro
	public double pSum2 = 0, pNum2 = 0, rSum2 = 0, rNum2 = 0;
	// loose macro
	public double pSum3 = 0, pNum3 = 0, rSum3 = 0, rNum3 = 0;
	// hamming loss
	public int pNum4 = 0, pSum4 = 0;

	public void computeMetric(ArrayList<Label> predLabels,
			ArrayList<Label> trueLabels) {
		double correct = 0, predSize = predLabels.size(), trueSize = trueLabels
				.size();
		if (predLabels.size() == trueLabels.size()) {
			boolean match = true;
			for (Label l : predLabels) {
				if (!trueLabels.contains(l)) {
					// strict
					match = false;
					break;
				} else {
					// loose
					correct += 1;
				}
			}
			if (match) {
				pSum += 1;
				rSum += 1;
			}
			if (printDetails)
				logger.debug("strict match:\t" + (match ? 1 : 0));
		} else {
			if (printDetails)
				logger.debug("strict match(size):\t0");
			for (Label l : predLabels) {
				if (trueLabels.contains(l)) {
					correct += 1;
				}
			}
		}
		// strict
		if (predLabels.size() != 0)
			pNum++;
		if (trueLabels.size() != 0)
			rNum++;
		// loose micro
		if (predSize != 0) {
			pSum2 += correct / predSize;
			pNum2 += 1;
		}
		if (trueSize != 0) {
			rSum2 += correct / trueSize;
			rNum2 += 1;
		}
		if (printDetails)
			logger.debug("loose (c/p/t)\t" + correct + "\t" + predSize + "\t"
					+ trueSize);
		// hamming loss
		if (predSize != 0 && trueSize != 0) {
			pNum4 += 1;
			pSum4 += predSize + trueSize - 2 * correct;
		}
		if (printDetails)
			logger.debug("hamming loss\t" + (predSize + trueSize - 2 * correct));
		// loose macro
		pSum3 += correct;
		rSum3 += correct;
		pNum3 += predLabels.size();
		rNum3 += trueLabels.size();
	}

	@Override
	public double getNumber(String name) {
		return 0;
	}
}
