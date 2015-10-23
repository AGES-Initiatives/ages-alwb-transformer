package net.ages.liturgical.workbench.transformer.epub;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

public class EpubUtils {
	
	private static String css = "ages.css";
	private static String fontsPath = ""; // stylesPath + "/fonts";
	private static String font1 = "Arimo-Bold.ttf";
	private static String font2 = "Arimo-BoldItalic.ttf";
	private static String font3 = "Arimo-Italic.ttf";
	private static String font4 = "Arimo-Regular.ttf";


	public static Book initializeEpubBook(
			String theTitle
			, String theLanguage
			, String theAuthor
			, String theDate
			, String pathToResources
			) {
		
		Book book = null;
		try {
			// Create new Book
			book = new Book();

			// Set the title
			book.getMetadata().addTitle(
					theTitle 
					);

			// Add an Author
			book.getMetadata().addAuthor(new Author(theAuthor));

			// Add the fonts
			book.getResources().add(
					new Resource(EpubUtils.class
							.getResourceAsStream(pathToResources + font1),
							fontsPath + font1));
			book.getResources().add(
					new Resource(EpubUtils.class
							.getResourceAsStream(pathToResources + font2),
							fontsPath + font2));
			book.getResources().add(
					new Resource(EpubUtils.class
							.getResourceAsStream(pathToResources + font3),
							fontsPath + font3));
			book.getResources().add(
					new Resource(EpubUtils.class
							.getResourceAsStream(pathToResources + font4),
							fontsPath + font4));

			// Add css file
			book.getResources().add(
					new Resource(EpubUtils.class
							.getResourceAsStream(pathToResources + css),
							css));
			
			// Add preface
			book.addSection(
					"Preface",
					getResource(EpubUtils.class
							.getResourceAsStream(pathToResources
									+ "preface.html"), "preface.html"));

		// Add sources
			book.addSection(
					"Sources",
					getResource(EpubUtils.class
							.getResourceAsStream(pathToResources
									+ "sources.html"), "sources.html"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return book;
	}

	private static Resource getResource(InputStream is, String href) {
		try {
			return new Resource(is, href);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param book handle to the Book
	 * @param folder The folder to put the ePub file into, i.e. the path to the containing folder
	 * @param file The name of the ePub file (without the path)
	 */
	public static void writeEpubFile(Book book, String folder, String file) {
		try {
			GeneralUtils.createDir(folder);
			EpubWriter epubWriter = new EpubWriter();
			epubWriter.write(book, new FileOutputStream(folder+file));
			System.out.println(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
