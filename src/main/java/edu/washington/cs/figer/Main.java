package edu.washington.cs.figer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import org.apache.commons.lang.StringUtils;

import edu.washington.cs.figer.analysis.MapType;
import edu.washington.cs.figer.analysis.MultiLabelPerceptronNERClassifier;
import edu.washington.cs.figer.analysis.NERClassifier;
import edu.washington.cs.figer.analysis.Preprocessing;
import edu.washington.cs.figer.analysis.feature.NERFeature;
import edu.washington.cs.figer.data.DataSet;
import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.data.Feature;
import edu.washington.cs.figer.data.FeatureFactory;
import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.data.Label;
import edu.washington.cs.figer.data.MentionReader;
import edu.washington.cs.figer.data.MultiLabelInstance;
import edu.washington.cs.figer.exp.Eval;
import edu.washington.cs.figer.exp.MultiLabelNERPerf;
import edu.washington.cs.figer.exp.Performance;
import edu.washington.cs.figer.ml.LRParameter;
import edu.washington.cs.figer.ml.LRPerceptronLearner;
import edu.washington.cs.figer.ml.Learner;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.ml.MultiLabelLRPerceptronLearner;
import edu.washington.cs.figer.ml.MultiLabelLogisticRegression;
import edu.washington.cs.figer.util.Debug;
import edu.washington.cs.figer.util.FileUtil;
import edu.washington.cs.figer.util.Timer;
import edu.washington.cs.figer.util.X;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;

public class Main {
	private Main() {
	}

