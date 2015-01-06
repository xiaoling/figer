package edu.washington.cs.figer.ml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.washington.cs.figer.data.DataSet;
import edu.washington.cs.figer.data.MultiLabelInstance;

public class MultiLabelLogisticRegressionTest {

	@Test
	public void test() {
		MultiLabelLogisticRegression logreg = new MultiLabelLogisticRegression();
		LRPerceptronLearner.MAX_ITER_NUM = 5;
		logreg.featureFactory.getFeature("f0");
		logreg.featureFactory.getFeature("f1");
		logreg.featureFactory.getFeature("f2");
		DataSet ds = new DataSet();
		MultiLabelInstance inst = new MultiLabelInstance();
		inst.addFeature(0, 1);
		inst.addFeature(2, 1);
		inst.labels.add(logreg.labelFactory.getLabel("C1"));
		inst.labels.add(logreg.labelFactory.getLabel("C2"));
		ds.add(inst);
		
		inst = new MultiLabelInstance();
		inst.addFeature(1, 1);
		inst.addFeature(2, 1);
		inst.labels.add(logreg.labelFactory.getLabel("C1"));
		inst.labels.add(logreg.labelFactory.getLabel("C3"));
		ds.add(inst);
		logreg.learner.learn(ds, logreg);
		for (double d : ((LRParameter) logreg.para).lambda) {
			System.out.println("" + d);
		}
		// TODO verify the following values are reasonable.
		assertEquals(1, ((LRParameter) logreg.para).lambda[0], 1e-8);
		assertEquals(1, ((LRParameter) logreg.para).lambda[1], 1e-8);
		assertEquals(1, ((LRParameter) logreg.para).lambda[2], 1e-8);
		assertEquals(1, ((LRParameter) logreg.para).lambda[3], 1e-8);
		assertEquals(0, ((LRParameter) logreg.para).lambda[4], 1e-8);
		assertEquals(1, ((LRParameter) logreg.para).lambda[5], 1e-8);
		assertEquals(0, ((LRParameter) logreg.para).lambda[3], 1e-8);
		assertEquals(1, ((LRParameter) logreg.para).lambda[4], 1e-8);
		assertEquals(1, ((LRParameter) logreg.para).lambda[5], 1e-8);
	}
}
