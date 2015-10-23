package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;


/**
 * Loads Matins Ordinary for whatever versions are available and stores the html
 * in a map that can be retrieved based on the versions.
 * @author mac002
 *
 */
public class MatinsOrdinary {
	Map<String,String> versions = new TreeMap<String,String>();
	String path;
	
	/**
	 * 
	 * @param path to the folder that contains the various language versions of Matins Ordinary
	 * @param classesToExclude match elements will be removed
	 */
	public MatinsOrdinary(
			String path
			, List<String> classesToExclude
			) {
		this.path = path;
		loadMap(classesToExclude);
	}
	
	/**
	 * Return the Matins Ordinary HTML content for the specified language key
	 * @param langKey
	 * @return html
	 */
	public String getContent(String langKey) {
		return versions.get(langKey);
	}
		
	/**
	 * Add a key-value pair for each version found, e.g. en, gr, gr-en, etc.
	 */
	private void loadMap(List<String> classesToExclude) {
		List<File> theFiles = AlwbFileUtils.getFilesInDirectory(path, "html");
		Iterator<File> it = theFiles.iterator();
		while (it.hasNext()) {
			File f = it.next();
			HtmlTransformer x = new HtmlTransformer(f.getPath());
			String html = x.transform("Matins Ordinary", null, null, classesToExclude);
			String version = x.getLangs().trim().toLowerCase();
			Document doc = Jsoup.parse(html);
			String content = doc.getElementsByTag("tbody").html();
			versions.put(version, content);
		}
	}
}
