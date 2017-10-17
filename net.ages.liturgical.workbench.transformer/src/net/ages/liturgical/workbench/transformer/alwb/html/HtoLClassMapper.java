package net.ages.liturgical.workbench.transformer.alwb.html;

import java.util.Map;
import java.util.TreeMap;

public class HtoLClassMapper {
	private String prefix = "\\lt";
	Map<String,String> theMap = new TreeMap<String,String>();
	
	public HtoLClassMapper() {
		theMap.put("actor", "Actor");
		theMap.put("black", "Black");
		theMap.put("cover1", "Cover1");
		theMap.put("designation", "Designation");
		theMap.put("dialog", "Dialog");
		theMap.put("hymn", "Hymn");
		theMap.put("hymnlinelast", "Hymnlinelast");
		theMap.put("inaudible", "Inaudible");
		theMap.put("kvp", "Sid");
		theMap.put("melody", "Melody");
		theMap.put("mixed", "Mixed");
		theMap.put("reading", "Reading");
		theMap.put("red", "Red");
		theMap.put("rubric", "Rubric");
		theMap.put("verse", "verse");
	}
	
	public String get(String key) {
		if (theMap.containsKey(key)) {
			return prefix + theMap.get(key);
		} else {
			System.out.println("Missing this one: " + key);
			return prefix + "missing:" + key;
		}
	}
}
