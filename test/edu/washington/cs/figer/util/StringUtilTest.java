package edu.washington.cs.figer.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilTest {

	private static final String UNICODE =
			 "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"             
			+ "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" 
			+ "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" 
			+ "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
			+ "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" 
			+ "\u00C5\u00E5"                                                             
			+ "\u00C7\u00E7" 
			+ "\u0150\u0151\u0170\u0171" 
			+ "\u2013\u2014\u2019\u201C\u201D"																		// &ndash;
			+ "\u0100\u0101\u0102\u0103\u0104\u0105\u0106\u0107\u0108\u0109\u010A\u010B\u010C\u010D\u010E\u010F\u0110\u0111"
			+ "\u0112\u0113\u0114\u0115\u0116\u0117\u0118\u0119\u011A\u011B\u011C\u011D\u011E\u011F\u0120\u0121\u0122\u0123"
			+ "\u0124\u0125\u0126\u0127\u0128\u0129\u012A\u012B\u012E\u012F\u0130\u0131\u0132\u0133\u0134\u0135\u0136\u0137"
			+ "\u0138\u0139\u013A\u013B\u013C\u013D\u013E\u013F\u0140\u0141\u0142\u0143\u0144\u0145\u0146\u0147\u0148\u0149"
			+ "\u014A\u014B\u014C\u014D\u0150\u0151\u0152\u0153\u0154\u0155\u0156\u0157\u0158\u0159\u015A\u015B\u015C\u015D"
			+ "\u015E\u015F\u0160\u0161\u0162\u0163\u0164\u0165\u0166\u0167\u0168\u0169\u016A\u016B\u016C\u016D\u016E\u016F"
			+ "\u0170\u0171\u0172\u0173\u0174\u0175\u0176\u0177\u0178\u0179\u017A\u017B\u017C\u017D\u017E";
	
	private static final String PLAIN_ASCII =
		      "AaEeIiOoUu"    // grave
		    + "AaEeIiOoUuYy"  // acute
		    + "AaEeIiOoUuYy"  // circumflex
		    + "AaOoNn"        // tilde
		    + "AaEeIiOoUuYy"  // umlaut
		    + "Aa"            // ring
		    + "Cc"            // cedilla
		    + "OoUu"          // double acute
		    + "--'\"\""		
		    + "AaAaAaCcCcCcCcDdDd"
		    + "EeEeEeEeEeGgGgGgGg"
		    + "HhHhIiIiIiIiIiJjKk"
		    + "kLlLlLlLlLlNnNnNnn"
		    + "NnOoOoOoRrRrRrSsSs"
		    + "SsSsTtTtTtUuUuUuUu"
		    + "UuUuWwYyYZzZzZz"
		    ;
	
	@Test
	public void test() {
		assertEquals(PLAIN_ASCII, StringUtil.convertNonAscii(UNICODE));
	}

}
