package edu.washington.cs.figer.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class FeatureFactoryTest {

	@Test
	public void test() {
		FeatureFactory ff = new FeatureFactory();
		ff.isTrain = false;
		assertNull(ff.getFeature("f1"));
		ff.isTrain = true;
		assertNotNull(ff.getFeature("f2"));
		ff.getFeature("f2");

		Instance inst = new Instance();
		inst.getFeatureIndex().add(0);
		inst.featureValue.add(0);
		DataSet ds = new DataSet();
		ds.add(inst);

		ff.filter(ds);
		assertEquals(0, inst.getFeatureIndex().size());
	}

}
