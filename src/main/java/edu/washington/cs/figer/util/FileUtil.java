package edu.washington.cs.figer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class FileUtil {
	public static String getTextFromFile(String filename) {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "UTF-8"));

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getLinesFromFile(String filename) {
		ArrayList<String> lines = null;
		try {
			InputStream in = new FileInputStream(filename);
			if (filename.endsWith(".gz")) {
				in = new GZIPInputStream(in);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));
			lines = new ArrayList<String>();
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static boolean writeLinesToFile(List<String> lines, String filename) {
		if (lines == null || filename == null) {
			return false;
		}
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(filename), "UTF8"));
			for (int i = 0; i < lines.size(); ++i) {
				pw.println(lines.get(i));
			}
			pw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean writeTextToFile(String text, String filename) {
		if (text == null || filename == null) {
			return false;
		}
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(filename), "UTF8"));
			pw.print(text);
			pw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	static public boolean deleteDirectory(File path) {
		if (path == null) {
			return false;
		}
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	static public boolean copyFile(String src, String dst) {
		if (src == null || dst == null) {
			return false;
		}
		return writeTextToFile(getTextFromFile(src), dst);
	}
}
