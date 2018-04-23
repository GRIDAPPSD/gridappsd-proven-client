/**
 * 
 *//*
package gov.pnnl.proven.api.exchange;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.pnnl.proven.api.producer.ProvenProducer;
import gov.pnnl.proven.message.ProvenMessage;

*//**
 * @author raju332
 *
 *//*

public class MqListener implements MessageListener {
	private final Logger log = LoggerFactory.getLogger(MqListener.class);

	@Override
	public void onMessage(Message message) {
		System.out.println(message);	
		String provenReponse = null;

		if (message instanceof ActiveMQBytesMessage) {
			ActiveMQBytesMessage byteMessage = (ActiveMQBytesMessage) message;
			byte[] bytes = byteMessage.getContent().getData();
			provenReponse = new String(bytes);
		}
		else if (message instanceof TextMessage) {
	            TextMessage textMessage = (TextMessage) message;
	            try {
					provenReponse = textMessage.getText();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
        else {
        	provenReponse = message.toString();
        }
		System.out.println("Response = " + provenReponse);
		log.info("Response = " + provenReponse);

	}
}
*/