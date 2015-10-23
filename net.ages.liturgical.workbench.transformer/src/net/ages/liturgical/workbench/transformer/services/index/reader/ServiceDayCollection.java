package net.ages.liturgical.workbench.transformer.services.index.reader;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ServiceDayCollection {
	String title;
	String href;
	private Map<String,ServiceDay> theServices = new TreeMap<String, ServiceDay>();

	public ServiceDayCollection(String title, String href) {
		this.title = title;
		this.href = href;
		theServices.put(title, new ServiceDay(href));
	}
	
	/**
	 * Get an iterator for all the entries in the ServiceDay collection
	 * @return the iterator
	 */
	public Iterator<Entry<String,ServiceDay>> getEntryIterator() {
		return theServices.entrySet().iterator();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public Map<String, ServiceDay> getTheServices() {
		return theServices;
	}
	public void setTheServices(Map<String, ServiceDay> theServices) {
		this.theServices = theServices;
	}
}
