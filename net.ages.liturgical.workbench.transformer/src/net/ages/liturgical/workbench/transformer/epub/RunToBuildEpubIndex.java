package net.ages.liturgical.workbench.transformer.epub;

import java.util.Properties;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

/**
 * Reads all the ePubs in the e/s directory and creates an index.html file
 * with all the ePubs as links.
 * 
 * Also, updates the servicesindex.html with a link to the ePub index.html.
 * 
 * RunToRemoveEpubIndex.java can be run to remove the link from the servicesindex.html.
 * 
 * @author mac002
 *
 */
public class RunToBuildEpubIndex {

	public static void main(String[] args) {
		System.out.println("Building indexes for ePubs.");
		String ePubConfig = "/Transformer.config";
		Properties prop = GeneralUtils.getProperties(
				RunToBuildEpubIndex.class, ePubConfig);
		
		try {
			String indexPath = prop.getProperty("pathToServicesIndexHtml");
			String mainIndexHtml = prop.getProperty("services.index.file.html.epub.info");
			String epubIndexTitle = prop.getProperty("epub.index.title");
			String epubIndexHeading = prop.getProperty("epub.index.heading");
			
			IndexUtils indexUtils = new IndexUtils(indexPath,mainIndexHtml);
			indexUtils.createEpubIndex(
					epubIndexTitle,
					epubIndexHeading);
			System.out.println("Finished indexing ePubs.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
