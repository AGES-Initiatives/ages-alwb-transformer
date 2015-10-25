package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.HtmlUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import org.apache.tools.ant.util.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class IndexUtils {
	String rootFolder;
	String pathToServicesIndex;
	String pathToEpubFolder;
	String pathToEpubIndex;
	String mainIndexEpubInfoHtml;
	
	public IndexUtils(
			String pathToServicesIndex
			, String mainIndexEpubInfoHtml) {
		this.pathToServicesIndex = pathToServicesIndex;
		rootFolder = new File(pathToServicesIndex).getParent();
		pathToEpubFolder = rootFolder + "/"+Constants.EPUB_MERGED_DIR;
		pathToEpubIndex = pathToEpubFolder + "index.html";
		this.mainIndexEpubInfoHtml = mainIndexEpubInfoHtml;
	}
	
	public void updateServicesIndex(String div) {
		Document doc;
		doc = HtmlUtils.getDoc(new File(this.pathToServicesIndex));
		Elements elements = doc.select("div.epub_link");
		if (elements.size() > 0) {
			elements.get(0).html(div);
		} else {
			elements = doc.select("h1.index-title");
			if (elements.size() > 0) {
				elements.get(0).append(div);
			}
		}
		AlwbFileUtils.writeFile(this.pathToServicesIndex, doc.html());
	}
	
	public void removeMainIndexEpubInfo() {
		Document doc;
		doc = HtmlUtils.getDoc(new File(this.pathToServicesIndex));
		Elements elements = doc.select("div.epub_link");
		if (elements.size() > 0) {
			elements.get(0).remove();
		}
		AlwbFileUtils.writeFile(this.pathToServicesIndex, doc.html());
	}
	
	public void removeEpubIndex() {
		 removeMainIndexEpubInfo();
		 try {
			 FileUtils.delete(new File(this.pathToEpubIndex));
		 } catch (Exception e) {
			 // ignore
		 }
	}
	
	public void createEpubIndex(
			String title
			, String heading
			) {
		
		List<String> months = new ArrayList<String>();
		List<String> days = new ArrayList<String>();
		List<String> adhoc = new ArrayList<String>();
		List<String> unknown = new ArrayList<String>();
		
		Document doc;
		// Use the servicesindex.html file as a template to write the ePub e/index.html file
		doc = HtmlUtils.getDoc(new File(this.pathToServicesIndex));
		doc.title(title);
		doc.head().prepend("	" + Constants.EPUB_INDEX_BASE_REF);
		Element contentElem = doc.select("div.index-content").get(0);
		
		EpubReader reader = new EpubReader();
		
		StringBuffer sb = new StringBuffer();
		sb.append("<h1 class=\"index-title\">" + heading + "</h1>");
		
		List<File> files = AlwbFileUtils.getFilesInDirectory(pathToEpubFolder, "epub");
		Iterator<File> it = files.iterator();

		while (it.hasNext()) {
			File f = it.next();

			System.out.println(f.getName());
			try {
				Book book = reader.readEpub(new FileInputStream(f.getPath()));
				Attributes attribs = null;
				String type = null;
				try {
					attribs = new Attributes(book.getMetadata().getDescriptions().get(0));
					type = attribs.getType();
				} catch (Exception e) {
					type = Attributes.VALUE_TYPE_UNKNOWN;
				}
				String div = "<div class=\"index-day\"><a href=\"" + subpath(f.getPath()) + "\">" + book.getTitle() + "</a></div>";
				if (type.equals(Attributes.VALUE_TYPE_DAY)) {
					days.add(div);
				} else if (type.equals(Attributes.VALUE_TYPE_MONTH)) {
					months.add(div);
				} else if (type.equals(Attributes.VALUE_TYPE_AD_HOC)) {
					adhoc.add(div);
				} else {
					unknown.add(div);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (months.size() > 0) {
			sb.append("<p>Services for Entire Month<p>");
			for (String div : months) {
				sb.append(div);
			}
		}
		
		if (days.size() > 0) {
			sb.append("<p>All the Services for a Specific Day</p>");
			for (String div : days) {
				sb.append(div);
			}
		}
		
		if (adhoc.size() > 0) {
			sb.append("<p>Miscellaneous ePubs</p>");
			for (String div : adhoc) {
				sb.append(div);
			}
		}

		if (unknown.size() > 0) {
			for (String div : unknown) {
				sb.append(div);
			}
		}

		// replace the content with the links to the epub files
		contentElem.html(sb.toString());
		// write the e/index.html file
		AlwbFileUtils.writeFile(pathToEpubIndex, doc.html());
		updateServicesIndex(this.mainIndexEpubInfoHtml);
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
	
	/**
	 * Takes a fully qualified path and strips off the path
	 * up to the start of the ePub directory.
	 * @param path
	 * @return
	 */
	private String subpath(String path) {
		String result = path;
		try {
			int i = path.indexOf("/" + Constants.EPUB_DIR);
			result = path.substring(i+1,path.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static InputStream getResource(String path) {
		return IndexUtils.class.getResourceAsStream(path);
	}
}
