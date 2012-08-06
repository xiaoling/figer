package edu.washington.cs.figer.data;

import edu.washington.cs.figer.ml.Model;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.Serializable;

/**
 * Instance for classification
 */
public class Instance implements Serializable {

	private static final long serialVersionUID = -851405716099693492L;
	public Label label = null;
	protected TIntList featureIndex = new TIntArrayList();
	protected TDoubleList featureValue = new TDoubleArrayList();

	public void addFeature(int idx, double val) {
		getFeatureIndex().add(idx);
		featureValue.add(val);
	}

	public boolean containsIndex(int id) {
		return getFeatureIndex().contains(id);
	}

	public TIntList getFeatureIndex() {
		return featureIndex;
	}

	public TDoubleList getFeatureValue() {
		return featureValue;
	}

	public int getIndex(int idx) {
		if (idx > -1 && idx < getFeatureIndex().size()) {
			return getFeatureIndex().get(idx);
		} else {
			return -1;
		}
	}

	public double getValue(int idx) {
		if (idx > -1 && idx < getFeatureIndex().size()) {
			return featureValue.get(idx);
		} else {
			return 0;
		}
	}

	public int length() {
		return getFeatureIndex().size();
	}

	public void removeAt(int idx) {
		if (idx > -1 && idx < getFeatureIndex().size()) {
			getFeatureIndex().removeAt(idx);
			featureValue.removeAt(idx);
		}
	}

	public void setFeatureIndex(TIntList featureIndex) {
		this.featureIndex = featureIndex;
	}

	public void setFeatureValue(TDoubleList featureValue) {
		this.featureValue = featureValue;
	}

	public void setIndex(int i, Integer integer) {
		if (i > -1 && i < getFeatureIndex().size()) {
			getFeatureIndex().set(i, integer);
		}
	}

	public void setLabel(Label l) {
		label = l;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(label);
		for (int i = 0; i < getFeatureIndex().size(); i++) {
			sb.append(" " + (getFeatureIndex().get(i)) + ":"
					+ featureValue.get(i));
		}
		return sb.toString();
	}

	public String toString(Model m) {
		StringBuffer sb = new StringBuffer();
		sb.append(label);
		for (int i = 0; i < getFeatureIndex().size(); i++) {
			sb.append(" "
					+ m.featureFactory.allFeatures
							.get(getFeatureIndex().get(i)).name + ":"
					+ featureValue.get(i));
		}
		return sb.toString();
	}
}
