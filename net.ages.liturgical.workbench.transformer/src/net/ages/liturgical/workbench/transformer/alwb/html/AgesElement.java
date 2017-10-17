package net.ages.liturgical.workbench.transformer.alwb.html;

import java.util.List;

public class AgesElement {
	private int rowNbr = 0;
	private String tagname = null;
	private String classname = null;
	private boolean isBlock = true;
	private boolean contentIsKvp = false;
	private Object content = null; // if not a Kvp, it is an AgesElement
	private HtoLClassMapper map = new HtoLClassMapper();
	
	public AgesElement() {

	}

	public String getTagname() {
		return tagname;
	}
	public void setTagname(String tagname) {
		this.tagname = tagname;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public boolean isBlock() {
		return isBlock;
	}
	public void setIsBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}
	public boolean isContentIsKvp() {
		return contentIsKvp;
	}
	public void setContentIsKvp(boolean contentIsKvp) {
		this.contentIsKvp = contentIsKvp;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	
	public String toString() {
		return tagname + " : " + classname + " : " + isBlock;
	}

	public int getRowNbr() {
		return rowNbr;
	}

	public void setRowNbr(int rowNbr) {
		this.rowNbr = rowNbr;
	}
	
	public String toLatexAtem() {
		StringBuffer result = new StringBuffer();
		result.append(map.get(this.classname) +"{");
		if (this.contentIsKvp) {
			AgesKVP content = (AgesKVP) this.content;
			result.append(content.toLatexAtem());
		} else {
			List<AgesElement> content = (List<AgesElement>) this.content;
			for (AgesElement ages : content) {
				result.append(ages.toLatexAtem());
			}
		}
		result.append("}");
		return result.toString();
	}
	public String toLatexAres() {
		StringBuffer result = new StringBuffer();
		if (this.contentIsKvp) {
			AgesKVP content = (AgesKVP) this.content;
			result.append(content.toLatexAres());
		} else {
			List<AgesElement> content = (List<AgesElement>) this.content;
			for (AgesElement ages : content) {
				result.append(ages.toLatexAres());
			}
		}
		return result.toString();
	}
}
