package edu.washington.cs.figer.data;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

	String filename = null;

	private List<Instance> instances = new ArrayList<Instance>();

	public DataSet() {
	}

	public void add(Instance inst){
		getInstances().add(inst);
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}
}
