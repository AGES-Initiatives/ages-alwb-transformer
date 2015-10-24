package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.ages.liturgical.workbench.transformer.epub.ResourceBundle;
import net.ages.liturgical.workbench.transformer.services.index.reader.MonthMap;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

public class EpubBuilder {
	private String fileIn;
	private String replace;
	private String with;
	
	private String pathToRoot;
	private String pathToServicesIndex;
	private int nGram = 5;
	private boolean generateToc = false;
	private boolean generateFirstWordsIndex = false;
	private boolean generateFirstWordsIndexExcludeBetweenParentheses = true;
	private boolean includeMatinsOrdinary = false;
	private boolean saveHtml = false;
	private String pathToMatinsOrdinary = null;
	private int lettersPerRow = 5;
	private String indexDisplayName = null;
	private String textDisplayName = null;
	private String tocDisplayName = null;
	private List<String> classesToExclude = null;
	private List<String> classesToIndex = null;
	private List<String> classesForTocFullText = null;
	private List<String> classesForTocFirstWords = null;
	
	private String author;
	private String css = "ages.css";
	
	MonthMap monthMap = new MonthMap();
	
	boolean updateIndex = false;
	boolean separateMonthFiles = false;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat titleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat tocDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private PropertyUtils props;
	
	private String additionalContent = null;
	
	/**
	 * 
	 * @param fileIn - full path to the HTML file to be converted to ePub
	 * @param replace - path substring to replace, e.g. /h/
	 * @param with - what to replace it with, e.g. /e/
	 */
	public EpubBuilder(PropertyUtils props
			, String fileIn
			, String replace
			, String with
			) {
		this.fileIn = fileIn;
		this.replace = replace;
		this.with = with;
		this.props = props;
		loadProps();
	}
	
	/**
	 * Creates an ePub from the html
	 * @param title - Title of the ePub
	 */
	public void htmlToEpub(String title) {
		
		System.out.println("Making ePub from " + fileIn);
		

		// Convert the HTML file to the format required for ePub
		HtmlTransformer hX = new HtmlTransformer(fileIn);
		String html = hX.transform(
				title
				, titleDateFormat
				, tocDateFormat
				, classesToExclude
				);
		
		
		if (includeMatinsOrdinary) {
			if (additionalContent != null) {
				Document doc = Jsoup.parse(html);
				doc.select("tbody").first().children().first().before(additionalContent);
				html = doc.html();
			}
		}

		String newFileName = newFileName(
				""
				, hX.getTitleText()
				, "." + hX.getLangs().toLowerCase()
				);
		String newPath = newPath(fileIn,replace,with);
		String newOut = newPath + newFileName;

		String date = getToday();
		
		String theTitle = null;
		String theTocTitle = null;
		String theTocDate = null;
		
		if (title == null || title.length() < 1) {
			theTitle = hX.getTitleText();
			theTocTitle = theTitle;
		} else {
			theTitle = title;
			if (hX.isService()) {
				if (hX.getServiceDateForTitle() != null) {
					theTitle = theTitle + " " + hX.getServiceDateForTitle();
				}
				if (hX.getServiceDateForToc() != null) {
					theTocTitle = title;
					theTocDate = hX.getServiceDateForToc();
				}
			}
		}
				
		//	Initialize the ePub book
		Book book = EpubUtils.initializeEpubBook(
				theTitle 
				, hX.getLangs()
				, author
				, date
				, Constants.PATH_TO_RESOURCES
				);
		
		if (generateFirstWordsIndex || generateToc) {
			
			ResourceBundle bundle = new HtmlIndexer().buildIndex(
					html
					, hX.getLangs()
					, theTitle
					, theTocTitle
					, theTocDate
					, hX.getHeading()
					, newFileName + ".html"
					, css
					, classesToIndex
					, classesForTocFullText
					, classesForTocFirstWords
					, generateFirstWordsIndexExcludeBetweenParentheses
					, this.nGram
					, this.lettersPerRow);

			// set title[1] and title[2] for future retrieval
			book.getMetadata().addTitle(bundle.getTocTitle());
			book.getMetadata().addTitle(bundle.getTocDate());
			book.getMetadata().addType(Constants.VALUE_TYPE_SERVICE);

			if (generateToc) {
				book.addSection(tocDisplayName, bundle.getToc());
			}
			
			book.addSection(textDisplayName, bundle.getMain());

			if (generateFirstWordsIndex) {
				if (bundle.getRightIndex() == null) {
					// for monolingual, we don't need to say what the language is for the index
					book.addSection(indexDisplayName, bundle.getLeftIndex());
				} else {
					book.addSection(indexDisplayName + " (" + hX.getLeftLang() + ")", bundle.getLeftIndex());
					book.addSection(indexDisplayName + " (" + hX.getRightLang() + ")", bundle.getRightIndex());
				}
			}
			
			// If requested, for debug purposes write the html that is being used in the ePub
			if (saveHtml) {
				AlwbFileUtils.writeFile(newOut+".toc"+".html", bundle.getTocHtml());
				AlwbFileUtils.writeFile(newOut+".index"+".html", bundle.getIndexHtml());
				AlwbFileUtils.writeFile(newOut+".text"+".html", bundle.getMainHtml());
			}

		} else {
			// Turn the transformed html into a Resource
			Resource r = new Resource(
					html.getBytes(Charset.forName("UTF-8"))
					, newFileName+".html");
			
			// Add the Resource to the ePub book section
			book.addSection(theTitle, r);

			// If requested, for debug purposes write the html that is being used in the ePub
			if (saveHtml) {
				AlwbFileUtils.writeFile(newOut+".html", html);
			}
		}


		// Write the ePub
		EpubUtils.writeEpubFile(
				book
				, newPath
				, newFileName + ".epub");
		
		// create the index.html that will load the ePub from a browser
		AlwbFileUtils.writeFile(newPath+"index.html", 
				loaderHtml(
						hX.getTitleCommemoration()
						, hX.getLangs()
						, newFileName
						, hX.getTitleText()));
	
		
	}
	
