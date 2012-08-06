package edu.washington.cs.figer.analysis.feature;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

public class PosFeaturizerTest extends FeaturizerTest{

	@Test
	public void test() {
		m = m.toBuilder().setStart(0).setEnd(16).build();
		ArrayList<String> features =new ArrayList<String>();
		featurizer.apply(m, features, model);
	    ArrayList<String> l = new ArrayList<String>();
		l.add("SELF_POS_DT");
		l.add("SELF_POS_NN");
		l.add("SELF_POS_WDT");
		l.add("SELF_POS_VBD");
		l.add("SELF_POS_VBN");
		l.add("SELF_POS_DT");
		l.add("SELF_POS_NN");
		l.add("SELF_POS_IN");
		l.add("SELF_POS_NNP");
		l.add("SELF_POS_POS");
		l.add("SELF_POS_VBN");
		l.add("SELF_POS_NN");
		l.add("SELF_POS_VBD");
		l.add("SELF_POS_DT");
		l.add("SELF_POS_JJ");
		l.add("SELF_POS_NN"); 
		HashSet<String> set1 = new HashSet<String>(l);
		HashSet<String> set2 = new HashSet<String>(features);
		assertEquals(set1, set2);
	}

}
