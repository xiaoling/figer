package edu.washington.cs.figer.util;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import org.junit.Before;
import org.junit.Test;

public class VTest {
	TIntList tiv = null;
	TDoubleList tdv = null;
	double[] da = null, da2 = null, daNan = null, daLong = null;;
	double[][] dda = null;

	@Before
	public void runBeforeEveryTest() {
		da = new double[] { 1, 2, 3, 4, 5, 6 };
		da2 = new double[] { 1, 2, 3, 4 };
		dda = new double[][] { { 11, 2, 3, 4 }, { 1, 2, 3, 4 }, { 1, 2, 3, 4 } };
		daLong = new double[] { 11, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4 };
		daNan = new double[] { Double.NaN, Double.NaN, Double.NaN, Double.NaN };
		tiv = new TIntArrayList(new int[] { 0, 1, 3 });
		tdv = new TDoubleArrayList(new double[] { 0.1, 5, 3 });
	}

	@Test
	public void testInner() {
		assertEquals(1 + 4 + 9 + 16 + 25 + 36, V.inner(da, da), 1e-6);
		assertEquals(0, V.inner(da, da2), 1e-6);
		assertEquals(0, V.inner(da, null), 1e-6);
		assertEquals(Double.NaN, V.inner(da2, daNan), 1e-6);
	}

	/**
	 * TODO: not fully tested
	 */
	@Test
	public void testSumProd() {
		assertEquals(V.sumprod(tiv, tdv, dda, 0), 23.1, 1e-6);
		assertEquals(V.sumprod(tiv, tdv, daLong, 0), 23.1, 1e-6);
	}
}
