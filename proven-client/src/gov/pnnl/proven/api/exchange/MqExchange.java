package gov.pnnl.proven.api.exchange;

import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.api.producer.ProvenInfo;
import gov.pnnl.proven.api.producer.SessionInfo;

import java.util.List;

import javax.jms.JMSException;


/**
 * Provides implementation for a Message Queue (MQ) based Exchange.
 * 
 * @see Exchange
 * 
 * @author d3j766
 *
 */
class MqExchange implements Exchange {


	/**
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override
	public void addProvenData(ExchangeInfo exchangeInfo, ProvenMessage message, SessionInfo sessionInfo, ProvenInfo provenInfo) {
		MqProducer mqProducer = new MqProducer(exchangeInfo);
		try {
			mqProducer.sendMessage(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new UnsupportedOperationException();
		
	}
	public void getProvenResults(ExchangeInfo exchangeInfo) {
		MqConsumer mqConsumer = new MqConsumer(exchangeInfo);	
		
	}

	/**
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override
	public boolean addProvenData(ExchangeInfo exchangeInfo, List<ProvenMessage> messages) {
		throw new UnsupportedOperationException();
	}

	


}
