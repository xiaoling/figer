package edu.washington.cs.figer.analysis.feature;

import org.junit.Before;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.LogisticRegression;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.X;

public class FeaturizerTest {
	public Model model;
	public AbstractFeaturizer featurizer;
	public Mention m;
	@Before
	public void runBeforeEveryTest(){
		try {
			X.useReverbFeaturizer = false;
			featurizer = (AbstractFeaturizer) Class.forName(this.getClass().getName().replace("Test", "")).newInstance();
			Model model = new LogisticRegression();
			featurizer.init(model);
			
			prepareMention();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void prepareMention() {
		Mention.Builder b = getMentionFromStringWithoutDep("A party that had backed a bailout for Greece �s failed economy won a narrow victory", 8, 9).toBuilder();
		// DT party/NN that/WDT had/VBD backed/VBN a/DT bailout/NN for/IN Greece/NNP 's/POS failed/VBN economy/NN won/VBD a/DT narrow/JJ victory/NN
		b.addPosTags("DT");
		b.addPosTags("NN");
		b.addPosTags("WDT");
		b.addPosTags("VBD");
		b.addPosTags("VBN");
		b.addPosTags("DT");
		b.addPosTags("NN");
		b.addPosTags("IN");
		b.addPosTags("NNP");
		b.addPosTags("POS");
		b.addPosTags("VBN");
		b.addPosTags("NN");
		b.addPosTags("VBD");
		b.addPosTags("DT");
		b.addPosTags("JJ");
		b.addPosTags("NN");
		b.addDepsBuilder().setDep(8).setGov(11).setType("poss").build();
		m = b.build();
		
	}
	
	protected Mention getMentionFromStringWithoutDep(String sent, int start, int end) {
		String[] tokens = sent.split(" ");
		Mention.Builder m = Mention.newBuilder();
		for (int i = 0; i < tokens.length; ++i) {
			m.addTokens(tokens[i]);
		}
		m.setStart(start);
		m.setEnd(end);
		m.setEntityName("e"+start+","+end);
		m.setFileid("*test*");
		m.setSentid(0);
		return m.build();
	}
	
	
}
