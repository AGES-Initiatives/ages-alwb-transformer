package net.ages.liturgical.workbench.transformer;

import net.ages.liturgical.workbench.transformer.epub.RunToBuildEpubFiles;
import net.ages.liturgical.workbench.transformer.epub.merger.RunToMergeEpubFilesGroupedByDay;
import net.ages.liturgical.workbench.transformer.epub.merger.RunToMergeEpubFilesGroupedByMonth;
import net.ages.liturgical.workbench.transformer.pdf.RunToBuildPdfFiles;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

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
			PropertyUtils props = new PropertyUtils(ePubConfig);
			boolean createPdfFiles = props.getPropBoolean("createPdfFiles");
			boolean createIndividualEpubs = props.getPropBoolean("createIndividualEpubFiles");

			if (createPdfFiles) {
				RunToBuildPdfFiles.main(null);
			}
			if (createIndividualEpubs) {
				RunToBuildEpubFiles.main(null);
			}
	}

}
