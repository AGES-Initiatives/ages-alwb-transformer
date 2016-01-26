package net.ages.liturgical.workbench.transformer.text.to.alwb;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ages.alwb.utils.core.error.handling.ErrorUtils;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

public class TextToAlwb {
	final Logger logger = LoggerFactory.getLogger(TextToAlwb.class);
	// Map below is temporary and used to detect duplicates in a file
	private Map<String, Integer> stringCount = new TreeMap<String,Integer>();
	// Map below holds the key-value pairs for the duplicates.  
	// It is read from the file system (if exists) and added to during processing.
	// It is written back to the file system.
	private Map<String,String> duplicates = new TreeMap<String,String>();
	// Map below is temporary.  Look up the key for a duplicate text
	private Map<String,String> duplicatesIndex = new TreeMap<String,String>();
	// Map below holds the ares key-value pairs for the text
	private Map<String,String> aresForDomain = new TreeMap<String,String>();
	private Map<String,String> aresForGreek = new TreeMap<String,String>();
	private List<String> actors = new ArrayList<String>();
	private String actorDelimiter;
	private String pathIn;
	private String pathOut;
	private String domain;
	private File duplicatesFile;
	private static final String duplicatesPrefix = "duplicates_"; 
	private String duplicatesResourceName;
	private String duplicatesFileName;
	private String aresLineOneKey = "A_Resource_Whose_Name";
	private final static String QUOTE = "\"";
	private String greekDuplicatesResourceName = duplicatesPrefix + "gr_GR_cog";
	private String greekDuplicatesFileName = greekDuplicatesResourceName + ".ares";
	private String aresResourceName;
	private String aresFileName;
	private String atemFileName;
	private String greekAresResourceName;
	private String greekAresFileName;
	private String greekDomain = "gr_GR_cog";
	private int aresKeyCounter = 0;
	private String templateName;
	private String templateFileName;
	private String title;
	
	public TextToAlwb(
			String actors
			, String actorDelimiter
			, String pathIn
			, String pathOut
			, String domain
			) {
		this.actors = GeneralUtils.stringToList(actors);
		this.actorDelimiter = actorDelimiter;
		this.pathIn = pathIn;
		this.pathOut = pathOut;
		this.domain = domain;
		duplicatesResourceName = duplicatesPrefix + domain;
		duplicatesFileName = duplicatesResourceName + ".ares";
	}

	private void loadDuplicatesFile() {
		duplicatesFile = new File(this.pathOut+"/"+ duplicatesFileName);
		if (duplicatesFile.exists()) {
			try {
				List<String> lines = AlwbFileUtils.linesFromFile(duplicatesFile);
				for (String line : lines) {
					if (line.startsWith(aresLineOneKey)) {
						// ignore
					} else {
						try {
							String [] parts = line.split("=");
							if (parts.length > 1) {
								String key = parts[0].trim();
								String value = parts[1].trim();
								duplicates.put(key, value);
								duplicatesIndex.put(value, key);
							}
						} catch (Exception e) {
							ErrorUtils.report(logger, e);
						}
					}
				}
			} catch (Exception e) {
				ErrorUtils.report(logger, e);
			}
		} else {
			File duplicatesDir = new File(pathOut);
			duplicatesDir.mkdirs();
		}
	}
	
	private String quote(String s) {
		if (s.startsWith(QUOTE)) {
			return s;
		} else {
			return QUOTE + s + QUOTE;
		}
	}
	
	private String deQuote(String s) {
		String result = s;
		try {
			if (s.startsWith(QUOTE) && s.endsWith(QUOTE)) {
				return s.substring(1, s.length()-1);
			}
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
		}
		return result;
	}
	
