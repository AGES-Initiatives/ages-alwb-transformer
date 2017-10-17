package net.ages.liturgical.workbench.transformer.alwb.html.models;

import java.util.ArrayList;
import java.util.List;

public class Cell {
	private List<CellElement> elements = new ArrayList<CellElement>();
	
	public List<CellElement> getList() {
		return elements;
	}
	public void setList(List<CellElement> elements) {
		this.elements = elements;
	}
}
