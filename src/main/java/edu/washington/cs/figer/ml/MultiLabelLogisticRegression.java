package edu.washington.cs.figer.ml;

import java.util.ArrayList;

import edu.washington.cs.figer.data.Label;

public class MultiLabelLogisticRegression extends LogisticRegression {

	public MultiLabelLogisticRegression() {
		super(new MultiLabelLRPerceptronLearner());
	}

	private static final long serialVersionUID = -3528643579641225556L;
	public static double prob_threshold = 0.0;

	/**
	 * return the labels with top probabilities, the lowest of which is at least @param
	 * threshold of the highest one.
	 * 
	 * @param predictions
	 * @return
	 */
	public ArrayList<Label> makePredictions(ArrayList<Prediction> predictions) {
		ArrayList<Label> predLabels = new ArrayList<Label>();
		double max = predictions.get(0).prob;
		int maxLabel = 0;
		for (int i = 0; i < predictions.size(); i++) {
			if (predictions.get(i).prob - prob_threshold > 1e-4) {
				predLabels.add(predictions.get(i).label);
			}
			if (predictions.get(i).prob > max) {
				maxLabel = i;
				max = predictions.get(i).prob;
			}
		}
		if (predLabels.size() == 0) {
			predLabels.add(predictions.get(maxLabel).label);
		}
		return predLabels;
	}
}