	private String newFileName(
			String prefix
			, String title
			, String suffix
			) {
		return prefix 
				+ title.toLowerCase().replaceAll(" ", "") 
				+ suffix 
				;
	}
	
	private String newPath(String path, String from, String to) {
		String result = path;
		try {
			File f  = new File(path);
			result = f.getParent().replace(delimit(from),delimit(to)) + "/";
			f = new File(result);
			f.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
	}

	private String delimit(String s) {
		return "/" + s + "/";
	}
	
	/**
	 * Index.html file that will automatically initiate a
	 * download of the ePub
	 * @param commemoration
	 * @param language
	 * @param fileName
	 * @param title
	 * @return
	 */
	private String loaderHtml(
			String commemoration
			, String language
			, String fileName
			, String title
			) {
		StringBuffer sb = new StringBuffer();
		String ePubFilename = fileName + ".epub";
		
		sb.append("\n<!DOCTYPE html>");
		sb.append("\n<html>");
		sb.append("\n<head>");
		sb.append("\n<title data-commemoration=\"");
		sb.append(commemoration);
		sb.append("\" data-language=\"");
		sb.append(language);
		sb.append("\" data-type=\"epub\" data-filename=\"");
		sb.append(ePubFilename);
		sb.append("\">");
		sb.append(title);
		sb.append("</title>");
		sb.append("\n<meta charset=\"utf-8\"/> ");
		sb.append("\n<meta name=\"keywords\" content=\"\"/>");
		sb.append("\n<script>");
		sb.append("window.location=\"");
		sb.append(ePubFilename);
		sb.append("\"");
		sb.append("</script>");
		sb.append("</head>");
		sb.append("\n<body>");
		sb.append("\n<p class=\"ePubDownloadNotice\">The ePub file is being downloaded.  Open it using an eReader capable of reading ePub, e.g. on an iPhone or iPad, use eBooks.  On an Android you can use Google Play Book.</p>");
		sb.append("\n</body>");

		return sb.toString();
	}
	private void loadProps() {
		this.pathToServicesIndex = props.getPropString("pathToServicesIndexHtml");
		this.pathToMatinsOrdinary = props.getPropString("epub.path.to.matins.ordinary");
		this.pathToRoot = GeneralUtils.getParentPath(this.pathToServicesIndex)  + "/";
		tocDisplayName = props.getPropString("epub.reader.toc.toc.display.name");
		textDisplayName = props.getPropString("epub.reader.toc.text.display.name");
		indexDisplayName = props.getPropString("epub.reader.toc.index.display.name");
		this.author = props.getPropString("epub.author");
		generateToc = props.getPropBoolean("epub.generate.toc");
		generateFirstWordsIndex = props.getPropBoolean("epub.generate.first.words.index");
		generateFirstWordsIndexExcludeBetweenParentheses = props.getPropBoolean("epub.generate.first.words.index.exclude.between.parentheses");
		this.nGram = props.getPropInt("epub.generate.toc.number.of.words");
		if (generateFirstWordsIndex) {
			lettersPerRow = props.getPropInt("epub.index.letters.per.row");
		} // use initialized value of zero
		includeMatinsOrdinary = props.getPropBoolean("epub.include.matins.ordinary");
		saveHtml = props.getPropBoolean("epub.save.html");
		classesToExclude = props.getListFromDelimitedString("epub.remove.html.classes");
		classesToIndex = props.getListFromDelimitedString("epub.generate.first.words.index.classes");
		classesForTocFullText = props.getListFromDelimitedString("epub.generate.toc.classes.full.text");
		classesForTocFirstWords = props.getListFromDelimitedString("epub.generate.toc.classes.first.words");
		titleDateFormat = new SimpleDateFormat(props.getPropString("epub.date.title.format"));
		tocDateFormat = new SimpleDateFormat(props.getPropString("epub.date.toc.format"));
	}

	/**
	 * Get today's date
	 * @return formated as yyyy-mm-dd
	 */
	private String getToday() {
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	public String getAdditionalContent() {
		return additionalContent;
	}

	public void setAdditionalContent(String additionalContent) {
		this.additionalContent = additionalContent;
	}


}
