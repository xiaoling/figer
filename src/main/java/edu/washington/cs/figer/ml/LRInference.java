package edu.washington.cs.figer.ml;

import java.util.ArrayList;

import edu.washington.cs.figer.data.Instance;
import edu.washington.cs.figer.util.V;

/**
 * 
 * @author Xiao Ling
 *
 */
public class LRInference extends Inference {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5056369182261762614L;

	public LRInference(){
		super();
	}
	
//	public LogisticRegression lr;
	public static double sigma = 1;

	@Override
	public Prediction findBestLabel(Instance x, Model m) {
		 LogisticRegression lr = (LogisticRegression)m;
		LRParameter para = (LRParameter)lr.para;
		int[] predictions = new int[lr.labelFactory.allLabels.size()];
		double[] probs = new double[lr.labelFactory.allLabels.size()];
		double[] origLambda = para.lambda;
		int times = 1;
//		if (lr.voted){
//			times = ((LRVotedParameter)lr.para).pool.size();
//			if (((LRVotedParameter)lr.para).pool.size() > 0 )
//				para.lambda = ((LRVotedParameter)lr.para).pool.get(0);
//		}

		for (int t = 0; t < times; t++){
			if (t > 0){
//				para.lambda = ((LRVotedParameter)lr.para).pool.get(t);
			}
			int L = lr.labelFactory.allLabels.size();
			int maxL = -1;
			double max = -Double.MAX_VALUE;
			ArrayList<Double> scores = new ArrayList<Double>();
			for (int j = 0; j < L; j++){
				double score = 0;
//				for (int i = 0; i < x.featureIndex.size(); i++){
				int startpoint = lr.featureFactory.allFeatures.size() * j;
				score += V.sumprod( x, para.lambda, startpoint);
					//			neg += sum_prod(para.lambda, x, 0);
//				}
				scores.add(score);
				if (score > max){
					maxL = j;
					max = score;
				}
			}
			//		ArrayList<Integer> result = new ArrayList<Integer>();
			//		if (pos >= neg)
			//			result.add(1);
			double part = 0;
			for (int i = 0; i < L; i++){
				part += Math.exp((scores.get(i) - max)/sigma);
			}

			predictions[maxL]++;
			probs[maxL] += 1/part;

		}
		
		int maxL = -1;
		int max = -1;
		for (int k = 0; k < predictions.length; k++){
			if (predictions[k]> max){
				maxL = k;
				max = predictions[k];
			}else if (predictions[k] == max && probs[k] > probs[maxL]){
				maxL = k;
				max = predictions[k];
			}
		}
		
		if (lr.voted){
			para.lambda = origLambda;
		}
		
		return new Prediction(lr.labelFactory.allLabels.get(maxL), probs[maxL] / max);
		//		else
		//			return lr.labelFactory.all_labels.get(0);
		//			result.add(0);
		//		return result;
	}

	

	/**
	 * return predictions with scores
	 */
	@Override
	public ArrayList<Prediction> findPredictions(Instance x, Model m) {
		LogisticRegression lr = (LogisticRegression)m;
		LRParameter para = (LRParameter)lr.para;
		int[] predictions = new int[lr.labelFactory.allLabels.size()];
		double[] probs = new double[lr.labelFactory.allLabels.size()];
		int times = 1;
//		if (lr.voted){
//			times = ((LRVotedParameter)lr.para).pool.size();
//			if (((LRVotedParameter)lr.para).pool.size() > 0 )
//				para.lambda = ((LRVotedParameter)lr.para).pool.get(0);
//		}
		ArrayList<Prediction> preds = null;
		for (int t = 0; t < times; t++){
			if (t > 0){
//				para.lambda = ((LRVotedParameter)lr.para).pool.get(t);
			}
			int L = lr.labelFactory.allLabels.size();
			int maxL = -1;
			double max = -Double.MAX_VALUE;
			ArrayList<Double> scores = new ArrayList<Double>();
			for (int j = 0; j < L; j++){
				int startpoint = lr.featureFactory.allFeatures.size() * j;
				double score = V.sumprod(x, para.lambda, startpoint);
				scores.add(score);
				if (score > max){
					maxL = j;
					max = score;
				}
			}
			if (maxL == -1){
				System.err.println("ERROR: pred not found "+x);
			}
			//		ArrayList<Integer> result = new ArrayList<Integer>();
			//		if (pos >= neg)
			//			result.add(1);
			double part = 0;
			for (int i = 0; i < L; i++){
				part += Math.exp((scores.get(i) - max)/sigma);
			}
			
			preds =new ArrayList<Prediction>();
			for (int i = 0; i < L; i++){
				preds.add(new Prediction(lr.labelFactory.allLabels.get(i), /*Math.exp((scores.get(i) - max)) / part*/scores.get(i)));
			}
			
			predictions[maxL]++;
			probs[maxL] += 1/part;
			
		}
		return preds;
	}

}
