/**
 * 
 */
package gov.pnnl.proven.api.exchange;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.ProvenMessage.MessageTopic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

/**
 * @author raju332
 *
 */
public class MqConsumer {
	
	public MqConsumer(ExchangeInfo exchangeInfo) {
		String simulationId = "1234";
		String topic_simulationOutput = "goss.gridappsd.simulation.output.>";
		
		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		Connection connection;
		try {
			connection = connectionFactory.createConnection("system","manager");
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination topic = session.createTopic(MessageTopic.Response.getTopicName());
			//Destination topic = session.createTopic(topic_simulationOutput+simulationId);
			
			MessageConsumer responseConsumer = session.createConsumer(topic);
			responseConsumer.setMessageListener(new MqListener());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) throws JMSException{
			


		


	        
	        
	        
	}
		
		

}

