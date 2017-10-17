package net.ages.liturgical.workbench.transformer.alwb.html.models;

public class CellElement{
	int row;
	String tag;
	String className;
	String text;
	String key;
	boolean tagIsForOuter; // Para sid x.y sid x.b.  For x.b, tagIsForOuter.
	
	public CellElement(
			int row
			, String tag
			, String className
			, String text
			, String key
			, boolean tagIsForOuter
			) {
		super();
		this.tag = tag;
		this.className = className;
		this.row = row;
		this.text = text;
		this.key = key;
		this.tagIsForOuter = tagIsForOuter;
	}
	
	public boolean tagIsForOuter() {
		return tagIsForOuter;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	

}
