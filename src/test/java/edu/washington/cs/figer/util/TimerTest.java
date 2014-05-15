package edu.washington.cs.figer.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimerTest {

	@Test
	public void testStartEnd() {
		Timer timer = new Timer(null);
		timer.start();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timer.end();
		assertEquals(1000, timer.duration, 5);
	}

	@Test
	public void testPause() {
		Timer timer = new Timer(null);
		timer.resume();
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timer.pause();
		timer.print();
	}
	
}
