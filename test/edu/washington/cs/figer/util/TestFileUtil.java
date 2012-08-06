package edu.washington.cs.figer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestFileUtil {

	@Test
	public void testReadWriteText() {
		String content = "a\nb\n";
		
		assertTrue(FileUtil.writeTextToFile(content, "tmp"));
		String readText = FileUtil.getTextFromFile("tmp");
		assertEquals(content, readText);
		
		List<String> lines = FileUtil.getLinesFromFile("tmp");
		assertEquals(2, lines.size());
		
		assertTrue(FileUtil.writeLinesToFile(lines, "tmp"));
		readText = FileUtil.getTextFromFile("tmp");
		assertEquals(content, readText);
		
		String text = FileUtil.getTextFromFile("tmp-not-exist");
		assertNull(text);
		
		lines = FileUtil.getLinesFromFile("tmp-not-exist");
		assertNull(lines);
		
		assertFalse(FileUtil.writeLinesToFile(null, "tmp-not-exist"));		
		assertFalse(FileUtil.writeLinesToFile(new ArrayList<String>(), null));
		assertFalse(FileUtil.writeTextToFile(null, "tmp-not-exist"));
		assertFalse(FileUtil.writeTextToFile("blah", null));
		
		new File("tmp").delete();
	}

	@Test
	public void testDeleteDirectory() {
		File dir = new File("tmp");
		{
			dir.mkdir();
			new File("tmp/sub").mkdir();
			FileUtil.writeTextToFile("x", "tmp/sub/x");
		}
		
		assertTrue(FileUtil.deleteDirectory(dir));
		File dir2 = new File("tmp2");
		assertFalse(FileUtil.deleteDirectory(dir2));
		assertFalse(FileUtil.deleteDirectory(null));
	}
	
	@Test
	public void testCopy() {
		assertFalse(FileUtil.copyFile(null, null));
		assertFalse(FileUtil.copyFile("non-exist", "tmp"));
		assertFalse(new File("tmp").exists());
	}
}
