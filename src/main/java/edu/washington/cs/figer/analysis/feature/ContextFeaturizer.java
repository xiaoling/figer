package edu.washington.cs.figer.analysis.feature;

import java.util.ArrayList;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;

public class ContextFeaturizer implements AbstractFeaturizer{
	static int windowSizeS = 3; 

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		// left
		for (int i = m.getStart()-1; i > -1 && i >= m.getStart()-windowSizeS; i--){
			String token = m.getTokens(i), pos = m.getPosTags(i);
			String morph = TokenFeaturizer.getToken(token, pos);
			features.add("CTXT_LEFT_TKN_"+morph);
		}
		// right
		for (int i = m.getEnd(); i < m.getTokensCount() && i < m.getEnd()+windowSizeS; i++){
			String token = m.getTokens(i), pos = m.getPosTags(i);
			String morph = TokenFeaturizer.getToken(token, pos);
			features.add("CTXT_RIGHT_TKN_"+morph);
		}		
	}
	
	@Override
	public void init(Model m) {
	}
}