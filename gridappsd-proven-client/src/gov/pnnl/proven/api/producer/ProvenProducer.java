/**
 * 
 */
package gov.pnnl.proven.api.producer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.api.exception.NullExchangeInfoException;
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
	private final Logger log = LoggerFactory.getLogger(ProvenProducer.class);


	
	public ProvenProducer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	ExchangeInfo exchangeInfo;
	private MessageInfo messageInfo;
	public void mqProducer(String servicesUri, String userName, String password) {
		
		exchangeInfo = new ExchangeInfo(ExchangeType.MQ, servicesUri, userName, password);
		
	}
	public void restProducer(String servicesUri,String userName, String password){
		exchangeInfo = new ExchangeInfo(ExchangeType.REST, servicesUri, userName, password);
	}
	
	public void setMessageInfo(String domain, String name, String source, List<String> Keywords){
		messageInfo = new MessageInfo(domain, name,source, Keywords);
	}

	public ProvenResponse sendMessage(String message, String requestId) throws InvalidProvenMessageException, SendMessageException, NullExchangeInfoException {

		ProvenMessage pm;
		if(messageInfo != null) {			
			pm = ProvenMessage.message(message).keywords(messageInfo.getKeywords()).domain(messageInfo.getDomain()).name(messageInfo.getName()).source(messageInfo.getSource()).build();
		}
		else
			pm = ProvenMessage.message(message).build();
		
		//System.out.println("PM Message: " + pm.getMessage() + " requestId: " + requestId);
		
		//log.info(pm.getMessage()+ " requestId: " + requestId);
		return sendMessage(pm, exchangeInfo, requestId);
		
		//MqConsumer consumer = new MqConsumer(exchangeInfo, requestId);
		//return consumer.receive();
		
		//ProvenResponse response = new ProvenResponse();
		//response.data
		
			
		
	}
	
	public void sendHWMessage(String message, String requestId) throws InvalidProvenMessageException, SendMessageException, NullExchangeInfoException {
		
		ProvenMessage pm;
		if(messageInfo != null) {			
			pm = ProvenMessage.message(message).keywords(messageInfo.getKeywords()).domain(messageInfo.getDomain()).name(messageInfo.getName()).source(messageInfo.getSource()).buildHW();
		}
		else
			pm = ProvenMessage.message(message).buildHW();
		//System.out.println("PM Message: " + pm.getMessage() + " requestId: " + requestId);
		//log.info(pm.getMessage()+ " requestId: " + requestId);
		sendMessage(pm, exchangeInfo, requestId);
	}

		

}