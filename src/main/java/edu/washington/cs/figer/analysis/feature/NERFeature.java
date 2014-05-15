package edu.washington.cs.figer.analysis.feature;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.X;

public class NERFeature {
	public NERFeature(Model m) {
		this();
		this.m = m;
	}

	public Model m = null;
	public ArrayList<AbstractFeaturizer> featurizerList = null;
	public Hashtable<String, AbstractFeaturizer> featurizerMap = null;

	public NERFeature() {
		featurizerList = new ArrayList<AbstractFeaturizer>();
		featurizerMap = new Hashtable<String, AbstractFeaturizer>();
		if (X.useWordShapeFeaturizer) {
			featurizerMap.put("WordShapeFeaturizer", new WordShapeFeaturizer());
			featurizerList.add(featurizerMap.get("WordShapeFeaturizer"));
		}
		if (X.useTokenFeaturizer) {
			if (X.useTokenFeaturizer)
				featurizerMap.put("TokenFeaturizer", new TokenFeaturizer());
			featurizerList.add(featurizerMap.get("TokenFeaturizer"));
		}
		if (X.usePosFeaturizer) {
			if (X.usePosFeaturizer)
				featurizerMap.put("PosFeaturizer", new PosFeaturizer());
			featurizerList.add(featurizerMap.get("PosFeaturizer"));
		}
		if (X.useHeadFeaturizer) {
			if (X.useHeadFeaturizer)
				featurizerMap.put("HeadFeaturizer", new HeadFeaturizer());
			featurizerList.add(featurizerMap.get("HeadFeaturizer"));
		}
		if (X.useContextFeaturizer) {
			if (X.useContextFeaturizer)
				featurizerMap.put("ContextFeaturizer", new ContextFeaturizer());
			featurizerList.add(featurizerMap.get("ContextFeaturizer"));
		}
		if (X.useNGramFeaturizer) {
			if (X.useNGramFeaturizer)
				featurizerMap.put("NGramFeaturizer", new NGramFeaturizer());
			featurizerList.add(featurizerMap.get("NGramFeaturizer"));
		}
		if (X.useLengthFeaturizer) {
			if (X.useLengthFeaturizer)
				featurizerMap.put("LengthFeaturizer", new LengthFeaturizer());
			featurizerList.add(featurizerMap.get("LengthFeaturizer"));
		}
		if (X.useBrownFeaturizer) {
			if (X.useBrownFeaturizer)
				featurizerMap.put("BrownFeaturizer", new BrownFeaturizer());
			featurizerList.add(featurizerMap.get("BrownFeaturizer"));
		}
		if (X.useDependencyFeaturizer) {
			if (X.useDependencyFeaturizer)
				featurizerMap.put("DependencyFeaturizer",
						new DependencyFeaturizer());
			featurizerList.add(featurizerMap.get("DependencyFeaturizer"));
		}

	}

	public void init() {
		for (AbstractFeaturizer f : featurizerList) {
			f.init(m);
		}
	}

	public void extract(Mention mention, ArrayList<String> features) {
		for (AbstractFeaturizer featurizer : featurizerList) {
			featurizer.apply(mention, features, m);
		}
	}
}
