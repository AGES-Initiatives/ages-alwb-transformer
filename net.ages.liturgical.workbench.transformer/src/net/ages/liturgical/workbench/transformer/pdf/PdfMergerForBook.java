package net.ages.liturgical.workbench.transformer.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;

public class PdfMergerForBook {
	private List<String> pdfName;
	private String pathIn;
	private String pathout;
	private String mergedFilename;
	private String title;
	private String author;
	private String date;
	private Map<String,String> fileMap = new TreeMap<String,String>();
	
	public PdfMergerForBook(
			List<String> pdfName
			, String pathIn
			, String pathOut
			, String mergedFilename
			) {
			this.pdfName = pdfName;			
			this.pathIn = pathIn;
			this.pathout = pathOut;
			this.mergedFilename = mergedFilename;
	}
	
	private boolean inList(String filename) {
		for (String name : pdfName) {
			if (name.matches(filename)) {
				return true;
			}
		}
		return false;
	}
	
	private void loadFileMap() {
 		List<File> files = AlwbFileUtils.getFilesInDirectory(pathIn, "pdf");
 		for (File f : files) {
 			if (inList(f.getName())) {
 				fileMap.put(f.getName(), f.getAbsolutePath());
 			}
 		}
	}
	
	public void mergePdfs() {
		loadFileMap();
 		PDFMergerUtility mergePdf = new PDFMergerUtility();
 		for (String pdf : pdfName) {
 			String pdfPath = fileMap.get(pdf);
 			if (pdfPath == null || pdfPath.length() < 1) {
 				System.out.println("Error: could not find " + pdf);
 			} else {
 				mergePdf.addSource(pdfPath);
 			}
 		}
 		File out = new File(pathout);
 		out.mkdirs();
		mergePdf.setDestinationFileName(pathout + "/" + mergedFilename);
		System.out.println("Combining files into: " + mergePdf.getDestinationFileName());
		try {
			mergePdf.mergeDocuments();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void listPdfs(String path) {
		List<File> files = AlwbFileUtils.getFilesInDirectory(path, "pdf");
		for (File f : files) {
			System.out.println("pdfFilenames.add(\"" + f.getName() + "\");");
		}
	}
}
