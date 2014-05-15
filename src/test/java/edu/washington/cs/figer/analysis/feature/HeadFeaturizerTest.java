package edu.washington.cs.figer.analysis.feature;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

public class HeadFeaturizerTest extends FeaturizerTest {

	@Test
	public void test() {
		ArrayList<String> features =new ArrayList<String>();
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		expected_features.add("HEAD_greece");
		expected_features.add("HEAD_SHP_Aa");
		expected_features.add("HEAD_CLUST_4_1110");
		expected_features.add("HEAD_CLUST_8_11101101");
		expected_features.add("HEAD_CLUST_12_111011011101");
		expected_features.add("HEAD_POS_NNP");
		HashSet<String> set1 = new HashSet<String>(expected_features);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
	}
	
	@Test
	public void testMultiple() {
		m = m.toBuilder().setStart(8).setEnd(10).build();
		ArrayList<String> features =new ArrayList<String>();
		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		expected_features.add("HEAD_greece");
		expected_features.add("HEAD_SHP_Aa");
		expected_features.add("HEAD_CLUST_4_1110");
		expected_features.add("HEAD_CLUST_8_11101101");
		expected_features.add("HEAD_CLUST_12_111011011101");
		expected_features.add("HEAD_POS_NNP");
		HashSet<String> set1 = new HashSet<String>(expected_features);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
		m = m.toBuilder().setStart(8).setEnd(10).build();
	}

}
