package edu.washington.cs.figer.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDebug {
	private OutputStream os = null;

	@Before
	public void runBeforeEveryTest() {
		os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		System.setOut(ps);
	}

	@After
	public void runAfterEveryTest() {
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.close();
	}

	@Test
	public void testVerbose() {
		String s = "print";
		Debug.setLevel(Debug.VERBOSE);
		Debug.pl(s);
		Debug.dpl(s);
		Debug.vpl(s);
		assertEquals("print\nprint\nprint\n", os.toString());
	}

	@Test
	public void testDebug() {
		String s = "print";
		Debug.setLevel(Debug.DEBUG);
		Debug.pl(s);
		Debug.dpl(s);
		Debug.vpl(s);
		assertEquals("print\nprint\n", os.toString());
	}

	@Test
	public void testInfo() {
		String s = "print";
		Debug.setLevel(Debug.BASIC);
		Debug.pl(s);
		Debug.dpl(s);
		Debug.vpl(s);
		assertEquals("print\n", os.toString());
	}

	@Test
	public void testSetLowLevel() {
		String s = "print";
		Debug.setLevel(-1);
		Debug.pl(s);
		Debug.dpl(s);
		Debug.vpl(s);
		assertEquals("print\n", os.toString());
	}

	@Test
	public void testSetHighLevel() {
		String s = "print";
		Debug.setLevel(100);
		Debug.pl(s);
		Debug.dpl(s);
		Debug.vpl(s);
		assertEquals("print\nprint\nprint\n", os.toString());
	}
}
