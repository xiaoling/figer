package edu.washington.cs.figer.ml;

import java.io.FileWriter;
import java.io.PrintWriter;

import edu.washington.cs.figer.data.DataSet;
import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.exp.Performance;
import edu.washington.cs.figer.util.Serializer;

public class LogisticRegression extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1911384000391474162L;
	public boolean voted = false;

	public LogisticRegression(Learner l) {
		super(l, new LRInference());
	}

	public LogisticRegression() {
		super(new LRPerceptronLearner(), new LRInference());
	}

	@Override
	public void learn(DataSet data) {
		learner.learn(data, this);
	}

	@Override
	public void writeModel(String file) {
		if (debug) {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(new FileWriter(file + ".debug"));
				int k = 0;
				for (int i = 0; i < labelFactory.allLabels.size(); i++) {
					for (int j = 0; j < featureFactory.allFeatures.size(); j++) {
						writer.println(labelFactory.allLabels.get(i) + ":"
								+ featureFactory.allFeatures.get(j) + "\t"
								+ ((LRParameter) para).lambda[k]);
						k++;
					}
				}
				writer.close();
			} catch (Exception e) {
				if (writer != null)
					writer.close();
			}
		}
		Serializer.serialize(this, file);
	}

	@Override
	public void readModel(String file) {
		LogisticRegression lr = (LogisticRegression) Serializer
				.deserialize(file);
		this.featureFactory = lr.featureFactory;
		this.labelFactory = lr.labelFactory;
		this.infer = lr.infer;
		this.learner = lr.learner;
		this.para = lr.para;
	}

	@Override
	public void predict(DataSet data, Performance perf) {
		predict(data, "lr_predictions", perf);
	}

	@Override
	public void predict(DataSet data, String predFile, Performance perf) {
		try {
			DataSet cData = data;
			PrintWriter pw = new PrintWriter(new FileWriter(predFile));

			for (Instance inst : cData.getInstances()) {
				int c = labelFactory.index(inst.label);
				int pred = labelFactory
						.index(infer.findBestLabel(inst, this).label);
				perf.update(pred, c);
			}

			pw.close();
			perf.print();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
