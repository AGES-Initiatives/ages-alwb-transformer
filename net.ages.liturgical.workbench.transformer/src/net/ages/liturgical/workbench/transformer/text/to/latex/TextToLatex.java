package net.ages.liturgical.workbench.transformer.text.to.latex;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ages.alwb.utils.core.error.handling.ErrorUtils;
import net.ages.liturgical.workbench.transformer.utils.AlwbFileUtils;
import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

/**
 * Read in all text files in given directory.
 * Outputs them as latex files, templates and resources
 * @author mac002
 *
 */
public class TextToLatex {
	final Logger logger = LoggerFactory.getLogger(TextToLatex.class);
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
	// Map to hold text of all files
	private Map<File,List<String>> mapFileLines = new TreeMap<File, List<String>>();
	private List<String> actors = new ArrayList<String>();
	private String actorDelimiter;
	private String pathIn;
	private String pathOutAres;
	private String pathOutAresDomain;
	private String pathOutAresGreek;
	private String pathOutAtem;
	private String domain;
	private String topic;
	private File duplicatesFile;
	private static final String duplicatesPrefix = "duplicates"; 
	private String duplicatesResourceName;
	private String duplicatesFileName;
	private String resourceFileOpening = "\\documentclass{memoir}\n";
	private String resourceFileBegin = "\\begin{document}\n";
	private final static String QUOTE = "\"";
	private String greekDuplicatesResourceName = duplicatesPrefix; // + "gr_GR_cog";
	private String greekDuplicatesFileName = greekDuplicatesResourceName + ".tex";
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
	private String atemTagOpen;
	private String atemTagEnd;
	private String ampersand;
	
	public TextToLatex(
			String actors
			, String actorDelimiter
			, String pathIn
			, String pathOutAres
			, String pathOutAtem
			, String domain
			, String atemTagOpen
			, String atemTagEnd
			, String ampersand
			) {
		this.actors = GeneralUtils.stringToList(actors);
		this.actorDelimiter = actorDelimiter;
		this.pathIn = pathIn;
		this.pathOutAres = pathOutAres;
		this.pathOutAresDomain = pathOutAres + "/" + domain;
		this.pathOutAresGreek = pathOutAres + "/" + "gr_gr_cog";
		this.pathOutAtem = pathOutAtem;
		this.domain = domain;
		duplicatesResourceName = duplicatesPrefix; // + domain;
		duplicatesFileName = duplicatesResourceName + ".tex";
		this.atemTagOpen = atemTagOpen;
		this.atemTagEnd = atemTagEnd;
		this.ampersand = ampersand;
		File f = new File(this.pathOutAresDomain);
		f.mkdirs();
		f = new File(this.pathOutAresGreek);
		f.mkdirs();
	}

