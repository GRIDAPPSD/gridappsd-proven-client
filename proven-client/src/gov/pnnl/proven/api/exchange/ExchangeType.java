/**
 * 
 */
package gov.pnnl.proven.api.exchange;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Identifies type of exchange by its communication protocol used to consume and produce provenance
 * messages.
 * 
 * @author d3j766
 *
 */
@XmlRootElement
public enum ExchangeType {

	/**
	 * Exchange provides REST services for provenance transport.
	 */
	REST,

	/**
	 * Exchange provides file services for provenance transport.
	 */
	FILE,

	/**
	 * Exchange provides a message queuing service for provenance transport.
	 */
	MQ
}
