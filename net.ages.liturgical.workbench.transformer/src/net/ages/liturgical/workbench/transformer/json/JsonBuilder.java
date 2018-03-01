package net.ages.liturgical.workbench.transformer.json;

import alwb.transformer.json.DirectoryHtmlToJson;
import alwb.transformer.models.Result;

public class JsonBuilder {
	private String path = "";
	
	public JsonBuilder(String path) {
		this.path = path;
	}
	
	public Result toJson() {
		DirectoryHtmlToJson htmlToJson = new DirectoryHtmlToJson(
				this.path
				, false 
				);
		return htmlToJson.process();
	}

	public Result toJson(boolean printPretty) {
			DirectoryHtmlToJson htmlToJson = new DirectoryHtmlToJson(
					this.path
					, printPretty // set to true in order to have pretty print json
					);
		return htmlToJson.process();
	}
}
