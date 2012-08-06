package edu.washington.cs.figer.analysis.feature;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.washington.cs.figer.util.X;

public class DependencyFeaturizerTest extends FeaturizerTest {

	@Test
	public void test() {
		ArrayList<String> features =new ArrayList<String>();
 		featurizer.apply(m, features, model);
		ArrayList<String> expected_features =new ArrayList<String>();
		
		expected_features.add("DEP_dep:poss:economy=N");
		assertEquals(expected_features, features);
	}

}
