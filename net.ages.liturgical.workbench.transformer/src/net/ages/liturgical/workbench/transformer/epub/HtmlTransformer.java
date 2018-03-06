package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.HtmlUtils;

import org.codehaus.plexus.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlTransformer {
	private String css = "ages.css";
	private String path = "";
	private String titleText = "";
	private String titleCommemoration = "";
	private String langs = "";
	private String leftLang = "";
	private String rightLang = "";
	private boolean isService = false;
	private boolean isBook = false;
	private String serviceDateForTitle = null;
	private String serviceDateForToc = null;
	private String heading = "";

	public HtmlTransformer(String path) {
		this.path = path;
	}
	
	/**
	 * Transforms HTML for the ALWB format to one usable by ePub
	 * @param title The title to show in the heading of the html
	 * @param dateFormatPattern format to use for service dates.  Null if not a service.
	 * @param excludeClasses List of classes who elements should be removed
	 * @return the transformed HTML.
	 */
	public String transform(
			String title
			, SimpleDateFormat dateFormatTitle
			, SimpleDateFormat dateFormatToc
			, List<String> excludeClasses
			) {
		StringBuffer sb = new StringBuffer();
		Document doc = null;
		String displayTitle = null;
		
		try {
			doc = getDoc(new File(path));

			// Save off header attributes before we strip it away
			titleText = doc.title();
			isService = titleText.startsWith("se");
			isBook = titleText.startsWith("bk");
			if (isBook) {
				titleText = doc.getElementsByTag("td").first().text();
				if (titleText == null) {
					titleText = doc.title();
				} else {
					titleText = StringUtils.capitaliseAllWords(titleText.toLowerCase());
				}
			}
			if (title == null) {
				displayTitle = titleText;
			} else {
				displayTitle = title;
			}
			titleCommemoration = doc.getElementsByTag("title").first().attr("data-commemoration").toLowerCase();
			langs = doc.getElementsByTag("title").first().attr("data-language");
			try {
				String [] parts = langs.split("-");
				if (parts.length > 0) {
					leftLang = parts[0].trim();
					rightLang = parts[1].trim();
				}
			} catch (Exception e) {
				leftLang = langs;
				rightLang = "";
			}
			if (displayTitle.contains(langs)) {
				displayTitle = displayTitle.replace(langs, "");
				if (displayTitle.contains("()")) {
					displayTitle = displayTitle.replace("()","");
				}
			}
			heading = 
					"<h1>" 
					+ displayTitle 
					+ "</h1>"
					+ "<p class=\"lang\">("
					+ langs
					+ ")</p>";

			if (titleText != null && titleText.startsWith("se")) {
				serviceDateForTitle = dateFormatTitle.format(GeneralUtils.serviceDateFromPath(path));
				serviceDateForToc = dateFormatToc.format(GeneralUtils.serviceDateFromPath(path));
				heading = heading + 
						"<p class=\"date\">" 
						+ serviceDateForTitle
						+ "</p>";			
			}
			
			// strip out classes to be excluded
			for (String theClass : excludeClasses) {
				Elements theElements = doc.getElementsByClass(theClass);
				for (Element element : theElements) {
					element.remove();
				}
			}
			
			// Grab the cleaned up content section of the doc
			Elements content = doc.getElementsByClass("content");
			
			sb.append(HtmlUtils.HTMLopen);
			sb.append(HtmlUtils.getHead(titleText, css));
			sb.append(HtmlUtils.BODYopen);
			sb.append(heading);
			sb.append(content);
			sb.append(HtmlUtils.BODYclose);
		    sb.append(HtmlUtils.HTMLclose);


		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private Document getDoc(File file) {
		Document result = null;
		try {
			result = Jsoup.parse(file, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getTitleCommemoration() {
		return titleCommemoration;
	}

	public void setTitleCommemoration(String titleCommemoration) {
		this.titleCommemoration = titleCommemoration;
	}

	public String getLangs() {
		return langs;
	}

	public void setLangs(String lang) {
		this.langs = lang;
	}

	private String serviceDateHeading(String title) {
		String result = "";
		try {
			if (title.startsWith("se")) { 
				// assume we have a service and the title is
				// formatted like this: se.m09.d01.ve 
				String [] theParts = title.split(".");
				if (theParts.length > 3) {
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean isService() {
		return isService;
	}

	public void setService(boolean isService) {
		this.isService = isService;
	}

	public String getServiceDateForTitle() {
		return serviceDateForTitle;
	}

	public void setServiceDateForTitle(String serviceDate) {
		this.serviceDateForTitle = serviceDate;
	}

	public String getServiceDateForToc() {
		return serviceDateForToc;
	}

	public void setServiceDateForToc(String serviceDate) {
		this.serviceDateForToc = serviceDate;
	}
	
	/**
	 * 
	 * @param file e.g. h/s/2015/10/28/li/gr-en/index.html
	 * @return 20151028ligr
	 */
	private String toServiceId(String file) {
		String result = file.substring(4,file.indexOf("/index.html")).replaceAll("/", "");
		return result;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getLeftLang() {
		return leftLang;
	}

	public void setLeftLang(String leftLang) {
		this.leftLang = leftLang;
	}

	public String getRightLang() {
		return rightLang;
	}

	public void setRightLang(String rightLang) {
		this.rightLang = rightLang;
	}

}
