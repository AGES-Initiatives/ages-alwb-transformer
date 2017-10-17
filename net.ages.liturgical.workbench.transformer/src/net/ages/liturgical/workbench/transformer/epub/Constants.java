package net.ages.liturgical.workbench.transformer.epub;

public class Constants {
	public static final String EPUB_DIR = "e/";
	public static final String EPUB_SERVICE_DIR = EPUB_DIR + "s/";
	public static final String EPUB_MERGED_DIR = EPUB_DIR + "m/";
	// the number of ../ must match the count of the forward slashes in EPUB_SERVICE_DIR
	public static final String EPUB_INDEX_BASE_REF = "<base href='../../'/>";
	public static final String PATH_TO_RESOURCES = "resources/";
}
