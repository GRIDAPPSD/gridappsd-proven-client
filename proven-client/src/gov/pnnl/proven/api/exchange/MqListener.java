/**
 * 
 */
package gov.pnnl.proven.api.exchange;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQBytesMessage;

import gov.pnnl.proven.message.ProvenMessage;

/**
 * @author raju332
 *
 */

public class MqListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		System.out.println(message);

		String messageText = null;

		if (message instanceof ActiveMQBytesMessage) {
			ActiveMQBytesMessage byteMessage = (ActiveMQBytesMessage) message;
			byte[] bytes = byteMessage.getContent().getData();
			String messageString = new String(bytes);
			System.out.println("message = " + messageString);

			//pass to TSDM

		}

	}
}
