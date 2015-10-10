package net.ages.liturgical.workbench.transformer.epub;

import java.util.Properties;

public class RunToBuildEpubIndex {

	public static void main(String[] args) {
		System.out.println("Building indexes for ePubs.  \nDon't worry about the red letters below that say INFO nl.siegmann.epublib.domain.Resource");
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
