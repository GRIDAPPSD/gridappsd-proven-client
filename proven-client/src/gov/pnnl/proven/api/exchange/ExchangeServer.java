package gov.pnnl.proven.api.exchange;

import java.util.List;

import gov.pnnl.proven.api.producer.ProvenInfo;


/**
 * Provides support for interacting with the provenance server.
 * 
 * @author d3j766
 *
 */
public interface ExchangeServer {

	/**
	 * Requests the provided server for a list of exchanges this client should send their provenance
	 * messages.
	 *  
	 * @param provenanceInfo
	 *            the exchange server information
	 * 
	 * @return a list of exchanges
	 * 
	 * @see ProvenInfo
	 * @see ExchangeInfo
	 * 
	 */
	public List<ExchangeInfo> getExchanges(ProvenInfo provenanceInfo);

}
