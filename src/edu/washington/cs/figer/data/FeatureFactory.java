package edu.washington.cs.figer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import edu.washington.cs.figer.util.Debug;

public class FeatureFactory implements Serializable {
	private static final long serialVersionUID = 660660786014221315L;

	public HashMap<String, Feature> featureNames = new LinkedHashMap<String, Feature>();
	public List<Feature> allFeatures = new ArrayList<Feature>();
	private int totalNum = 0;
	public boolean isTrain = true;

	public void clear() {
		totalNum = 0;
		featureNames.clear();
		allFeatures.clear();
		isTrain = true;
	}

	public void filter(DataSet data) {
		Debug.vpl("filtering most common features");
		int max = 0;
		for (int i = 0; i < allFeatures.size(); i++) {
			if (allFeatures.get(i).freq > max)
				max = allFeatures.get(i).freq;
		}
		for (int i = allFeatures.size() - 1; i >= 0; i--) {
			if (allFeatures.get(i).freq > 0.5 * max) {
				featureNames.remove(allFeatures.get(i).name);
				allFeatures.remove(i);
			}
		}
		Hashtable<Integer, Integer> old2new = new Hashtable<Integer, Integer>();
		for (int i = 0; i < allFeatures.size(); i++) {
			old2new.put(allFeatures.get(i).id, i);
			allFeatures.get(i).id = i;
		}

		for (Instance inst : data.getInstances()) {
			for (int i = inst.length() - 1; i >= 0; i--) {
				if (!old2new.containsKey(inst.getFeatureIndex().get(i))) {
					inst.removeAt(i);
				} else {
					inst.setIndex(i, old2new.get(inst.getIndex(i)));
				}
			}
		}
		totalNum = allFeatures.size();
		Debug.vpl("after filtering " + allFeatures.size());
	}

	public Feature getFeature(String s) {
		if (featureNames.containsKey(s)) {
			Feature f = featureNames.get(s);
			if (isTrain)
				f.freq++;
			return f;
		} else {
			if (isTrain) {
				Feature f = new Feature(s, totalNum);
				f.freq++;
				totalNum++;
				allFeatures.add(f);
				featureNames.put(s, f);
				return f;
			} else
				return null;
		}
	}

	public static void setValue(Instance inst, Feature f, String fea) {
		if (f != null && !inst.containsIndex(f.id)) {
			/*if (fea.startsWith("CTXT")){
				inst.addFeature(f.id, 0.2);								
			}else if (fea.contains("_TKN_")){
				inst.addFeature(f.id, 0.5);
			}else if (fea.contains("_CLUST_")){
				inst.addFeature(f.id, 0.3);
			}else if (fea.startsWith("SELF")){
				if (fea.startsWith("SELF_SHP")){
					inst.addFeature(f.id, 0.3);
				}
				else{
					inst.addFeature(f.id, 0.4);
				}
			}else if (fea.startsWith("HEAD_SHP")){
				inst.addFeature(f.id, 0.2);
			}else if (fea.startsWith("CLUST_")){
				inst.addFeature(f.id, 0.3);
			}else*/ {
				inst.addFeature(f.id, 1);
			}
			/*if (fea.startsWith("DEP_") || fea.startsWith("REVERB_")) {
				inst.addFeature(f.id, 2);
			}
			else if (fea.startsWith("CLUST_") || fea.startsWith("SELF_") || fea.startsWith("GRM_")) {
				inst.addFeature(f.id, 0.5);
			}
			else {
				inst.addFeature(f.id, 1);
			}*/
		}
	}
}
