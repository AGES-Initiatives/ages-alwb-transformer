package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * HTML constants and helper methods to create HTML blocks
 * @author mac002
 *
 */
public class HtmlUtils {
	public static final String HTMLopen = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">";
	public static final String HTMLclose = "\n</html>";
	public static final String HEADopen = "\n<head>";
	public static final String HEADclose = "\n</head>";
	public static final String BODYopen = "\n<body>";
	public static final String BODYclose = " </body>";
	public static final String TABLEopen = "\n<table>";
	public static final String TABLEopenParm = "<table ";
	public static final String TABLEclose = " </table>";
	public static final String Q = "\"";
	public static final String ROWopenParm = "\n<tr ";
	public static final String ROWclose = "\n</tr>";
	public static final String COLopenParm = "\n<td ";
	public static final String COLclose = "</td>";
	public static final String PopenParm = "\n<p ";
	public static final String Pclose = "</p>";
	public static final String CLASS = " class=";
	public static final String metaUtf8 = "\n\t<meta charset=\"utf-8\">";
	
	public static String para(String theClass, String theValue) {
		return PopenParm + CLASS + q(theClass) + ">"+ theValue + Pclose;
	}
	
	/**
	 * 
	 * @param title
	 * @param css
	 * @return
	 */
	public static String getHead(String title, String css) {
		return HEADopen
				+ "<title>"
				+ title
				+ "</title>"
				+ "<link href=\""
				+ css
				+ "\" rel=\"stylesheet\" type=\"text/css\"/>"
				+ HEADclose;
	}
	
	public static Document getDoc(File file) {
		Document result = null;
		try {
			result = Jsoup.parse(file, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String td(String name, String id, String text) {
		return wrap("td",name,id,text);
	}
	
	public static String tr(String name, String id, String text) {
		return wrap("tr",name,id,text);
	}
	
	public static String wrap(String tag, String name, String id, String text) {
		return
				"\n<" 
				+ tag
				+ " "
				+ "class = \""
				+ name
				+ "\" "
				+ "id = \""
				+ id
				+ "\">"
				+ text
				+ "\n</"
				+ tag
				+ ">";
	}
	public static String wrappedAnchor(String tag, String name, String href, String text) {
		return
				"\n<" 
				+ tag
				+ " "
				+ "class = \""
				+ name
				+ "\">"
				+ "\n<a href=\""
				+ href
				+ "\">"
				+ text
				+"\n</a>"
				+ "\n</"
				+ tag
				+ ">"
				;
		
	}
	public static String getIndexPage(String heading, String message, String css) {
		StringBuffer sb = new StringBuffer();
		sb.append(HTMLopen);
		sb.append(getHead(heading,css));
		sb.append(BODYopen);
		sb.append("<h1>"+heading+"</h1>");
		sb.append("<p>"+message+"</p>");
		sb.append(BODYclose);
		sb.append(HTMLclose);
		return sb.toString();
	}
	
	public static String getTableOpen(
			int border, 
			int cellspacing, 
			int cellpadding, 
			String theClass) {
		return TABLEopenParm 
				+ " border=" + q(border)
				+ " cellspacing=" + q(cellspacing)
				+ " cellpadding=" + q(cellpadding)
				+ " class=" + Q + theClass
				+ TABLEclose
				;
	}
	
	public static String getRowOpen(String theClass) {
		return ROWopenParm + "class=" + q(theClass) + ">";
	}
	
	public static String getColOpen(String theClass) {
		return COLopenParm + " class=" + q(theClass) + ">";
	}
	
	public static String getCol(String theClass, String text) {
		return getColOpen(theClass) + text.trim() + COLclose;
	}
	
	public static String q(int v) {
		return Q + v + Q;
	}
	public static String q(String v) {
		return Q + v + Q;
	}
	
	public static void main(String[] args) {
	}	

}
