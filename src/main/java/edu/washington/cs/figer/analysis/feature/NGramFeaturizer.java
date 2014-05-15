package edu.washington.cs.figer.analysis.feature;

import java.util.ArrayList;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.X;

public class NGramFeaturizer implements AbstractFeaturizer {
	static int NS = 2;

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		// bigrams
		if (X.useContextFeaturizer) {
			if (m.getTokensCount() > 1) {
				int start = m.getStart() - ContextFeaturizer.windowSizeS >= 0 ? m
						.getStart() - ContextFeaturizer.windowSizeS
						: 0;
				int end = m.getEnd() - 1 + ContextFeaturizer.windowSizeS > m
						.getTokensCount() ? m.getTokensCount() : m.getEnd() - 1
						+ ContextFeaturizer.windowSizeS;
				for (int i = start; i < end - 1; i++) {
					String token = TokenFeaturizer.getToken(m.getTokens(i),
							m.getPosTags(i));
					String token2 = TokenFeaturizer.getToken(
							m.getTokens(i + 1), m.getPosTags(i + 1));
					if (m.getStart()<= i && i < m.getEnd()-1) {
						features.add(("GRM_" + token + "_" + token2));
					} else {
						if (i < m.getStart()) {
							features.add(("CTXT_LEFT_GRM_" + token + "_" + token2));
						} else {
							features.add(("CTXT_RIGHT_GRM_" + token + "_" + token2));
						}
					}
					
				}
			}
		} else {
			if (m.getTokensCount() > 1) {
				for (int i = m.getStart(); i < m.getEnd() - 1; i++) {
					String token = TokenFeaturizer.getToken(m.getTokens(i),
							m.getPosTags(i));
					String token2 = TokenFeaturizer.getToken(
							m.getTokens(i + 1), m.getPosTags(i + 1));
					features.add(("GRM_" + token + "_" + token2));
				}
			}
		}
	}

	@Override
	public void init(Model m) {
	}
}
