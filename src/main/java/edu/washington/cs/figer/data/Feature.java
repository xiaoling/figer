package edu.washington.cs.figer.data;

import java.io.Serializable;

public class Feature implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1900972523053178672L;

	public int freq = 0;
	public int id = -1;
	public String name = null;

	Feature(String s, int i) {
		name = s;
		id = i;
	}

	@Override
	public String toString() {
		return "Feature:" + name+"["+id+"]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return name.equals(((Feature) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}