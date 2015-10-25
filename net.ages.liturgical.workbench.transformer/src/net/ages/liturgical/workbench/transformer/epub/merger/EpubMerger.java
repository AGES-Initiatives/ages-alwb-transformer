package net.ages.liturgical.workbench.transformer.epub.merger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import net.ages.liturgical.workbench.transformer.epub.Attributes;
import net.ages.liturgical.workbench.transformer.epub.Constants;
import net.ages.liturgical.workbench.transformer.epub.EpubUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

public class EpubMerger {
	private List<File> files = null;
	private List<String> exclusions = null;
	private String title = null;
	private String author = null;
	private String pathOut = null;
	private String fileOut = null;
	private Book mergedBook = null;
	private String mergeType = null;
	private boolean daySpecific = false;
	
	public EpubMerger (
			List<File> files
			, String title
			, String author
			, String path
			, String filename
			, List<String> exclusions
			, String mergeType
			) {
		this.files = files;
		this.title = title;
		this.author = author;
		this.pathOut = path;
		this.fileOut = filename;
		this.exclusions = exclusions;
		this.mergeType = mergeType;
		
		mergedBook = EpubUtils.initializeEpubBook(
				this.title
				, "" // language
				, this.author
				, ""  // date
				, Constants.PATH_TO_RESOURCES
		);
		Attributes mergedAttribs = new Attributes();
		mergedAttribs.setType(mergeType);
		mergedBook.getMetadata().addDescription(mergedAttribs.toJsonString());
		daySpecific = mergeType.equals(Attributes.VALUE_TYPE_DAY) 
				|| mergeType.equals(Attributes.VALUE_TYPE_SERVICE);
		merge();
	}
	
	public void merge() {
		EpubReader reader = new EpubReader();

		for (File f: files) {
			System.out.println(f.getName());
			try {
				Book book = reader.readEpub(new FileInputStream(f.getPath()));
				Attributes attribs = null;
				try {
					attribs = new Attributes(book.getMetadata().getDescriptions().get(0));
				} catch (Exception e) {
					attribs = null;
				}
				String tocTitle = null;
				if (attribs == null) {
					tocTitle = title;
				} else {
					if (daySpecific) { // no need to show the date in the toc
						tocTitle = attribs.getTocTitle();
					} else { // need to show the date in the toc
						tocTitle = attribs.getTocDate() + " " + attribs.getTocTitle();
					}
				}
				TableOfContents toc = book.getTableOfContents();
				for (TOCReference tocr : toc.getTocReferences()) {
					if (include(tocr.getCompleteHref())) {
						mergedBook.addSection(
								tocTitle
								+ ", "
								+ tocr.getTitle()
								, tocr.getResource()
						);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Write the ePub
		EpubUtils.writeEpubFile(
				mergedBook
				, pathOut
				, fileOut + ".epub");
		
		System.out.println("Merged ePub is called " + fileOut + ".epub");
		System.out.println("It is in " + pathOut);
	}
	
	/**
	 * Compare the href to the exclusions.
	 * @param href
	 * @return true if no match found among the exclusions
	 */
	private boolean include(String href) {
		for (String e : exclusions) {
			if (href.startsWith(e)) {
				return false;
			}
		}
		return true;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPathOut() {
		return pathOut;
	}

	public void setPathOut(String pathOut) {
		this.pathOut = pathOut;
	}
	/**
	 * For debugging, can be used to view contents of the ePub file
	 * @param b
	 */
	private void showContents(Book b) {
		Iterator<Resource> it = b.getResources().getAll().iterator();
		while (it.hasNext()) {
			Resource r = it.next();
			System.out.println(r.getId() + ": " + r.getHref());
		}
	}
}
