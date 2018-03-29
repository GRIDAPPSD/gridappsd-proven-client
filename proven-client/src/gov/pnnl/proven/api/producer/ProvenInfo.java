package gov.pnnl.proven.api.producer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import gov.pnnl.proven.api.exchange.ExchangeLocator;
import gov.pnnl.proven.api.exchange.ExchangeServer;
import gov.pnnl.proven.api.exchange.ExchangeType;

/**
 * Stores information describing the ProvEn server associated with the client.
 * 
 * @author d3j766
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class ProvenInfo {

	private String domain;

	private String domainVersion;

	private String context;
	private boolean saveMessagesInFile;

	
	public ProvenInfo(String domain, String domainVersion, String context, boolean saveMessagesInFile) {
		this.domain = domain;
		this.domainVersion = domainVersion;
		this.context = context;
		this.saveMessagesInFile = saveMessagesInFile;
	}

	public ProvenInfo() {
	};

	public ExchangeServer getExchangeServer() {
		return ExchangeLocator.getExchangeServer(ExchangeType.REST);
	}
	
	public boolean isSaveMessagesInFile() {
		return saveMessagesInFile;
	}

	public void setSaveMessagesInFile(boolean saveMessagesInFile) {
		this.saveMessagesInFile = saveMessagesInFile;
	}


	/**
	 * Gets the domain.
	 * 
	 * @see #setDomain(String)
	 * 
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Sets the provenance server's domain.
	 * 
	 * @param domain
	 *            the provenance server's domain for which consumed provenance will be stored in.
	 *            This identifies the provenance server's graph context.
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Gets the version of the domain's provenance context.
	 * 
	 * @see #setDomainVersion(String)
	 * 
	 */
	public String getDomainVersion() {
		return domainVersion;
	}

	/**
	 * Sets the version of the domain's provenance context.
	 * 
	 * @param domainVersion
	 *            the version of the domain's provenance context.
	 * 
	 */
	public void setDomainVersion(String domainVersion) {
		this.domainVersion = domainVersion;
	}

	/**
	 * Gets the services URI
	 * 
	 * @see #setServicesUri(String)
	 * 
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Sets the REST services URI for the server
	 * 
	 * @param servicesUri
	 *            the services URI
	 * 
	 */
	public void setContext(String context) {
		this.context = context;
	}

}