package edu.washington.cs.figer.analysis.feature;

import java.util.ArrayList;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;

public class WordShapeFeaturizer implements AbstractFeaturizer {

	public static String getWordShape(String token) {
		return token.replaceAll("\\p{Lower}+", "a")
				.replaceAll("\\p{Upper}+", "A").replaceAll("\\p{Punct}+", ".")
				.replaceAll("\\p{Digit}+", "0");
	}

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		for (int i = m.getStart(); i < m.getEnd(); i++) {
			features.add(("SELF_SHAPE_" + getWordShape(m.getTokens(i))));
		}
	}

	@Override
	public void init(Model m) {
	}
}
