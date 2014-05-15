package edu.washington.cs.figer.ml;

import java.io.Serializable;
import java.util.ArrayList;

import edu.washington.cs.figer.data.Instance;

public abstract class Inference implements Serializable {
	private static final long serialVersionUID = -4671202801217272775L;

	public abstract Prediction findBestLabel(Instance x, Model m);

	public abstract ArrayList<Prediction> findPredictions(Instance x, Model m);
}