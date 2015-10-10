package net.ages.liturgical.workbench.transformer.epub;

import java.util.Map;
import java.util.TreeMap;

public class MonthMap {
	private Map<String,String> theMap = new TreeMap<String,String>();
	
	public MonthMap() {
		theMap.put("jan", "01");
		theMap.put("feb", "02");
		theMap.put("mar", "03");
		theMap.put("apr", "04");
		theMap.put("may", "05");
		theMap.put("jun", "06");
		theMap.put("jul", "07");
		theMap.put("aug", "08");
		theMap.put("sep", "09");
		theMap.put("oct", "10");
		theMap.put("nov", "11");
		theMap.put("dec", "12");
	}
	
	/**
	 * 
	 * @param month as either full name or first three letters, any case
	 * @return the number for the month, with a leading zero if less than 10
	 */
	public String getMonthNbr(String month) {
		return theMap.get(month.trim().toLowerCase().substring(0,3));
	}
}
