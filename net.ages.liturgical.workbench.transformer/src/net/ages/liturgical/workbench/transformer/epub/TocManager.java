package net.ages.liturgical.workbench.transformer.epub;

/**
 * Handles entries to a Table of Contents
 * @author mac002
 *
 */
public class TocManager {
	private StringBuffer sb = new StringBuffer();
	private boolean excludeBetweenParentheses = true;
	
	public TocManager(
			boolean excludeBetweenParentheses
			) {
		this.excludeBetweenParentheses = excludeBetweenParentheses;
		sb.append("\n<table>");
	}
	
	public void add(String className, String href, String text) {
		if (excludeBetweenParentheses) {
			if (! text.trim().startsWith("(")) {
				sb.append(
						row(
								className
								, href
								, text
						)
				);
			}
		}
	}
	
	public void add(String className, String href, String textLeft, String textRight) {
		if (excludeBetweenParentheses) {
			if (! textLeft.trim().startsWith("(")) {
				sb.append(
						row(
								className
								, href
								, textLeft
								, textRight
						)
				);
			}
		}
	}
	
	public String tocAsHtmlTable() {
		// StringBuffer is already loaded up.
		// Just need to close off the table
		// and return it as a string
		sb.append("\n</table>");
		return sb.toString();
	}
	
	private String row(String cClassName, String href, String textLeft, String textRight) {
		return "<tr>"
				+ tdLeft(cClassName, href,textLeft)
				+ tdRight(cClassName,href,textRight)
				+ "</tr>";
	}
	
	private String row(String cClassName, String href, String text) {
		return "<tr>"
				+ tdLeft(cClassName,href,text)
				+ "</tr>";
	}
	
	private String tdLeft(String className, String href, String text) {
		return td("leftCell", className, href,text);
	}
	
	private String tdRight(String className, String href, String text) {
		return td("rightCell", className,href,text);
	}

	private String td(String tdClassName, String pClassName, String href, String text) {
		return
				 "<td class=\""
				+ tdClassName 
				+ "\">" 
				+ anchor(pClassName, href,text)
				+ "</td> "
				;
	}
	
	private String anchor(String pClassName, String href, String text) {
		return
				"<p class=\""
				+ pClassName 
				+ "\">"
				+  "<a class=\"" 
				+ pClassName 
				+ "\" href=\"" 
				+ href 
				+ "\">"
				+ text 
				+ "</a>"
				+ "</p>";
	}
	
}
