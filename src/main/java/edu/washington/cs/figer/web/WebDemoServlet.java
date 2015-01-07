package edu.washington.cs.figer.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import edu.washington.cs.figer.FigerSystem;
import edu.washington.cs.figer.analysis.Preprocessing;

/**
 * 
 * @author Xiao Ling
 */

public class WebDemoServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2498702085857068707L;

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
				response.getWriter().write("MISSING: Text");
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (figer == null) {
			figer = FigerSystem.instance();
		}

		try {
			Annotation annotation = new Annotation(text);
			if (Preprocessing.pipeline == null) {
				Preprocessing.initPipeline(false, false);
			}
			Preprocessing.pipeline.annotate(annotation);

			int sentId = 0;
			response.getWriter().write(
					"<html>\n<head>" + "<link rel=\"stylesheet\" "
							+ "href=\"https://maxcdn.bootstrapcdn.com"
							+ "/bootstrap/3.3.1/css/bootstrap.min.css\">"
							+ "</head>\n<body>\n");
			for (CoreMap sentence : annotation.get(SentencesAnnotation.class)) {
				response.getWriter().write("<table>");
				List<StringBuilder> tags = new ArrayList<StringBuilder>();
				List<StringBuilder> words = new ArrayList<StringBuilder>();
				for (CoreLabel label : sentence.get(TokensAnnotation.class)) {
					tags.add(new StringBuilder("<td></td>"));
					words.add(new StringBuilder("<td>" + label.originalText()
							+ "&nbsp;</td>"));
				}

				// System.out.println("[s" + sentId + "]"
				// + sentence.get(TextAnnotation.class));
				List<Pair<Integer, Integer>> entityMentionOffsets = FigerSystem
						.getNamedEntityMentions(sentence);
				for (Pair<Integer, Integer> offset : entityMentionOffsets) {
					StringBuilder mention = new StringBuilder();
					for (int i = offset.first; i < offset.second; i++) {
						// clean
						tags.get(i).delete(0, tags.get(i).length());
						mention.append(words.get(i));
						words.get(i).delete(0, words.get(i).length());
					}
					words.get(offset.first).append(
							mention.toString()
									.replace("</td><td>", "")
									.replace("<td>",
											"<td><p class=\"bg-success\">")
									.replace("</td>", "</p></td>"));
					String label = figer.predict(annotation, sentId,
							offset.first, offset.second);
					// String mention = StringUtils
					// .joinWithOriginalWhiteSpace(sentence.get(
					// TokensAnnotation.class).subList(
					// offset.first, offset.second));
					tags.get(offset.first)
							.append("<td><p class=\"bg-info\">"
									+ label.replace(",", "<br/>") + "</p></td>");
				}
				response.getWriter().write(
						"<tr><td><p class=\"text-primary\">Sentence:</p></td>");
				for (int i = 0; i < tags.size(); i++) {
					response.getWriter().write(words.get(i).toString());
				}
				response.getWriter().write("</tr>\n");
				response.getWriter().write(
						"<tr><td><p class=\"text-info\">Annotation:</p></td>");
				for (int i = 0; i < tags.size(); i++) {
					response.getWriter().write(tags.get(i).toString());
				}
				response.getWriter().write("</tr>\n");
				response.getWriter().write("</table>\n");

				sentId++;
			}
			response.getWriter().write("</body>\n</html>");
		} catch (Exception e) {
			e.printStackTrace();
			// response.getWriter().write("ERROR: ");
		}
	}
}
