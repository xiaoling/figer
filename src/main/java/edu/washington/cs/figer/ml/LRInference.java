package edu.washington.cs.figer.ml;

import java.util.ArrayList;

import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.util.V;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;

/**
 * 
 * @author Xiao Ling
 * 
 */
public class LRInference extends Inference {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5056369182261762614L;

	public LRInference() {
		super();
	}

	public static double sigma = 1;

	@Override
	public Prediction findBestLabel(Instance x, Model m) {
		LogisticRegression lr = (LogisticRegression) m;
		LRParameter para = (LRParameter) lr.para;
		int[] predictions = new int[lr.labelFactory.allLabels.size()];
		double[] probs = new double[lr.labelFactory.allLabels.size()];
		double[] origLambda = para.lambda;
		{
			int L = lr.labelFactory.allLabels.size();
			int maxL = -1;
			double max = -Double.MAX_VALUE;
			ArrayList<Double> scores = new ArrayList<Double>();
			for (int j = 0; j < L; j++) {
				double score = 0;
				int startpoint = lr.featureFactory.allFeatures.size() * j;
				score += V.sumprod(x, para.lambda, startpoint);
				scores.add(score);
				if (score > max) {
					maxL = j;
					max = score;
				}
			}
			double part = 0;
			for (int i = 0; i < L; i++) {
				part += Math.exp((scores.get(i) - max) / sigma);
			}

			predictions[maxL]++;
			probs[maxL] += 1 / part;

		}

		int maxL = -1;
		int max = -1;
		for (int k = 0; k < predictions.length; k++) {
			if (predictions[k] > max) {
				maxL = k;
				max = predictions[k];
			} else if (predictions[k] == max && probs[k] > probs[maxL]) {
				maxL = k;
				max = predictions[k];
			}
		}

		if (lr.voted) {
			para.lambda = origLambda;
		}

		return new Prediction(lr.labelFactory.allLabels.get(maxL), probs[maxL]
				/ max);
	}

	/**
	 * return predictions with scores
	 */
	@Override
	public ArrayList<Prediction> findPredictions(Instance x, Model m) {
		LogisticRegression lr = (LogisticRegression) m;
		LRParameter para = (LRParameter) lr.para;
		ArrayList<Prediction> preds = null;
		int L = lr.labelFactory.allLabels.size();
		int F = lr.featureFactory.allFeatures.size();
		preds = new ArrayList<Prediction>();
		for (int j = 0; j < L; j++) {
			int startpoint = F * j;
			double score = V.sumprod(x, para.lambda, startpoint);
			preds.add(new Prediction(lr.labelFactory.allLabels.get(j), score));
		}
		return preds;
	}
}
