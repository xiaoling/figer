package edu.washington.cs.figer.web;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.pipeline.Annotation;
import edu.washington.cs.figer.FigerSystem;
import edu.washington.cs.figer.analysis.Preprocessing;

/**
 * 
 * @author Xiao Ling
 */

public class WebDemoServlet extends HttpServlet {
	private final static Logger logger = LoggerFactory
			.getLogger(WebDemoServlet.class);

	static FigerSystem figer = null;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		String text = request.getParameter("text");

		if (text == null) {
			try {
				response.getWriter().write("MISSING: Text to Disambiguate");
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (figer == null) {
			figer = FigerSystem.instance();
		}

		try {
			// DisambiguateResource dResource = new DisambiguateResource(
			// technique, isManual);
			// return dResource.process(text);
			Annotation annotation = new Annotation(text);
			if (Preprocessing.pipeline == null) {
				Preprocessing.initPipeline(false, false);
			}
			Preprocessing.pipeline.annotate(annotation);
			String pred = figer.predict(annotation, 0, 0, 1);
			response.getWriter().write(pred);
		} catch (Exception e) {
			e.printStackTrace();
			// response.getWriter().write("ERROR: ");
		}
	}

}
