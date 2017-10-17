package net.ages.liturgical.workbench.transformer.pdf;

import java.util.List;

import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

public class RunToMergePdfFilesForBook {

	public static void main(String[] args) {
		try {
			String ePubConfig = "/Transformer.config";
			PropertyUtils props = new PropertyUtils(ePubConfig);
			String pathIn = props.getPropString("pdf.book.merge.path.in");
			String pathOut = props.getPropString("pdf.book.merge.path.out");
			String mergedFilename = props.getPropString("pdf.book.merge.new.file.name");
			List<String> filesToMerge = props.getPropArray("pdf.book.merge.file");
			
			PdfMergerForBook merger = new PdfMergerForBook(
					filesToMerge
					, pathIn
					, pathOut
					, mergedFilename
					); 
			merger.mergePdfs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
