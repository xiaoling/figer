package edu.washington.cs.figer.util;

import edu.washington.cs.figer.data.Instance;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;

public class V {

	public static double sumprod(Instance x, double[][] w, int cl) {
		return sumprod(x.getFeatureIndex(), x.getFeatureValue(), w, cl);
	}

	public static double sumprod(Instance x, double[] w, int start) {
		return sumprod(x.getFeatureIndex(), x.getFeatureValue(), w, start);
	}

	public static double sumprod(TIntList fi, TDoubleList fv, double[][] w,
			int cl) {
		if (fi == null || fv == null || w == null) {
			Debug.dpl("[EXCEPTION]sumprod(" + fi + ", " + fv + "," + w
					+ ")@V has null argument.");
			return 0;
		}
		if (fi.size() != fv.size()) {
			Debug.dpl("[EXCEPTION]sumprod(fi=" + fi.size() + ", fv="
					+ fv.size() + ")@V has two lists in different lengths.");
			return 0;
		}
		if (cl > w.length || cl < 0) {
			Debug.dpl("[EXCEPTION]sumprod(cl=" + cl + ", w.len=" + w.length
					+ ")@V has different lengths.");
			return 0;
		}
		double result = 0;
		for (int i = 0; i < fi.size(); i++) {
			result += w[cl][fi.get(i)] * fv.get(i);
		}
		return result;
	}

	public static double sumprod(TIntList fi, TDoubleList fv, double[] w,
			int start) {
		if (fi == null || fv == null || w == null) {
			Debug.dpl("[EXCEPTION]sumprod(" + fi + ", " + fv + "," + w
					+ ")@V has null argument.");
			return 0;
		}
		if (fi.size() != fv.size()) {
			Debug.dpl("[EXCEPTION]sumprod(fi=" + fi.size() + ", fv="
					+ fv.size() + ")@V has two lists in different lengths.");
			return 0;
		}
		if (start > w.length || start < 0) {
			Debug.dpl("[EXCEPTION]sumprod(start=" + start + ", w.len="
					+ w.length + ")@V has different lengths.");
			return 0;
		}
		double result = 0;
		for (int i = 0; i < fi.size(); i++) {
			result += w[start + fi.get(i)] * fv.get(i);
		}
		return result;
	}

	/**
	 * compute the inner product of two vectors. return 0 if at least one vector
	 * is null or the lengths differ.
	 * 
	 * @param g
	 * @param g2
	 * @return
	 */
	public static double inner(double[] g, double[] g2) {
		if (g == null || g2 == null) {
			Debug.dpl("[EXCEPTION]inner(" + g + ", " + g2
					+ ")@V has null argument.");
			return 0;
		}
		if (g.length != g2.length) {
			Debug.dpl("[EXCEPTION]inner(" + g.length + ", " + g2.length
					+ ")@V has two arrays in different lengths.");
			return 0;
		}

		double s = 0;
		for (int i = 0; i < g.length; i++) {
			s += g[i] * g2[i];
		}
		return s;
	}
}
