package edu.washington.cs.figer.analysis.feature;

import java.util.ArrayList;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;

public class PosFeaturizer implements AbstractFeaturizer {

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		String fea = "SELF_POS";
		for (int i = m.getStart(); i < m.getEnd(); i++) {
			fea += "_" + m.getPosTags(i);
			features.add("SELF_POS_" + m.getPosTags(i));
		}
	}

	@Override
	public void init(Model m) {
	}
}