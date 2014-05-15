package edu.washington.cs.figer.analysis.feature;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

public class ContextFeaturizerTest extends FeaturizerTest{

	@Test
	public void testSingleToken() {
		// test "Greece"
//		Mention m = getMentionFromStringWithoutDep("A party that had backed a bailout for Greece Õs failed economy won a narrow victory", 8, 9);
		ArrayList<String> features =new ArrayList<String>();
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		// Greece -> 1110110111011
		expected_features.add("CTXT_LEFT_TKN_for");
		expected_features.add("CTXT_LEFT_TKN_bailout");
		expected_features.add("CTXT_LEFT_TKN_a");
		expected_features.add("CTXT_RIGHT_TKN_fail");
		expected_features.add("CTXT_RIGHT_TKN_Õs");
		expected_features.add("CTXT_RIGHT_TKN_economy");
		expected_features.add("CTXT_LEFT_2GRAM_a_bailout");
		expected_features.add("CTXT_LEFT_2GRAM_bailout_for");
		expected_features.add("CTXT_RIGHT_2GRAM_Õs_fail");
		expected_features.add("CTXT_RIGHT_2GRAM_fail_economy");
		//				expected_features.add("CLUST20_1110");
		HashSet<String> set1 = new HashSet<String>(expected_features);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
	}

	@Test
	public void testBoundary() {
		m = m.toBuilder().setStart(0).setEnd(2).build();
		ArrayList<String> features =new ArrayList<String>();
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		expected_features.add("CTXT_RIGHT_TKN_that");
		expected_features.add("CTXT_RIGHT_TKN_have");
		expected_features.add("CTXT_RIGHT_TKN_back");
		expected_features.add("CTXT_RIGHT_2GRAM_that_have");
		expected_features.add("CTXT_RIGHT_2GRAM_have_back");
		HashSet<String> set1 = new HashSet<String>(expected_features);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
		m = m.toBuilder().setStart(8).setEnd(9).build();
	}
	
}
