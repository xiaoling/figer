package edu.washington.cs.figer.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import edu.washington.cs.figer.data.EntityProtos.Mention;

public class MentionReader {

	public String inputFile = null;
	public InputStream inputStream = null;
	public Mention current = null;

	private MentionReader() {
	}

	public static MentionReader getMentionReader(String file) {
		if (file == null) {
			return null;
		}
		MentionReader reader = null;

		try {
			reader = new MentionReader();
			reader.inputFile = file;
			reader.inputStream = new FileInputStream(file);
			if (file.endsWith(".gz")) {
				reader.inputStream = new GZIPInputStream(reader.inputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reader;
	}

	public Mention readMention() {
		try {
			current = Mention.parseDelimitedFrom(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return current;
	}

	public void close() {
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
	    MentionReader reader = MentionReader.getMentionReader("train.data.gz");
	    int c = 0;
	    Mention mention = null;
	    while ((mention = reader.readMention())!=null) {
                c++;
	    }
	    reader.close();
	    System.out.println(c);
	}
}
