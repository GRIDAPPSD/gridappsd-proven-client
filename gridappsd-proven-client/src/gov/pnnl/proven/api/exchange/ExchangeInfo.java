package gov.pnnl.proven.api.exchange;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Stores information describing how provenance messages will be exchanged between a
 * ProvenanceProducer and its registered provenance server consumer.
 * 
 * @author d3j766
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeInfo {

	private ExchangeType exchangeType;

	private String servicesUri;
	private String userName;
	private String password;
	
	public ExchangeInfo(ExchangeType exchangeType, String servicesUri, String userName, String password) {
		super();
		this.exchangeType = exchangeType;
		this.servicesUri = servicesUri;
		this.setUserName(userName);
		this.setPassword(password);
	}


	public ExchangeInfo() {
	}
	

	public Exchange getExchange() {
	  return ExchangeLocator.getExchange(getExchangeType());
	}

	public ExchangeType getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(ExchangeType exchangeType) {
		this.exchangeType = exchangeType;
	}

	public String getServicesUri() {
		return servicesUri;
	}

	public void setServicesUri(String servicesUri) {
		this.servicesUri = servicesUri;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


}
