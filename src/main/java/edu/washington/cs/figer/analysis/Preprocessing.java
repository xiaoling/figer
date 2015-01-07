package edu.washington.cs.figer.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.TreeReaderFactory;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import edu.washington.cs.figer.analysis.feature.NERFeature;
import edu.washington.cs.figer.data.EntityProtos.Mention;
import edu.washington.cs.figer.data.EntityProtos.Mention.Dependency;
import edu.washington.cs.figer.ml.Model;
import edu.washington.cs.figer.util.FileUtil;
import edu.washington.cs.figer.util.StanfordDependencyResolver;
import edu.washington.cs.figer.util.Timer;
import edu.washington.cs.figer.util.X;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class Preprocessing {
  private static final Logger logger = LoggerFactory.getLogger(Preprocessing.class);
  public static int count = 0;
  public static Pattern depPattern = Pattern
      .compile("(.+?)\\((.+?)\\-(\\d+)'*, *(.+?)\\-(\\d+?)'*\\)");
  public static String outputFile = null;
  public static FileOutputStream outputStream = null;
  public static NERFeature nerFeature = null;
  private static String inputPath = "data/";
  public static TreebankLanguagePack tlp = new PennTreebankLanguagePack();
  public static GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
  public static TreePrint tp = new TreePrint("typedDependenciesCollapsed");
  public static TreeReaderFactory tf = new TreeReaderFactory() {
    @Override
    public TreeReader newTreeReader(Reader in) {
      return new PennTreeReader(in, new LabeledScoredTreeFactory());
    }
  };

  public static StanfordCoreNLP pipeline = null;
  public static Pattern pHead = Pattern.compile("<HEADLINE>(.+?)</HEADLINE>", Pattern.DOTALL);
  public static Pattern pDate = Pattern.compile("<DATETIME>(.+?)</DATETIME>", Pattern.DOTALL);
  public static Pattern pText = Pattern.compile("<P>(.+?)</P>", Pattern.DOTALL);
  public static Pattern pBackupText = Pattern.compile("<TEXT>(.+?)</TEXT>", Pattern.DOTALL);

  public static BufferedWriter wtxt, wdep, wparse, wseg, wpos;
  public static BufferedWriter outputWriter;

  public static StringBuffer txt = new StringBuffer(), dep = new StringBuffer(),
      parse = new StringBuffer(), seg = new StringBuffer(), pos = new StringBuffer();
  public static String filePrefix = "";
  public static int sentId = 0;

  public static String[] ignoreNerTypes = {"DURATION", "MONEY", "NUMBER", "ORDINAL", "PERCENT",
      "SET", "TIME", "DATE"};
  public static HashSet<String> ignoreNerTypeSet = new HashSet<String>();
  static {
    for (String s : ignoreNerTypes) {
      ignoreNerTypeSet.add(s);
    }
  }

  public static void main(String[] args) {
//    String parse = "\nasfd\n\ndaf";
//    String[] lines = parse.split("\n");
//    System.out.println(lines.length);
//    System.exit(0);
    X.prop.put("tokenized", "true");
    X.prop.put("singleSentences", "true");
    initPipeline(true, true);
    Annotation ann =
        new Annotation("BOSTON 69 65 .515 5 1/2\n1. Michelle Freeman ( Jamaica ) 12.71 seconds");
    pipeline.annotate(ann);
    for (CoreMap sent : ann.get(SentencesAnnotation.class)) {
      for (CoreLabel token : sent.get(TokensAnnotation.class)) {
        logger.info(token.get(TextAnnotation.class));
      }
      logger.info("sentence done");
    }

  }

  public static void go(String testFile) {
    initPipeline(X.getBoolean("tokenized"), X.getBoolean("singleSentences"));
    prepareProcess(testFile);
    processFile(testFile, inputPath);
    try {
      wtxt.close();
      wpos.close();
      wparse.close();
      wdep.close();
      if (X.getBoolean("segmentation")) {
        wseg.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    String filePrefix = testFile.replace(".txt", "");
    preparePbf(filePrefix);
    try {
      outputStream.close();
      if (X.get("outputFile") != null) {
        outputWriter.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void preparePbf(String testFile) {
    Model model = new Model();
    nerFeature = new NERFeature(model);
    Timer timer = new Timer();
    timer.start("extracting features");
    processFileForPbf(testFile);
    timer.endPrint();
  }

  public static ArrayList<Mention> processCurrentSentenceForPbf() {
    try {
      BufferedReader reader = new BufferedReader(new StringReader(seg.toString()));
      String line = null;
      String prevTag = null, curTag = null;
      int sentId = 0, tokenId = 0, startToken = -1, endToken = -1;
      ArrayList<String> sent = new ArrayList<String>();
      TIntList starts = new TIntArrayList(), ends = new TIntArrayList();
      ArrayList<String> tags = new ArrayList<String>();
      ArrayList<Mention> mentions = new ArrayList<Mention>();
      while ((line = reader.readLine()) != null) {
        {
          String[] items = line.split("\t");

          if (items.length < 2) {
            logger.error("not enough fields\t " + sentId + "*" + line + "*");
            curTag = "O";
          } else {
            curTag = items[1];
          }

          if (!curTag.equals("O")) {
            if (prevTag != null) {
              if (curTag.startsWith("I-")) {
                endToken++;
              } else {
                starts.add(startToken);
                ends.add(endToken);
                tags.add(prevTag);
                startToken = tokenId;
                endToken = tokenId + 1;
                prevTag = curTag;
              }
            } else {
              startToken = tokenId;
              endToken = tokenId + 1;
              prevTag = curTag;
            }
          } else {
            if (prevTag != null) {
              starts.add(startToken);
              ends.add(endToken);
              tags.add(prevTag);
              startToken = -1;
              endToken = -1;
              prevTag = null;
            }
          }
          sent.add(line);
          tokenId++;
        }
      }

      {
        // after last line
        if (prevTag != null) {
          starts.add(startToken);
          ends.add(endToken);
          tags.add(prevTag);

          startToken = -1;
          endToken = -1;
          prevTag = null;
          curTag = null;
        }
        for (int i = 0; i < starts.size(); i++) {
          mentions.add(buildCurrentMessage(tags.get(i), starts.get(i), ends.get(i), sent));
        }

        starts.clear();
        ends.clear();
        tags.clear();
        sent.clear();
        sentId++;
        tokenId = 0;
      }
      reader.close();
      return mentions;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static void processFileForPbf(String string) {
    try {
      BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(new FileInputStream(string + ".segment"), "UTF8"));
      String line = null;
      String prevTag = null, curTag = null;
      int tokenId = 0, startToken = -1, endToken = -1;
      ArrayList<String> sent = new ArrayList<String>();
      TIntList starts = new TIntArrayList(), ends = new TIntArrayList();
      ArrayList<String> tags = new ArrayList<String>();
      while ((line = reader.readLine()) != null) {
        if (line.trim().equals("")) {
          if (prevTag != null) {
            // do something
            starts.add(startToken);
            ends.add(endToken);
            tags.add(prevTag);

            startToken = -1;
            endToken = -1;
            prevTag = null;
            curTag = null;
          }
          for (int i = 0; i < starts.size(); i++) {
            buildMessage(tags.get(i), string, sentId, starts.get(i), ends.get(i), sent);
          }

          starts.clear();
          ends.clear();
          tags.clear();
          sent.clear();
          sentId++;
          tokenId = 0;
        } else {
          String[] items = line.split("\t");

          if (items.length < 2) {
            System.err.println("[ERR]not enough fields\t " + string + "\t" + sentId + "*" + line
                + "*");
            curTag = "O";
          } else {
            curTag = items[1];
          }

          if (!curTag.equals("O")) {
            if (prevTag != null) {
              if (curTag.startsWith("I-")) {
                endToken++;
              } else {
                starts.add(startToken);
                ends.add(endToken);
                tags.add(prevTag);
                startToken = tokenId;
                endToken = tokenId + 1;
                prevTag = curTag;
              }
            } else {
              startToken = tokenId;
              endToken = tokenId + 1;
              prevTag = curTag;
            }
          } else {
            if (prevTag != null) {
              starts.add(startToken);
              ends.add(endToken);
              tags.add(prevTag);
              startToken = -1;
              endToken = -1;
              prevTag = null;
            }
          }
          sent.add(line);
          tokenId++;
        }
      }

      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Mention buildCurrentMessage(String tag, int startToken, int endToken,
      ArrayList<String> sent) {
    Mention.Builder m = Mention.newBuilder();
    m.setStart(startToken);
    m.setEnd(endToken);
    for (int i = 0; i < sent.size(); i++) {
      String[] items = sent.get(i).split("\t");
      m.addTokens(items[0]);
    }
    String[] posTags = pos.toString().split(" ");
    for (int i = 0; i < sent.size(); i++) {
      m.addPosTags(posTags[i]);
    }
    m.setEntityName(tag);
    m.setFileid(filePrefix);
    m.setSentid(sentId);

    // dependency
    {
      String depStr = dep.toString();
      if (depStr != null) {
        for (String d : depStr.split("\t")) {
          Matcher match = depPattern.matcher(d);
          if (match.find()) {
            m.addDeps(Dependency.newBuilder().setType(match.group(1))
                .setGov(Integer.parseInt(match.group(3)) - 1)
                .setDep(Integer.parseInt(match.group(5)) - 1).build());
          } else {

          }
        }
      }
    }

    // features
    {
      ArrayList<String> features = new ArrayList<String>();
      nerFeature.extract(m.build(), features);
      m.addAllFeatures(features);
    }

    try {
      m.build().writeDelimitedTo(outputStream);
      count++;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return m.build();
  }

  private static void buildMessage(String prevTag, String string, int sentId, int startToken,
      int endToken, ArrayList<String> sent) {
    Mention.Builder m = Mention.newBuilder();
    m.setStart(startToken);
    m.setEnd(endToken);
    for (int i = 0; i < sent.size(); i++) {
      String[] items = sent.get(i).split("\t");
      m.addTokens(items[0]);
    }
    String[] posLines = FileUtil.getTextFromFile(string + ".pos").split("\n");
    String[] posTags = posLines[sentId].split(" ");
    for (int i = 0; i < sent.size(); i++) {
      m.addPosTags(posTags[i]);
    }
    m.setEntityName(prevTag);
    m.setFileid(string);
    m.setSentid(sentId);

    // dependency
    {
      String dep = null;
      if (new File(string + ".dep").exists()) {
        String[] deps =
            StringUtils.splitPreserveAllTokens(FileUtil.getTextFromFile(string + ".dep"), '\n');
        String[] parses = FileUtil.getTextFromFile(string + ".parse").split("\n+");
        if (deps.length - 1 != parses.length) {
          logger.warn(string + "@" + sentId + "  dep:" + deps.length + "  parse:" + parses.length);
        } else {
          dep = deps[sentId].replace("\t", "\n").trim();
        }
      }
      if (dep == null) {
        if (new File(string + ".parse").exists()) {
          logger.debug("transforming dep parses for " + string);
          String[] parses = FileUtil.getTextFromFile(string + ".parse").split("\n+");
          dep = getDep(parses[sentId]).trim();
        }
      }
      if (dep != null) {
        for (String d : dep.split("\n")) {
          Matcher match = depPattern.matcher(d);
          if (match.find()) {
            m.addDeps(Dependency.newBuilder().setType(match.group(1))
                .setGov(Integer.parseInt(match.group(3)) - 1)
                .setDep(Integer.parseInt(match.group(5)) - 1).build());

          } else {

          }
        }
      }
    }

    // features
    {
      ArrayList<String> features = new ArrayList<String>();
      nerFeature.extract(m.build(), features);
      m.addAllFeatures(features);
    }

    try {
      m.build().writeDelimitedTo(outputStream);
      count++;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void initPipeline() {
    initPipeline(false, false);
  }

  public static void initPipeline(boolean tokenized, boolean singleSentence) {
    if (!singleSentence && tokenized) {
      logger.error("Input can't be not sentence splitted but tokenized.");
      System.exit(-1);
    }
    if (!X.getBoolean("segmentation")
        && (!X.getBoolean("tokenized") || !X.getBoolean("singleSentences"))) {
      logger.info(X.getBoolean("segmentation") + "=segmentation");
      logger.error("Input with segmentation should be both sentence-splitted and tokenized.");
      System.exit(-1);
    }
    Properties props = new Properties();
    props.put("parse.maxlen", "80");
    if (tokenized) {
      props.put("tokenize.whitespace", "true");
    }
    if (singleSentence) {
      props.put("ssplit.isOneSentence", "true");
      props.put("ssplit.eolonly", "true");
    }
    // props.put("ssplit.eolonly", true);
    props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
//    props.put("ner.model.7class", "");
//    props.put("ner.model.3class", "");
//    props.put("ner.model.MISCclass", "");
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
    pipeline = new StanfordCoreNLP(props);
  }

  /**
   * open the files to store processing info
   * 
   * @param filename
   */
  public static void prepareProcess(String filename) {
    filePrefix = filename.replace(".txt", "");
    try {
      wtxt =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePrefix + ".tokens"),
              "UTF-8"));
      wpos =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePrefix + ".pos"),
              "UTF-8"));
      wparse =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePrefix + ".parse"),
              "UTF-8"));
      wdep =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePrefix + ".dep"),
              "UTF-8"));
      if (X.getBoolean("segmentation")) {
        wseg =
            new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePrefix + ".segment"), "UTF-8"));
      }

      // TODO: use MentionWriter instead
      outputFile = filePrefix + ".pbf";
      outputStream = new FileOutputStream(outputFile);
      String predFile = X.get("outputFile");
      if (predFile != null) {
        outputWriter =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(predFile), "UTF-8"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * close the files
   * 
   * @param filename
   */
  public static void finishProcess() {
    try {
      wtxt.close();
      wpos.close();
      wparse.close();
      wdep.close();
      if (X.getBoolean("segmentation")) {
        wseg.close();
      }
      outputStream.close();
      if (X.get("outputFile") != null) {
        outputWriter.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void processFile(String file, String path) {
    String text = FileUtil.getTextFromFile(file);
    try {
      annotate(text);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static void annotate(String text) throws IOException {
    String[] lines = text.split("\n");
    if (lines.length > 10000) {
      int folds = lines.length / 10000;
      logger.info("splitting into " + folds + " folds\n");
      for (int i = 0; i < folds; i++) {
        logger.info("processing the " + i + "-th fold\n");
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 10000; j++) {
          sb.append(lines[i * 10000 + j] + "\n");
        }
        annotateImpl(sb.toString());
      }
      // finish the rest
      StringBuilder sb = new StringBuilder();
      for (int j = folds * 10000; j < lines.length; j++) {
        sb.append(lines[j] + "\n");
      }
      annotateImpl(sb.toString());
    } else {
      annotateImpl(text);
    }
  }

  public static void annotateImpl(String text) throws IOException {
    Annotation document = new Annotation(text);
    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
    logger.info("# of sent = " + sentences.size());
    for (CoreMap sentence : sentences) {
      // parse info
      Tree tree = sentence.get(TreeAnnotation.class);
      wparse.write(tree.toString() + "\n");
      parse.setLength(0);
      parse.append(tree.toString());
      // this is the Stanford dependency graph of the current sentence
      String deps = StanfordDependencyResolver.getString(sentence);
      wdep.write(deps.replace("\n", "\t") + "\n");
      dep.setLength(0);
      dep.append(deps.replace("\n", "\t"));
      // traversing the words in the current sentence
      // a CoreLabel is a CoreMap with additional token-specific methods
      ArrayList<String> tags = new ArrayList<String>(), tokens = new ArrayList<String>();
      txt.setLength(0);
      pos.setLength(0);
      for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
        String tokenTxt = token.word();
        // this is the POS tag of the token
        String posStr = token.get(PartOfSpeechAnnotation.class);
        String ner = token.get(NamedEntityTagAnnotation.class);
        tokens.add(tokenTxt);
        if (!ignoreNerTypeSet.contains(ner)) {
          tags.add(ner);
        } else {
          tags.add("O");
        }
        wtxt.write(tokenTxt + " ");
        txt.append(tokenTxt + " ");
        wpos.write(posStr + " ");
        pos.append(posStr + " ");
      }
      wtxt.write("\n");
      wpos.write("\n");
      if (X.getBoolean("segmentation") == true) {
        seg.setLength(0);
        String prevTag = "O";
        for (int i = 0; i < tags.size(); i++) {
          if (prevTag.equals("O")) {
            if (!tags.get(i).equals(prevTag)) {
              wseg.write(tokens.get(i) + "\t" + "B-E\t" + tags.get(i) + "\n");
              seg.append(tokens.get(i) + "\t" + "B-E\t" + tags.get(i) + "\n");
            } else {
              wseg.write(tokens.get(i) + "\t" + "O\t" + tags.get(i) + "\n");
              seg.append(tokens.get(i) + "\t" + "O\t" + tags.get(i) + "\n");
            }
          } else {
            if (!tags.get(i).equals(prevTag)) {
              if (!tags.get(i).equals("O")) {
                wseg.write(tokens.get(i) + "\t" + "B-E\t" + tags.get(i) + "\n");
                seg.append(tokens.get(i) + "\t" + "B-E\t" + tags.get(i) + "\n");
              } else {
                wseg.write(tokens.get(i) + "\t" + "O\t" + tags.get(i) + "\n");
                seg.append(tokens.get(i) + "\t" + "O\t" + tags.get(i) + "\n");
              }
            } else {
              wseg.write(tokens.get(i) + "\t" + "I-E\t" + tags.get(i) + "\n");
              seg.append(tokens.get(i) + "\t" + "I-E\t" + tags.get(i) + "\n");
            }
          }
          prevTag = tags.get(i);
        }
        wseg.write("\n");
      }

    }
  }

  public static String getDep(String parse) {
    Tree t;
    StringBuilder sb = new StringBuilder();
    try {
      t = tf.newTreeReader(new StringReader(parse)).readTree();
      GrammaticalStructure gs = gsf.newGrammaticalStructure(t);
      Iterator<TypedDependency> it = gs.typedDependenciesCollapsed().iterator();
      while (it.hasNext()) {
        sb.append(it.next() + "\t");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }
}
