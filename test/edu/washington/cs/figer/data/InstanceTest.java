package edu.washington.cs.figer.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class InstanceTest {

	@Test
	public void test() {
		Instance inst = new Instance();
		Label l = new Label("testLabel");
		inst.setLabel(l);
		assertEquals(l, inst.label);
		inst.setLabel(null);
		assertNull(inst.label);
		inst.addFeature(0, 10);
		assertEquals(0, inst.getIndex(0));
		assertEquals(10, inst.getValue(0), 1e-6);
		assertEquals(-1, inst.getIndex(1));
		assertEquals(0, inst.getValue(1), 1e-6);
		assertEquals(-1, inst.getIndex(-1));
		assertEquals(0, inst.getValue(-1), 1e-6);
		
		inst.removeAt(0);
		assertEquals(-1, inst.getIndex(0));	
	}

}
