package edu.washington.cs.knowitall.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import uk.ac.susx.informatics.Morpha;


/***
 * A class for taking a verbal relation string and normalizing it by removing auxiliary verbs, modal 
 * verbs, and inflection. 
 * @author afader
 *
 */
public class VerbalRelationNormalizer {
	
	private static Morpha lexer;
	private static HashSet<String> ignorePosTags;
	private static HashSet<String> auxVerbs;
	
	public VerbalRelationNormalizer() {
		lexer = new Morpha(System.in);
		
		ignorePosTags = new HashSet<String>();
		ignorePosTags.add("MD"); // can, must, should
		ignorePosTags.add("DT"); // the, an, these
		ignorePosTags.add("PDT"); // predeterminers 
		ignorePosTags.add("WDT"); // wh-determiners
		ignorePosTags.add("JJ"); // adjectives
		ignorePosTags.add("RB"); // adverbs
		ignorePosTags.add("PRP$"); // my, your, our
		
		auxVerbs = new HashSet<String>();
		auxVerbs.add("be");
		auxVerbs.add("have");
		auxVerbs.add("do");
	}
	
	private String stem(String token, String posTag) {
		token = token.toLowerCase();
		String wordTag = token + "_" + posTag;
		try {
	        lexer.yyreset(new StringReader(wordTag));
	        lexer.yybegin(Morpha.scan);
	        String tokenNorm = lexer.next();
	        return tokenNorm;
	    } catch (Throwable e) {
	        return token;
	    }
	}
	
	private void stemAll(ArrayList<String> tokens, ArrayList<String> posTags) {
		for (int i = 0; i < tokens.size(); i++) {
			String tok = tokens.get(i);
			String tag = posTags.get(i);
			String newTok = stem(tok, tag);
			tokens.set(i, newTok);
		}
	}
	
	private void removeIgnoredPosTags(ArrayList<String> tokens, ArrayList<String> posTags) {
		int i = 0;
		while (i < posTags.size()) {
			if (ignorePosTags.contains(posTags.get(i))) {
				tokens.remove(i);
				posTags.remove(i);
			} else {
				i++;
			}
		}
	}
	
	private void removeLeadingBeHave(ArrayList<String> tokens, ArrayList<String> posTags) {
		int lastVerbIndex = -1;
		int n = tokens.size();
		for (int i = 0; i < n; i++) {
			String tag = posTags.get(n-i-1);
			if (tag.startsWith("V")) {
				lastVerbIndex = n-i-1;
				break;
			}
		}
		if (lastVerbIndex < 0) return;
		int i = 0;
		while (i < lastVerbIndex) {
			String tok = tokens.get(i);
			if (i+1 < posTags.size() && !posTags.get(i+1).startsWith("V")) break;
			if (auxVerbs.contains(tok)) {
				tokens.remove(i);
				posTags.remove(i);
				lastVerbIndex--;
			} else {
				i++;
			}
		}
	}
	
	/**
	 * Normalizes the given (token, POS tag) pairs by doing the following:
	 * - Removes inflection in each token using the Morpha class.
	 * - Removes any tokens that are modal verbs, determiners, adjectives, and adverbs. 
	 * - Removes auxiliary verbs.
	 * @param tokensAr
	 * @param posTagsAr
	 * @return
	 */
	public String[] normalize(String[] tokensAr, String[] posTagsAr) {
		if (tokensAr.length != posTagsAr.length) {
			throw new IllegalArgumentException("tokens and POS tags must be same length");
		}
		ArrayList<String> tokens = new ArrayList<String>(tokensAr.length);
		ArrayList<String> posTags = new ArrayList<String>(posTagsAr.length);
		for (int i = 0; i < tokensAr.length; i++) {
			tokens.add(tokensAr[i]);
			posTags.add(posTagsAr[i]);
		}
		
		stemAll(tokens, posTags);
		removeIgnoredPosTags(tokens, posTags);
		removeLeadingBeHave(tokens, posTags);
		
		return tokens.toArray(new String[0]);
	}
	
	/**
	 * Reads tab-delimited (tokens, pos tags) from standard input and prints the resulting 
	 * normalized string. The tokens and tags should be delimited by spaces.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int lineNum = 1;
		VerbalRelationNormalizer normalizer = new VerbalRelationNormalizer();
		while ((line = reader.readLine()) != null) {
			String[] fields = line.split("\t");
			if (fields.length == 2) {
				String[] tokens = fields[0].split(" ");
				String[] posTags = fields[1].split(" ");
				
				if (tokens.length != posTags.length) {
					System.err.println("Couldn't read line " + lineNum + ": different lengths");
					lineNum++;
					continue;
				}
				
				String[] result = normalizer.normalize(tokens, posTags);
				System.out.println(StringUtils.join(result, " "));
			} else {
				System.err.println("Couldn't read line " + lineNum + ": expected 2 columns, got " + fields.length);
			}
			lineNum++;
		}
	}

}