package edu.washington.cs.figer.data;

import java.util.ArrayList;

import edu.washington.cs.figer.ml.Model;

public class MultiLabelInstance extends Instance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4438701446745939348L;
	public ArrayList<Label> labels = new ArrayList<Label>();

	@Override
	public void setLabel(Label l) {
		if (l == null)
			return;
		if (!labels.contains(l))
			labels.add(l);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LABELS::");
		for (int i = 0; i < labels.size(); i++) {
			sb.append(labels.get(i) + ",");
		}
		for (int i = 0; i < getFeatureIndex().size(); i++) {
			sb.append(" " + (getFeatureIndex().get(i)) + ":"
					+ featureValue.get(i));
		}
		return sb.toString();
	}

	@Override
	public String toString(Model m) {
		StringBuffer sb = new StringBuffer();
		sb.append("LABELS::");
		for (int i = 0; i < labels.size(); i++) {
			sb.append(labels.get(i) + ",");
		}
		for (int i = 0; i < getFeatureIndex().size(); i++) {
			sb.append(" "
					+ m.featureFactory.allFeatures
							.get(getFeatureIndex().get(i)).name + ":"
					+ featureValue.get(i));
		}
		return sb.toString();
	}
}
