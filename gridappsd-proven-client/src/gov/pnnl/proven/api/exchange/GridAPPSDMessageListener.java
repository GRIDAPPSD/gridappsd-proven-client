/*package gov.pnnl.proven.api.exchange;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class GridAPPSDMessageListener implements MessageListener{

	public GridAPPSDMessageListener() {
		
	}

	@Override
	public void onMessage(Message message) {
		System.out.println(message);
		
	}
	
	
	public static void main(String[] args) throws JMSException{
		
		GridAPPSDMessageListener listener = new GridAPPSDMessageListener();
		
		//StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61613");
		 
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Topic topic = session.createTopic("goss.gridappsd.proven.input");
        
        MessageConsumer consumer = session.createConsumer(topic);
        
        consumer.setMessageListener(listener);
        
        
        MessageProducer producer = session.createProducer(topic);
        Message msg = session.createTextMessage("testing");

        producer.send(msg);
        
        
	}
}



*/