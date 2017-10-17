package net.ages.liturgical.workbench.transformer.alwb.html;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.ages.liturgical.workbench.transformer.alwb.html.models.Atem;
import net.ages.liturgical.workbench.transformer.alwb.html.models.Cell;
import net.ages.liturgical.workbench.transformer.alwb.html.models.CellElement;
import net.ages.liturgical.workbench.transformer.alwb.html.models.ClassToAtem;
import net.ages.liturgical.workbench.transformer.alwb.html.models.Row;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;


public class AlwbHtmlToLatex {

	
	/**
	 * 1. Read in html files
	 * 2. Extract info from them
	 * 3. Read in text files
	 * 4. Extract info from them
	 * 5. Write out tex files for atem and tex files for ares
	 * @param args
	 */
	public static void main(String[] args) {
		String base = "/Users/mac002/Git/ages-alwb-transformer/net.ages.liturgical.workbench.transformer/data";
		String in = base + "/in/priestsbook";
		String out = base + "/out/priestsbook";

		// read the files in
		AgesAbstractLibrary library = new AgesAbstractLibrary(in,out);
		library.loadAlwbHtml(InputLanguage.BOTH);

		// write them out to the new format
		library.exportAsLatex(InputLanguage.BOTH);
		
	}

}
