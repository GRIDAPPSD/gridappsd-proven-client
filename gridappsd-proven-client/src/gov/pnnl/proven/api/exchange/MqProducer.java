/**
 * 
 *//*
package gov.pnnl.proven.api.exchange;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.MessageContent;
import gov.pnnl.proven.message.MessageTopic;

*//**
 * @author raju332
 *
 *//*
public class MqProducer {
	
	Session session;
	
	
	public MqProducer(ExchangeInfo exchangeInfo){
		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(exchangeInfo.getServicesUri());
		Connection connection;
		try {
			connection = connectionFactory.createConnection(exchangeInfo.getUserName(), exchangeInfo.getPassword());
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void sendMessage(ProvenMessage provenMessage, String requestId) throws JMSException{
		
		
		Topic topic = null;
		if (provenMessage.getMessageContent().equals(MessageContent.Explicit)) {
			topic = session.createTopic(MessageTopic.Disclosure.getTopicName());
		}
		else if (provenMessage.getMessageContent().equals(MessageContent.Query)) {
			topic = session.createTopic(MessageTopic.Query.getTopicName());
		}
		
		topic = session.createTopic(MessageTopic.Disclosure.getTopicName());
		MessageProducer messageProducer = session.createProducer(topic);
		Message message = session.createObjectMessage(provenMessage);
		if(requestId != null)
			message.setStringProperty("requestId", requestId);
		messageProducer.send(message);
	}
	//Log errors to log mananger
	public void sendLogToGridAPPSD(LogMessage logMessage) throws JMSException{
		Destination topic = session.createTopic("goss.gridappsd.proven.log");
		MessageProducer messageProducer = session.createProducer(topic);
		Message message = session.createObjectMessage(provenMessage);
		messageProducer.send(message);
	}
}
*/