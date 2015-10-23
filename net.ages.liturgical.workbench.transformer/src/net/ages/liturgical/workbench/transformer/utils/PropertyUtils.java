package net.ages.liturgical.workbench.transformer.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyUtils {
	Properties props =  null;
	
	public PropertyUtils(String path) {
		this.props = getProperties(PropertyUtils.class, path);
	}
	
	/**
	 * Gets a value that is a delimited string and returns it as a List<String>
	 * @param key
	 * @return the List
	 */
	public List<String> getListFromDelimitedString(String key) {
		return GeneralUtils.stringToList(getPropString(key));
	}
	
	public static Properties getProperties(Class c, String path) {
		Properties prop = new Properties();
		try {
			InputStream input = null;
				input = GeneralUtils.getResource(c,path);
				prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}

	
	/**
	 * Gets a straightforward String value 
	 * @param key
	 * @return the value
	 */
	public String getPropString(String key) {
		return props.getProperty(key);
	}
	
	/**
	 * Convenience method to get a boolean from a string property
	 * @param value yes or true or no or false
	 * @return true if yes or true, false if no or false
	 */
	public boolean getPropBoolean(String key) {
		String value = getPropString(key);
		return value.toLowerCase().startsWith("y") || value.toLowerCase().startsWith("t");
	}
	
	/**
	 * Convenience method to get an int from a string property
	 * @param key
	 * @return the int value
	 */
	public int getPropInt(String key) {
		String value = getPropString(key);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 
	 * Expects to find a set of keys that start with keyPrefix
	 * and end with an arbitrary set of sequential numbers, starting with 1.
	 * 
	 * @param keyPrefix - the first part of the key
	 * @return an array of all the values
	 */
	public List<String> getPropArray(String keyPrefix) {
		List<String> result = new ArrayList<String>();
		boolean more = true;
		int counter = 1;
		
		// load up the patterns
		while (more) {
			String p = props.getProperty(keyPrefix + "." + counter);
			if (p == null) {
				more = false;
			} else {
				result.add(p);
				counter++;
			}
		}
		return result;
	}
	
}
