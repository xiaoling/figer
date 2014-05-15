package edu.washington.cs.figer.ml;

import java.util.Random;

public class LRParameter extends Parameter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5403514844833400672L;
	public double[] lambda = null;

	public LRParameter(int num) {
		lambda = new double[num];
	}

	public void init() {
		if (lambda == null)
			return;
		Random r = new Random(
				(int) (System.currentTimeMillis() % Integer.MAX_VALUE));
		for (int i = 0; i < lambda.length; i++) {
			lambda[i] = r.nextDouble() * var;
		}
	}

	public static double var = 0.2;

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < lambda.length; i++)
			b.append(lambda[i] + "\n");

		return b.toString();
	}

	@Override
	public void clear() {
		if (lambda != null)
			lambda = new double[lambda.length];
	}

}
