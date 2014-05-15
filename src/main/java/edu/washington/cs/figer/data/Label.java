package edu.washington.cs.figer.data;

import java.io.Serializable;

public class Label implements Serializable {
	public int id = -1;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7791472947840136786L;
	public String name = null;

	Label(String s) {
		name = s;
	}

	Label(String s, int i) {
		this(s);
		id = i;
	}

	@Override
	public String toString() {
		return "Label:" + name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return name.equals(((Label) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}