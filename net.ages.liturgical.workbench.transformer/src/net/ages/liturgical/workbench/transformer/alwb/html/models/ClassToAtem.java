package net.ages.liturgical.workbench.transformer.alwb.html.models;

import java.util.Map;
import java.util.TreeMap;

public class ClassToAtem {
	public Map<String,Atem> map = new TreeMap<String,Atem>();
	
	public ClassToAtem() {
		loadMap();
	}
	
	public static void loadMap() {
		String key = "";
		Atem atem = new Atem(
				"Title role"
				, "End-Title"
				);
	}
}