	private void loadDuplicatesFile() {
		if (duplicatesFile.exists()) {
			try {
				List<String> lines = AlwbFileUtils.linesFromFile(duplicatesFile);
				for (String line : lines) {
					if (line.startsWith(resourceFileOpening)) {
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
			File duplicatesDir = new File(pathOutAres);
			duplicatesDir.mkdirs();
		}
	}
	
	private String quote(String s) {
//		if (s.startsWith(QUOTE)) {
			return s;
//		} else {
//  		return QUOTE + s + QUOTE;
//		}
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
		domainContents.append(this.resourceFileOpening + this.resourceFileBegin);
		greekContents.append(this.resourceFileOpening + this.resourceFileBegin);
		Iterator<Entry<String,String>> it = duplicates.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			String value = entry.getValue();
			value = value.replaceAll("&", ampersand);
			value = value.replaceAll("&", ampersand);
			if (StringUtils.isAllUpperCase(value)) {
				domainContents.append(texResWrap(entry.getKey()+"title.doc",value));
				domainContents.append(texResWrap(entry.getKey()+"title.toc",value));
				domainContents.append(texResWrap(entry.getKey()+"title.heading",value));
				greekContents.append(texResWrap(entry.getKey()+"title.doc",value));
				greekContents.append(texResWrap(entry.getKey()+"title.toc",value));
				greekContents.append(texResWrap(entry.getKey()+"title.heading",value));
			} else {
				domainContents.append(texResWrap(entry.getKey(),value));
				greekContents.append(texResWrap(entry.getKey(), ""));
			}
		}
		AlwbFileUtils.writeFile(pathOutAresDomain + "/" + duplicatesFileName, domainContents.toString());
		AlwbFileUtils.writeFile(pathOutAresGreek + "/" + greekDuplicatesFileName, greekContents.toString());
	}
	
	private void loadLines(List<File> files) {
		for (File f : files) {
			try {
				List<String> lines = AlwbFileUtils.linesFromFile(f);
				int s = lines.size();
				List<String> newLines = new ArrayList<String>();
				for (int i=0; i < s; i++) {
					if (i == s-1) {
						newLines.add(amp(lines.get(i)));
					} else {
						String l1 = lines.get(i);
						String l2 = lines.get(i+1);
						if (l1.length() > 0 && Character.isLowerCase(l1.codePointAt(0))) {
							// ignore
						} else {
								if (l2.length() > 0 && Character.isLowerCase(l2.codePointAt(0))) {
									newLines.add(amp(l1 + " " + l2));
								} else {
									newLines.add(amp(l1));
								}
						}
					}
				}
				mapFileLines.put(f, newLines);
			} catch (Exception e) {
				ErrorUtils.report(logger, e);
			}
		}
	}
	
	private String amp(String s) {
		String result = s.replaceAll("&", ampersand);
		result = result.replaceAll("&", ampersand);
		return result;
	}

	private String getTopicDomain() {
		return 
				texResWrap("ltType", "resource") 
				+ texResWrap("ltTopic", topic) 
				+ texResWrap("ltDomain", domain)
				;
	}
	
	private void writeDomainAresFile() {
		StringBuffer contents = new StringBuffer();
		contents.append(
				this.resourceFileOpening
				+ this.resourceFileBegin
				+ getTopicDomain()
				);
		Iterator<Entry<String,String>> it =  this.aresForDomain.entrySet().iterator();
		boolean dummy = false;
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			String value = entry.getValue();
			value = value.replaceAll("&", ampersand);
			value = value.replaceAll("&", ampersand);
			if (StringUtils.isAllUpperCase(value)) {
				contents.append(texResWrap(entry.getKey()+"title.doc",value));
				contents.append(texResWrap(entry.getKey()+"title.toc",value));
				contents.append(texResWrap(entry.getKey()+"title.heading",value));
			} else {
				contents.append(texResWrap(entry.getKey(),value));
			}
		}
		AlwbFileUtils.writeFile(pathOutAresDomain + "/" + aresFileName, contents.toString());
	}

	
	private String escape(String s) {
		return StringEscapeUtils.escapeJava(s);
	}
	
	private void writeTemplateFile() {
		String topic = aresResourceName.split("_")[0];
		StringBuffer contents = new StringBuffer();
		contents.append("\\chapter{\\ltMixed{\\ltSid{\"pub.priest.servicebook\"}{\"li." + topic + ".title\"}}}\n\n");
		
		Iterator<Entry<String,String>> it =  this.aresForDomain.entrySet().iterator();
		boolean inDialog = false;
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			String altKey = altKey(entry.getKey());
			if (altKey != null) {
				String altValue = this.duplicates.get(altKey);
				if (isActor(this.duplicates.get(altKey))) {
					contents.append(getActor(getSid(topic,entry.getKey())));
					inDialog = true;
				} else {
					if (inDialog) {
						inDialog = false;
						contents.append(getDialog(getSid(topic,entry.getKey())));
					} else {
						contents.append(getDefault(getSid(topic,entry.getKey())));
					}
				}
			} else {
				inDialog = false;
				contents.append(getDefault(getSid(topic,entry.getKey())));
			}
		}
		AlwbFileUtils.writeFile(pathOutAtem + "/" + templateFileName, contents.toString());
	}
	
	private String getDefault(String value) {
		return getLabel(atemTagOpen, value);
	}
	
