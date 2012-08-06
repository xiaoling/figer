package edu.washington.cs.figer.exp;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import edu.washington.cs.figer.data.Label;
import edu.washington.cs.figer.ml.Model;

public class MultiLabelNERPerfTest {
	@Before
	public void runBeforeEveryTest() {
		Model model = new Model();
		l1 = model.labelFactory.getLabel("L1");
		l2 = model.labelFactory.getLabel("L2");
		l3 = model.labelFactory.getLabel("L3");
		l4 = model.labelFactory.getLabel("L4");
		perf = new MultiLabelNERPerf(model);
	}
	Label l1, l2, l3, l4;
	ArrayList<Label> predSet = new ArrayList<Label>();
	ArrayList<Label> refSet = new ArrayList<Label>();
	MultiLabelNERPerf perf = null;
	static final double eps = 1e-9;
	@Test
	public void testNormalCase() {
		predSet.add(l1);
		predSet.add(l2);
		refSet.add(l2);
		refSet.add(l3);
		perf.update(predSet, refSet);
		perf.print();
		// strict: tp = 0
		assertEquals(0.0, perf.pSum, eps);
		// strict: tp+fp = 1
		assertEquals(1.0, perf.pNum, eps);
		// strict: tp = 0
		assertEquals(0.0, perf.rSum, eps);
		// strict: tp+fn = 1
		assertEquals(1.0, perf.rNum, eps);
		// loose macro: tp = 1, tp+fp = 2
		assertEquals(0.5, perf.pSum2, eps);
		// loose macro: 
		assertEquals(1.0, perf.pNum2, eps);
		// loose macro: tp+fn = 2
		assertEquals(0.5, perf.rSum2, eps);
		// loose macro: 
		assertEquals(1.0, perf.rNum2, eps);
		// loose micro: tp = 1
		assertEquals(1.0, perf.pSum3, eps);
		// loose micro: tp+fp = 2
		assertEquals(2.0, perf.pNum3, eps);
		// loose micro: 
		assertEquals(1.0, perf.rSum3, eps);
		// loose micro: tp+fn = 2
		assertEquals(2.0, perf.rNum3, eps);
	}

	@Test
	public void testEmptyLabels() {
		predSet.add(l1);
		predSet.add(l2);
//		refSet.add(l2);
//		refSet.add(l3);
		perf.update(predSet, refSet);
		perf.print();
		// strict: tp = 0
		assertEquals(0.0, perf.pSum, eps);
		// strict: tp+fp = 1
		assertEquals(1.0, perf.pNum, eps);
		// strict: tp = 0
		assertEquals(0.0, perf.rSum, eps);
		// strict: tp+fn = 0
		assertEquals(0.0, perf.rNum, eps);
		// loose macro: tp = 1, tp+fp = 2
		assertEquals(0.0, perf.pSum2, eps);
		// loose macro: 
		assertEquals(1.0, perf.pNum2, eps);
		// loose macro: tp+fn = 0
		assertEquals(0.0, perf.rSum2, eps);
		// loose macro: 
		assertEquals(0.0, perf.rNum2, eps);
		// loose micro: tp = 1
		assertEquals(0.0, perf.pSum3, eps);
		// loose micro: tp+fp = 2
		assertEquals(2.0, perf.pNum3, eps);
		// loose micro: 
		assertEquals(0.0, perf.rSum3, eps);
		// loose micro: tp+fn = 0
		assertEquals(0.0, perf.rNum3, eps);
	}
	
	@Test
	public void testEmptyPredictions() {
//		predSet.add(l1);
//		predSet.add(l2);
		refSet.add(l2);
		refSet.add(l3);
		perf.update(predSet, refSet);
		perf.print();
		// strict: tp = 0
		assertEquals(0.0, perf.pSum, eps);
		// strict: tp+fp = 0
		assertEquals(0.0, perf.pNum, eps);
		// strict: tp = 0
		assertEquals(0.0, perf.rSum, eps);
		// strict: tp+fn = 1
		assertEquals(1.0, perf.rNum, eps);
		// loose macro: tp = 1, tp+fp = 2
		assertEquals(0.0, perf.pSum2, eps);
		// loose macro: 
		assertEquals(0.0, perf.pNum2, eps);
		// loose macro: tp+fn = 2
		assertEquals(0.0, perf.rSum2, eps);
		// loose macro: 
		assertEquals(1.0, perf.rNum2, eps);
		// loose micro: tp = 1
		assertEquals(0.0, perf.pSum3, eps);
		// loose micro: tp+fp = 2
		assertEquals(0.0, perf.pNum3, eps);
		// loose macro: tp+fn = 2
		assertEquals(0.0, perf.rSum3, eps);
		// loose micro: tp+fn = 2
		assertEquals(2.0, perf.rNum3, eps);
	}
}
