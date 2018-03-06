package net.ages.liturgical.workbench.transformer.alwb.html;

public class AgesKVP {
	private String topic = null;
	private String key = null;
	private String value = null;
	public String getTopic() {
		return topic;
	}
	
	public AgesKVP() {
	
	}
	public AgesKVP(
			String topic
			, String key
			, String value
			) {
		this.topic = topic;
		this.key = key;
		this.value = value;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toLatexAtem(){
		StringBuffer result  = new StringBuffer();
		result.append(q(this.topic));
		result.append("}{");
		result.append(q(this.key));
		return result.toString();
	}
	
	public String toLatexAres(){
		String start = "%<*";
		String end = "\n%</";
		String close = ">\n";
		StringBuffer result  = new StringBuffer();
		result.append(start);
		result.append(q(this.key));
		result.append(close);
		result.append(this.value);
		result.append(end);
		result.append(q(this.key));
		result.append(close);
		return result.toString();
	}

	private String q(String s) {
		return "\"" + s + "\"";
	}
	
}
