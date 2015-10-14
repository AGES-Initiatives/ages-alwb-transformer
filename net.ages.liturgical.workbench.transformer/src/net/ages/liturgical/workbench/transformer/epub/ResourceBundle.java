package net.ages.liturgical.workbench.transformer.epub;

import nl.siegmann.epublib.domain.Resource;

/**
 * Contains a resource for an html file,
 * a resource for its table of contents
 * and a resource for its index
 * 
 * @author mac002
 *
 */
public class ResourceBundle {
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	private Resource main; // resource representing the actual html file with its text
	private Resource toc; // its table of contents
	private Resource index; // its index

	public Resource getMain() {
		return main;
	}
	public void setMain(Resource main) {
		this.main = main;
	}
	public Resource getToc() {
		return toc;
	}
	public void setToc(Resource toc) {
		this.toc = toc;
	}
	public Resource getIndex() {
		return index;
	}
	public void setIndex(Resource index) {
		this.index = index;
	}
}
