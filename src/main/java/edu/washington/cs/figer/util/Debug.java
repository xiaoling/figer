package edu.washington.cs.figer.util;

public class Debug {
	private static int level = 3;
	public final static String ERROR = "ERROR";
	public final static String INFO = "INFO";
	public final static String TIMING = "TIMING";
	public final static int VERBOSE = 3; // print details
	public final static int DEBUG = 2; // print debug
	public final static int BASIC = 1;

	public static void pl() {
		System.out.println();
	}
	
	public static void pl(String s) {
		if (getLevel() >= BASIC)
			System.out.println(s);
	}

	public static void pl(String category, String info) {
		if (getLevel() >= BASIC)
			System.out.println("[" + category + "] : " + info);
	}

	public static void print(String s) {
		if (getLevel() >= BASIC)
			System.out.print(s);
	}

	public static void dp(String s) {
		if (getLevel() >= DEBUG)
			System.out.print(s);
	}

	public static void dpl(String s) {
		if (getLevel() >= DEBUG)
			System.out.println(s);
	}

	public static void vp(String s) {
		if (getLevel() >= VERBOSE)
			System.out.print(s);
	}

	public static void vpl(String s) {
		if (getLevel() >= VERBOSE)
			System.out.println(s);
	}

	public static int getLevel() {
		return level;
	}

	public static void setLevel(int l) {
		if (l > VERBOSE) {
			level = VERBOSE;
		} else if (l < BASIC) {
			level = BASIC;
		} else {
			level = l;
		}
	}
}