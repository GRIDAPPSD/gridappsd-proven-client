package gov.pnnl.proven.api.exchange;

import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.ProvenMessageResponse;
import gov.pnnl.proven.api.producer.ProvenResponse;
import gov.pnnl.proven.api.producer.SessionInfo;

import java.util.List;

/**
 * Provides implementation for a file based Exchange.
 * 
 * @see Exchange
 * 
 * 
 * @author d3j766
 *
 */
class FileExchange implements Exchange {



	/**
	 * @return 
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override
	public ProvenResponse addProvenData(ExchangeInfo exchangeInfo, ProvenMessage message, SessionInfo sessionInfo, String requestId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override
	public boolean addProvenData(ExchangeInfo exchangeInfo, List<ProvenMessage> messages) {
		throw new UnsupportedOperationException();
	}


}
