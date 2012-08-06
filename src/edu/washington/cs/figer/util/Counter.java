package edu.washington.cs.figer.util;

public class Counter {
	public void Increment() {
		value++;
	}

	public void IncrementBy(int amount) {
		value += amount;
	}

	public int getValue() {
		return value;
	}

	private int value = 0;
}