	private void writeDuplicates() {
		StringBuffer domainContents = new StringBuffer();
		StringBuffer greekContents = new StringBuffer();
		domainContents.append(this.aresLineOneKey + " = " + duplicatesResourceName +  "\n");
		greekContents.append(this.aresLineOneKey + " = " + greekDuplicatesResourceName +  "\n");
		Iterator<Entry<String,String>> it = duplicates.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			domainContents.append(entry.getKey() + " = " + quote(entry.getValue()) + "\n");
			greekContents.append(entry.getKey() + " = \"\""  + "\n");
		}
		AlwbFileUtils.writeFile(pathOut + "/library/" + duplicatesFileName, domainContents.toString());
		AlwbFileUtils.writeFile(pathOut + "/library/" + greekDuplicatesFileName, greekContents.toString());
	}
	
	private void writeDomainAresFile() {
		StringBuffer contents = new StringBuffer();
		contents.append(this.aresLineOneKey + " = " + aresResourceName +  "\n");
		Iterator<Entry<String,String>> it =  this.aresForDomain.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			String value;
			if (entry.getValue().contains(domain) || entry.getValue().contains(greekDomain)) {
				value = entry.getValue();
			} else {
				value = quote(StringUtils.escape(entry.getValue()));
			}
			contents.append(entry.getKey() + " = " + value + "\n");
		}
		AlwbFileUtils.writeFile(pathOut + "/library/" + aresFileName, contents.toString());
	}

	private void writeTemplateFile() {
		StringBuffer contents = new StringBuffer();
		contents.append("Template " + templateName +  "\n\n");
		
		// set imports
		contents.append("Status Review\n\n");
//		contents.append("\timport duplicates_gr_GR_cog.*\n");
		contents.append("\timport " + this.aresResourceName + ".*\n\n");
		contents.append("\timport iTags.*\n");
		contents.append("\timport bTags.*\n");
		contents.append("\timport roles.*\n");
		
		// set header
		contents.append("\n\tHead\n");
		contents.append("\t\tPage_Header_Even\n");
		contents.append("\t\t\tcenter @text \"" + title + "\"\n");
		contents.append("\t\tEnd_Page_Header_Even\n");
		contents.append("\t\tPage_Header_Odd\n");
		contents.append("\t\t\tcenter @text \"" + title + "\"\n");
		contents.append("\t\tEnd_Page_Header_Odd\n");
		contents.append("\t\tPage_Footer_Even\n");
		contents.append("\t\t\tcenter @pageNbr\n");
		contents.append("\t\tEnd_Page_Footer_Even\n");
		contents.append("\t\tPage_Footer_Odd\n");
		contents.append("\t\t\tcenter @pageNbr\n");
		contents.append("\t\tEnd_Page_Footer_Odd\n");
		contents.append("\t\tSet_Page_Number 1 End_Set_Page_Number\n");
		contents.append("\tEnd_Head\n\n");
		
		/**
		 * Actor sid Priest End-Actor
		 * Dialog sid euLI.Key0109.text End-Dialog
		 * Para role alttext sid euLI.Key0301.text End-Para
		 */
		Iterator<Entry<String,String>> it =  this.aresForDomain.entrySet().iterator();
		boolean inDialog = false;
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			String value;
			if (entry.getValue().contains(domain) || entry.getValue().contains(greekDomain)) {
				if (isActor(entry.getValue())) {
					contents.append("\t\tActor sid " + entry.getKey() + " End-Actor\n");
					inDialog = true;
				} else {
					if (inDialog) {
						inDialog = false;
						contents.append("\t\tDialog sid " + entry.getKey() + " End-Dialog\n");
					} else {
						contents.append("\t\tPara role alttext sid " + entry.getKey() + " End-Para\n");
					}
				}
			} else {
				inDialog = false;
				contents.append("\t\tPara role alttext sid " + entry.getKey() + " End-Para\n");
			}
		}
		contents.append("End-Template");
		AlwbFileUtils.writeFile(pathOut + "/templates/" + templateFileName, contents.toString());
	}
	
	private boolean isActor(String value) {
		String[] parts = value.split(this.duplicatesResourceName+".");
		if (parts.length > 1) {
			String text = duplicates.get(parts[1]);
			for (String actor : actors) {
				if (text.toLowerCase().contains(actor)) {
					return true;
				}
			}
		} 
		return false;
	}
	private void writeGreekAresFile() {
		StringBuffer contents = new StringBuffer();
		contents.append(this.aresLineOneKey + " = " + greekAresResourceName +  "\n");
		Iterator<Entry<String,String>> it =  this.aresForGreek.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			contents.append(entry.getKey() + " = " + entry.getValue() + "\n");
		}
		AlwbFileUtils.writeFile(pathOut + "/library/" + greekAresFileName, contents.toString());
	}

	private String pad(int i) {
		return String.format("%04d", i);
	}
	
	private String createAlwbFileName(String name) {
		String result = "";
		try {
			if (name.endsWith(".txt")) {
				result = name.replace(".txt", "");
				result = result.replaceAll(" ", "");
				result = result.toLowerCase();
			}
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
		}
		return result;
	}
	
	private String createAresFileName(String name, String domain) {
		String result = "";
		try {
			result = createAresResourceName(name, domain);
		    result = result + ".ares";
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
		}
		return result;
	}
	
	private String createAresResourceName(String name, String domain) {
		String result = "";
		try {
			result = createAlwbFileName(name);
		    result = result +"_" + domain;
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
		}
		return result;
	}

	private String[] lineParts(String line) {
		String matchingActor = "";
		String newLine = line.trim();
		String[] result  = new String[2];
		for (String actor : actors) {
			if (newLine.toLowerCase().startsWith(actor+this.actorDelimiter)) {
				matchingActor = newLine.substring(0,actor.length());
				break;
			}
		}
		if (matchingActor.length() > 0) {
			if (newLine.length() > matchingActor.length()) {
				String[] parts = newLine.split(matchingActor+this.actorDelimiter);
				result[0] = matchingActor;
				if (parts.length > 0) {
					try {
						result[1] = parts[1].trim();
					} catch (Exception e) {
						ErrorUtils.report(logger, e);
					}
				}
			} else {
				result[0] = newLine;
			}
		} else {
			result[0] = newLine;
		}
		return result;
	}
	
	private void recordStringForDuplicates(String s) {
		try {
			String newLine = s.trim();
			newLine.replaceAll("\t", "");
			if (isActor(s)) {
				newLine.toLowerCase();
			}
			int code = 0;
			if (newLine.length() == 1) {
				newLine = newLine.trim();
				char[] chars = newLine.toCharArray();
				code = chars[0];
			}
			if (newLine.length() > 0 && code != 160) {
				if (stringCount.containsKey(s)) {
					Integer count = stringCount.get(s);
					count++;
					stringCount.put(s, count);
				} else {
					stringCount.put(s, 1);
				}
			}
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
		}
	}
	
	private void addLine(String line) {
		String newLine = line.trim();
		newLine.replaceAll("\t", "");
		int code = 0;
		if (newLine.length() == 1) {
			newLine = newLine.trim();
			char[] chars = newLine.toCharArray();
			code = chars[0];
		}
		if (newLine.length() > 0 && code != 160) {
			String[] parts = lineParts(newLine);
			for (int i=0; i < parts.length; i++) {
				if (parts[i] != null) {
					aresKeyCounter++;
					String key = "p"+pad(aresKeyCounter);
					String value = "";
					String quoted = quote(StringUtils.escape(parts[i]));
					if (duplicatesIndex.containsKey(quoted)) {
						value = this.duplicatesResourceName + "." + duplicatesIndex.get(quoted);
					} else {
						value = parts[i];
					}
					addAresKeyValue(key,value);
				}
			}
		}
	}
	
	private void addAresKeyValue(String key, String value) {
		this.aresForDomain.put(key,value);
		if (value.contains(greekDomain)) {
			this.aresForGreek.put(key,value);
		} else {
			this.aresForGreek.put(key,"\"\"");
		}
	}

	private void findDuplicates(List<File> files) {
		for (File file : files) {
			List<String> lines = AlwbFileUtils.linesFromFile(file);
			for (String line : lines) {
				if (line.length() != 0) {
					String[] lineParts = lineParts(line);
					recordStringForDuplicates(lineParts[0]);
					if (lineParts[1] != null && lineParts.length > 0) {
						recordStringForDuplicates(lineParts[1]);
					}
				}
			}
		}
		int counter = duplicates.size();
		for (String key : stringCount.keySet()) {
			Integer count = stringCount.get(key);
			if (count > 1) {
				if (! duplicates.containsValue(quote(StringUtils.escape(key)))) {
					counter++;
					String propKey = "d" + pad(counter);
					String value = quote(StringUtils.escape(key));
					duplicates.put(propKey, value);
					duplicatesIndex.put(value, propKey);
				}
			}
		}
		if (duplicates.size() > 0) {
			try {
				writeDuplicates();
			} catch (Exception e) {
				ErrorUtils.report(logger, e);
			}
		}
		
	}

	public boolean process() {
		boolean result = true;
		try {
			List<File> files = AlwbFileUtils.getFilesInDirectory(pathIn, "txt");
			loadDuplicatesFile();
			findDuplicates(files);
			for (File file : files) {
				aresForDomain = new TreeMap<String,String>();
				aresForGreek = new TreeMap<String,String>();
				title = null;
				aresKeyCounter = 0;
				templateName = createAresResourceName(file.getName(), "");
				templateName = "bk."+templateName.substring(0, templateName.length()-1);
				templateFileName = templateName + ".atem";
				aresResourceName = createAresResourceName(file.getName(), domain);
				greekAresResourceName = createAresResourceName(file.getName(), greekDomain);
				aresFileName = createAresFileName(file.getName(), domain);
				greekAresFileName = createAresFileName(file.getName(), greekDomain);
				atemFileName = createAlwbFileName(file.getName())+".atem";
				print("Transforming \n"+ file.getName()+ "\nto\n"+ aresFileName+ "\nand to\n"+ atemFileName);
				List<String> lines = AlwbFileUtils.linesFromFile(file);
				title = lines.get(0);
				for (String line : lines) {
					addLine(line);
				}
				writeDomainAresFile();
				writeGreekAresFile();
				writeTemplateFile();
			}
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
		}
		return result;
	}

	private void print(String s) {
		System.out.println(s);
	}
}

