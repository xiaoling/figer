package edu.washington.cs.figer;

import edu.washington.cs.figer.data.MentionReader;


public class MainTest {
	public static void test() {
		MentionReader reader1 = MentionReader
				.getMentionReader("data/demoQuery_stephenTags.pbf");// ,
		// reader2 = new MentionReader(testFile);
		// Hashtable<String, ArrayList<Label>> truth = new Hashtable<String,
		// ArrayList<Label>>();
		edu.washington.cs.figer.data.EntityProtos.Mention mention = null;

		int num = 0;
		int numNominals = 0;
		while ((mention = reader1.readMention()) != null) {
			num++;
			String head = null;
			String pos = null;
			{
				for (int i = mention.getStart(); i < mention.getEnd(); i++) {
					String token = mention.getTokens(i), pt = mention
							.getPosTags(i);
					if (pt.startsWith("N")) {
						// last noun
						head = token;
						pos = pt;
					} else if (pt.equals("IN") || pt.equals(",")) {
						// before IN
						break;
					} else {
						// TODO FIXME add a rule for VBN in e.g. players working
						// for Canada...
					}
				}
				if (head == null) {
					// String[] items = sent.get(idx.get(idx.size() -
					// 1)).split(X.delimS);
					head = mention.getTokens(mention.getEnd() - 1);
					pos = mention.getPosTags(mention.getEnd() - 1);
				}
			}

			if (Character.isLowerCase(head.charAt(0))) {
				numNominals++;
			}

		}
		reader1.close();
		System.out.println(numNominals + "\t" + num);
	}
}
