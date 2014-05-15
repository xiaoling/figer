package edu.washington.cs.figer.analysis.feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.data.EntityProtos.Mention.Dependency;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.X;

public class DependencyFeaturizer implements AbstractFeaturizer {
	public static HashSet<String> reverbPatterns = new HashSet<String>();
	public static HashSet<String> ignorePosTags = new HashSet<String>(),
			auxVerbs = new HashSet<String>();

	// e.g. ccomp(said-40, shut-8')
	// public static Pattern p = Pattern
	// .compile("(.+?)\\((.+?)\\-(\\d+)'*, *(.+?)\\-(\\d+?)'*\\)");

	public static boolean acceptPosTag(String string) {
		return string.charAt(0) == 'N' || string.charAt(0) == 'V';
	}

	public static boolean acceptDepType(String type) {
		if (type.startsWith("prep")) {
			return true;
		} else if (type.equals("nn")) {
			return true;
		} else if (type.equals("agent")) {
			return true;
		} else if (type.equals("dobj")) {
			return true;
		} else if (type.equals("nsubj")) {
			return true;
		} else if (type.equals("amod")) {
			return true;
		} else if (type.equals("nsubjpass")) {
			return true;
		} else if (type.equals("poss")) {
			return true;
		} else if (type.equals("appos")) {
			return true;
		}
		return false;
	}

	@Override
	public void init(Model m) {
		if (X.useReverbFeaturizer) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(DependencyFeaturizer.class.getResourceAsStream(
								"reverbFeatures.list.gz"))));
				String line = null;
				while ((line = reader.readLine()) != null) {
					reverbPatterns.add(line.split("\t" + "")[1].replaceAll(
							"\\s+", " ").toLowerCase());
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ignorePosTags.add("MD"); // can, must, should
			ignorePosTags.add("DT"); // the, an, these
			ignorePosTags.add("PDT"); // predeterminers
			ignorePosTags.add("WDT"); // wh-determiners
			ignorePosTags.add("JJ"); // adjectives
			ignorePosTags.add("RB"); // adverbs
			ignorePosTags.add("PRP$"); // my, your, our

			auxVerbs.add("be");
			auxVerbs.add("have");
			auxVerbs.add("do");
		}
	}

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		// analyze dep
		for (Dependency dep : m.getDepsList()) {
			String type = dep.getType();
			if (type.startsWith("prepc_")) {
				type = "prep_" + type.substring(6);
			}

			if ((m.getStart() <= dep.getGov() && dep.getGov() < m.getEnd())
					^ (m.getStart() <= dep.getDep() && dep.getDep() < m
							.getEnd())) {
				if (m.getStart() <= dep.getGov() && dep.getGov() < m.getEnd()) {
					if (dep.getDep() >= m.getPosTagsCount()) {
						X.getCounter("NUM_errors_training_mentions")
								.Increment();
						continue;
					}
					String posTag = m.getPosTags(dep.getDep());// sent.get(dep).split(X.delimS)[X.idxPosS];
					if (acceptDepType(type) && acceptPosTag(posTag)) {
						String t = TokenFeaturizer.getToken(
								m.getTokens(dep.getDep()), posTag);
						String key = "gov:" + type + ":" + t + "="
								+ posTag.charAt(0);
						features.add(("DEP_" + key));
					}
				}
				if (m.getStart() <= dep.getDep() && dep.getDep() < m.getEnd()) {
					if (dep.getGov() >= m.getPosTagsCount()) {
						X.getCounter("NUM_errors_training_mentions")
								.Increment();
						continue;
					}
					String posTag = m.getPosTags(dep.getGov());// sent.get(dep).split(X.delimS)[X.idxPosS];

					if (acceptDepType(type) && acceptPosTag(posTag)) {
						String t = TokenFeaturizer.getToken(
								m.getTokens(dep.getGov()), posTag);
						String key = "dep:" + type + ":" + t + "="
								+ posTag.charAt(0);
						features.add(("DEP_" + key));

						if (X.useReverbFeaturizer) {
							if (type.equals("nsubj") && posTag.startsWith("V")) {
								ArrayList<String> rvtokens = new ArrayList<String>(), 
									rvpos = new ArrayList<String>();
								for (int k = 1; k < 9
										&& dep.getGov() + k < m
												.getTokensCount(); k++) {
									if (!ignorePosTags.contains(m
											.getPosTags(dep.getGov() + k))) {
										String tk = TokenFeaturizer.getToken(
												m.getTokens(dep.getGov() + k),
												m.getPosTags(dep.getGov() + k));
										rvtokens.add(tk);
										rvpos.add(m.getPosTags(dep.getGov() + k));
									}
								}
								{
									// remove leading "be" and "have"
									int lastVerbIndex = -1;
									int n = rvtokens.size();
									for (int i = 0; i < n; i++) {
										String tag = rvpos.get(n - i - 1);
										if (tag.startsWith("V")) {
											lastVerbIndex = n - i - 1;
											break;
										}
									}
									if (lastVerbIndex < 0)
										return;
									int i = 0;
									while (i < lastVerbIndex) {
										String tok = rvtokens.get(i);
										if (i + 1 < rvpos.size()
												&& !rvpos.get(i + 1)
														.startsWith("V"))
											break;
										if (auxVerbs.contains(tok)) {
											rvtokens.remove(i);
											rvpos.remove(i);
											lastVerbIndex--;
										} else {
											i++;
										}
									}
								}
								{
									String longest = null;
									if (rvtokens.size() > 0) {
										StringBuilder pat = new StringBuilder(
												rvtokens.get(0));

										for (int k = 1; k < rvtokens.size(); k++) {
											pat.append(" " + rvtokens.get(k));
											if (reverbPatterns.contains(pat
													.toString().trim())) {
												longest = pat.toString().trim()
														.replace(" ", "_");
											}
										}
										if (longest != null) {
											features.add(("REVERB_" + longest));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}