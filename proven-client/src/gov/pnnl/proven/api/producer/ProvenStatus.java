package gov.pnnl.proven.api.producer;

import java.util.Date;

/**
 * Provides status information regarding a ProveanceProducer's provenance exchange session.
 * 
 * @author d3j766
 *
 */
public interface ProvenStatus {

	/**
	 * Returns the date time a ProveanceProducer's exchange session was created.
	 */
	Date createdDtm();
	
	/**
	 * Returns the number of messages that failed to be sent to the exchange.
	 */
	Long messageFailCount();

	/**
	 * Returns the total number of messages that were successfully sent to the exchange.
	 */
	Long messageCount();

}
