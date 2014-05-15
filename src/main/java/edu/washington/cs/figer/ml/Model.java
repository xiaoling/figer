package edu.washington.cs.figer.ml;

import java.io.Serializable;

import edu.washington.cs.figer.data.DataSet;
import edu.washington.cs.figer.data.FeatureFactory;
import edu.washington.cs.figer.data.LabelFactory;
import edu.washington.cs.figer.exp.Performance;

public class Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3518156260351112092L;

	public Model() {
		featureFactory = new FeatureFactory();
		labelFactory = new LabelFactory();
	}

	public Model(Learner l, Inference i) {
		this();
		learner = l;
		infer = i;
	}

	public void clear() {
		featureFactory.clear();
		labelFactory.clear();
	}

	public void restore(Model m) {
		featureFactory = m.featureFactory;
		labelFactory = m.labelFactory;
	}

	public  void learn(DataSet data){}

	public  void predict(DataSet data, Performance perf) {}

	public  void predict(DataSet data, String predFile, Performance perf) {
	}

	public  void writeModel(String file) {
	}

	public  void readModel(String file) {
	}

	public Parameter para = null;
	public FeatureFactory featureFactory = null;
	public LabelFactory labelFactory = null;
	public boolean debug = false;
	public Learner learner = null;
	public Inference infer = null;
}
