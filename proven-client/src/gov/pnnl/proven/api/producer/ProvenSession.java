package gov.pnnl.proven.api.producer;

import java.util.ArrayList;
import java.util.List;

import gov.pnnl.proven.api.exchange.ExchangeInfo;
import gov.pnnl.proven.api.exchange.Exchange;
import gov.pnnl.proven.message.ProvenMessage;


/**
 *
 * Represents a provenance exchange session. Interacts directly with the exchange to add provenance
 * messages.
 * 
 */
class ProvenSession {

	//private ExchangeInfo exchangeInfo;
	private SessionInfo sessionInfo;
	private List<ExchangeInfo> exchanges = new ArrayList<ExchangeInfo>();
	private ProvenInfo provenInfo;

	ProvenSession() {

		//this.exchangeInfo = list;
		this.sessionInfo = new SessionInfo();
		//this.provenInfo = provenanceContext.getProvenInfo();

		//exchanges = provenanceContext.getExchanges();
		
		
		// auto connect to server for exchange information
		/*exchanges = serverInfo.getExchangeServer().getExchanges(serverInfo);

		if (exchanges.isEmpty()) {
			log.error("Provenance session initialization failed.");
		}*/

	}

	SessionInfo getSessionInfo() {
		return sessionInfo;
	}

	ProvenInfo getSessionProvenInfo() {
		return provenInfo;
	}

	void setSessionProvenanceInfo(ProvenInfo provinfo) {
		provenInfo.setContext(provinfo.getContext());
		provenInfo.setDomain(provinfo.getDomain());
		provenInfo.setDomainVersion(provinfo.getDomainVersion());
	}
	
	/*List<ExchangeInfo> getExchangeInfo() {
		return exchanges;
	}*/

	void sendMessage(ProvenMessage message, ExchangeInfo exchangeInfo) {
	
		
		Exchange exchange = exchangeInfo.getExchange();
		exchange.addProvenData(exchangeInfo, message, sessionInfo, provenInfo);
		
		//use Round Robin for load balancing
		//Round robin Implementation
		/*if (message.setNodeIdentifiers()) {
			//use Round Robin for load balancing
			ExchangeInfo exchangeInfo = exchanges.get(roundRobinState);
			Exchange exchange = exchangeInfo.getExchange();
			exchange.addProvenance(exchangeInfo, message, sessionInfo, provenanceInfo);
			if (roundRobinState != exchanges.size()-1)
				roundRobinState++;
			else
				roundRobinState = 0;
			*/
			/*for (ExchangeInfo exchangeInfo : exchanges) {
				Exchange exchange = exchangeInfo.getExchange();
				exchange.addProvenance(exchangeInfo, message, sessionInfo, provenanceInfo);
			}*/


			/*if (exchange.addProvenance(exchangeInfo, message)) {
					ret = true;
					sessionInfo.setMessageCount(sessionInfo.getMessageCount() + 1L);
					break;
				} else {
					sessionInfo.setErrorCount(sessionInfo.getErrorCount() + 1L);
				}*/



		//}

	}
}
