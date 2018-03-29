/**
 * 
 */
package gov.pnnl.proven.api.producer;

import java.util.List;

import gov.pnnl.proven.api.exception.SendMessageException;
import gov.pnnl.proven.api.exchange.ExchangeInfo;
import gov.pnnl.proven.api.exchange.ExchangeType;
import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.exception.InvalidProvenMessageException;

/**
 * @author raju332
 *
 */
public class ProvenProducer extends Producer{


	
	public ProvenProducer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	ExchangeInfo exchangeInfo;
	public void mqProducer(String servicesUri) {
		
		exchangeInfo = new ExchangeInfo(ExchangeType.MQ, servicesUri);
		
	}
	public void restProducer(String servicesUri){
		exchangeInfo = new ExchangeInfo(ExchangeType.REST, servicesUri);
	}

	public void sendMessage(List<String> Keywords, String message) throws InvalidProvenMessageException, SendMessageException {

		
		ProvenMessage pm = ProvenMessage.message(message).keywords(Keywords).build();
		System.out.println("PM Message: " + pm.getMessage());
		
		sendMessage(pm, exchangeInfo);
		
	}
	

		

}
