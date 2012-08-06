package edu.washington.cs.figer.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.stanford.nlp.util.StringUtils;
import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.util.Debug;

public class MentionIOTest {

	/*@Test
	public void test0() {
		MentionReader reader = MentionReader.getMentionReader(
				"/projects/pardosa/data14/xiaoling/data/news12.raw3.pbf");
		Mention mention = reader.readMention();
		while (mention != null) {
			System.out.println(StringUtils.join(mention.getFeaturesList().toArray(), ";"));
			Debug.pl("===========");
			mention = reader.readMention();
		}
		reader.close();
	}*/
	
	@Test
	public void testBoth() {
		Mention m = Mention.newBuilder().setFileid("").setStart(0).setEnd(1)
				.setEntityName("").setSentid(0).build();

		// write
		assertNull(MentionWriter.getMentionWriter(""));
		assertNull(MentionWriter.getMentionWriter(null));
		MentionWriter writer = MentionWriter.getMentionWriter("tmp");
		assertTrue(writer.writeObject(m));
		assertFalse(writer.writeObject(null));
		writer.close();

		// read

		MentionReader reader = MentionReader.getMentionReader("tmp");
		assertNotNull(reader);
		assertNull(MentionReader.getMentionReader(null));
		Mention m2 = null;
		m2 = reader.readMention();
		assertEquals(m, m2);
		m2 = reader.readMention();
		assertNull(m2);
		m2 = reader.readMention();
		assertNull(m2);
		reader.close();
	}

}
