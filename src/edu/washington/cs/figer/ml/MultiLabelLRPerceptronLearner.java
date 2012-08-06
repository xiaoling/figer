package edu.washington.cs.figer.ml;

import java.util.ArrayList;
import edu.washington.cs.figer.data.DataSet;
import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.data.Label;
import edu.washington.cs.figer.data.MultiLabelInstance;
import edu.washington.cs.figer.exp.MultiLabelNERPerf;
import edu.washington.cs.figer.util.Debug;
import edu.washington.cs.figer.util.Timer;
import edu.washington.cs.figer.util.X;

public class MultiLabelLRPerceptronLearner extends LRPerceptronLearner {
	/**
	 * 
	 */
	private static final long serialVersionUID = 545200973761286865L;

	@Override
	public void learn(DataSet data, Model m) {
		this.m = m;
		if (m == null) {
			Debug.pl("learner doesn't have a model");
			return;
		}
		LogisticRegression lr = (LogisticRegression) m;
		boolean voted = ((LogisticRegression) m).voted;
		if (!voted) {
			if (m.para == null)
				m.para = new LRParameter(m.labelFactory.allLabels.size()
						* m.featureFactory.allFeatures.size());
		}

		LRParameter para = (LRParameter) (m.para);

		Debug.vpl(para.lambda.length + " = parameter length");

		for (int idx_iter = 0; idx_iter < MAX_ITER_NUM; idx_iter++) {
			double learningRate = STEP;// * (MAX_ITER_NUM - idx_iter) / MAX_ITER_NUM;
			Timer timer = new Timer(idx_iter + "th iteration").start();
			if (X.getBoolean("testWhenLearn"))
				test(data, m);
			
			int F = m.featureFactory.allFeatures.size();
			int idx_inst = 0;
//			Collections.shuffle(data.getInstances());
			for (Instance x : data.getInstances()) {
				idx_inst++;
				int pred = -1;
				ArrayList<Prediction> predictions = ((LRInference) lr.infer)
						.findPredictions(x, m);
				ArrayList<Label> trueLabels = ((MultiLabelInstance) x).labels;

				ArrayList<Label> labels = ((MultiLabelLogisticRegression) m)
						.makePredictions(predictions);

				for (Label l : labels) {
					if (trueLabels.contains(l)) {
						continue;
					} else {
						pred = m.labelFactory.index(l);
						for (int idx_fea = 0; idx_fea < x.length(); idx_fea++) {
							para.lambda[pred * F + x.getIndex(idx_fea)] -= learningRate
									* x.getValue(idx_fea);
						}
					}
				}
				for (Label l : trueLabels) {
					if (labels.contains(l)) {
						continue;
					} else {
						int truth = m.labelFactory.index(l);
						for (int idx_fea = 0; idx_fea < x.length(); idx_fea++) {
							para.lambda[truth * F + x.getIndex(idx_fea)] += learningRate
									* x.getValue(idx_fea);
						}
					}
				}
			}
			timer.endPrint();
		}
	}

	private void test(DataSet data, Model m) {
		Timer timer = new Timer("testing").start();
		MultiLabelNERPerf perf = new MultiLabelNERPerf(m);
		Debug.setLevel(2);
		for (int i = 0; i < data.getInstances().size(); i++) {
			MultiLabelInstance inst = (MultiLabelInstance) data.getInstances()
					.get(i);
			
			ArrayList<Prediction> predictions = m.infer
					.findPredictions(inst, m);
			ArrayList<Label> labels = ((MultiLabelLogisticRegression) m)
					.makePredictions(predictions);
			perf.computeMetric(labels, inst.labels);
		}

		System.out.println("strict\t"
				+ MultiLabelNERPerf.getResultString(perf.pSum, perf.pNum, perf.rSum,
						perf.rNum));

		System.out.println("loose micro\t"
				+ MultiLabelNERPerf.getResultString(perf.pSum2, perf.pNum2, perf.rSum2,
						perf.rNum2));

		System.out.println("loose macro\t"
				+ MultiLabelNERPerf.getResultString(perf.pSum3, perf.pNum3, perf.rSum3,
						perf.rNum3));
		
		Debug.setLevel(X.getInt("debugLevel"));
		timer.endPrint();
	}

}
