package net.ages.liturgical.workbench.transformer.epub;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ServiceDay {
	private String href;
	private Map<String,Service> theServices = new TreeMap<String,Service>();
	
	ServiceDay(String href) {
		try {
			this.href = href;
			load();
//			print();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Iterator<Entry<String,Service>> getEntryIterator() {
		return theServices.entrySet().iterator();
	}

	private Document getDoc(File file) {
		Document result = null;
		try {
			result = Jsoup.parse(file, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void load() {
		Document doc = getDoc(new File(href));
			String indexTitle = doc.getElementsByClass("index-title-date").first().text();
//			System.out.println(indexTitle);

			String serviceUrl = null;
			String serviceTitle = null;
			String serviceLanguage = null;
			String serviceDescription = null;
			int serviceDayCounter = 0;
			int serviceCounter = 0;
			
			Element e = null;

			Iterator<Element> rowIt = doc.getElementsByTag("tr").iterator();

			while (rowIt.hasNext()) {
				Element row = rowIt.next();
				if (row.hasClass("index-service-day-tr")) {
					serviceTitle = row.getElementsByClass("index-service-day").first().text();
					serviceDayCounter++;
				} else if (row.hasClass("index-service-language-tr")) {
					serviceLanguage = row.getElementsByClass("index-language").first().text();
					e = row.getElementsByTag("a").first();
					serviceUrl = e.attr("href");
					serviceDescription = e.text();
					// ignore PDF files.
					if (serviceDescription.toLowerCase().contains("pdf") 
							|| serviceUrl.startsWith("p")
							) {
						// ignore
					} else {
						Service s = new Service(
								serviceTitle,
								serviceDescription,
								serviceUrl,
								serviceLanguage);
						serviceCounter++;
						theServices.put(pad(serviceDayCounter,serviceCounter), s);
					}
				}
			}
	}
	
	private void print() {
		Iterator<String> it = theServices.keySet().iterator();
		while (it.hasNext()) {
			Service s = theServices.get(it.next());
			System.out.println(s.getTitle() + " - " + s.getLanguage() + " - " + s.getDescription() + " - " + s.getUrl());
		}
	}
	private String pad(int i, int j) {
		return "sd" + String.format("%03d", i) + String.format("%03d", j);
	}
	/**
	 * Add a service to the map of services for this day
	 * @param key - a unique key to preserve the displayed sort order of the services
	 * @param value - the Service to be added
	 */
	public void addService(String key, Service value) {
		theServices.put(key, value);
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}


	public Map<String, Service> getTheServices() {
		return theServices;
	}

	public void setTheServices(Map<String, Service> theServices) {
		this.theServices = theServices;
	}
}
