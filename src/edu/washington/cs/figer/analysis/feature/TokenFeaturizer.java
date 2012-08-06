package edu.washington.cs.figer.analysis.feature;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;

import uk.ac.susx.informatics.Morpha;
import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.Debug;

public class TokenFeaturizer implements AbstractFeaturizer {

	public static Morpha morpha = null;
	public static Hashtable<String, String> cache = new Hashtable<String, String>();

	public static String getToken(String token, String pos) {
		String key = token.trim().toLowerCase() + "_" + pos;
		if (cache.containsKey(key))
			return cache.get(key);
		else {
			if (token.matches("\\p{Alpha}.*")) {
				morpha = new Morpha(new StringReader(key));
				try {
					String p = morpha.next();
					if (p != null) {
						cache.put(key, p);
						return p;
					} else {
						cache.put(key, token);
						return token;
					}
				} catch (IOException e) {
					Debug.vpl("morpha err:" + key + "@@");
				} catch (Error e) {
					Debug.vpl("morpha err:" + key + "@@");
				}
			} else {
				cache.put(key, token);
			}
			cache.put(key, token);
			return token;
		}
	}

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		for (int i = m.getStart(); i < m.getEnd(); i++) {
			features.add("SELF_TKN_" + m.getTokens(i)/*getToken(m.getTokens(i), m.getPosTags(i))*/);
		}
	}

	@Override
	public void init(Model m) {
	}
}