package net.ages.liturgical.workbench.transformer.json;

import java.io.File;

import alwb.transformer.models.Result;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;
import net.ages.liturgical.workbench.transformer.utils.PropertyUtils;

public class RunToBuildJsonFiles {

	public static void main(String[] args) {
		Result result = new Result();
		try {
			String config = "/Transformer.config";
			PropertyUtils props = new PropertyUtils(config);
			String inputPath = GeneralUtils.getParentPath(props.getPropString("pathToServicesIndexHtml"));
			boolean createJsonFiles = props.getPropBoolean("createJsonFiles");

			if (createJsonFiles) {
				JsonBuilder t = new JsonBuilder(inputPath + File.separator + "h");
				result = t.toJson();
			}

			if (result.errorCount > 0 || result.fileCount == 0) {
				if (result.fileCount == 0) {
					System.out.println("No Json files were created.");
					System.out.println("Maybe you forgot to set createJsonFiles = \"yes\" in the Transformer.config file?");
				} else {
					System.out.println("There were problems with creating json for the files listed below...");
				}
				System.out.println(result.toJsonString());
			} else {
				System.out.println(result.fileCount + " json files were created.");
				System.out.println("They are in: " +  inputPath + File.separator + "j" );
				System.out.println("Be sure to upload them to the website...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
