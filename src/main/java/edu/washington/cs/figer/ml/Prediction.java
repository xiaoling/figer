package edu.washington.cs.figer.ml;

import edu.washington.cs.figer.data.Label;

public class Prediction {
	public Label label = null;
	public double prob = 0;

	public Prediction(Label l, double p) {
		label = l;
		prob = p;
	}
}
