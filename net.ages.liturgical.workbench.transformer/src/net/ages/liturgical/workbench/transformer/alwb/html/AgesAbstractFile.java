package net.ages.liturgical.workbench.transformer.alwb.html;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AgesAbstractFile {
	private String filename = null;  // without extension
	private Map<Integer,List<AgesElement>> leftContents = new TreeMap<Integer,List<AgesElement>>();
	private Map<Integer,List<AgesElement>> rightContents = new TreeMap<Integer,List<AgesElement>>();
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Map<Integer, List<AgesElement>> getLeftContents() {
		return leftContents;
	}
	public void setLeftContents(Map<Integer, List<AgesElement>> leftContents) {
		this.leftContents = leftContents;
	}
	public Map<Integer, List<AgesElement>> getRightContents() {
		return rightContents;
	}
	public void setRightContents(Map<Integer, List<AgesElement>> rightContents) {
		this.rightContents = rightContents;
	}

}