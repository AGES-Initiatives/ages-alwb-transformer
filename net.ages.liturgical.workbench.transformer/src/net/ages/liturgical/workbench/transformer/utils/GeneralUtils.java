package net.ages.liturgical.workbench.transformer.utils;

import java.io.File;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.util.FileUtils;

public class GeneralUtils {

	public static InputStream getResource(Class c, String path) {
		return c.getResourceAsStream(path);
	}

	public static void createDir(String path) {
		try {
			File f = new File(path);
			f.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Properties getProperties(Class c, String path) {
		Properties prop = new Properties();
		try {
			InputStream input = null;
				input = getResource(c,path);
				prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	/**
	 * Convenience method to get the Path to the folder containing
	 * the file or directory in the supplied path.
	 * @param path
	 * @return path to parent
	 */
	public static String getParentPath(String path) {
		try {
			return new File(path).getParent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Convenience method to get a boolean from a string property
	 * @param value yes or true or no or false
	 * @return true if yes or true, false if no or false
	 */
	public static boolean getPropBoolean(String value) {
		return value.toLowerCase().startsWith("y") || value.toLowerCase().startsWith("t");
	}
	
	public static List<String> getPropSubFolders(String folders) {
		List<String> result = new ArrayList<String>();
		try {
			String[] parts = folders.split(",");
			if (parts.length > 0) {
				for (String folder : parts) {
					result.add(folder.toLowerCase().trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Removes diacritics from UTF-8
	 * @param text
	 * @return lower case version of text with all diacritic marks removed
	 */
	public static String normalize(String text) {
		String result = text;
		try {
			while(! Character.isAlphabetic(result.charAt(0)) && result.length() > 1) {
				if (result.length() > 1) {
					result = result.substring(1,result.length());
				}
			}
			result = Normalizer.normalize(result.toLowerCase(), Normalizer.Form.NFD);
			result = result.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Create a service date by using the directory information in the path
	 * @param path - must contain at end /s/{year}/{month}{service}/{lang}/filename.html
	 * @return year/month/day
	 */
	public static Date serviceDateFromPath(String path) {
		Date result = null;
		try {
			String [] theDirs = FileUtils.getPathStack(path);
			int l = theDirs.length;
			if (theDirs.length > 7) {
				String s = theDirs[l-7];
				if (s.startsWith("s")) {
					String day = theDirs[l-4];
					String month = theDirs[l-5];
					String year = theDirs[l-6];
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					result = sdf.parse(
							day 
							+ "-"
							+ month
							+ "-"
							+ year
							+ "-"
							);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Converts a comma delimited String into a List<String>
	 * @param delimitedString
	 * @return the List. 
	 */
	public static List<String> stringToList(String delimitedString) {
		List<String> result = new ArrayList<String>();
		try {
			for (String s : Arrays.asList(delimitedString.split(","))) {
				result.add(s.trim());
			}
		} catch (Exception e) {
			result = new ArrayList<String>();
		}
		return result;
	}
	public static int getPropInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public static String firstNWords(String s, int nbrWords) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		try {
			String [] parts = s.split(" ");
			int cnt;
			if (parts.length > nbrWords) {
				cnt = nbrWords;
				for (int i = 0; i < cnt; i++) {
					String word = parts[i];
					sb.append(word + " ");
				}
				result = sb.toString().trim();
				char last = result.charAt(result.length()-1);
				if (! Character.isLetter(last)) {
					if (last != ')') {
						result = result.substring(0, result.length()-1);
					}
				}
				result = result.replaceAll(" * ", " ");
				result = result + "...";
			} else {
				result = s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
