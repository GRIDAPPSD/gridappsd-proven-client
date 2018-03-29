package gov.pnnl.proven.api.producer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import gov.pnnl.proven.api.exception.NullTermValueException;
import gov.pnnl.proven.message.LiteralTerm;
import gov.pnnl.proven.message.Node;
import gov.pnnl.proven.message.NodeTerm;
import gov.pnnl.proven.message.ProvenanceMessage;
import gov.pnnl.proven.message.Term;
import gov.pnnl.proven.message.TermValueType;

/**
 * @author raju332
 *
 */
public class HarvesterProducer extends Producer{

	public HarvesterProducer() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * Modifies a provenance message's term value. The term is identified by name within the
	 * message. Any matching term will be set to the value provided.
	 * 
	 * @param message
	 *            the provenance message
	 * @param termName
	 *            terms with this name will be modified. A value of null for termName will be
	 *            ignored and result in no update.
	 * @param termValue
	 *            the term value, all values are strings. A null value will be treated as an empty
	 *            string.
	 */
	public void setMessageTermValue(ProvenanceMessage message, String termName, String termValue) {

		if (!(null == termName)) {

			if (null == termValue) {
				termValue = "";
			}
			message.setLiteralTermValue(termName, termValue);
		}
	}
	//create an independent node
		public Node createNode(String nodeName){
			Node addedNode = null;

			try {
			Parser parserObj = new Parser();		
			JsonNode nodesJson = parserObj.getNodes(), jsonNode = null;
				
			for (int nodesList=0; nodesList<nodesJson.size(); nodesList++)
			{
				if(nodesJson.get(nodesList).get("name").asText().equals(nodeName))
				{
					jsonNode = nodesJson.get(nodesList).get("name");
					break;
				}
			}
			
			addedNode = parserObj.loadNodes(jsonNode, null);
							
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return addedNode;
			
		}
		
		//adds the newly created node to a message
		
		public NodeTerm addNodetoMessage(ProvenanceMessage message, String parentNode, String nodeTermName, Node node){
			
			
			List<Node> nodeValueList = new ArrayList<Node>();
			nodeValueList.add(node);
			NodeTerm addednodeTerm = new NodeTerm(nodeTermName, TermValueType.NODE, nodeValueList);
			List<Node> rootNodes = message.getNodes();
			for(Node rootnode : rootNodes)
			{
				List<Term> rootNodeTerms = rootnode.getMessageTerms();
				if(rootnode.getName().equalsIgnoreCase(parentNode))
				{
					rootNodeTerms.add(addednodeTerm);
				}
				for(Term subTerm : rootNodeTerms)
				{

					if(subTerm instanceof NodeTerm)
					{
						NodeTerm subNodeTerm = (NodeTerm) subTerm;
						Node subNode = subNodeTerm.getNodeValues().get(0);
						if(subNode.getName().equalsIgnoreCase(parentNode))
						{										
							subNode.getMessageTerms().add(addednodeTerm);
						}
					}
					
				}

			}
			
			return addednodeTerm;
			
		}
		
		//create a node and adds it to message
		//Can only add nodes to the root node in a message.
		public NodeTerm createNode(ProvenanceMessage message, String nodeTermName, String nodeName)
		{
			Node addedNode = null;
			NodeTerm addednodeTerm = null;
			try {
			Parser parserObj = new Parser();
			JsonNode messagesJson = parserObj.getMessages(), jsonMessage = null;
			JsonNode nodesJson = parserObj.getNodes(), jsonNode = null;
			
			for(int messageList=0; messageList<messagesJson.size(); messageList++)
			{
				if(messagesJson.get(messageList).get("name").asText().equals(message.getName()))
				{
					jsonMessage = messagesJson.get(messageList).get("name");
					break;
				}
								
			}
			
			for (int nodesList=0; nodesList<nodesJson.size(); nodesList++)
			{
				if(nodesJson.get(nodesList).get("name").asText().equals(nodeName))
				{
					jsonNode = nodesJson.get(nodesList).get("name");
					break;
				}
			}
			

				addedNode = parserObj.loadNodes(jsonNode, jsonMessage);
				List<Node> nodeValueList = new ArrayList<Node>();
				nodeValueList.add(addedNode);
				addednodeTerm = new NodeTerm(nodeTermName, TermValueType.NODE, nodeValueList);
				message.getNodes().get(0).getMessageTerms().add(addednodeTerm);
				
				
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return addednodeTerm;
			
				
			
		}
		
		
		
		//checks if the value is set for the newly created node
		boolean isFirstNodeValueSet(NodeTerm addednodeTerm)
		{
			Node nodeName = addednodeTerm.getNodeValues().get(0);
			LiteralTerm msgTerms = (LiteralTerm)nodeName.getMessageTerms().get(0);
			boolean isValueSet = false;
			if(msgTerms.getValue()!= null)
				isValueSet = true;
		
			return isValueSet;
			
		}

		//set values for the newly created node.
		//All the terms in the newly created node have to be literal terms,
		//it doesn't handle node terms within the newly created node
		public void setNodeTermValues(NodeTerm addednodeTerm, Map<String,String> termValueMap) 
		{
			List<Term> msgTerms;
			Node newNodeValue;
			//addednodeTerm.getNodeValues().add(new MessageNode());
			
			Node nodeName = addednodeTerm.getNodeValues().get(0);
			if(isFirstNodeValueSet(addednodeTerm))
			{
				newNodeValue = new Node(nodeName);
				addednodeTerm.getNodeValues().add(newNodeValue);
			}
			else
			{
				newNodeValue = nodeName;			
			}		

			
			msgTerms = newNodeValue.getMessageTerms();
			
			//
			//Check to make sure existing terms are valid
			//
					
			termValueMap.forEach((key,value) -> {
				for (int j=0; j<msgTerms.size(); j++) {
					if (key.equalsIgnoreCase(((LiteralTerm)msgTerms.get(j)).getName()) ) { 
						LiteralTerm lterm = (LiteralTerm) msgTerms.get(j);
						lterm.setValue(termValueMap.get(msgTerms.get(j).getName()));		
	                    break;
					} 
				}

			});

					
			}

		public void sendMessage(ProvenanceMessage message) throws NullTermValueException {

			// Verify all LiteralTerms have non null values, if not then raise exception
			if (!message.allLiteralValuesProvided()) {
				//log.warn("Literal Terms have null values");
				throw new NullTermValueException();
			}
		}
}
