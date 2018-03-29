package gov.pnnl.proven.api.exchange;

import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.api.producer.ProvenInfo;
import gov.pnnl.proven.api.producer.SessionInfo;

import java.util.List;

/**
 * 
 * Provides support for a ProvenanceProducer's interaction with a provenance exchange. The exchange
 * represents a collection area for provenance messages, enabling loosely coupled communication
 * between a provenance producer and it's registered provenance server (i.e. consumer).
 * 
 * @author d3j766
 *
 */
public interface Exchange {

	/**
	 * Adds the provided message to the exchange.
	 * 
	 * @param exchangeInfo
	 *            the exchange's information 
	 * @param message
	 *            the provenance message
	 * @param sessionInfo 
	 * @return true if the message was successfully added, false otherwise
	 */
	public void addProvenData(ExchangeInfo exchangeInfo, ProvenMessage message, SessionInfo sessionInfo, ProvenInfo provenanceInfo);

	/**
	 * Adds provided messages to the exchange.
	 * 
	 * @param exchangeInfo
	 *            the exchange's information
	 * @param messages
	 *            the provenance messages
	 * @return true if the messages were successfully added, false otherwise
	 */
	public boolean addProvenData(ExchangeInfo exchangeInfo, List<ProvenMessage> messages);

}