	private String getActor(String value) {
		return getLabel("Actor", value);
	}
	
	private String getDialog(String value) {
		return getLabel("Dialog", value);
	}
	
	private String getLabel(String label, String value) {
		return "\\lt" + label + "{" + value + "}\n\n";
	}
	private String getSid(String resource, String key) {
		String altKey = altKey(key);
		if (altKey == null) {
			return "\\ltSid{\"" + resource + "\"}{\"" + key + "\"}";
		} else {
			return "\\ltSid{\"duplicates\""  + "}{\"" + altKey + "\"}";
		}
	}
	
	private String altKey(String key) {
		String value = aresForDomain.get(key);
		if (value.startsWith("duplicates")) {
			return value.substring(11, value.length());
		} else {
			return null;
		}
	}
	
	private boolean isActor(String value) {
			for (String actor : actors) {
				if (value.toLowerCase().contains(actor)) {
					return true;
				}
			}
		return false;
	}
	private void writeGreekAresFile() {
		StringBuffer contents = new StringBuffer();
		contents.append(this.resourceFileOpening + " = " + greekAresResourceName +  "\n");
		Iterator<Entry<String,String>> it =  this.aresForGreek.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> entry = it.next();
			contents.append(entry.getKey() + " = " + entry.getValue() + "\n");
		}
		AlwbFileUtils.writeFile(pathOutAresGreek + "/" + greekAresFileName, contents.toString());
	}

	/**
	 * Wrap an id and value as a Latex Resource for lookup by id
	 * @param id
	 * @param value
	 * @return wrapped value, e.g. %<*"li.rubric.forvigilance">\nThe Priest, bowing profoundly, continues:\n%</"li.rubric.forvigilance">
	 */
	private String texResWrap(String id, String value) {
		return "%<*\"" + id + "\">\n" + value + "\n%</\"" + id + "\">\n";
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
		    result = result + ".tex";
		} catch (Exception e) {
			ErrorUtils.report(logger, e);
		}
		return result;
	}
	
	private String createAresResourceName(String name, String domain) {
		String result = "";
		try {
			result = createAlwbFileName(name);
	//	    result = result +"_" + domain;
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
					String quoted = quote(escape(parts[i]));
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
		for (File f : mapFileLines.keySet()) {
			List<String> lines = mapFileLines.get(f);
			for (String line : lines) {
				try {
					if (line.length() != 0) {
						String[] lineParts = lineParts(line);
						recordStringForDuplicates(lineParts[0]);
						if (lineParts[1] != null && lineParts.length > 0) {
							recordStringForDuplicates(lineParts[1]);
						}
					}
				} catch (Exception e) {
					ErrorUtils.report(logger, e);
				}
			}
		}
		int counter = duplicates.size();
		for (String key : stringCount.keySet()) {
			Integer count = stringCount.get(key);
			if (count > 1) {
				if (! duplicates.containsValue(quote(escape(key)))) {
					counter++;
					String propKey = "d" + pad(counter);
					if (propKey.contains("d1154")) {
						int i = 0;
						i = 3;
					}
					String value = quote(escape(key));
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
			loadLines(files);
			duplicatesFile = new File(this.pathOutAres+"/"+ duplicatesFileName);
//			loadDuplicatesFile();
			findDuplicates(files);
			for (File file : files) {
				aresForDomain = new TreeMap<String,String>();
				aresForGreek = new TreeMap<String,String>();
				title = null;
				aresKeyCounter = 0;
				templateName = createAresResourceName(file.getName(), "");
				topic = templateName.substring(0, templateName.length());
				templateName = "bk."+ topic;
				templateFileName = templateName + ".tex";
				aresResourceName = createAresResourceName(file.getName(), domain);
				greekAresResourceName = createAresResourceName(file.getName(), greekDomain);
				aresFileName = createAresFileName(file.getName(), domain);
				greekAresFileName = createAresFileName(file.getName(), greekDomain);
				atemFileName = createAlwbFileName(file.getName())+".tex";
				print("Transforming "+ file.getName());
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

