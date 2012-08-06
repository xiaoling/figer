package edu.washington.cs.figer.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class XTest {

	@Test
	public void test() {
		Counter c = X.getCounter(null);
		c.Increment();
		c.IncrementBy(2);
		assertEquals(3, c.getValue());
	}

}
