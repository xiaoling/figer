package edu.washington.cs.figer.analysis.feature;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import edu.washington.cs.figer.data.EntityProtos.Mention;

public class BrownFeaturizerTest extends FeaturizerTest{
	
	
	@Test
	public void testSingleToken() {
		// test "Greece"
//		Mention m = getMentionFromStringWithoutDep("A party that had backed a bailout for Greece �s failed economy won a narrow victory", 8, 9);
		ArrayList<String> features =new ArrayList<String>();
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		// Greece -> 1110110111011
		expected_features.add("CLUST_4_1110");
		expected_features.add("CLUST_8_11101101");
		expected_features.add("CLUST_12_111011011101");
//		expected_features.add("CLUST20_1110");
		assertEquals(expected_features, features);
	}

	@Test
	public void testMultipleTokens() {
		// test "narrow victory"
		Mention m = getMentionFromStringWithoutDep("A party that had backed a bailout for Greece �s failed economy won a narrow victory", 14, 16);
		ArrayList<String> features =new ArrayList<String>();
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		// 11011101001, 101110110110
		expected_features.add("CLUST_4_1101");
		expected_features.add("CLUST_8_11011101");
//		expected_features.add("CLUST_12_111011011101");
//		expected_features.add("CLUST20_1110");
		expected_features.add("CLUST_4_1011");
		expected_features.add("CLUST_8_10111011");
		expected_features.add("CLUST_12_101110110110");
		assertEquals(expected_features, features);
	}
	
}
