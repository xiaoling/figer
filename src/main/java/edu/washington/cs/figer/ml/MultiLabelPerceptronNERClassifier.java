package edu.washington.cs.figer.ml;

import java.util.ArrayList;
import java.util.Hashtable;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.data.Label;
import edu.washington.cs.figer.util.X;
import gnu.trove.list.TIntList;

public class MultiLabelPerceptronNERClassifier extends NERClassifier {
	private static final Logger logger = LoggerFactory.getLogger(MultiLabelPerceptronNERClassifier.class);
	public MultiLabelPerceptronNERClassifier() {
	}

	public MultiLabelPerceptronNERClassifier(Inference inf) {
		infer = inf;
	}

	Inference infer = null;

	@Override
	public ArrayList<Prediction> findPredictions(Instance inst, Model m) {
		return infer.findPredictions(inst, m);
	}

	@Override
	public void predict(Instance inst, Hashtable<TIntList, String> pool,
			TIntList entity, Model m) {
		ArrayList<Prediction> preds = infer.findPredictions(inst, m);
		ArrayList<Label> predLabels = ((MultiLabelLogisticRegression) m)
				.makePredictions(preds);

		if (X.getBoolean("printLabels")) {
			int numFeatures = m.featureFactory.allFeatures.size();
			for (int i = 0; i < preds.size(); i++) {
				if (!predLabels.contains(preds.get(i).label)) {
					continue;
				}
				logger.debug(preds.get(i).label + "@" + preds.get(i).prob );
				if (X.getBoolean("printWeights")) {
					int idxLabel = preds.get(i).label.id;
					double[] lambda = ((LRParameter) ((MultiLabelLogisticRegression) m).para).lambda;
					for (int j = 0; j < inst.getFeatureIndex().size(); j++) {
						logger.debug(m.featureFactory.allFeatures.get(inst
								.getIndex(j))
								+ ":"
								+ lambda[inst.getIndex(j) + idxLabel
										* numFeatures] + "\t");
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < preds.size(); i++) {
            if (!predLabels.contains(preds.get(i).label)) {
                continue;
            }
		    if (X.getBoolean("printLabelWithScore")) {
		      sb.append(preds.get(i).label.name + "@" + preds.get(i).prob+",");
		    } else {
		      sb.append(preds.get(i).label.name + ",");
		    }
		}
		sb.deleteCharAt(sb.length() - 1);
		pool.put(entity, sb.toString());
	}
}
