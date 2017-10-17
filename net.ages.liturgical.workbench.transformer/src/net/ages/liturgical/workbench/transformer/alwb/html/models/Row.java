package net.ages.liturgical.workbench.transformer.alwb.html.models;

public class Row {
	int row;
	Cell left;
	Cell right;
	
	public Row(int row, Cell left, Cell right) {
		super();
		this.row = row;
		this.left = left;
		this.right = right;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public Cell getLeft() {
		return left;
	}

	public void setLeft(Cell left) {
		this.left = left;
	}

	public Cell getRight() {
		return right;
	}

	public void setRight(Cell right) {
		this.right = right;
	}
}
