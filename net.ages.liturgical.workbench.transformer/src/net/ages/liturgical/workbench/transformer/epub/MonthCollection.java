package net.ages.liturgical.workbench.transformer.epub;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MonthCollection {
	private String month;
	private Map<String,ServiceDayCollection> theMap = new TreeMap<String,ServiceDayCollection>();

	public MonthCollection(String month) {
		this.month = month;
	}
	
	public Iterator<Entry<String,ServiceDayCollection>> getEntryIterator() {
		return theMap.entrySet().iterator();
	}
	
	public void add(String day, String href) {
		theMap.put(day, new ServiceDayCollection(day, href));
	}
	
	public void print() {
		System.out.println(month);
		Iterator<Entry<String,ServiceDayCollection>> it = theMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,ServiceDayCollection> e = it.next();
			System.out.println(e.getValue().getTitle() + " - " + e.getValue().getHref());
		}
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Map<String, ServiceDayCollection> getTheMap() {
		return theMap;
	}

	public void setTheMap(Map<String, ServiceDayCollection> theMap) {
		this.theMap = theMap;
	}
	
}
