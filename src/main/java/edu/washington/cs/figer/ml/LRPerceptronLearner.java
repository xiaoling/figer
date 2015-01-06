package edu.washington.cs.figer.ml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.washington.cs.figer.data.DataSet;
import edu.washington.cs.figer.data.Instance;

/**
 * LRLearn Perceptron
 * 
 */
public class LRPerceptronLearner extends Learner {
  private static final Logger logger = LoggerFactory.getLogger(LRPerceptronLearner.class);
  /**
	 * 
	 */
  private static final long serialVersionUID = -1096711123000152900L;

  @Override
  public void learn(DataSet cdata, Model m) {
    this.m = m;
    if (m == null) {
      logger.error("learner doesn't have a model");
      return;
    }

    LogisticRegression lr = (LogisticRegression) m;

    LRParameter para = (LRParameter) (m.para);
    boolean changed = true;
    for (int idx_iter = 0; changed && idx_iter < MAX_ITER_NUM; idx_iter++) {
      changed = false;
      int F = lr.featureFactory.allFeatures.size();
      for (int idx_inst = 0; idx_inst < cdata.getInstances().size(); idx_inst++) {
        // positive = 1, negative = 0
        int pred = -1;
        if (((LogisticRegression) m).voted) {
          ((LogisticRegression) m).voted = false;
          pred =
              lr.labelFactory.index((lr.infer)
                  .findBestLabel(cdata.getInstances().get(idx_inst), lr).label);
          ((LogisticRegression) m).voted = true;
        } else {
          pred =
              lr.labelFactory.index((lr.infer)
                  .findBestLabel(cdata.getInstances().get(idx_inst), lr).label);
        }

        int truth = lr.labelFactory.index(cdata.getInstances().get(idx_inst).label);

        Instance x = cdata.getInstances().get(idx_inst);

        if (pred != truth) {
          changed = true;
          for (int idx_fea = 0; idx_fea < x.length(); idx_fea++) {
            para.lambda[pred * F + x.getIndex(idx_fea)] -= STEP * x.getValue(idx_fea);
            para.lambda[truth * F + x.getIndex(idx_fea)] += STEP * x.getValue(idx_fea);
          }
        }

      }
    }
  }

  public static int MAX_ITER_NUM = 10;
  public static double STEP = 1;
}
