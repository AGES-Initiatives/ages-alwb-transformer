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
					, true // print pretty
					);
		return htmlToJson.process();
	}
}
