package edu.washington.cs.figer.analysis.feature;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import edu.washington.cs.figer.util.X;

public class NGramFeaturizerTest extends FeaturizerTest {

	@Test
	public void testSingleToken() {
		// test "Greece"
//		Mention m = getMentionFromStringWithoutDep("A party that had backed a bailout for Greece �s failed economy won a narrow victory", 8, 9);
		ArrayList<String> features =new ArrayList<String>();
		X.useContextFeaturizer = false;
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		HashSet<String> set1 = new HashSet<String>(expected_features);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
	}

	
	@Test
	public void testMultipleTokensNoContext() {
		m = m.toBuilder().setStart(0).setEnd(2).build();
		ArrayList<String> features =new ArrayList<String>();
		X.useContextFeaturizer = false;
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		expected_features.add("GRM_a_party");
		HashSet<String> set1 = new HashSet<String>(expected_features);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
		m = m.toBuilder().setStart(8).setEnd(9).build();
	}
	
	@Test
	public void testMultipleTokensWithContext() {
		m = m.toBuilder().setStart(0).setEnd(2).build();
		ArrayList<String> features =new ArrayList<String>();
		X.useContextFeaturizer = true;
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		expected_features.add("GRM_a_party");
		expected_features.add("GRM_party_that");
		expected_features.add("GRM_that_have");
		expected_features.add("GRM_have_back");
		HashSet<String> set1 = new HashSet<String>(expected_features);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
		m = m.toBuilder().setStart(8).setEnd(9).build();
	}
}
