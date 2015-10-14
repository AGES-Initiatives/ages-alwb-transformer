package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Properties;

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
}
