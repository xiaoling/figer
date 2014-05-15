package edu.washington.cs.figer.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;

public class Serializer {
	public static boolean serialize(Object obj, String filename) {
		if (obj == null || filename == null)
			return false;

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(obj);
			out.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static Object deserialize(String filename) {
		if (filename == null) {
			return null;
		}
		InputStream fis = null;
		ObjectInputStream in = null;
		Object obj = null;
		try {
			fis = new FileInputStream(filename);
			if (filename.endsWith(".gz")) {
				fis = new GZIPInputStream(fis);
			}
			in = new ObjectInputStream(fis);
			obj = in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}
}
