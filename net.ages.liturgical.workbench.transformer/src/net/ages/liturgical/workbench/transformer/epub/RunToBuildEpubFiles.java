package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.util.List;

import net.ages.liturgical.workbench.transformer.services.index.reader.HtmlServicesIndexReader;
import net.ages.liturgical.workbench.transformer.services.index.reader.Service;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

/**
 * Run this to build ePub files.  Will create an ePub for every
 * html file it finds.
 * 
 * Note that for services, currently the title of the ePub is derived from the indexes
 * pointed to by the servicesIndex.html.  It would be better to replace this with the
 * implementation of Trello ALWB-Eclipse E-2015-004.  This would place the language
 * specific full titles as a data attribute in the title of the html.  Then the ePub builder
 * would pick up the titles from there rather than extracting them from the website 
 * indexes.
 * 
 * @author mac002
 *
 */
public class RunToBuildEpubFiles {

	public static void main(String[] args) {
		try {
			String ePubConfig = "/Transformer.config";
			PropertyUtils props = new PropertyUtils(ePubConfig);
			String pathToServicesIndexHtml = props.getPropString("pathToServicesIndexHtml");
			String source = GeneralUtils.getParentPath(pathToServicesIndexHtml);
			source = source + "/h/"; // append the h folder.  Our target is the html
			HtmlServicesIndexReader htmlIndexer = new HtmlServicesIndexReader(pathToServicesIndexHtml);
			boolean createFiles = props.getPropBoolean("createIndividualEpubFiles");
			boolean excludeRoot = props.getPropBoolean("exclude.html.in.root.folder");
			boolean includeMatinsOrdinary = props.getPropBoolean("epub.include.matins.ordinary");
			boolean missingMatinsOrdinary = false;

			MatinsOrdinary theMatinsOrdinary = null;
			String matinsOrdinarySource = props.getPropString("epub.path.to.matins.ordinary");
			
			if (createFiles) {
				String title = null;
				List<File> files = null;
				if (excludeRoot) { // we typically don't want to convert our index files in the root html dir
					files = AlwbFileUtils.getFilesFromSubdirectories(source,"html");
				} else {
					files = AlwbFileUtils.getFilesInDirectory(source,"html");
				}

				if (includeMatinsOrdinary) {
					try {
						theMatinsOrdinary = new MatinsOrdinary(
								source + matinsOrdinarySource
								, props.getListFromDelimitedString("epub.remove.html.classes")
						);
						missingMatinsOrdinary = false;
					} catch (Exception e) {
						missingMatinsOrdinary = true;
					}
				}
				
				if (files == null || files.size() < 1) {
					System.out.println("Could not find html files in ");
					System.out.println(source);
					System.out.println("So, no ePub files were created.");
				} else if (missingMatinsOrdinary) {
					System.out.println("Matins Ordinary was not found in ");
					System.out.println(matinsOrdinarySource);
					System.out.println("So, no ePub files were created.");
					System.out.println("Open the cu.MatinsOrdinary.atem template, and generate the html. Then, re-run the ePub generator.");
					System.out.println("cu.MatinsOrdinary.atem should be in templates/Blocks/Ordinaries");
				} else {
					for (File f : files) {
						EpubBuilder t = new EpubBuilder(props,f.getPath(),"h","e");
						Service s = htmlIndexer.getService(f.getPath());
						if (s != null) {
							title = s.getTitle();
							if (s.getLanguage() != null) {
								title = title + " (" + s.getLanguage() + ")";
							}
							if (s.getTitle().toLowerCase().startsWith("ma")) {
								// this is a Matins service
								if (includeMatinsOrdinary) {
									// provide the matins ordinary content to the ePub builder
									t.setAdditionalContent(theMatinsOrdinary.getContent(s.getLanguage().toLowerCase()));
								}
							}
						} else {
							title = null;
						}
						t.htmlToEpub(title);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
