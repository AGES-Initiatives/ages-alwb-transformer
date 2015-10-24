package net.ages.liturgical.workbench.transformer.epub.merger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import net.ages.liturgical.workbench.transformer.epub.Constants;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

/**
 * Run this to build combine (merge) ePub files.  
 * 
 * @author mac002
 *
 */
public class RunToMergeEpubFilesGroupedByMonth {

	public static void main(String[] args) {
		try {
			
			// read the properties
			String ePubConfig = "/Transformer.config";
			PropertyUtils props = new PropertyUtils(ePubConfig);
			String pathToServicesIndexHtml = props.getPropString("pathToServicesIndexHtml");
			String source = GeneralUtils.getParentPath(pathToServicesIndexHtml);
			source = source + "/e/"; 
			String title = props.getPropString("merge.month.title");
			String author = props.getPropString("merge.month.author");
			String filenamePrefix = props.getPropString("merge.month.filename.prefix");
			SimpleDateFormat sdf = new SimpleDateFormat(props.getPropString("merge.month.date.format"));
			boolean mergeFiles = props.getPropBoolean("mergeEpubFilesByMonth");
			List<String> exclusions = props.getListFromDelimitedString("merge.month.exclusions");
			String mergeMonthRegEx = props.getPropString("merge.month.regular.expression");
			// We use the Calendar to format the date on the title of the ePub
			Calendar calendar = new GregorianCalendar();
			
			if (mergeFiles) {

				File[] years = AlwbFileUtils.getDirectChildYearDirectories(source+"/s");
				for (File year : years) {
					calendar.set(Calendar.YEAR, Integer.parseInt(year.getName()));
					File[] months = AlwbFileUtils.getDirectChildMonthDirectories(year.getPath());
					for (File month : months) {
						calendar.set(Calendar.MONTH, Integer.parseInt(month.getName())-1);
						// get the files that match the patterns
						List<File> files = AlwbFileUtils.getMatchingFilesInDirectory(month.getPath(), mergeMonthRegEx,"epub");
						if (files.size() > 0) {
							// Create the EpubMerger.  Instantiating it will invoke the merge process.
							EpubMerger merger = new EpubMerger(
									files
									, title + sdf.format(calendar.getTime())
									, author
									, source + "m/s/" + year.getName() + "/" + month.getName() + "/"
									, filenamePrefix + "." + year.getName() + "." + month.getName()
									, exclusions
									, Constants.VALUE_TYPE_MONTH
									);
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
