package net.ages.liturgical.workbench.transformer.epub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FirstWordsIndexer {
	private Map<String,List<IndexEntry>> theMap = new TreeMap<String,List<IndexEntry>>();
	private boolean excludeBetweenParentheses = true;
	private int lettersPerRow;
	private String idPrefix = "letterIndex";
	
	public FirstWordsIndexer(
			boolean excludeBetweenParentheses
			, int lettersPerRow
			) {
		this.excludeBetweenParentheses = excludeBetweenParentheses;
		this.lettersPerRow = lettersPerRow;
	}
	
	public void add(String text, String id) {
		boolean add = true;
		if (excludeBetweenParentheses) {
			if (text.trim().startsWith("(")) {
				add = false;
			}
		}
		if (add) {
			
			String key = firstChar(text);
					
			if (! theMap.containsKey(key)) {
				theMap.put(key, new ArrayList<IndexEntry>());
			}
			List<IndexEntry> value = theMap.get(key);
			value.add(
					new IndexEntry(
					  text
					  , id
			        )
			);
			theMap.put(key, value);
		}
	}
	
	private String firstChar(String text) {
		String result = GeneralUtils.normalize(text).substring(0, 1);
		return result.toUpperCase();
	}
	
	public String indexAsHtmlTable() {
		StringBuffer sb = new StringBuffer();
		sb.append(letterTable());
		sb.append("<table>");
		Iterator<String> theLetters = theMap.keySet().iterator();
		int letterCount = 1;
		while (theLetters.hasNext()) {
			String key = theLetters.next();
			sb.append(HtmlUtils.tr("indexLetterRow", idPrefix+letterCount, key));
			letterCount++;
			List<IndexEntry> theIndex = theMap.get(key);
			java.util.Collections.sort(theIndex, new IndexEntryComparator());
			Iterator<IndexEntry> it = theIndex.iterator();
			while (it.hasNext()) {
				sb.append(it.next().getValue());
			}
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	/**
	 * Create an HTML table whose cells are the 
	 * letter hyperlinks for the index
	 * @return html table
	 */
	private String letterTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("<table>");
		Iterator<String> it = theMap.keySet().iterator();
		int cellCount = 0;
		int letterCount = 1;
		StringBuffer cellSb = new StringBuffer();
		while (it.hasNext()) {
			String key = it.next();
			cellSb.append(HtmlUtils.wrappedAnchor("td", "indexLetterAnchor", "#"+idPrefix+letterCount, key));
			cellCount++;
			letterCount++;
			if (cellCount == lettersPerRow) {
				sb.append(HtmlUtils.tr("","",cellSb.toString()));
				cellSb = new StringBuffer();
				cellCount = 0;
			}
		}
		if (cellCount > 0) {
			sb.append(HtmlUtils.tr("","",cellSb.toString()));
		}
		sb.append("</table>");
		return sb.toString();
	}
	
}
