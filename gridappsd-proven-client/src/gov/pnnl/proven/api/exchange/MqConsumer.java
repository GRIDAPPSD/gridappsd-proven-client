/**
 * 
 *//*
package gov.pnnl.proven.api.exchange;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import gov.pnnl.proven.message.MessageTopic;

*//**
 * @author raju332
 *
 *//*
public class MqConsumer {
	
	MessageConsumer responseConsumer;
	Session session;
	Connection connection;
	
	public MqConsumer(ExchangeInfo exchangeInfo, String requestId) {
		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(exchangeInfo.getServicesUri());
		
		try {
			connection = connectionFactory.createConnection(exchangeInfo.getUserName(), exchangeInfo.getPassword());
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination topic = session.createQueue(MessageTopic.Response.getTopicName());
			
			responseConsumer = session.createConsumer(topic);
			responseConsumer.setMessageListener(new MqListener());
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Serializable receive() throws JMSException{
		
		Serializable response = null;
			
		Message message = responseConsumer.receive(5000);

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            response = textMessage.getText();
        }else if (message instanceof ActiveMQBytesMessage) {
			ActiveMQBytesMessage byteMessage = (ActiveMQBytesMessage) message;
			byte[] bytes = byteMessage.getContent().getData();
			response = new String(bytes);
        }
        else {
            response = message.toString();
        }

        responseConsumer.close();
        session.close();
       connection.close();
       
		return response;

	}
	

}

*/