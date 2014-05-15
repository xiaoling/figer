package edu.washington.cs.figer.analysis;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.data.Label;
import edu.washington.cs.figer.ml.Inference;
import edu.washington.cs.figer.ml.LRParameter;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.ml.MultiLabelLogisticRegression;
import edu.washington.cs.figer.ml.Prediction;
import edu.washington.cs.figer.util.Debug;
import edu.washington.cs.figer.util.X;
import gnu.trove.list.TIntList;

public class MultiLabelPerceptronNERClassifier extends NERClassifier {
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
				Debug.vp(preds.get(i).label + "@" + preds.get(i).prob + "\t");
				if (X.getBoolean("printWeights")) {
					int idxLabel = preds.get(i).label.id;
					double[] lambda = ((LRParameter) ((MultiLabelLogisticRegression) m).para).lambda;
					for (int j = 0; j < inst.getFeatureIndex().size(); j++) {
						Debug.vp(m.featureFactory.allFeatures.get(inst
								.getIndex(j))
								+ ":"
								+ lambda[inst.getIndex(j) + idxLabel
										* numFeatures] + "\t");
					}
					Debug.vpl("");
				}
			}
			Debug.vpl("");
		}

		StringBuilder sb = new StringBuilder();
		for (Label label : predLabels) {
			sb.append(label.name + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		pool.put(entity, sb.toString());
	}
}
