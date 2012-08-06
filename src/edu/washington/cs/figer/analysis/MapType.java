package edu.washington.cs.figer.analysis;

import java.util.HashSet;
import java.util.Hashtable;

import edu.washington.cs.figer.util.FileUtil;

public class MapType {
	public static String typeFile = "config/types.map";
	public static Hashtable<String, String> mapping = null;

	public static void init() {
		if (mapping == null) {
			mapping = new Hashtable<String, String>();
			String[] lines = FileUtil.getTextFromFile(typeFile).split("\n");
			for (String line : lines) {
				String arg = line.substring(0, line.indexOf("\t")), newType = line
						.substring(line.indexOf("\t") + 1).trim()
						.replace("\t", "/");
				mapping.put(arg, newType);
			}
		}
	}

	/**
	 * 
	 * @param types
	 * @return
	 */
	public static String getMappedTypes(String str) {
		StringBuilder sb = new StringBuilder();
		String[] types = str.split(",");
		HashSet<String> set = new HashSet<String>();
		for (String type : types) {
			if (mapping.containsKey(type)) {
				set.add(mapping.get(type));
			}
		}
		for (String s : set) {
			sb.append(s + ",");
		}
		if (sb.length() == 0) {
			return null;
		} else {
			return sb.substring(0, sb.length() - 1);
		}
	}
}
