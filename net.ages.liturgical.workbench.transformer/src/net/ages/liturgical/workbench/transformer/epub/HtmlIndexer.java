package net.ages.liturgical.workbench.transformer.epub;

import java.nio.charset.Charset;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.ages.liturgical.workbench.transformer.epub.FirstWordsIndexer;
import net.ages.liturgical.workbench.transformer.epub.TocManager;
import net.ages.liturgical.workbench.transformer.epub.ResourceBundle;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.HtmlUtils;
import nl.siegmann.epublib.domain.Resource;

/**
 * Creates an index of an html file using the first n words of
 * specified class names
 * @author mac002
 *
 */
public class HtmlIndexer {

	/**
	 * 
	 * @param html - the HTML to be indexed
	 * @param langs - the code(s) of the language(s).  If two, must have a hyphen separator
	 * @param title
	 * @param heading
	 * @param htmlFileName - name to use for the html file
	 * @param css - name of the css to link in the head section of the html
	 * @param classesToIndex - comma delimited list of classes to be indexed
	 * @param classesForTocFullText - comma delimited list of classes to be used for Table of Contents
	 * @param excludeBetweenParentheses
	 * @param wordsPerLine - number of words to include in the index
	 * @param lettersPerRow - for the alphabetic index to the index sections, number of letters per row
	 * @return a ResourceBundle with the html and index(es) as resources
	 */
	public ResourceBundle buildIndex(
			String html
			, String langs
			, String title
			, String tocTitle
			, String tocDate
			, String heading
			, String htmlFileName
			, String css
			, List<String> classesToIndex
			, List<String> classesForTocFullText
			, List<String> classesForTocFirstWords
			, boolean excludeBetweenParentheses
			, int wordsPerLine
			, int lettersPerRow
			) {
		ResourceBundle result = new ResourceBundle();
		result.setTitle(title);
		result.setTocTitle(tocTitle);
		result.setTocDate(tocDate);
		String indexOfFirstLine = "";
		// Set up indexers for the left and right languages.
		// If it turns out to be a monolingual text, we will only use the left index.
		FirstWordsIndexer theLeftIndex = new FirstWordsIndexer(
				excludeBetweenParentheses
				, lettersPerRow);
		FirstWordsIndexer theRightIndex = null; // to be initialized if bilingual text
		
		// Table of Contents Manager
		TocManager tocManager = new TocManager(excludeBetweenParentheses);

		try {
			Document doc = Jsoup.parse(html);
			String serviceId = htmlFileName;
			Elements rows = doc.getElementsByTag("tr");
			String rightTdClassName = null;
			String rightLang = null;
			String leftLang = langs;
			
			try {
				if (rows.size() > 0) {
					rows.get(0).attr("id",serviceId);
					// get the <td> class names so we can index them separately
					Elements cells = rows.get(0).children();
					if (cells.size() == 2) { // assume this is bilingual text
						rightTdClassName = cells.get(1).className();
						theRightIndex = new FirstWordsIndexer(
								excludeBetweenParentheses
								, lettersPerRow);
						try {
							String [] parts = langs.split("-");
							if (parts.length > 0) {
								leftLang = parts[0];
								rightLang = parts[1];
							}
						} catch (Exception e) {
							// ignore
						}
					}
					indexOfFirstLine = HtmlUtils.anchor(
							"indexEntry"
							, htmlFileName + "#" + rows.get(0).id()
							,"Go directly to the start of the service...");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			int cnt = rows.size();
			
			for (int i=0; i < cnt; i++) {

				Element row = rows.get(i);
				String rowId = serviceId + i;
				row.attr("id", rowId);
				String href = htmlFileName + "#" + rowId;
				
				// add to the table of contents if class matches
				for (String className : classesForTocFullText) {
					Elements elements = row.getElementsByClass(className);
					if (elements.size() > 0) {
						switch(elements.size()) {
							case 1: {
								tocManager.add(
										"toc"+ className
										, href
										, elements.get(0).text()
								);
								break;
							}
							case 2: {
								tocManager.add(
										"toc"+ className
										, href
										, elements.get(0).text()
										, elements.get(1).text()
								);
								break;
							}
							case 3: {
								// ignore - shouldn't happen
								break;
							}
							case 4:
								tocManager.add(
										"toc"+ className
										, href
										, elements.get(0).text() + " " + elements.get(1).text()
										, elements.get(2).text() + " " + elements.get(3).text()
								);
								break;
							}
					}
				}

				// Add first words to TOC if class matches
				for (String className : classesForTocFirstWords) {
					Elements elements = row.getElementsByClass(className);
					if (elements.size() > 0) {
						switch(elements.size()) {
						case 1: {
							tocManager.add(
									"toc"+ className
									, href
									, GeneralUtils.firstNWords(elements.get(0).text(), wordsPerLine)
							);
							break;
						}
						case 2: {
							tocManager.add(
									"toc"+ className
									, href
									, GeneralUtils.firstNWords(elements.get(0).text(), wordsPerLine)
									, GeneralUtils.firstNWords(elements.get(1).text(), wordsPerLine)
							);
							break;
						}
						case 3: {
							// ignore - shouldn't happen
							break;
						}
						case 4:
							tocManager.add(
									"toc"+ className
									, href
									, GeneralUtils.firstNWords(elements.get(0).text(), wordsPerLine) 
											+ " " 
											+ GeneralUtils.firstNWords(elements.get(1).text(), wordsPerLine)
									, GeneralUtils.firstNWords(elements.get(2).text(), wordsPerLine) 
											+ " " 
											+ GeneralUtils.firstNWords(elements.get(3).text(), wordsPerLine)
							);
							break;
						}
					}
				}

				// index the row if class matches
				for (String className : classesToIndex) {
					Elements elements = row.getElementsByClass(className);
					if (elements.size() > 0) {
						for (Element e : elements) {
							boolean rightSide = e.parent().className().equals(rightTdClassName);
							if (rightSide) { // this is right side of a bilingual text
								theRightIndex.add(
										GeneralUtils.firstNWords(e.text(), wordsPerLine)
										,href
								);
							} else { // add the left side.  For a monolingual text, there is only a left side...
								theLeftIndex.add(
										GeneralUtils.firstNWords(e.text(), wordsPerLine)
										,href
								);
							}
						}
					}
				}
			}
			
			// Set up HTML for the main contents file
			 result.setMain(
			 	new Resource(
					doc.html().getBytes(Charset.forName("UTF-8"))
				, htmlFileName)
			);
			 

			// Set up HTML for the TOC
			result.setToc(
					getResource(
							title
							, css
							, heading
							, "Outline"
							, indexOfFirstLine
							, tocManager.tocAsHtmlTable()
							, "toc-" + htmlFileName
					)
			);				

			// Set up HTML for the left index
			result.setLeftIndex(
					getResource(
							title
							, css
							, heading
							, "Index" + " (" + leftLang + ")"
							, indexOfFirstLine
							, theLeftIndex.indexAsHtmlTable()
							, "index-" + leftLang + htmlFileName
					)
				);

			// Set up HTML for the right index
			if (theRightIndex != null) {
				result.setRightIndex(
						getResource(
								title
								, css
								, heading
								, "Index" + " (" + rightLang + ")"
								, indexOfFirstLine
								, theRightIndex.indexAsHtmlTable()
								, "index-" + rightLang + htmlFileName
						)
				);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}




	private Resource getResource(
			String title
			, String css
			, String heading
			, String subHeading
			, String indexOfFirstLine
			, String contents
			, String filename) {
		Resource result = null;
		StringBuffer sb = new StringBuffer();
		sb.append(HtmlUtils.HTMLopen);
		sb.append(HtmlUtils.getHead(title, css));
		sb.append(HtmlUtils.BODYopen);
		sb.append(heading);
		if (subHeading != null) {
			sb.append("<h3>"+subHeading+"</h3>");
		}
		if (indexOfFirstLine != null) {
			sb.append("<p class=\"entryIndex\">" + indexOfFirstLine + "</p>");
		}
		sb.append(contents);
		sb.append(HtmlUtils.BODYclose);
		sb.append(HtmlUtils.HTMLclose);
		
		result = 	new Resource(
				sb.toString().getBytes(Charset.forName("UTF-8"))
				, filename);
		
		return result;
	}
	
}
