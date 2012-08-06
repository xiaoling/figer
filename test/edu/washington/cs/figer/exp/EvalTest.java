package edu.washington.cs.figer.exp;

import org.junit.Test;

public class EvalTest {

	@Test
	public void test() {
		
//		args = new String[]{"/homes/gws/xiaoling/dataset/news12/"+myFile};
//		String file = args[0];
		String myFile = "/homes/gws/xiaoling/dataset/news12/"+"";
		String refFile = "";
		Eval.trueLabelFile = refFile;
		Performance perf = Eval.process(myFile);
//		TODO: verify the perf numbers
//		generateGoldStfdResults();
	}
	


}
