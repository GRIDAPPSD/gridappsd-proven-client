package gov.pnnl.proven.api.producer;

import gov.pnnl.proven.api.exception.ContextFileNotFoundException;
import gov.pnnl.proven.api.exception.ContextLoadException;
import gov.pnnl.proven.api.exception.CreateMessageException;
import gov.pnnl.proven.api.exchange.ExchangeInfo;
import gov.pnnl.proven.api.exchange.ExchangeType;
import gov.pnnl.proven.message.LiteralTerm;
import gov.pnnl.proven.message.Node;
import gov.pnnl.proven.message.Term;
import gov.pnnl.proven.message.NodeTerm;
import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.message.TermValueOrigin;
import gov.pnnl.proven.message.TermValueType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Describes context of provenance production for a ProvenanceProducer. The context includes the
 * available provenance messages and exchange information used with the producer's registered
 * provenance server.
 *
 * @author raju332
 *
 */
@Deprecated
class ProvenContext {

	private final Logger log = LoggerFactory.getLogger(ProvenContext.class);

	/**
	 * The single context file distributed with the API
	 */
	//4-29-2017 "final" removed from CONTEXT_FILE so that it can
	//          either be assigned from the resource directory or 
	//          if user has created context file it will look for
	//          contextfile in user directory.
	static String CONTEXT_FILE =  "context.json";
//////	static String CONTEXT_FILE =  File.separator + "context.json";
//EGS	static final String CONTEXT_FILE =  "\\context.json";
	

	/**
	 * Single ProvenContext instance created from the context file. Shared by all
	 * ProvenanceProducers.
	 */
	static ProvenContext contextInstance = new ProvenContext();

	/**
	 * ProvEn server information
	 */
	private ProvenInfo provenInfo;
	private ExchangeInfo exchangeInfo;

	/**
	 * Provenance messages for the client's domain
	 */
	private List<ProvenMessage> messages;
	
	private List<ExchangeInfo> exchanges = new ArrayList<ExchangeInfo>();
	
	/**
	 * Returns ProvenContext instance shared by all ProvenanceProducers.
	 */
	public static ProvenContext getInstance() {
		return contextInstance;
	}

	/**
	 * Returns provenance context file contents as a stream
	 */
	//4-29-2017 default CONTEXT_FILE will assigned from the resource directory unless 
	//          user has created context file in user directory.	
	
	
	public static InputStream getContextStream() throws ContextLoadException {
        
		try {
			File f = new File(System.getProperty("user.dir") +File.separator+ CONTEXT_FILE);
			if(f.exists() && !f.isDirectory()) { 
				FileInputStream fis = new FileInputStream(f);
				System.out.print("Using " + System.getProperty("user.dir") +File.separator + CONTEXT_FILE + "<---Context file");
				   return (InputStream) fis;
			} else {
				System.out.print("User defined context file not found, using default " +  CONTEXT_FILE + "<---Context file");
                InputStream x = ProvenContext.class.getResourceAsStream("/" + CONTEXT_FILE);
				return x;

			}
			
		} catch (Exception e) {
			throw new ContextLoadException();
		}
	}

	/**
	 * Must use getInstance() to get the ProvenContext instance. This is the private constructor
	 * called at class-load time.
	 * 
	 * @throws ContextFileNotFoundException
	 *             if the context file could not be found on the class path.
	 * @throws ContextLoadException
	 *             if the context file loading failed.
	 */
	private ProvenContext() {

	
		provenInfo = new ProvenInfo();	

		InputStream contextStream = null;
		try {		

			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			JsonNode rootNode;
			contextStream = getContextStream();
			rootNode = mapper.readTree(contextStream);
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
			while (fieldsIterator.hasNext()) {
				Map.Entry<String, JsonNode> field = fieldsIterator.next();
				if (field.getKey().equals("ExchangeInfo")) {
					JsonNode exchangeNodes = field.getValue();
					for(int exchangeList=0; exchangeList<exchangeNodes.size(); exchangeList++)
					{
						exchangeInfo = new ExchangeInfo();
						JsonNode exchangeInfoStr = mapper.readTree(exchangeNodes.get(exchangeList).toString());
						Iterator<Map.Entry<String, JsonNode>> itr = exchangeInfoStr.fields();
						while (itr.hasNext()) {
							Map.Entry<String, JsonNode> entry = itr.next();						
							if (entry.getKey().equals("exchangeType")) {
								String exchangeType = entry.getValue().textValue();
								exchangeInfo.setExchangeType(ExchangeType.valueOf(exchangeType));
							}
							if (entry.getKey().equals("servicesUri")) {
								String servicesUri = entry.getValue().textValue();							
								exchangeInfo.setServicesUri(servicesUri);
							}
							
						}
						exchanges.add(exchangeInfo);
					}
				}
				
				if (field.getKey().equals("ProvenInfo")) {
					JsonNode provenanceInfoStr = mapper.readTree(field.getValue().toString());
					Iterator<Map.Entry<String, JsonNode>> itr = provenanceInfoStr.fields();
					while (itr.hasNext()) {
						Map.Entry<String, JsonNode> entry = itr.next();

						if (entry.getKey().equals("domain")) {
							String domain = entry.getValue().textValue();
							provenInfo.setDomain(domain);
						}
						if (entry.getKey().equals("domainVersion")) {
							String domainVersion = entry.getValue().textValue();
							provenInfo.setDomainVersion(domainVersion);
						}
						if (entry.getKey().equals("saveMessagesInFile")) {
							boolean saveMessagesInFile = Boolean.parseBoolean(entry.getValue().textValue());
							provenInfo.setSaveMessagesInFile(saveMessagesInFile);
						}
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ContextLoadException();
		} finally {			
			if (null != contextStream) {

				try {
					contextStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new ContextLoadException("Failed to close context stream");
				}
			}
		}
	}



	public ExchangeInfo getExchangeInfo() {
		return exchangeInfo;
	}
	public List<ExchangeInfo> getExchanges()
	{
		return exchanges;
	}

	public void setExchangeInfo(ExchangeInfo exchange)
	{
		exchanges.add(exchange);
	}	
	
	public ProvenInfo getProvenInfo() {
		return provenInfo;
	}
	public ProvenInfo setProvenanceInfo(ProvenInfo provinfo) {
		return provenInfo = provinfo;
	}
	
	public List<ProvenMessage> getMessages() {
		return messages;
	}

}
