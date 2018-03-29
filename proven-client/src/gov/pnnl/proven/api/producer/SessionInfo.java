package gov.pnnl.proven.api.producer;

import gov.pnnl.proven.message.ProvenMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Information related to a provenance exchange session. This information is initially provided by a
 * provenance server as part of the connect service request.
 * 
 * @author d3j766
 *
 */
@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
public class SessionInfo {

	
	
	private String sessionId;



//	private Date connectedDtm;
//
//	private Date disconnectedDtm;
//
//	private boolean isConnected;
	
	
	
	private Date createdDtm;
	
	private Long messageCount;

	private Long errorCount;
	
	private Map<ProvenMessage, String> errorCache;


	/**
	 * Constructs default session information, representing a disconnected provenance exchange
	 * session.
	 */
	public SessionInfo() {
		this.createdDtm = new Date();
//		this.isConnected = false;
		this.messageCount = 0L;
		this.errorCount = 0L;
		this.errorCache = new HashMap<ProvenMessage, String>();
	}

	/**
	 * Copy constructor
	 */
	public SessionInfo(SessionInfo si) {
		this.sessionId = si.sessionId;
		this.createdDtm = (Date) si.createdDtm.clone();
//		this.connectedDtm = (Date) si.connectedDtm.clone();
//		this.disconnectedDtm = (Date) si.disconnectedDtm.clone();
//		this.isConnected = si.isConnected;
		this.messageCount = new Long(si.messageCount);
		this.errorCount = new Long(si.errorCount);
		this.errorCache = new HashMap<ProvenMessage, String>(si.errorCache);
	}


	public Map<ProvenMessage, String> getErrorCache() {
		return errorCache;
	}
	
	public void setErrorCache(ProvenMessage failedMsg, String failureReason) {
		this.errorCache.put(failedMsg, failureReason);
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getCreatedDtm() {
		return createdDtm;
	}

	public void setCreatedDtm(Date createdDtm) {
		this.createdDtm = createdDtm;
	}

//	public Date getConnectedDtm() {
//		return connectedDtm;
//	}
//
//	public void setConnectedDtm(Date connectedDtm) {
//		this.connectedDtm = connectedDtm;
//	}
//
//	public Date getDisconnectedDtm() {
//		return disconnectedDtm;
//	}
//
//	public void setDisconnectedDtm(Date disconnectedDtm) {
//		this.disconnectedDtm = disconnectedDtm;
//	}
//
//	public boolean isConnected() {
//		return isConnected;
//	}
//
//	public void setConnected(boolean isConnected) {
//		this.isConnected = isConnected;
//	}

	public Long getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(Long messageCount) {
		this.messageCount = messageCount;
	}

	public Long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

}
