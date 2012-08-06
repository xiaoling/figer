package edu.washington.cs.figer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class TestSerializer {

	@Test
	public void testSerialize() {
		String s = "blah";
		assertFalse(Serializer.serialize(s, null));
		assertFalse(Serializer.serialize(null, "tmp"));
		assertFalse(new File("tmp").exists());
		assertTrue(Serializer.serialize(s, "tmp"));
		assertTrue(new File("tmp").exists());

		// de-s
		assertNull(Serializer.deserialize(null));
		assertNull(Serializer.deserialize("not-exist"));
		assertEquals("blah", Serializer.deserialize("tmp"));

		new File("tmp").delete();
	}

}
