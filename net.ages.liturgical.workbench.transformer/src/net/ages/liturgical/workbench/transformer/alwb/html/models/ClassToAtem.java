package net.ages.liturgical.workbench.transformer.alwb.html.models;

import java.util.Map;
import java.util.TreeMap;

public class ClassToAtem {
	public static Map<String,Atem> map = new TreeMap<String,Atem>();
	
	public ClassToAtem() {
		loadMap();
	}
	
	public void add(String key, String start, String end) {
		Atem atem = new Atem(start,end);
		map.put(key, atem);
	}
	
	public Atem get(String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return null;
		}
	}
	
	public void loadMap() {
		add("cover1", "Title role cover1", "End-Title");
		add("mixed", "Title", "End-Title");
		add("rubric","Rubric ","End-Rubric");
		add("dialog","Dialog ","End-Dialog");
		add("actor","Actor ","End-Actor");
		add("red","<r>","</>");
		add("black","<bl>","</>");
		add("hymn","Hymn ","End-Hymn");
		add("melody","Title role melody","End-Title");
		add("designation","Title role desig","End-Title");
		add("inaudible","Para role inaudible ","End-Para");
		add("verse","Verse ","End-Verse");
		add("hymnlinelast","Para role hymnlinelast","End-Para");
		add("reading","Reading ","End-Reading");
		add("mode","Title role","End-Title");
	}
}
