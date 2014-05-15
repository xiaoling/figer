package edu.washington.cs.figer.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;

import edu.washington.cs.figer.ml.Model;

public class X {

	public static Properties prop = new Properties();

	public static void print() {
		Debug.dpl("========PARAMETERS=========");
		for (Object key : prop.keySet()) {
			Debug.dpl(key + "\t" + prop.get(key));
		}
		Debug.dpl("===========================");
	}

	public static void parseArgs(String filename) {
		prop.clear();
		try {
			prop.load(new FileReader(filename));
			print();
			if (prop.containsKey("debugLevel")) {
				Debug.setLevel(getInt("debugLevel"));
			}
			if (prop.containsKey("useWordShape")) {
				useWordShapeFeaturizer = getBoolean("useWordShape");
			}
			if (prop.containsKey("useToken")) {
				useTokenFeaturizer = getBoolean("useToken");
			}
			if (prop.containsKey("usePos")) {
				usePosFeaturizer = getBoolean("usePos");
			}
			if (prop.containsKey("useHead")) {
				useHeadFeaturizer = getBoolean("useHead");
			}
			if (prop.containsKey("useContext")) {
				useContextFeaturizer = getBoolean("useContext");
			}
			if (prop.containsKey("useNGram")) {
				useNGramFeaturizer = getBoolean("useNGram");
			}
			if (prop.containsKey("useLength")) {
				useLengthFeaturizer = getBoolean("useLength");
			}
			if (prop.containsKey("useBrown")) {
				useBrownFeaturizer = getBoolean("useBrown");
			}
			if (prop.containsKey("useDependency")) {
				useDependencyFeaturizer = getBoolean("useDependency");
			}
			if (prop.containsKey("useReverb")) {
				useDependencyFeaturizer = getBoolean("useReverb");
			}
			if (prop.containsKey("MAX_ITER_NUM")) {
				MAX_ITER_NUM = getInt("MAX_ITER_NUM");
			}
			if (prop.containsKey("FEATURE_FREQ_THRESHOLD")) {
				FEATURE_FREQ_THRESHOLD = getInt("FEATURE_FREQ_THRESHOLD");
			}
			if (prop.containsKey("PERCEPTRON_STEP")) {
				PERCEPTRON_STEP = getDouble("PERCEPTRON_STEP");
			}
			if (prop.containsKey("instanceClass")) {
				try {
					instanceClass = Class.forName(prop
							.getProperty("instanceClass"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (prop.containsKey("modelFile")) {
				modelFile = get("modelFile");
			}
			if (prop.containsKey("tagset")) {
				tagset = get("tagset");
			}
			String m = X.get("method");
			if (m.equals(X.PERCEPTRON)) {
				X.methodS = X.PERCEPTRON;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		return (String) prop.get(key);
	}

	public static double getDouble(String key) {
		if (get(key) == null) {
			return 0;
		} else {
			return Double.parseDouble(get(key));
		}
	}

	public static int getInt(String key) {
		if (get(key) == null) {
			return 0;
		} else {
			return Integer.parseInt(get(key));
		}
	}

	public static boolean getBoolean(String key) {
		if (get(key) == null) {
			return false;
		} else {
			return Boolean.parseBoolean(get(key));
		}
	}

	/**
	 * for the class type of non-gold mentions
	 * 
	 * @return
	 */
	public static String getPlaceholder(Model m) {
		if (PLACEHOLDER == null) {
			PLACEHOLDER = m.labelFactory.allLabels.get(0).name;
		}
		return PLACEHOLDER;
	}

	public static String notEntityTypeS = "O";
	public static String tagset = "config/types.map";
	public static final String PERCEPTRON = "PERCEPTRON";
	public static String methodS = PERCEPTRON;
	public static String delimS = "\t";
	public static int idxTypeS = 2, idxTokenS = 0, idxPosS = 1, idxBoundS = 3;
	public static final int idxGoldBoundS = 4;
	public static String PLACEHOLDER = null;
	public static String modelFile = null;
	// Instance class
	public static Class<?> instanceClass = null;

	// Perceptron
	public static int MAX_ITER_NUM = 30;
	public static double PERCEPTRON_STEP = 0.1;
	// features
	public static int FEATURE_FREQ_THRESHOLD = 3;
	public static boolean useWordShapeFeaturizer = true;
	public static boolean useTokenFeaturizer = true;
	public static boolean usePosFeaturizer = true;
	public static boolean useHeadFeaturizer = true;
	public static boolean useContextFeaturizer = true;
	public static boolean useNGramFeaturizer = true;
	public static boolean useLengthFeaturizer = false;
	public static boolean useBrownFeaturizer = true;
	public static boolean useDependencyFeaturizer = true;
	public static boolean useReverbFeaturizer = true;

	// Counters
	private static Hashtable<String, Counter> counters = new Hashtable<String, Counter>();

	public static Counter getCounter(String k) {
		String key = "" + k;
		if (counters.containsKey(key)) {
			return counters.get(key);
		} else {
			Counter c = new Counter();
			counters.put(key, c);
			return c;
		}
	}

	public static void printCounters() {
		Debug.pl("================COUNTERS==============");
		for (Entry<String, Counter> entry : counters.entrySet()) {
			Debug.pl("INFO", entry.getKey() + "\t"
					+ entry.getValue().getValue());
		}
		Debug.pl("=============END COUNTERS==============");
	}
}
