package net.ages.liturgical.workbench.transformer.services.index.reader;

/**
 * Contains information about a specific service
 * @author mac002
 *
 */
public class Service {
	private String title;
	private String description;
	private String url;
	private String language;
	
	public Service(String title, String description, String url, String language) {
		this.title = title;
		this.description = description;
		this.url = url;
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
