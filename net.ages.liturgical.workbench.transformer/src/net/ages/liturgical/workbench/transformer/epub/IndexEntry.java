package net.ages.liturgical.workbench.transformer.epub;

public class IndexEntry {
	String key;
	String value;
		
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public IndexEntry(String text, String id) {
		this.key = text;
		this.value = 	row("indexEntry", id, text);
	}
	
	private String tdLeft(String className, String href, String text) {
		return td("indexCell", className, href,text);
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
	
	private String row(String cClassName, String href, String text) {
		return "<tr>"
				+ tdLeft(cClassName,href,text)
				+ "</tr>";
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
