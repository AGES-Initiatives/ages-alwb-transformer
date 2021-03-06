package net.ages.liturgical.workbench.transformer.epub;

import org.json.JSONObject;

/**
 * The purpose of this class is to store ALWB specific attributes in
 * an ePub file so that they can be extracted later.
 * 
 * The epublib has a Map<String,String> for MetaData, but it 
 * does not seem to support ad hoc key-value pairs.  It lets you store
 * them, but if you write the ePub, then try to retrieve them, they are
 * gone.  So, I created Attributes as an alternative.
 * 
 * The underlying storage uses Json with simple String key-value pairs.  
 * 
 * Get the Json string by
 * calling getJsonString(), then store it book.getMetaData().addDescription();
 * 
 * Assuming that the .addDescription() method is only called once with the
 * set of Transformer programs, you can retrieve the Json as follows:
 * String json = book.getMetaData().getDescriptions().get(0);
 * 
 * Then pass the result into new Attributes(json);
 * 
 * After that you can call the various get methods from the Attributes instance.
 * 
 * @author mac002
 *
 */
public class Attributes {
	JSONObject map;
	public static final String KEY_TOC_TITLE = "tocTitle";
	public static final String KEY_TOC_DATE = "tocDate";
	public static final String KEY_TYPE = "type";
	public static final String VALUE_TYPE_SERVICE = "service";
	public static final String VALUE_TYPE_DAY = "day";
	public static final String VALUE_TYPE_MONTH = "month";
	public static final String VALUE_TYPE_AD_HOC = "adhoc";
	public static final String VALUE_TYPE_UNKNOWN = "unknown";
	
	/**
	 * Call this constructor when you do not have an existing ePub Book
	 * instance to read.
	 */
	public Attributes() {
		map = new JSONObject();
	}
	
	/**
	 * Use this constructor when you have an existing ePub Book,
	 * and you were able to retrieve the json string from
	 * book.getMetaData().getDescriptions.get(0);
	 * 
	 * 
	 * @param json The string retrieved from the MetaData.
	 */
	public Attributes(String json) {
		map = new JSONObject(json);
	}
	
	public void put(String key, String value) {
		map.put(key, value);
	}
	
	public void setType(String value) {
		map.put(KEY_TYPE, value);
	}
	
	public String getType() {
		if (map.has(KEY_TYPE)) {
			return (String) map.get(KEY_TYPE);
		} else {
			return "";
		}
	}
	
	public void setTocTitle(String value) {
		map.put(KEY_TOC_TITLE, value);
	}
	
	public String getTocTitle() {
		if (map.has(KEY_TOC_TITLE)) {
			return (String) map.getString(KEY_TOC_TITLE);
		} else {
			return "";
		}
	}
	
	public void setTocDate(String value) {
		map.put(KEY_TOC_DATE, value);
	}
	
	public String getTocDate() {
		if (map.has(KEY_TOC_DATE)) {
			return (String) map.getString(KEY_TOC_DATE);
		} else {
			return "";
		}
	}
	
	public String get(String key) {
		return (String) map.get(key);
	}

	public JSONObject getJson() {
		return map;
	}
	
	/**
	 * 
	 * @return a String that is valid json
	 */
	public String toJsonString() {
		return map.toString();
	}
	
}