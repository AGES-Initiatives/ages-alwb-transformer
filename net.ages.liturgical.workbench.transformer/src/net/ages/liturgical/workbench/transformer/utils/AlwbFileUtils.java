package net.ages.liturgical.workbench.transformer.utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class AlwbFileUtils {
	
	/**
	 * Read all the lines in the given file and return as a list
	 * @param f - the file to read
	 * @return the lines as a List
	 */
	public static List<String> linesFromFile(File f) {
		Path path = f.toPath();
		List<String> list = null;
		try {
			list = Files.readAllLines(path, Charset.forName("UTF-8"));
		} catch (IOException e) {
			StringBuffer sb = reportBadGuys(f);
			if (sb.length() > 0) {
				System.out.println(sb.toString());
				System.out.println("There are bad characters in the file " + f.getName());
			}
		}
		return list;
	}
	
	public static StringBuffer reportBadGuys(File f) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			int lineCount = 0;
			int charCount = 0;
		    while ((line = br.readLine()) != null) {
		    	lineCount++;
		    	char[] theChars = line.toCharArray();
		    	for (int i = 0; i < theChars.length; i++) {
				    byte[] theBytes = Character.toString(theChars[i]).getBytes(StandardCharsets.UTF_8);
				    charCount++;
				    if (theBytes.length > 1) {
					    sb.append((f.getName() + ":" + lineCount + ":" + charCount + ":" + theChars[i])+"\n");
				    }
		    	}
		    }		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb;
	}

	public static void reportBadChars(File f) {
		try {
			int charCount = 0;
			InputStream inputStream       = new FileInputStream(f);
			Reader  inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			int data = inputStreamReader.read();
			while(data != -1){
			    char theChar = (char) data;
			    byte[] theBytes = Character.toString(theChar).getBytes(StandardCharsets.UTF_8);
			    charCount++;
			    if (theBytes.length > 1) {
				    System.out.println(charCount + ": " + theChar);
			    }
			    data = inputStreamReader.read();
			}
			inputStreamReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void copyFiles(
			String fromDir
			, String toDir
			, String extension
			) {
		CopyOption[] options = new CopyOption[]{
			      StandardCopyOption.REPLACE_EXISTING,
			      StandardCopyOption.COPY_ATTRIBUTES
			    }; 
		List<File> filesIn = getFilesInDirectory(fromDir, extension);
		for (File file : filesIn) {
			try {
				Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(toDir+"/"+ file.getName()), options);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getFileAsString(File f) {
		StringBuffer result = new StringBuffer();
		List<String> lines = linesFromFile(f);
		for (String line : lines) {
			result.append(line.trim() + " ");
		}
		return result.toString();
	}
	
	/**
	 * Used to get a report of files of a specific type, and optionally to prefix and/or suffix
	 * the name, for whatever purpose is needed.
	 * @param prefix - will occur before the filename.  Can be set to null;
	 * @param suffix - will occur after the filename.  Can be set to null;
	 * @param path - directory to search
	 * @param extension - file extension to find
	 */
	public static  List<String > listFiles(String prefix, String suffix, String path, String extension) {
		List<String> result = new ArrayList<String>();
		if (prefix == null) {
			prefix = "";
		}
		if (suffix == null) {
			suffix = "";
		}
		List<File> files = AlwbFileUtils.getFilesInDirectory(path, extension);
		for (File f : files) {
			result.add(prefix + f.getName() + suffix);
		}
		return result;
	}
	
	/**
	 * Used to get a report of files of a specific type, and optionally to prefix and/or suffix
	 * the name, for whatever purpose is needed.
	 * @param prefix - will occur before the filename.  Can be set to null;
	 * @param suffix - will occur after the filename.  Can be set to null;
	 * @param path - directory to search
	 * @param extension - file extension to find
	 */
	public static  void printFileList(String prefix, String suffix, String path, String extension) {
		List<String> files = listFiles(prefix,suffix,path,extension);
		for (String s : files) {
			System.out.println(s);
		}
	}
	
	public static String[] getPathsToFilesInDirectory(String directory, String extension, String excludeSubPath) {
		List<File> list = getFilesInDirectory(directory, extension);
		List<String> paths = new ArrayList<String>();
		Iterator<File> it = list.iterator();
		while(it.hasNext()) {
			File f = it.next();
			String a = f.getAbsolutePath();
			String b = f.pathSeparator;
			String path = (String)f.getPath();
			if (! path.contains(excludeSubPath)) {
				paths.add(path);
			}
		}
		return paths.toArray(new String[paths.size()]);	
	}
	

	/**
	 * Finds all index.html files that are for services
	 * @param directory
	 * @param extension
	 * @return list<File> of index.html files
	 */
	public static List<File> getServicesHtmlFilesInDirectory(String directory, final String extension) {
		List<File> list = getFilesInDirectory(directory, extension);
		List<File> files = new ArrayList<File>();
		Iterator<File> it = list.iterator();
		String path = "";
		File f;
		while(it.hasNext()) {
			f = it.next();
			path = FilenameUtils.separatorsToUnix((String) f.getPath());
			if (path.contains("/h/s") && (path.endsWith("index.html"))) {
				files.add(f);
			}
		}
		return files;
	}
	
	public static List<File> getMatchingFilesInDirectory(String directory, String fileRegularExpression, String extension) {
		List<File> list = getFilesInDirectory(directory, extension);
		List<File> files = new ArrayList<File>();
		Iterator<File> it = list.iterator();
		File f;
		while(it.hasNext()) {
			f = it.next();
			if (f.getName().matches(fileRegularExpression)) {
				files.add(f);
			}
		}
		return files;
	}

	/**
	 * Finds all the files in specified directory with specified extension.
	 * Returns only those files that match one of the supplied regular expressions.
	 * @param directory - to search, including subdirectories
	 * @param fileRegularExpressions - all the expressions to apply against the filenames
	 * @param extension - file extension to use, e.g. html
	 * @return those files that match any of the regular expressions
	 */
	public static List<File> getMatchingFilesInDirectory(String directory, List<String> fileRegularExpressions, String extension) {
		List<File> files = new ArrayList<File>();
			for (String p : fileRegularExpressions) {
				for (File f : AlwbFileUtils.getMatchingFilesInDirectory(directory, p, extension)) {
					files.add(f);
				}
			}
		return files;
	}
	
	public static List<File> getFilesFromSubdirectories(String directory, String extension) {
		List<File> list = getFilesInDirectory(directory, extension);
		String rootPath = new File(directory).getPath();
		List<File> files = new ArrayList<File>();
		Iterator<File> it = list.iterator();
		File f;
		while(it.hasNext()) {
			f = it.next();
			if (f.getParent().length() > rootPath.length()) {
				files.add(f);
			}
		}
		return files;
	}

	/**
	 * Recursively read contents of directory and return all files found
	 * @param directory
	 * @param file extension, e.g. html
	 * @return List containing all files found
	 */
	public static List<File> getFilesInDirectory(String directory, final String extension) {
		File dir = new File(directory);
		String [] extensions = {extension};
		List<File> files = null;
		try {
			files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		} catch (Exception e) {
			files = null;
		}
		return files;
	}

	/**
	 * The static main method is provided for test purposes and as an example of how to use this class.
	 * <p>Normal usage is to instantiate the class elsewhere 
	 * set the parameters, and call the 
	 * xmlToHtml method or the xmlToPdf method.
	 * 
	 * @param args -- do not use.
	 */
	public static void main(String[] args) {
		String serviceYear = "2014"; 
//		String source = "../net.ages.liturgical.workbench.templates.ematins/src-gen/website";
		String source = "filesIn";
		List<File> files = getFilesInDirectory(source,"pdf");
		System.out.println(files.size() + " files found!");
	}
	
	public static Elements getPdfHrefs(File file) {
		Elements result = null;
		try {
			org.jsoup.nodes.Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");
			result = doc.select("a[href$=pdf]");
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		}
//		hrefsToString(result); // use for debugging
		return result;
	}
	
	/**
	 * Get all the left cells for table rows in the html
	 * @param file
	 * @return
	 */
	public static Elements getHtmlLeftCellsFromTable(File file) {
		Elements result = null;
		try {
			org.jsoup.nodes.Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");
			result = doc.select(".leftCell");
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	// Use for debugging.  Prints elements to system out
	public static void hrefsToString(Elements list) {
		Iterator<Element> it = list.iterator();
		while (it.hasNext()) {
			Element e = it.next();
			System.out.println(e.attr("href"));
		}
	}
	
	/**
	 * Normalizes a HREF so it points to the local media Eclipse project
	 * @param hrefBaseUrl - part of URL to strip from href.  Needs to end with forward slash
	 * @param localMediaPath - path to prefix to the stripped href.  Needs to end with forward slash
	 * @param href - href to normalize
	 * @return normalized href
	 */
	public static String normalizeMediaPath(String hrefBaseUrl, String localMediaPath, String href) {
		String result = "";
		try {
			result = localMediaPath + href.split(hrefBaseUrl)[1];
		} catch (Exception e) {
//			e.printStackTrace();
			result = localMediaPath + href;
		}
		return result;
	}
	
	/**
	 * For the supplied path to a directory
	 * @param path
	 * @return the immediate child directories
	 */
	public static File[] getDirectChildDirectories(String path) {
		File root = new File(path);
		return root.listFiles(directoryFilter);
	}
	
	public static File[] getDirectChildYearDirectories(String path) {
		File root = new File(path);
		return root.listFiles(directoryYearFilter);
	}

	public static File[] getDirectChildMonthDirectories(String path) {
		File root = new File(path);
		return root.listFiles(directoryMonthFilter);
	}

	public static File[] getDirectChildDayDirectories(String path) {
		File root = new File(path);
		return root.listFiles(directoryDayFilter);
	}

	public static FileFilter directoryFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.isDirectory();
		}
	};
	
	public static FileFilter directoryYearFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) {
				try {
					int year = Integer.parseInt(file.getName());
					return (year > 1000 && year < 3000);
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
	};
	
	public static FileFilter directoryMonthFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) {
				try {
					int month = Integer.parseInt(file.getName());
					return (month > 0 && month < 13);
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
	};

	public static FileFilter directoryDayFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) {
				try {
					int day = Integer.parseInt(file.getName());
					return (day > 0 && day < 32);
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
	};

	/**
	 * Get the part of the path that starts after the delimiter
	 * @param delimiter - string at end of which to split
	 * @param path - the path to be split
	 * @return the subpath
	 */
	public static String getSubPath(String delimiter, String path) {
		String result = "";
		try {
			result = path.split(delimiter)[1];
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	/**
	 * Replaces forward slashes in path with periods and returns a file 
	 * name based on the path information
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static String pathToName(String path, String fileName) {
		return path.split("m/s/")[1].replace("/", ".") + fileName;
	}
	
	/**
	 * Attempts to get the Href to use for the combined PDFs for this service.
	 * @param file
	 * @return the Href if found, else null
	 */
	public static String getMergedPdfHrefFromHtmlFile(File file) {
		String result = null;
		try {
			org.jsoup.nodes.Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");
			result = doc.select("title").attr("data-combo-pdf-href");
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	public static String pathFromHref(String href) {
		String result = "";
		try {
			String [] parts = href.split("m/s")[1].split("/");
			result = StringUtils.join(parts,"/",0,parts.length-1);
		} catch (Exception e) {
			e.printStackTrace();
			result = href;
		}
		return result;
	}

	public static String filenameFromHref(String href) {
		String result = "";
		try {
			String [] parts = href.split("m/s")[1].split("/");
			result = parts[parts.length-1];
		} catch (Exception e) {
			e.printStackTrace();
			result = href;
		}
		return result;
	}
	
	/**
	 * For a given ares filename, return the prefix and domain parts
	 * @param file - ares filename
	 * @return array with prefix in [0] and domain in [1]
	 */
	public static String[] getAresFileParts(String file) {
		String [] theParts;
		String [] result;
		try {
			theParts = file.split("_");
			result = new String[2];
			if (theParts.length ==4) {
				result[0] = theParts[0];
				result[1] = (theParts[1] + "." + theParts[2] + "." + theParts[3].replace(".tsf", "")).toLowerCase();
			} else {
				result = null;
			}
		} catch (Exception e) {
			result = null;
		}
		return result;
	}	
	
	public static String aresFileToMongoDbCollectionName(String filename) {
		String [] parts = getAresFileParts(filename);
		return parts[1]+"."+parts[0];
	}
	
	/**
	 * Create a new file and write the contents to it
	 * @param filename the name of the file, including the path
	 * @param content the contents to write
	 */
			
	public static void writeFile(String filename, String content) {
		File file = new File(filename);
		BufferedWriter bw = null;
		 
		try {
			file = new File(filename);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
//			bw = Files.newBufferedWriter(file.toPath(),StandardCharsets.UTF_8, StandardOpenOption.WRITE);
			bw = new BufferedWriter(fw);
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
