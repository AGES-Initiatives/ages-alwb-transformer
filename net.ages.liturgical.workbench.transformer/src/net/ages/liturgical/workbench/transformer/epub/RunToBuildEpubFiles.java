package net.ages.liturgical.workbench.transformer.epub;

import java.util.Properties;

/**
 * Run this class as a Java application to build ePub files from the 
 * HTML files for services in a generated AGES website.
 * 
 * First edit the Run.config file to set the properties you want to use.
 * 
 * @author mac002
 *
 */
public class RunToBuildEpubFiles {
	
	public static void main(String[] args) {
		try {
			String ePubConfig = "/Transformer.config";
			Properties prop = GeneralUtils.getProperties(
					RunToBuildEpubFiles.class, ePubConfig);
			EpubBuilder b = new EpubBuilder(prop);
			b.build();
			RunToBuildEpubIndex.main(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
