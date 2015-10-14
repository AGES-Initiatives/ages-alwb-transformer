package net.ages.liturgical.workbench.transformer;
import java.util.Properties;

import net.ages.liturgical.workbench.transformer.epub.GeneralUtils;
import net.ages.liturgical.workbench.transformer.epub.RunToBuildEpubFiles;
import net.ages.liturgical.workbench.transformer.pdf.RunToBuildPdfFiles;

public class AlwbTransformer {

	/**
	 * Run this to transform fo into PDF and HTML into ePub.
	 * Set properties in the file Transformer.config.
	 * It uses key = value pairs.
	 * Unlike an ares file, the values do not have quotation marks arodeund them.
	 * 
	 * @param args - none
	 */
	public static void main(String[] args) {
			String ePubConfig = "/Transformer.config";
			Properties prop = GeneralUtils.getProperties(
					AlwbTransformer.class, ePubConfig);
			boolean createPdfFiles = GeneralUtils.getPropBoolean(prop.getProperty("createPdfFiles"));
			boolean createMonthlyEpubs = GeneralUtils.getPropBoolean(prop.getProperty("createMonthlyEpubs"));

			if (createPdfFiles) {
				RunToBuildPdfFiles.main(null);
			}
			if (createMonthlyEpubs) {
				RunToBuildEpubFiles.main(null);
			}
	}

}
