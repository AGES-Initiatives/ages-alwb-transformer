package net.ages.liturgical.workbench.transformer;

import java.io.File;

import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

public class RunToListFiles {

	public static void main(String[] args) {
		boolean listText = false;
		boolean listPdf = false;
		boolean listHtml =false;
		boolean listEpub = true;
		String ePubConfig = "/Transformer.config";
		PropertyUtils props = new PropertyUtils(ePubConfig);
		String nonTextPath = props.getPropString("pathToServicesIndexHtml");
		String textPath = props.getPropString("text.to.alwb.path.in");
		File dir = new File(nonTextPath);
		if (dir.exists()) {
			nonTextPath = dir.getParentFile().getAbsolutePath();
			if (listHtml) {
				AlwbFileUtils.printFileList(
						null
						, null
						, nonTextPath+"/h"
						, "html"
						);
			} 
			if (listPdf) {
				AlwbFileUtils.printFileList(
						null
						, null
						, nonTextPath+"/p"
						, "pdf"
						);
			}
			if (listEpub) {
				AlwbFileUtils.printFileList(
						"epub.merge.file.1 = "
						, null
						, nonTextPath+"/e"
						, "epub"
						);				
			}
		} else {
			System.out.println("Can't find services index file: " + nonTextPath);
		}
		if (listText) {
			dir = new File(textPath);
			if (dir.exists()) {
				AlwbFileUtils.printFileList(
						null
						, null
						, textPath
						, "txt"
						);
			} else {
				System.out.println("Can't find services index file: " + nonTextPath);
			}
		}
	}
}
