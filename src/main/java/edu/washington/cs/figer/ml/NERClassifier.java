package edu.washington.cs.figer.ml;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.washington.cs.figer.data.Instance;
import gnu.trove.list.TIntList;

public abstract class NERClassifier{
	public NERClassifier(){}
	public abstract ArrayList<Prediction> findPredictions(Instance inst, Model m);
	public abstract void predict(Instance inst, Hashtable<TIntList, String> pool,
			TIntList entity, Model m);
}
