package net.ages.liturgical.workbench.transformer.epub;

import org.apache.commons.io.IOUtils;

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
	private String tocTitle;
	
	private Resource main = null; // resource representing the actual html file with its text
	private Resource toc = null; // its table of contents
	private Resource leftIndex = null; // its index
	private Resource rightIndex = null; // index for the language in right cell, if exists
	
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
	public Resource getLeftIndex() {
		return leftIndex;
	}
	public void setLeftIndex(Resource index) {
		this.leftIndex = index;
	}
	public String getMainHtml() {
		try {
			return IOUtils.toString(main.getInputStream(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
	public String getTocHtml() {
		try {
			return IOUtils.toString(toc.getInputStream(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
	public String getIndexHtml() {
		try {
			return IOUtils.toString(leftIndex.getInputStream(), "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
	public Resource getRightIndex() {
		return rightIndex;
	}
	public void setRightIndex(Resource rightIndex) {
		this.rightIndex = rightIndex;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTocTitle() {
		return tocTitle;
	}
	public void setTocTitle(String tocTitle) {
		this.tocTitle = tocTitle;
	}
}
