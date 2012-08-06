package edu.washington.cs.figer.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.washington.cs.figer.data.EntityProtos.Mention;

public class MentionWriter {
	private OutputStream outputStream = null;

	public static MentionWriter getMentionWriter(String file) {
		if (file == null) {
			return null;
		}
		MentionWriter writer = null;
		try {
			OutputStream output = new FileOutputStream(file);
			writer = new MentionWriter();
			writer.outputStream = output;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer;
	}

	private MentionWriter() {

	}

	public boolean writeObject(Mention m) {
		if (m == null) {
			return false;
		}
		try {
			m.writeDelimitedTo(outputStream);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void close() {
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
