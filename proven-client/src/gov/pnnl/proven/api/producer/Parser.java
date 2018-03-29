package gov.pnnl.proven.api.producer;
/**
 * Parses the context file.
 */
import gov.pnnl.proven.message.LiteralTerm;
import gov.pnnl.proven.message.Namespace;
import gov.pnnl.proven.message.Node;
import gov.pnnl.proven.message.NodeIdType;
import gov.pnnl.proven.message.NodeTerm;
import gov.pnnl.proven.message.ProvenanceMessage;
import gov.pnnl.proven.message.ProvenanceMetric;
import gov.pnnl.proven.message.ProvenMetric;
import gov.pnnl.proven.message.Term;
import gov.pnnl.proven.api.exception.ContextLoadException;
import gov.pnnl.proven.message.TermValueOrigin;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
@Deprecated
class Parser {

	private final Logger log = LoggerFactory.getLogger(Parser.class);
	static JsonNode messages = null, nodes = null, terms = null, namespaces = null;
	public JsonNode getMessages() {
		return messages;
	}

	public JsonNode getNodes() {
		return nodes;
	}
	static Namespace[] namespaceObj = null;
	ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	public List<ProvenanceMessage> JacksonParse() throws JsonParseException, JsonMappingException, IOException{
		// TODO Auto-generated method stub

		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);

		ArrayList<ProvenanceMessage> messageObjList = new ArrayList<ProvenanceMessage>();
		log.debug("Parsing context file...");
		InputStream contextStream = null;
		try {
			contextStream = ProvenContext.getContextStream();
			JsonNode rootNode = mapper.readTree(contextStream);
			Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
			while (fieldsIterator.hasNext()) {
				Map.Entry<String,JsonNode> field = fieldsIterator.next();
				JsonNode value = field.getValue();
				String key = field.getKey();					
				if (key.equals("ProvenanceMessages"))
				{			        	  
					messages = value;					
				}
				else if(key.equals("Nodes"))
				{
					nodes = value;

				}
				else if(key.equals("Terms"))
				{
					terms = value;

				}
				else if(key.equals("Namespaces"))
				{
					namespaces = value;					
					namespaceObj = objectMapper.readValue(namespaces.toString(), Namespace[].class);					
				}

				log.info("Key: " + key + "\tValue:" + value);
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error("Exception parsing the Context file.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != contextStream) {
				contextStream.close();
			}
		}

		//Provenance Messages
		for(int messageList=0; messageList<messages.size(); messageList++)
		{
			ProvenanceMessage messageObj = loadMessages(messages.get(messageList));

			messageObjList.add(messageObj);
		}
		return messageObjList;
	}

	ProvenanceMessage loadMessages(JsonNode message) throws JsonParseException, JsonMappingException, IOException
	{

		JsonNode messageName = message.get("name");			
		JsonNode messageNodes = message.get("messageNodes");
		JsonNode keywords = message.get("keywords");
		JsonNode metrics = message.get("hasProvenanceMetric");
		JsonNode messageDescription = message.get("description");
		ArrayList<String> keywordsList = new ArrayList<String>();	
		ArrayList<ProvenanceMetric> metricList = new ArrayList<ProvenanceMetric>();
		String description = null;
		if(messageDescription != null)
		{
			description = messageDescription.textValue();
		}
		if(keywords != null)
		{			
			for(int j=0; j<keywords.size(); j++)
			{			
				keywordsList.add(keywords.get(j).asText());
			}
		}
		/*ArrayList<String> metricList = new ArrayList<String>();
		for(int j=0; j<metrics.size(); j++)
		{			
			metricList.add(metrics.get(j).asText());
		}*/
		if(metrics != null)
		{
			for(int j=0; j<metrics.size(); j++)
			{				
				ProvenanceMetric metricObj = objectMapper.readValue(metrics.get(j).toString(), ProvenanceMetric.class);
				metricList.add(metricObj);
			}
		}

		//List<String> keywords = new ObjectMapper().readValue(message.get("keywords").toString(), List.class);
		ArrayList<Node> nodeObjList = new ArrayList<Node>();
		//nodes
		for(int j=0; j<messageNodes.size(); j++)
		{			
			Node nodeObj = loadNodes(messageNodes.get(j), messageName);	
			nodeObjList.add(nodeObj);
		}			
		String name;		
		ProvenanceMessage messageObj = new ProvenanceMessage(messageName.textValue(),description,keywordsList, metricList, nodeObjList);		
		return messageObj;
	}

