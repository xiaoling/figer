package edu.washington.cs.figer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.washington.cs.figer.analysis.MapType;
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
import edu.washington.cs.figer.ml.MultiLabelPerceptronNERClassifier;
import edu.washington.cs.figer.ml.NERClassifier;
import edu.washington.cs.figer.ml.Prediction;
import edu.washington.cs.figer.util.FileUtil;
import edu.washington.cs.figer.util.Timer;
import edu.washington.cs.figer.util.X;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private Main() {
	}

	// useful variables
	public static Model model = new MultiLabelLogisticRegression();
	public static DataSet train = new DataSet(), test = new DataSet();
	public static NERFeature nerFeature = null;
	public static String[] trainFiles = null;
	public static String testDataLocation = null;
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
				Eval.process(args[1], args[2]).print();
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
		logger.info("1. To run the main function:");
		logger.info("\t./run.sh <config_file>");
		logger.info("2. To only preprocess a txt file:");
		logger.info("\t./run.sh preprocess <txt_file>");
		logger.info("3. To evaluate a prediction file:");
		logger.info("\t./run.sh eval <pred_file> <label_file>");
	}

	public static void run() {
		Timer mainTimer = new Timer("TOTAL").start();
		Timer timer = new Timer();
		model.debug = X.getBoolean("debugModel");

		// read all tags
		readTags(X.tagset);

		trainFiles = X.get("trainFile").split(",");
		testDataLocation = X.get("testFile");

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
				//
				// sample a portion as dev
				Random rand = new Random(10349);
				Iterator<Instance> iterator = train.getInstances().iterator();
				while (iterator.hasNext()) {
					Instance x = iterator.next();
					if (rand.nextDouble() > 0.8) {
						test.add(x);
						iterator.remove();
					}
				}
				
				l.learn(train, model);
				testDev(test, model);

				logger.debug(train.getInstances().size()
						+ " examples for training");
				logger.debug("# of weights = "
						+ ((LRParameter) model.para).lambda.length);
				if (X.getBoolean("writeModel"))
					model.writeModel(X.modelFile);
				timer.endPrint();
				train.getInstances().clear();
				test.getInstances().clear();
				mentions.clear();
			}
		}

		model.featureFactory.isTrain = false;
		if (X.methodS == X.PERCEPTRON) {
			MultiLabelLogisticRegression.prob_threshold = X
					.getDouble("prob_threshold");
			if (new File(testDataLocation).isDirectory()) {
				String[] testFiles = new File(testDataLocation).list();
				NERClassifier classifier = new MultiLabelPerceptronNERClassifier(
						model.infer);
				Performance perf = new MultiLabelNERPerf(model);
				for (String tf : testFiles) {
					if (tf.endsWith(".txt")) {
						timer.start("predicting " + tf);
						if (new File(testDataLocation + "/"
								+ tf.replace(".txt", ".out")).exists()) {
							continue;
						}
						X.prop.put(
								"outputFile",
								testDataLocation + "/"
										+ tf.replace(".txt", ".out"));
						predict(testDataLocation + "/" + tf, classifier, perf,
								model);
						timer.endPrint();
					}
				}
			} else {
				timer.start("predicting");
				predict(testDataLocation,
						new MultiLabelPerceptronNERClassifier(model.infer),
						new MultiLabelNERPerf(model), model);
				timer.endPrint();
			}
		}

		if (X.getBoolean("eval")) {
			Eval.process(X.get("outputFile"), X.get("labelFile")).print();
		}
		mainTimer.endPrint();

	}

	private static void testDev(DataSet data, Model m) {
		Timer timer = new Timer("testing dev").start();
		MultiLabelNERPerf perf = new MultiLabelNERPerf(m);
		int i = 0;
		for (Instance x : data.getInstances()) {
			if (i % 500000 == 0) {
				logger.info("tested " + i + " instances");
			}
			MultiLabelInstance inst = (MultiLabelInstance) x;
			ArrayList<Prediction> predictions = m.infer
					.findPredictions(inst, m);
			ArrayList<Label> labels = ((MultiLabelLogisticRegression) m)
					.makePredictions(predictions);
			perf.computeMetric(labels, inst.labels);
			i++;
		}
		System.out.println("strict\t"
				+ MultiLabelNERPerf.getResultString(perf.pSum, perf.pNum,
						perf.rSum, perf.rNum));

		System.out.println("loose micro\t"
				+ MultiLabelNERPerf.getResultString(perf.pSum2, perf.pNum2,
						perf.rSum2, perf.rNum2));

		System.out.println("loose macro\t"
				+ MultiLabelNERPerf.getResultString(perf.pSum3, perf.pNum3,
						perf.rSum3, perf.rNum3));

		timer.endPrint();
	}

	private static void initNerFeature() {
		nerFeature = new NERFeature(model);
		nerFeature.init();
	}

	public static void readTags(String tagFile) {
		MapType.typeFile = tagFile;
		MapType.init();
		for (String newType : MapType.mapping.values()) {
			model.labelFactory.getLabel(newType);
		}
		logger.info("labels:\t" + model.labelFactory.allLabels);
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
		if (X.getBoolean("preprocess")) {
			Preprocessing.initPipeline(X.getBoolean("tokenized"),
					X.getBoolean("singleSentences"));
			Preprocessing.prepareProcess(testFile);
			String[] lines = FileUtil.getTextFromFile(testFile).split("\n");
			String[] segs = null;
			if (!X.getBoolean("segmentation")) {
				segs = FileUtil.getTextFromFile(X.get("inputSegments")).split(
						"\n\n");
				if (segs.length != lines.length) {
					logger.error("The size of sentences"
							+ lines.length
							+ " is NOT equal to the size of segmented sentences="
							+ segs.length);
					logger.error(segs[0]);
					logger.error("=========================");
					logger.error(segs[1]);
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
					predictCurrent(testFile, mentions, classifier, performance,
							model);
					logger.info("sent {} is done", i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Preprocessing.finishProcess();
		} else {
			Preprocessing.filePrefix = testFile.replace(".txt", "");
			try {
				Preprocessing.outputWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								Preprocessing.filePrefix + ".out"), "UTF-8"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			String[] segs = null;
			segs = FileUtil.getTextFromFile(
					Preprocessing.filePrefix + ".segment").split("\n\n");
			String[] tokens = FileUtil.getTextFromFile(
					Preprocessing.filePrefix + ".tokens").split("\n");
			String[] postags = FileUtil.getTextFromFile(
					Preprocessing.filePrefix + ".pos").split("\n");
			String[] parses = FileUtil.getTextFromFile(
					Preprocessing.filePrefix + ".parse").split("\n");
			String[] deps = FileUtil.getTextFromFile(
					Preprocessing.filePrefix + ".dep").split("\n");
			Preprocessing.nerFeature = nerFeature;
			for (int i = 0; i < segs.length; ++i) {
				// Preprocessing.txt =
				// public static StringBuffer txt = new StringBuffer(), dep =
				// new StringBuffer(),
				// parse = new StringBuffer(), seg = new StringBuffer(), pos =
				// new StringBuffer();
				try {
					Preprocessing.sentId = i;
					Preprocessing.seg.setLength(0);
					Preprocessing.seg.append(segs[i]);
					Preprocessing.txt.setLength(0);
					Preprocessing.txt.append(tokens[i]);
					Preprocessing.pos.setLength(0);
					Preprocessing.pos.append(postags[i]);
					Preprocessing.dep.setLength(0);
					Preprocessing.dep.append(deps[i]);
					Preprocessing.parse.setLength(0);
					Preprocessing.parse.append(parses[i]);
					// convert the result in pbf format
					ArrayList<Mention> mentions = Preprocessing
							.processCurrentSentenceForPbf();
					predictCurrent(testFile, mentions, classifier, performance,
							model);
					logger.info("sent {} is done", i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Preprocessing.outputWriter.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void predictCurrent(String testFile,
			ArrayList<Mention> mentionList, NERClassifier classifier,
			Performance performance, Model model) {
		Hashtable<String, String[][]> results = new Hashtable<String, String[][]>();
		String filePrefix = testFile.substring(0, testFile.lastIndexOf("."));
		String[] sents = Preprocessing.txt.toString().split("\n");
		for (int s = 0; s < sents.length; s++) {
			String[] tokens = sents[s].split(" ");
			String[][] sent = new String[tokens.length][2];
			for (int k = 0; k < tokens.length; k++) {
				sent[k][0] = tokens[k];
				sent[k][1] = "O";
			}
			String sid = filePrefix + "\t" + s + "\t" + Preprocessing.sentId;
			results.put(sid, sent);
		}
		try {
			edu.washington.cs.figer.data.EntityProtos.Mention m = null;
			// do predictions
			Hashtable<TIntList, String> pool = new Hashtable<TIntList, String>();

			for (int mi = 0; mi < mentionList.size(); mi++) {
				m = mentionList.get(mi);
				String sid = m.getFileid() + "\t" + m.getSentid();
				String[][] sent = results.get(sid);
				if (sent == null) {
					logger.error("sid [{}] not recognized for finding its output sentence");
					continue;
				}

				// String mention =
				// StringUtils.join(m.getTokensList().toArray(),
				// ' ', m.getStart(), m.getEnd());

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
					logger.debug("SENTENCE@"
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
				pool.clear();
			}

			// write results
			for (int s = 0; s < sents.length; s++) {
				StringBuilder sb = new StringBuilder();
				String[][] sent = results.get(filePrefix + "\t" + s + "\t"
						+ Preprocessing.sentId);
				for (int k = 0; k < sent.length; k++) {
					sb.append(sent[k][0] + "\t" + sent[k][1] + "\n");
				}
				sb.append("\n");
				Preprocessing.outputWriter.write(sb.toString());
			}
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
				logger.debug("===label frequency===");
				labelFreq.forEachEntry(new TObjectIntProcedure<Label>() {
					@Override
					public boolean execute(Label arg0, int arg1) {
						logger.debug(arg0 + "\t" + arg1);
						return true;
					}
				});
				logger.debug("===END=label frequency==");
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
