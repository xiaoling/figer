package edu.washington.cs.figer.data;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class LabelFactory implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(LabelFactory.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 7272496705680760370L;
	public Hashtable<String, Label> labelNames = new Hashtable<String, Label>();
	public TObjectIntMap<Label> labelIndex = new TObjectIntHashMap<Label>();
	public int numLabels = 0;
	public ArrayList<Label> allLabels = new ArrayList<Label>();
	public boolean isTrain = true;

	public void clear() {
		labelNames.clear();
		labelIndex.clear();
		allLabels.clear();
	}

	public int index(Label label) {
		if (labelIndex == null || label == null) {
			logger.warn("" + labelIndex + " " + label);
		}
		Integer l = labelIndex.get(label);
		if (l == null) {
			logger.warn("" + labelIndex + " " + label + " " + l);
		}
		return l;
	}

	public Label getLabel(String s) {
		if (s == null)
			return null;
		if (labelNames.containsKey(s)) {
			return labelNames.get(s);
		} else {
			if (isTrain) {
				Label l = new Label(s);
				l.id = numLabels;
				numLabels++;
				allLabels.add(l);
				labelNames.put(s, l);
				labelIndex.put(l, labelIndex.size());
				return l;
			} else {
				return null;
			}
		}
	}
}