	// useful variables
	public static Model model = new MultiLabelLogisticRegression();
	public static DataSet train = new DataSet(), test = new DataSet();
	public static NERFeature nerFeature = null;
	public static String[] trainFiles = null;
	public static String testFile = null;
	// debug variables
	public static HashSet<String> mentions = new HashSet<String>();

	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		if (args.length != 1) {
			if (args.length == 2 && args[0].equals("preprocess")) {
				X.prop.put("segmentation", "true");
				Preprocessing.go(args[1]);
			} else if (args.length == 3 && args[0].equals("eval")) {
				X.prop.put("tagset", "types.map");
				Eval.trueLabelFile = args[2];
				Debug.setLevel(2);
				Eval.process(args[1]).print();
			} else {
				showHelpMessage();
				System.exit(-1);
			}
		} else {
			X.parseArgs(args[0]);
			run();
		}
	}

	private static void showHelpMessage() {
		Debug.pl("1. To run the main function:");
		Debug.pl("\t./run.sh <config_file>");
		Debug.pl("2. To only preprocess a txt file:");
		Debug.pl("\t./run.sh preprocess <txt_file>");
		Debug.pl("3. To evaluate a prediction file:");
		Debug.pl("\t./run.sh eval <pred_file> <label_file>");
	}

	public static void run() {
		Timer mainTimer = new Timer("TOTAL").start();
		Timer timer = new Timer();
		model.debug = X.getBoolean("debugModel");

		// read all tags
		readTags();

		trainFiles = X.get("trainFile").split(",");
		testFile = X.get("testFile");

		if (X.methodS == X.PERCEPTRON) {
			if (X.getBoolean("useModel")) {
				// test using an existing model
				timer.task = "reading the model";
				timer.start();
				model.readModel(X.modelFile);
				model.featureFactory.isTrain = false;
				model.labelFactory.isTrain = false;
				initNerFeature();
				timer.endPrint();
			} else {
				// train a model
				timer.task = "reading training data";
				timer.start();
				initNerFeature();
				// training the classifier
				readTrainDataFromPbf();
				// feature filtering
				model.featureFactory.filter(train);
				timer.endPrint();

				timer.task = "learning";
				timer.start();
				Learner l = new MultiLabelLRPerceptronLearner();
				LRPerceptronLearner.MAX_ITER_NUM = X.MAX_ITER_NUM;
				LRPerceptronLearner.STEP = X.PERCEPTRON_STEP;
				l.learn(train, model);
				Debug.dpl(train.getInstances().size()
						+ " examples for training");
				Debug.dpl("# of weights = "
						+ ((LRParameter) model.para).lambda.length);
				if (X.getBoolean("writeModel"))
					model.writeModel(X.modelFile);
				timer.endPrint();
				train = null;
				mentions.clear();
			}
		}

		timer.start("predicting");
		model.featureFactory.isTrain = false;
		if (X.methodS == X.PERCEPTRON) {
			MultiLabelLogisticRegression.prob_threshold = X
					.getDouble("prob_threshold");
			predict(testFile,
					new MultiLabelPerceptronNERClassifier(model.infer),
					new MultiLabelNERPerf(model), model);
		}
		Debug.pl();
		timer.endPrint();
		if (X.getBoolean("eval")) {
			Eval.trueLabelFile = X.get("labelFile");
			Debug.setLevel(2);
			Eval.process(X.get("outputFile")).print();
		}
		mainTimer.endPrint();

	}

	private static void initNerFeature() {
		nerFeature = new NERFeature(model);
		nerFeature.init();
	}

	private static void readTags() {
		MapType.typeFile = X.tagset;
		MapType.init();
		for (String newType : MapType.mapping.values()) {
			model.labelFactory.getLabel(newType);
		}
		Debug.pl("labels:\t" + model.labelFactory.allLabels);
	}

	/**
	 * given the testFile, split into single sentences, and test the entities
	 * from each sentence
	 * 
	 * @param testFile
	 *            : path + file
	 * @param classifier
	 * @param performance
	 * @param model
	 */
	public static void predict(String testFile, NERClassifier classifier,
			Performance performance, Model model) {
		Preprocessing.initPipeline(X.getBoolean("tokenized"),
				X.getBoolean("singleSentences"));
		Preprocessing.prepareProcess(testFile);

		String[] lines = FileUtil.getTextFromFile(testFile).split("\n");
		String[] segs = null;
		if (!X.getBoolean("segmentation")) {
			segs = FileUtil.getTextFromFile(X.get("inputSegments")).split(
					"\n\n");
			if (segs.length != lines.length) {
				Debug.pl("ERROR", "The size of sentences" + lines.length
						+ " is NOT equal to the size of segmented sentences="
						+ segs.length);
				Debug.vpl(segs[0]);
				Debug.vpl("=========================");
				Debug.vpl(segs[1]);
			}
		}
		Preprocessing.nerFeature = nerFeature;
		for (int i = 0; i < lines.length; ++i) {
			try {
				Preprocessing.sentId = i;
				Preprocessing.annotateImpl(lines[i]);
				if (!X.getBoolean("segmentation")) {
					Preprocessing.seg.setLength(0);
					Preprocessing.seg.append(segs[i]);
				}
				// convert the result in pbf format
				ArrayList<Mention> mentions = Preprocessing
						.processCurrentSentenceForPbf();
				predictCurrent(mentions, classifier, performance, model);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Preprocessing.finishProcess();
	}

	public static void predictCurrent(ArrayList<Mention> mentionList,
			NERClassifier classifier, Performance performance, Model model) {
		Hashtable<String, String[][]> results = new Hashtable<String, String[][]>();
		String filePrefix = testFile.substring(0, testFile.indexOf("."));
		{
			String[] tokens = Preprocessing.txt.toString().split(" ");
			String[][] sent = new String[tokens.length][2];
			for (int k = 0; k < tokens.length; k++) {
				sent[k][0] = tokens[k];
				sent[k][1] = "O";
			}
			String sid = filePrefix + "\t" + Preprocessing.sentId;
			results.put(sid, sent);
		}
		try {
			edu.washington.cs.figer.data.EntityProtos.Mention m = null;
			// do predictions
			Hashtable<TIntList, String> pool = new Hashtable<TIntList, String>();
			int c = 0;
			int j = 0;
			int exist = 0;

			for (int mi = 0; mi < mentionList.size(); mi++) {
				m = mentionList.get(mi);
				String sid = m.getFileid() + "\t" + m.getSentid();
				String[][] sent = results.get(sid);
				if (sent == null) {
					System.err.println("sid not found");
				}
				j++;

				String mention = StringUtils.join(m.getTokensList().toArray(),
						' ', m.getStart(), m.getEnd());
				if (mentions.contains(mention)) {
					exist++;
				}

				Instance inst = (Instance) X.instanceClass.newInstance();
				if (X.getBoolean("generateFeature")) {
					ArrayList<String> features = new ArrayList<String>();
					nerFeature.extract(m, features);
					for (String fea : features) {
						Feature f = model.featureFactory.getFeature(fea);
						FeatureFactory.setValue(inst, f, fea);
					}
				} else {
					for (String fea : m.getFeaturesList()) {
						Feature f = model.featureFactory.getFeature(fea);
						FeatureFactory.setValue(inst, f, fea);
					}
				}
				TIntList entity = new TIntArrayList();
				for (int i = m.getStart(); i < m.getEnd(); i++) {
					entity.add(i);
				}
				if (X.getBoolean("printSentence")) {
					Debug.vpl("SENTENCE@"
							+ m.getStart()
							+ "-"
							+ m.getEnd()
							+ ":"
							+ StringUtils.join(m.getTokensList().toArray(),
									' ', m.getStart(), m.getEnd()) + ":"
							+ StringUtils.join(m.getTokensList(), " "));
				}
				classifier.predict(inst, pool, entity, model);
				String plabels = pool.get(entity);
				sent[m.getStart()][1] = "B-" + plabels;
				for (int k = m.getStart() + 1; k < m.getEnd(); k++) {
					sent[k][1] = "I-" + plabels;
				}
				c++;
				pool.clear();
			}

			// write results
			{
				StringBuilder sb = new StringBuilder();
				String[][] sent = results.get(filePrefix + "\t"
						+ Preprocessing.sentId);
				for (int k = 0; k < sent.length; k++) {
					sb.append(sent[k][0] + "\t" + sent[k][1] + "\n");
				}
				sb.append("\n");
				Preprocessing.outputWriter.write(sb.toString());
			}
			Debug.print(".");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readTrainDataFromPbf() {
		try {
			if (trainFiles != null) {
				TObjectIntMap<Label> labelFreq = new TObjectIntHashMap<Label>();
				for (String tFile : trainFiles) {
					if (tFile.length() > 0) {
						readPbfTrainFile(tFile, labelFreq);
					}
				}
				Debug.vpl("===label frequency===");
				labelFreq.forEachEntry(new TObjectIntProcedure<Label>() {
					@Override
					public boolean execute(Label arg0, int arg1) {
						Debug.vpl(arg0 + "\t" + arg1);
						return true;
					}
				});
				Debug.vpl("===END===");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readPbfTrainFile(String tFile,
			TObjectIntMap<Label> labelFreq) throws Exception {
		MentionReader reader = MentionReader.getMentionReader(tFile);
		edu.washington.cs.figer.data.EntityProtos.Mention m = null;
		while ((m = reader.readMention()) != null) {
			// filtering
			if (m.getTokensCount() < 15 || m.getTokensCount() > 50)
				continue;

			Instance inst = (Instance) X.instanceClass.newInstance();
			for (String str : m.getLabelsList()) {
				String mappedType = MapType.mapping.get(str);
				if (mappedType != null)
					inst.setLabel(model.labelFactory.getLabel(mappedType));
			}
			if (((MultiLabelInstance) inst).labels.size() == 0)
				continue;
			else {
				for (Label l : ((MultiLabelInstance) inst).labels) {
					labelFreq.adjustOrPutValue(l, 1, 1);
				}
			}

			if (X.getBoolean("generateFeature")) {
				ArrayList<String> features = new ArrayList<String>();
				nerFeature.extract(m, features);

				if (features.size() < m.getFeaturesList().size()) {
					X.getCounter("NUM_more_original_features").Increment();
				} else {
					X.getCounter("NUM_less_original_features").Increment();
				}

				for (String fea : features) {

					Feature f = model.featureFactory.getFeature(fea);
					FeatureFactory.setValue(inst, f, fea);
				}
			} else {
				for (String fea : m.getFeaturesList()) {
					Feature f = model.featureFactory.getFeature(fea);
					FeatureFactory.setValue(inst, f, fea);
				}
			}
			mentions.add(StringUtils.join(m.getTokensList().toArray(), ' ',
					m.getStart(), m.getEnd()));
			train.getInstances().add(inst);
		}
		reader.close();
	}
}
