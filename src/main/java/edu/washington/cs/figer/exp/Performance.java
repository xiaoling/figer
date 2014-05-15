package edu.washington.cs.figer.exp;

public interface Performance {
	public void update(Object pred, Object truth);
	
	public void print();
	
	public double getNumber(String name);
}
