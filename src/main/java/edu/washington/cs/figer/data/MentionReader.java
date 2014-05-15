package edu.washington.cs.figer.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.washington.cs.figer.data.EntityProtos.Mention;

public class MentionReader {
	public static void main(String[] args) {
		/*String[] files = new String[]{"/projects/pardosa/data14/xiaoling/data/wex.train.pbf",
		"/projects/pardosa/data14/xiaoling/data/wex.dev.pbf",
		"/projects/pardosa/data14/xiaoling/data/wex.test.pbf"};
		MentionWriter writer = MentionWriter.getMentionWriter("train.data.noparse");
		for (String file: files) {
			Debug.pl(file);
			MentionReader reader = getMentionReader(file);
			Mention mention = null;
			while ((mention = reader.readMention())!=null) {
				writer.writeObject(mention.toBuilder().clearFeatures()
						.clearDeps().clearFileid().clearPosTags().clearSentid().clearEntityName().build());
			}
		}
		writer.close();*/
	}
	
	
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
		} catch (FileNotFoundException e) {
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
}
