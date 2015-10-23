package net.ages.liturgical.workbench.transformer.pdf;

import java.io.File;
import java.util.List;
import java.util.Properties;

import net.ages.liturgical.workbench.transformer.AlwbTransformer;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

public class RunToBuildPdfFiles {

	public static void main(String[] args) {
		try {
			String ePubConfig = "/Transformer.config";
			Properties prop = GeneralUtils.getProperties(
					AlwbTransformer.class, ePubConfig);
			String foSource = GeneralUtils.getParentPath(prop.getProperty("pathToServicesIndexHtml"));
			boolean createPdfFiles = GeneralUtils.getPropBoolean(prop.getProperty("createPdfFiles"));
			boolean deleteFoFiles = GeneralUtils.getPropBoolean(prop.getProperty("deleteFoFiles"));

			boolean hadErrors = false;

			if (createPdfFiles) {
				List<File> files = AlwbFileUtils.getFilesInDirectory(foSource,"fo");

				if (files == null || files.size() < 1) {
					hadErrors = true;
				} else {
					for (File f : files) {
						System.out.println(f.getPath());
						generatePdf(f.getPath(),foSource, deleteFoFiles);
					}
				}
			}

			if (hadErrors) {
				System.out.println("Could not find fo files in ");
				System.out.println(foSource);
				System.out.println("So, no PDF files were created.");
				System.out.println("Maybe you forgot to set generate.file.pdf = \"yes\" in the pref.generation file?");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void generatePdf(String inputFile, String dirOut, boolean deleteFoFiles) {
		PdfBuilder t = new PdfBuilder(inputFile,dirOut,deleteFoFiles,true);
		t.parameters.clear();
		t.foToPdf();
	}
}
