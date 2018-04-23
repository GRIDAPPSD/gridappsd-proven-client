package gov.pnnl.proven.api.producer;

import java.util.List;
/**
 * Stores the information related to a ProvenMessage.
 * 
 * 
 */
public class MessageInfo {
	
	private List<String> Keywords;
	private String name;
	private String domain;
	private String source;

	
	public MessageInfo( String domain,  String name,  String source, List<String> keywords) {
		super();
		Keywords = keywords;
		this.name = name;
		this.domain = domain;
		this.source = source;
	}

	public List<String> getKeywords() {
		return Keywords;
	}

	public void setKeywords(List<String> keywords) {
		Keywords = keywords;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}




}
