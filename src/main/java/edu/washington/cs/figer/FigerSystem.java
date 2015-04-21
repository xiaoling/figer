package edu.washington.cs.figer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.OriginalTextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.StringUtils;
import edu.washington.cs.figer.analysis.MapType;
import edu.washington.cs.figer.analysis.Preprocessing;
import edu.washington.cs.figer.analysis.feature.NERFeature;
import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.data.EntityProtos.Mention.Dependency;
import edu.washington.cs.figer.data.Feature;
import edu.washington.cs.figer.data.FeatureFactory;
import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.ml.MultiLabelLogisticRegression;
import edu.washington.cs.figer.ml.MultiLabelPerceptronNERClassifier;
import edu.washington.cs.figer.ml.NERClassifier;
import edu.washington.cs.figer.util.FileUtil;
import edu.washington.cs.figer.util.StanfordDependencyResolver;
import edu.washington.cs.figer.util.Timer;
import edu.washington.cs.figer.util.X;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class FigerSystem {
	private static Logger logger = LoggerFactory.getLogger(FigerSystem.class);

	private static FigerSystem instance = null;
	public static String configFile = "config/figer.conf";

	// useful variables
	public Model model = new MultiLabelLogisticRegression();
	public NERClassifier classifier = null;
	public NERFeature nerFeature = null;
	// debug variables
	public static HashSet<String> mentions = new HashSet<String>();

	public synchronized static FigerSystem instance() {
		if (instance == null) {
			instance = new FigerSystem();
		}
		return instance;
	}

	private FigerSystem() {
		X.parseArgs(configFile);

		Timer timer = new Timer();
		model.debug = X.getBoolean("debugModel");

		// read all tags
		MapType.typeFile = X.tagset;
		MapType.init();
		for (String newType : MapType.mapping.values()) {
			model.labelFactory.getLabel(newType);
		}
		logger.info("labels:\t" + model.labelFactory.allLabels);

		// read the model
		if (X.methodS == X.PERCEPTRON) {
			timer.task = "reading the model " + X.modelFile;
			timer.start();
			model.readModel(X.modelFile);
			model.featureFactory.isTrain = false;
			model.labelFactory.isTrain = false;
			timer.endPrint();

			// init NER Features
			nerFeature = new NERFeature(model);
			nerFeature.init();

			MultiLabelLogisticRegression.prob_threshold = X
					.getDouble("prob_threshold");
			classifier = new MultiLabelPerceptronNERClassifier(model.infer);
		}
	}

	public String predict(Annotation annotation, int sentId, int startToken,
			int endToken) {
		Mention m = buildMention(annotation, sentId, startToken, endToken);
		// features
		ArrayList<String> features = new ArrayList<String>();
		nerFeature.extract(m, features);

		return predict(features);
	}

	public Mention buildMention(Annotation annotation, int sentId,
			int startToken, int endToken) {
		CoreMap sentAnn = annotation.get(SentencesAnnotation.class).get(sentId);
		List<CoreLabel> tokens = sentAnn.get(TokensAnnotation.class);
		// create a Mention object
		Mention.Builder m = Mention.newBuilder();
		m.setStart(startToken);
		m.setEnd(endToken);
		for (int i = 0; i < tokens.size(); i++) {
			m.addTokens(tokens.get(i).get(OriginalTextAnnotation.class));
			m.addPosTags(tokens.get(i).get(PartOfSpeechAnnotation.class));
		}
		m.setEntityName("");
		m.setFileid("on-the-fly");
		m.setSentid(sentId);

		// dependency
		String depStr = StanfordDependencyResolver.getString(sentAnn);
		if (depStr != null) {
			for (String d : depStr.split("\t")) {
				Matcher match = Preprocessing.depPattern.matcher(d);
				if (match.find()) {
					m.addDeps(Dependency.newBuilder().setType(match.group(1))
							.setGov(Integer.parseInt(match.group(3)) - 1)
							.setDep(Integer.parseInt(match.group(5)) - 1)
							.build());
				} else {

				}
			}
		}
		return m.build();
	}

	public String predict(List<String> features) {
		try {
			Instance inst = (Instance) X.instanceClass.newInstance();
			// logger.info("{}", features);
			for (String fea : features) {
				Feature f = model.featureFactory.getFeature(fea);
				FeatureFactory.setValue(inst, f, fea);
			}
			Hashtable<TIntList, String> pool = new Hashtable<TIntList, String>();
			TIntList entity = new TIntArrayList();
			classifier.predict(inst, pool, entity, model);

			String plabels = pool.get(entity);
			return plabels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// only use the following tag set to find named entities
	private static final Set<String> validTags = new HashSet<String>(
			Arrays.asList(new String[] { "PERSON", "ORGANIZATION", "LOCATION",
					"MISC", "O" }));

	public static List<Pair<Integer, Integer>> getNamedEntityMentions(
			CoreMap sentence) {
		List<Pair<Integer, Integer>> offsets = new ArrayList<Pair<Integer, Integer>>();
		String prevTag = "O";
		int tid = 0;
		int start = -1;
		for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
			String tag = token.get(NamedEntityTagAnnotation.class);
			if (!validTags.contains(tag)) {
				tag = "O";
			}
			if (tag.equals(prevTag)) {

			} else {
				if (tag.equals("O")) {
					offsets.add(Pair.makePair(start, tid));
					start = -1;
				} else {
					if (prevTag.equals("O")) {
						start = tid;
					} else {
						offsets.add(Pair.makePair(start, tid));
						start = tid;
					}
				}
			}
			prevTag = tag;
			tid++;
		}
		if (!prevTag.equals("O")) {
			offsets.add(Pair.makePair(start, tid));
		}
		return offsets;
	}

	private static void usage() {
		System.out
				.println("sbt \"runMain edu.washington.cs.figer.FigerSystem [config_file] text_file\"");
		System.out
				.println("    [config_file] is optional with a default value \"config/figer.conf\"");
	}

	public static void main(String[] args) {
		String textFile = null;
		if (args.length == 1) {
			textFile = args[0];
		} else if (args.length == 2) {
			configFile = args[0];
			textFile = args[1];
		} else {
			usage();
			System.exit(0);
		}

		// initialize the system
		FigerSystem sys = instance();
		Preprocessing.initPipeline();

		// preprocess the text
		List<String> list = FileUtil.getLinesFromFile(textFile);
		for (int i = 0; i < list.size(); i++) {
			Annotation annotation = new Annotation(list.get(i));
			Preprocessing.pipeline.annotate(annotation);

			// for each sentence
			int sentId = 0;
			for (CoreMap sentence : annotation.get(SentencesAnnotation.class)) {
				System.out.println("[l"
						+ i
						+ "][s"
						+ sentId
						+ "]tokenized sentence="
						+ StringUtils.joinWithOriginalWhiteSpace(sentence
								.get(TokensAnnotation.class)));
				List<Pair<Integer, Integer>> entityMentionOffsets = getNamedEntityMentions(sentence);
				for (Pair<Integer, Integer> offset : entityMentionOffsets) {
					String label = sys.predict(annotation, sentId,
							offset.first, offset.second);
					String mention = StringUtils
							.joinWithOriginalWhiteSpace(sentence.get(
									TokensAnnotation.class).subList(
									offset.first, offset.second));
					System.out.println("[l" + i + "][s" + sentId + "]mention"
							+ mention + "(" + offset.first + ","
							+ offset.second + ") = " + mention + ", pred = "
							+ label);
				}
				sentId++;
			}
		}
	}

}
