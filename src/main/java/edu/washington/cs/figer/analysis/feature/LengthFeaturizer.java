package edu.washington.cs.figer.analysis.feature;

import java.util.ArrayList;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;

public class LengthFeaturizer implements AbstractFeaturizer {

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		int length = m.getEnd() - m.getStart();
		if (length < 6)
			features.add(("LENGTH_" + length));
		else
			features.add(("LENGTH_>5"));
	}

	@Override
	public void init(Model m) {
	}
}