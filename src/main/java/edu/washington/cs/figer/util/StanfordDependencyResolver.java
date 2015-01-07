package edu.washington.cs.figer.util;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.util.CoreMap;

/**
 * 
 * @author Xiao Ling
 */

public class StanfordDependencyResolver {
	private final static Logger logger = LoggerFactory
			.getLogger(StanfordDependencyResolver.class);
	// keep this order; otherwise depMethod will be overwritten to null
	public static Method depMethod = null;
	public final static Class depClass = getDependencyClass();

	private static Class getDependencyClass() {
		try {
			// stanford 1.3.4
			depMethod = Class.forName(
					"edu.stanford.nlp.trees.semgraph.SemanticGraph")
					.getDeclaredMethod("toList");
			return Class
					.forName("edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations$CollapsedDependenciesAnnotation");
		} catch (Exception e) {
			try {
				// for stanford 3.4+ or earlier
				depMethod = Class.forName(
						"edu.stanford.nlp.semgraph.SemanticGraph")
						.getDeclaredMethod("toList");
				return Class
						.forName("edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations$CollapsedDependenciesAnnotation");
			} catch (Exception e2) {
				logger.error("couldn't find the correct class of CollapsedDependenciesAnnotation");
				System.exit(-1);
				return null;
			}
		}
	}

	public static String getString(CoreMap sentAnn) {
		Object dependencies = sentAnn.get(depClass);
		try {
			return (String) (depMethod.invoke(dependencies));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
