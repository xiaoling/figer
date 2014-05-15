package edu.washington.cs.figer.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class MultiLabelInstanceTest {

	@Test
	public void test() {
		MultiLabelInstance inst = new MultiLabelInstance();
		Label l = new Label("testLabel");
		inst.setLabel(l);
		assertNull(inst.label);
		inst.setLabel(null);
		inst.setLabel(l);
		assertEquals(1, inst.labels.size());
	}

}
