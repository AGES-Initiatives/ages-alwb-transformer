package net.ages.liturgical.workbench.transformer.services.index.reader;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.ages.liturgical.workbench.transformer.utils.GeneralUtils;

/**
 * Reads the servicesindex.html file and extracts information from it and
 * the sub-indexes that it points to.
 * 
 * @author mac002
 *
 */
public class HtmlServicesIndexReader {
	private String pathToServicesIndex;
	private String pathToRoot;
	private Document serviceIndexDoc = null;
	private String classIndexMonthTr = "index-month-tr";
	private String classIndexMonth = "index-month";
	private String classIndexDayTr = "index-day-tr";
	private String classIndexDayLink = "index-day-link";
	private String classServicesIndexTable = "services-index-table";
	
	private Map<String, MonthCollection> calendarMap = new TreeMap<String, MonthCollection>();
	private Map<String,Service> servicesMap = new TreeMap<String,Service>();
	
	public HtmlServicesIndexReader(String pathToServicesIndex) {
		this.pathToServicesIndex = pathToServicesIndex;
		this.pathToRoot = GeneralUtils.getParentPath(this.pathToServicesIndex)  + "/";
		loadCalendarMap();
		loadServicesMap();
	}
	
	/**
	 * Reads in the servicesindex.html file and initializes a map that records
	 * the titles of the month, service days, and the href to the index for each
	 * service day.
	 */
	
	private void loadCalendarMap() {
		serviceIndexDoc = getDoc(new File(pathToServicesIndex));
		Element servicesIndexTable = serviceIndexDoc.getElementsByClass(
				classServicesIndexTable).first();
		Elements rows = servicesIndexTable.getElementsByTag("tr");
		int monthCounter = 0;
		String monthKey = null;
		Iterator<Element> it = rows.iterator();
		while (it.hasNext()) {
			Element row = it.next();
			if (row.hasClass(classIndexMonthTr)) {
				String monthTitle = row.getElementsByClass(classIndexMonth)
						.first().text();
				monthCounter++;
				monthKey = padMonth(monthCounter);
				calendarMap.put(monthKey, new MonthCollection(monthTitle));
			} else if (row.hasClass(classIndexDayTr)) {
				Element link = row.getElementsByClass(classIndexDayLink)
						.first();
				String dayKey = link.text();
				String href = link.attr("href");
				MonthCollection m = calendarMap.get(monthKey);
				m.add(dayKey, pathToRoot + href);
				calendarMap.put(monthKey, m);
			}
		}
	}
	
	private void loadServicesMap() {
		Iterator<String> monthIt = calendarMap.keySet().iterator();

		while (monthIt.hasNext()) {
			MonthCollection month = calendarMap.get(monthIt.next());
			Iterator<Entry<String, ServiceDayCollection>> sdcIt = month
					.getEntryIterator();
			while (sdcIt.hasNext()) { // Service Day Colleciton
				Entry<String, ServiceDayCollection> sdeIt = sdcIt.next();
				ServiceDayCollection sdcValue = sdeIt.getValue();
				Iterator<Entry<String, ServiceDay>> sdIt = sdcValue
						.getEntryIterator();
				while (sdIt.hasNext()) { // Service Day
					Entry<String, ServiceDay> sde = sdIt.next();
					ServiceDay sdValue = sde.getValue();
					Iterator<Entry<String, Service>> seIt = sdValue
							.getEntryIterator();
					while (seIt.hasNext()) { // Service
						Entry<String, Service> se = seIt.next();
						Service seValue = se.getValue();
						String url = seValue.getUrl();
						servicesMap.put(pathToRoot + seValue.getUrl(), seValue);
						}
					} // end while ServiceDay has entries
				} // end while ServiceDayCollection has entries
			} // end while Month has entries
	}
	
	/**
	 * Get the service object for this href
	 * @param href
	 * @return
	 */
	public Service getService(String href) {
		return servicesMap.get(href);
	}
	
	private String pad(int i, String prefix) {
		return prefix + String.format("%05d", i);
	}

	private String padMonth(int i) {
		return pad(i, "m");
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
}
