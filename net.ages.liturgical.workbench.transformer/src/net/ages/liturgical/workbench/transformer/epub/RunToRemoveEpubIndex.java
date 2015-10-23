package net.ages.liturgical.workbench.transformer.epub;

import java.util.Properties;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

/**
 * Run this class if you want to remove the link to ePubs from
 * the servicesindex.html file.  It will also delete the /e/s/index.html
 * file in the ePub directory.  It will not delete the actual ePub files.
 * @author mac002
 *
 */
public class RunToRemoveEpubIndex {

	public static void main(String[] args) {
		System.out.println("Removing ePub index.");
		String ePubConfig = "/Transformer.config";
		Properties prop = GeneralUtils.getProperties(
				RunToBuildEpubIndex.class, ePubConfig);
		
		try {
			String indexPath = prop.getProperty("pathToServicesIndexHtml");
			String mainIndexHtml = prop.getProperty("services.index.file.html.epub.info");
			
			IndexUtils indexUtils = new IndexUtils(indexPath,mainIndexHtml);
			indexUtils.removeEpubIndex();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished removing ePub index.");
	}

}