	public Node loadNodes(JsonNode messageNode, JsonNode messageName) throws JsonParseException, JsonMappingException, IOException
	{		
		Term namespace = null;
		JsonNode msgNodeName = null;
		JsonNode nodeIdType = null;
		List<Term> nodeTerms = new ArrayList<Term>();
		List<Term> contextTerms = new ArrayList<Term>();		
		Node nodeObj = null;
		for(int k=0; k<nodes.size(); k++)
		{
			JsonNode nodeName = nodes.get(k).get("name");
			JsonNode nodeNamespace = nodes.get(k).get("namespace");			
			JsonNode msgTerms = nodes.get(k).get("messageTerms");
			JsonNode messageTerms = msgTerms.deepCopy();			
			JsonNode idContext = nodes.get(k).get("idContext");
			JsonNode additionalMessageTerms = null;
			if(messageName !=null)
			{
				additionalMessageTerms = nodes.get(k).get(messageName.textValue());
			}
			nodeIdType = nodes.get(k).get("nodeIdtype");
			msgNodeName = nodeName;

			// Add additional terms to message terms, if any
			if (null != additionalMessageTerms) {				
				((ArrayNode)messageTerms).addAll((ArrayNode)additionalMessageTerms);				
			}
			if(nodeName.equals(messageNode)){						

				//namespaces
				for (int namespaceList=0; namespaceList<namespaces.size(); namespaceList++)
				{
					JsonNode namespaceName = namespaces.get(namespaceList).get("name");
					if(namespaceName.equals(nodeNamespace)){						
						namespace = namespaceObj[namespaceList];
					}
				}
				//terms
				for (int termList=0; termList<terms.size(); termList++)
				{	
					JsonNode termName = terms.get(termList).get("name");								
					for(int nodeMessageTerms=0; nodeMessageTerms<messageTerms.size(); nodeMessageTerms++)
					{	

						if(termName.equals(messageTerms.get(nodeMessageTerms))){															
							// the below code can be replaced with get methods like literalObj.getNamespaceTerm() to get values from context file
							JsonNode termType = terms.get(termList).get("type");	
							JsonNode namespaceName = terms.get(termList).get("namespaceTerm");
							//Litreal Term
							if(termType.textValue().equals("LiteralTerm")){								
								LiteralTerm literalObj = objectMapper.readValue(terms.get(termList).toString(), LiteralTerm.class);										
								for(int ns = 0; ns<namespaceObj.length; ns++)
								{
									if(namespaceName.textValue().equals(namespaceObj[ns].getName()))
										literalObj.setNamespace(namespaceObj[ns]);
								}
								// Make sure term value origin and type have been configured properly, otherwise raise exception
								if ((literalObj.getTermValueOrigin().equals(TermValueOrigin.API)) && (!literalObj.getTermValueType().isHasApiProvidedValue())) {
									log.warn("Mismatch between TermValueOrigin and TermValueType for Literal Term: " + literalObj.getName());
									throw new ContextLoadException("Mismatch between TermValueOrigin and TermValueType for Literal Term: " + literalObj.getName());
								}
								nodeTerms.add(literalObj);
							}
							//Node Term
							if(termType.textValue().equals("NodeTerm")){								
								NodeTerm nodeTermObj = objectMapper.readValue(terms.get(termList).toString(), NodeTerm.class);								
								for(int nodeCnt = 0; nodeCnt<nodes.size(); nodeCnt++)
								{									
									if(nodes.get(nodeCnt).get("name").equals(terms.get(termList).get("value")))
									{
										List<Node> nodesList = new ArrayList<Node>();
										nodesList.add(loadNodes(nodes.get(nodeCnt).get("name"), messageName));
										nodeTermObj.setNodeValues(nodesList);
									}
								}																		
								nodeTerms.add(nodeTermObj);
							}
						}
					}
				}

				//idContext										
				for (int termList=0; termList<terms.size(); termList++)
				{	
					JsonNode termName = terms.get(termList).get("name");								
					for(int nodeContextTerms=0; nodeContextTerms<idContext.size(); nodeContextTerms++)
					{													
						if(termName.equals(idContext.get(nodeContextTerms))){							
							JsonNode termType = terms.get(termList).get("type");	
							JsonNode namespaceName = terms.get(termList).get("namespaceTerm");							
							if(termType.textValue().equals("LiteralTerm")){								
								LiteralTerm literalObj = objectMapper.readValue(terms.get(termList).toString(), LiteralTerm.class);										
								for(int ns = 0; ns<namespaceObj.length; ns++)
								{
									if(namespaceName.textValue().equals(namespaceObj[ns].getName()))
										literalObj.setNamespace(namespaceObj[ns]);
								}	

								// Make sure term value origin and type have been configured properly, otherwise raise exception
								if ((literalObj.getTermValueOrigin().equals(TermValueOrigin.API)) && (!literalObj.getTermValueType().isHasApiProvidedValue())) {
									log.warn("Mismatch between TermValueOrigin and TermValueType for Literal Term: " + literalObj.getName());
									throw new ContextLoadException("Mismatch between TermValueOrigin and TermValueType for Literal Term: " + literalObj.getName());
								}
								contextTerms.add(literalObj);
							}
							//Node Term							
							if(termType.textValue().equals("NodeTerm")){								
								NodeTerm nodeTermObj = objectMapper.readValue(terms.get(termList).toString(), NodeTerm.class);								
								for(int nodeCnt = 0; nodeCnt<nodes.size(); nodeCnt++)
								{									
									if(nodes.get(nodeCnt).get("name").equals(terms.get(termList).get("value")))
									{
										List<Node> contextNodesList = new ArrayList<Node>();
										contextNodesList.add(loadNodes(nodes.get(nodeCnt).get("name"), messageName));

										nodeTermObj.setNodeValues(contextNodesList);
									}
								}																		
								contextTerms.add(nodeTermObj);
							}
						}
					}
				}						
				nodeObj = new Node(msgNodeName.textValue(), nodeTerms, contextTerms, NodeIdType.valueOf(nodeIdType.textValue()), namespace);				
			}					
		}
		return nodeObj;
	}
}
