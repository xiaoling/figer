package edu.washington.cs.figer.ml;

import java.io.Serializable;

import edu.washington.cs.figer.data.DataSet;

public abstract class Learner implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -600512262908205095L;
	public Model m = null;

	public abstract void learn(DataSet data, Model m);

}
