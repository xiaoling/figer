package edu.washington.cs.figer.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class LabelFactoryTest {

	@Test
	public void test() {
		LabelFactory lf = new LabelFactory();
		lf.isTrain = false;
		assertNull(lf.getLabel("l1"));
		lf.isTrain = true;
		assertNotNull(lf.getLabel("l2"));
		assertNull(lf.getLabel(null));

		Instance inst = new Instance();
		inst.setLabel(lf.getLabel("l1"));
		assertEquals(0, inst.getFeatureIndex().size());
		assertNotNull(inst.label);
	}

}
