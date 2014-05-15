package edu.washington.cs.figer.analysis.feature;

import java.util.ArrayList;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.X;

/**
 * simple head heurestics
 * 
 * @author Xiao Ling
 * 
 */
public class HeadFeaturizer implements AbstractFeaturizer {

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		String head = null;
		String pos = null;
		for (int i = m.getStart(); i < m.getEnd(); i++) {
			String token = m.getTokens(i), pt = m.getPosTags(i);
			if (pt.startsWith("N")) {
				// last noun
				head = token;
				pos = pt;
			} else if (pt.equals("IN") || pt.equals(",")) {
				// before IN
				break;
			}
		}
		if (head == null) {
			head = m.getTokens(m.getEnd() - 1);
			pos = m.getPosTags(m.getEnd() - 1);
		}

		features.add(("HEAD_" + TokenFeaturizer.getToken(head, pos)));
		if (X.useWordShapeFeaturizer)
			features.add(("HEAD_SHP_" + WordShapeFeaturizer.getWordShape(head)));
		if (X.useBrownFeaturizer)
			for (String clust : BrownFeaturizer.getCluster(head))
				features.add(("HEAD_" + clust));
		if (X.usePosFeaturizer)
			features.add(("HEAD_POS_" + pos));
	}

	@Override
	public void init(Model m) {
	}
}