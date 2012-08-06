package edu.washington.cs.figer.analysis.feature;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.X;

/**
 * Brown clustering of the words, 1000 classes + NA
 * 
 * @author Xiao Ling
 * 
 */
public class BrownFeaturizer implements AbstractFeaturizer {
	public Hashtable<String, String> mappingS = null;
	public String filename = "config/brown-c1000.txt.gz";
	public static int notFoundS = 0;
	public static int[] LEN = new int[] { /*4, 8, 12, 20*/ };
	private static BrownFeaturizer singleton = null;

	public static ArrayList<String> getCluster(String token) {
		if (singleton == null) {
			singleton = new BrownFeaturizer();
			singleton.init(new Model());
		}
		ArrayList<String> list = new ArrayList<String>();
		String cluster = singleton.mappingS.get(token);
		if (cluster == null) {
			cluster = "NONE";
		} else {
			for (int len : LEN) {
				if (cluster.length() >= len) {
					String pf = cluster.substring(0, len);
					list.add("CLUST_" + len + "_" + pf);
				} 
			}
			list.add("CLUST_ALL_"+cluster);
		}
		return list;
	}

	@Override
	public void init(Model m) {
		mappingS = new Hashtable<String, String>();
		try {
			if (X.get("brownFile") != null) {
				filename = X.get("brownFile");
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(filename))));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] items = line.split("\t");
				mappingS.put(items[1], items[0]);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (singleton == null) {
			singleton = this;
		}
	}

	@Override
	public void apply(Mention m, ArrayList<String> features, Model model) {
		if (mappingS == null) {
			init(model);
		}

		for (int i = m.getStart(); i < m.getEnd(); i++) {
			String cluster = mappingS.get(m.getTokens(i));
			if (cluster == null) {
				cluster = "NONE";
				notFoundS++;
			}
			if (cluster != null) {
				for (int len : LEN) {
					if (cluster.length() >= len) {
						String pf = cluster.substring(0, len);
						features.add("CLUST_" + len + "_" + pf);
					} 
				}
				features.add("CLUST_ALL_"+cluster);
			}
		}
	}

}
