package gov.pnnl.proven.harvester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import gov.pnnl.proven.api.consts.hapi.*;
import gov.pnnl.proven.api.exception.InvalidHapiHeadingValueException;
import gov.pnnl.proven.api.exception.NullTermValueException;
import gov.pnnl.proven.api.exchange.ExchangeInfo;
import gov.pnnl.proven.api.exchange.ExchangeType;
import gov.pnnl.proven.message.LiteralTerm;
import gov.pnnl.proven.message.Namespace;
import gov.pnnl.proven.message.Node;
import gov.pnnl.proven.message.NodeIdType;
import gov.pnnl.proven.message.NodeTerm;
import gov.pnnl.proven.message.ProvenanceMessage;
import gov.pnnl.proven.message.ProvenanceMetric;
import gov.pnnl.proven.message.Term;
import gov.pnnl.proven.message.TermValueOrigin;
import gov.pnnl.proven.message.TermValueType;
import gov.pnnl.proven.api.producer.HarvesterProducer;
import gov.pnnl.proven.api.producer.ProvenInfo;

public class Harvester {


	static String [] blockTypes = {"MESSAGE","NAMESPACE","SCHEMA","CONTENT","METRIC","CONSTRAINT"};
	static List<Object> contentHeadings = new ArrayList<Object>();
	static Map<String, File> tempFiles = new HashMap<String,File>();
	static CellProcessor namespaceProcessor[] = null;
	static CellProcessor nodeProcessor[] = null;
	static CellProcessor contentProcessor[] = null;
	static CellProcessor metricProcessor[] = null;
	static Map<String, List<ContentSchema>> schemas = new HashMap<String,List<ContentSchema>>();
	//	static Map<String, CellProcessor> contentProcessors = new HashMap<String,CellProcessor>();
	
	static ProvenanceMessage provenanceMessage = null;
	static List<Namespace> namespaces = null;
    static List<ProvenanceMetric> metrics = null;
	static List<Node> nodes = null;
	
	//Empty node that is defined by parseNodeBlock and reused for adding terms.
	//	static Node blank_node = null;
	static ProvenInfo provinfo = null;
	static ExchangeInfo exchangeinfo = null;

	static String trimStringEnds(String rawString) {
		String updatedString = rawString;
		updatedString = updatedString.replace("\\?", "");
		updatedString = updatedString.replace("\\\"", "");		
		updatedString = updatedString.replace("'", "");	
		updatedString = updatedString.trim();
		return updatedString;
	}

	static List<Object> trimStringEnds(List<Object> rawStrings) {
		List<Object> newStrings = new ArrayList<Object>();
		String updatedString = "";
		for (Object rawString : rawStrings) {
			if (rawString == null) {
				updatedString = "";
			} else {
				updatedString = trimStringEnds(rawString.toString());
			}
			newStrings.add(updatedString);          			
		}

		return newStrings;

	}

	static List<File> getTempFiles(String blockType) {
		List<File>  newList = new ArrayList<File>();
		tempFiles.forEach((k,v)->{
			if (k.contains(blockType)) {
				newList.add(v);
			}
		});
		return newList;
	}

	static  int countColumns(File filename, String delimiter) {
		int result = -1;
		try {
			File file = filename;
			FileReader reader = new FileReader(file);
			BufferedReader bufferedreader = new BufferedReader(reader);
			String line = bufferedreader.readLine();
			if (line != null) {
				String[] tokens = line.split(delimiter);
				result = tokens.length;
			}
			bufferedreader.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
		return result;
	}	

	private static File create_tempFile(String blocktype) {
		File result = null;
		try {
			result = File.createTempFile(System.getProperty("user.dir")+ File.separator +"_" + blocktype, ".csv");
			result.deleteOnExit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private static List<String> parseMicroSyntax (String data) {
		List<String> list = new ArrayList<String>(Arrays.asList(data.split("\\|")));
		return list;
	}

	static Namespace getNamespace(String prefix) {
		Namespace results = null;
		for (Namespace ns : namespaces) {
			if (ns.getName().toLowerCase().contains(prefix)) {
				return ns;
			}
		}

		return results;
	}

	static Boolean isTermKey(String column, ContentSchema termDefinition) {
		if (termDefinition.constraint.equalsIgnoreCase("PRIMARYKEY")) {
			return true;
		}
		return false;
	}

	static Namespace getNamespacePrefix(String name) {
		Namespace results = null;
		for (Namespace ns : namespaces) {
			if (ns.getName().equalsIgnoreCase(name.toLowerCase())) {
				results = ns;
				break;
			}
		}
		if (results == null) {
			results = getDefaultNamespace();
		}

		return results;
	}

	static Namespace getDefaultNamespace() {
		Namespace results = null;
		for (Namespace ns : namespaces) {
			if (ns.getName().equalsIgnoreCase("proven")) {
				return ns;
			}
		}

		return results;
	}
	static Term assembleTerm (ContentSchema termDefinition) {
		Term term = null;
		//	ContentSchema termDefinition = getContentSchemaTerm(contentHeadings.get(column).toString(),schema);
		Map<String,String> headerDict = contentsDictionary(termDefinition);
		Namespace ns = getNamespacePrefix(headerDict.get("NAMESPACE"));
		if (ns == null) {
			ns = getDefaultNamespace();
		}

		if (headerDict.get("TYPE").equalsIgnoreCase("NODE")) {
			term = new NodeTerm(headerDict.get("TERMNAME"), TermValueType.NODE,null,ns);
		} else {
			term = new LiteralTerm(headerDict.get("TERMNAME"), TermValueType.valueOf(headerDict.get("TYPE")), TermValueOrigin.USER,null, ns) ;
		}

		return term;

	}
	static String getValueType(String valuetype) {
		if (!valuetype.contains("STRING") &&
				!valuetype.contains("DATE_TIME") &&
				!valuetype.contains("LONG")      && 
				!valuetype.contains("INTEGER")   &&
				!valuetype.contains("BOOLEAN")   &&
				!valuetype.contains("FLOAT")     &&
				!valuetype.contains("DOUBLE")    &&
				!valuetype.contains("FILE")      &&
				!valuetype.contains("NODE") ) {
			valuetype = "STRING";	
		}
		return valuetype;
	}

	static Map<String,String> extractTermAndPrefix(String name, Map<String,String> map) {
		if (name.contains(MicroSyntax.NAMESPACE_DELIMITER.getValue())) {
			String[] nametokens = name.split(MicroSyntax.NAMESPACE_DELIMITER.getValue());
			map.put("TERMNAME", nametokens[1]);
			map.put("NAMESPACE", nametokens[0]);
		} else {
			map.put("TERMNAME", name);
		}
		return map;
	}

	static Map<String,String> contentsDictionary (ContentSchema columnDefinition) {
		//		String[] headingTokens = headingName.split("\\|");
		Map<String,String> map = new HashMap<String,String>();
		map.put("KEY","FALSE");
		map.put("TERMNAME", "");
		map.put("TYPE", "STRING");
		map.put("NAMESPACE", "proven");
		//		
		//		 Simple term name
		//

		String[] termtokens = columnDefinition.column.split(MicroSyntax.NAMESPACE_DELIMITER.getValue());
		if (termtokens.length == 2) {
			map.put("NAMESPACE",termtokens[0]); 
			map.put("TERMNAME",termtokens[1]);
		} else {
			map.put("NAMESPACE","proven"); 
			map.put("TERMNAME",termtokens[0]);
		}		

		if (columnDefinition.datatype.length() > 0) {
			map.put("TYPE", columnDefinition.datatype);
		} else {
			map.put("TYPE", "STRING");
		}

		if (columnDefinition.constraint.equalsIgnoreCase("primaryKey")) {
			map.put("KEY","TRUE");			
		}

		if (columnDefinition.constraint.equalsIgnoreCase("referenceNode")) {
			map.put("TYPE", "NODE");
		}		

		return map;

	}

	static String assembleSchemaTableKey(String nodeName,File schemaTableFile) {
		String key = nodeName;
		InputStream fis = null;
		try {
			fis = new FileInputStream(schemaTableFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		String line = new String();
		line = "";
		int index = 1;
		try {
			while ((line = br.readLine()) != null) {
				if (index >= 2) {
					String[] tokens = line.toString().split(GeneralSyntax.DELIMITER.getValue());
					String buff = trimStringEnds(tokens[0]);
					key = key + buff;
				}
				index = index + 1;
			}
			isr.close();
			br.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}

	static String assembleContentKey(String nodeName,File contentTableFile) {
		String key = nodeName;
		InputStream fis = null;
		try {
			fis = new FileInputStream(contentTableFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		// Test to see if content is table
		// If content is table then the first row will form a key
		// that matches one schema key.
		//
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		String line = new String();
		line = null;
		try {
			line = br.readLine();
			if (line != null) {
				if (!line.contains(MicroSyntax.MICRO_ASSIGNMENT_OPERATOR.getValue())) {
					key = key + trimStringEnds(line.replaceAll(GeneralSyntax.DELIMITER.getValue(), ""));	
				}
			}
			isr.close();
			br.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		// Perform content table test
		//		
		List<ContentSchema> test = getContentSchema(key);
		if (test != null) {
			return key;			
		}

		//
		// Content must be parameters
		//
		try {
			fis = new FileInputStream(contentTableFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		br = new BufferedReader(isr);
		line = new String();
		key = nodeName;
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(MicroSyntax.MICRO_ASSIGNMENT_OPERATOR.getValue());
				if (tokens.length <= 2) {
					key = key + trimStringEnds(tokens[0]);
				}
			}
			//if (line != null) {
			//  key = key + trimStringEnds(line.replaceAll(GeneralSyntax.DELIMITER.getValue(), ""));	
			//}
			isr.close();
			br.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//
		// Perform content table test
		//		
		test = getContentSchema(key);
		if (test != null) {
			return key;			
		} else {
			throw new NullPointerException("Content Key does not map any schemas");
		}
	}


	static Node assembleNodeTermKey (Map<String,String> keyval_pairs, Node linknode) {
		List<Term> identifiers = new ArrayList<Term>();
		LiteralTerm term;
		for (Map.Entry<String, String> entry : keyval_pairs.entrySet()) {
			String[] tokens = entry.getKey().split(MicroSyntax.NAMESPACE_DELIMITER.getValue());
			if (tokens.length == 2) {
				term = new LiteralTerm(tokens[1],TermValueType.STRING,TermValueOrigin.USER,entry.setValue(entry.getValue()),getNamespace(tokens[0]));
			} else {
				term = new LiteralTerm(tokens[0],TermValueType.STRING,TermValueOrigin.USER,entry.setValue(entry.getValue()),getDefaultNamespace());
			}
			identifiers.add(term);
		}
		linknode.setIdContext(identifiers);
		linknode.setMessageTerms(identifiers);
		return linknode; 

	}

	static Node assembleNodeTermValue (String value, ContentSchema termDefinition) {
		//
		// Find target node
		//
		Node linknode = new Node();
		linknode.setNodeIdType(NodeIdType.LOCAL);

		// Initialize variables for processing key/values
		Map<String,String> keyval_pairs = new HashMap<String,String>();
		//
		// Process context identifiers
		//
		String[] tokens = value.split(MicroSyntax.MICRO_DELIMITER.getValue());


		//
		// Split node from terms
		//
		for (int i = 0; i < tokens.length ; i++) {


			//
			// Split main parts of node term value
			//
			if (i == 0) {
				//
				//Check for namespace if it doesn't exist, use default namespace and set name
				//
				String[] nameTokens = tokens[0].split(MicroSyntax.NAMESPACE_DELIMITER.getValue());

				if (nameTokens.length == 2) {
					linknode.setNamespace(getNamespace(nameTokens[0]));
					linknode.setName(nameTokens[1]);
				}	else {
					linknode.setNamespace(getDefaultNamespace());		
					linknode.setName(nameTokens[0]);
				}
				//
				//  Find specified keys and values so that the correct target Node is linked
				//	
			} else if (i >= 1)  {


				String[] kvtokens = tokens[i].split(MicroSyntax.MICRO_ASSIGNMENT_OPERATOR.getValue());
				//				if (kvtokens[0].toUpperCase().equals(MicroSyntax.LINK_KEYNAME.getValue())) {
				//					keylabel = kvtokens[1];
				//				} else {
				keyval_pairs.put(kvtokens[0], kvtokens[1]);
				//				}

			}


		}

		//
		// Take the extracted key/values and assemble them as context identifiers in the node.
		//
		linknode = assembleNodeTermKey(keyval_pairs,linknode);


		return linknode;
	}

	static String readFile(String path) 
			throws IOException
	{ 

		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded,StandardCharsets.UTF_8);
	}

	static Term populateTerm (String value, ContentSchema termDefinition) {
		Term term = assembleTerm(termDefinition);
		if (term instanceof LiteralTerm) {
			Map<String,String> headerDict = contentsDictionary(termDefinition);
			String datatype = headerDict.get("TYPE");
			if (datatype.equalsIgnoreCase("FILE")) {
				String newValue = "";
				try {
					newValue = readFile(value);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((LiteralTerm)term).setValue(newValue);
				term.setTermValueType(TermValueType.STRING);
			} else {
				((LiteralTerm)term).setValue(value);
			}
		} else if (term instanceof NodeTerm) {
			Node node = assembleNodeTermValue(value,termDefinition);
			List<Node> nodes = new ArrayList<Node>();
			nodes.add(node);
			((NodeTerm)term).setNodeValues(nodes);
		}
		//		Node needs to add new terms
		return term;


	}




	/**
	 * Uses message temporary file 
	 * @param block
	 * @throws  
	 */
	//
	// TODO Throw exception!
	//
	private static void parseMessageBlock() throws InvalidHapiHeadingValueException {
		provenanceMessage = new ProvenanceMessage();
		provinfo = new ProvenInfo();
		File mf = getTempFiles(blockTypes[0]).get(0);
		InputStream fis = null;
		try {
			fis = new FileInputStream(mf);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		String line = new String();
		line = "";
        provinfo.setContext("proven_default");
        provinfo.setDomain("default_harvester");
        provinfo.setDomainVersion("0.0");
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("=");
				if (tokens.length == 2) {
					if (tokens[0].equalsIgnoreCase(MessageHeadings.NAME.getValue())) {
						provenanceMessage.setName(trimStringEnds(tokens[1]));
					} else if (tokens[0].equalsIgnoreCase((MessageHeadings.DESCRIPTION.getValue()))) {
						provenanceMessage.setDescription(trimStringEnds(tokens[1]));								
					} else if (tokens[0].equalsIgnoreCase(MessageHeadings.KEYWORDS.getValue())) {
						provenanceMessage.setKeywords(parseMicroSyntax(trimStringEnds(tokens[1])));
					} else if (tokens[0].toUpperCase().equalsIgnoreCase(MessageHeadings.PRODUCER.getValue())) {
						provinfo.setDomain(trimStringEnds(tokens[1]));
					} else if (tokens[0].equalsIgnoreCase(MessageHeadings.PRODUCER_VERSION.getValue())) {
						provinfo.setDomainVersion(trimStringEnds(tokens[1]));
					} else if (tokens[0].equalsIgnoreCase(MessageHeadings.DOMAIN.getValue())) {
						provinfo.setContext(tokens[1]);
					}
				}
			}
			//
			//TODO Set defaults in Message Headings
			//
			//  The purpose of this code is to fill in any gaps that the provenance discloser migth have missed.
			//
			if (provenanceMessage.getName().length() == 0) {
				provenanceMessage.setName("NullProvEnMessage");
			}
			if (provinfo.getContext() == null) {
				provinfo.setContext("ProvEnDefault");
			} 

			if (provinfo.getDomain() == null) {
				provinfo.setDomain("Harvester");
			}
			if (provinfo.getDomainVersion() == null) {
				provinfo.setDomain("0.0");
			}			


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static ICsvListReader getDefaultNamespacesResource() {
		ICsvListReader defaultReader = null;  

//	    InputStreamReader reader = new InputStreamReader(ClassLoader.class.getResourceAsStream("/default_namespaces.csv"));

	    java.io.InputStream io = ClassLoader.class.getResourceAsStream("/default_namespaces.csv");
	    java.io.Reader featIO = new InputStreamReader(io);	    
	    
//		String x = ClassLoader.getSystemResource("default_namespaces.csv").getFile();
		
		//			defaultReader = new CsvListReader(new FileReader(x), CsvPreference.STANDARD_PREFERENCE);
		defaultReader = new CsvListReader(featIO, CsvPreference.STANDARD_PREFERENCE);
		return defaultReader;
	}

	private static void parseNamespaceBlock(Boolean found) throws IOException {
		ICsvListReader listReader = null;
		namespaces = new ArrayList<Namespace>();

		try {
			if (found) {
				listReader = new CsvListReader(new FileReader(getTempFiles(blockTypes[1]).get(0)), CsvPreference.STANDARD_PREFERENCE);
			} else {
				listReader = getDefaultNamespacesResource();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int numColumns = 0;
		if (found) {
			numColumns = countColumns((getTempFiles(blockTypes[1]).get(0)),  Character.toString ((char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar()));
		} else {
//			String x = ClassLoader.getSystemResource("default_namespaces.csv").getFile();
//			numColumns = countColumns(new File(x),  Character.toString ((char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar()));
			numColumns = 2;

		}
		if (numColumns == NamespaceHeadings.values().length) {
			namespaceProcessor = new CellProcessor[numColumns];			
			List<Object> recordList;
			List<Object> headings = null;

			while (( recordList = listReader.read(namespaceProcessor)) != null) {

				if (listReader.getLineNumber() == 1) {
					headings = trimStringEnds(recordList);
				} else {
					String prefix = null;
					String name = null;
					for (int i = 0; i < recordList.size(); i++) {

						if (headings.get(i).toString().contains(NamespaceHeadings.PREFIX.getValue())) {
							prefix = trimStringEnds((String)recordList.get(i));
						} else if (headings.get(i).toString().contains(NamespaceHeadings.NAME.getValue())) {
							name = trimStringEnds((String)recordList.get(i));
						}
					}
					if ((prefix != null) && (name != null)) {
						Namespace ns = new Namespace(prefix, TermValueType.NAMESPACE, name);
						namespaces.add(ns);
					}
				}

			}

		}
		listReader.close();
	}


	private static void parseMetricBlock() throws IOException {
		ICsvListReader listReader = null;
		metrics = new ArrayList<ProvenanceMetric>();
        String measurementName = extractNodename(getTempFiles(blockTypes[4]).get(0));
        File metricTable = create_tempFile("mt_");
        extractMetricTable(getTempFiles(blockTypes[4]).get(0),metricTable);
		try {
			listReader = new CsvListReader(new FileReader(metricTable), CsvPreference.STANDARD_PREFERENCE);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int numColumns = 2;
		if (numColumns == MetricHeadings.values().length) {
			metricProcessor = new CellProcessor[numColumns];			
			List<Object> recordList;
			List<Object> headings = null;

			while (( recordList = listReader.read(metricProcessor)) != null) {

				if (listReader.getLineNumber() == 1) {
					   headings = trimStringEnds(recordList);
				} else {
					String metric = null;
					String ismetadata = "False";
					boolean mdflag = false;
					metric = trimStringEnds((String)recordList.get(0));
					ismetadata = trimStringEnds((String)recordList.get(1));
					mdflag = false;
					if (ismetadata.equalsIgnoreCase("True")) {
						mdflag = true;
					}
					if  ((metric != null) && (ismetadata != null)) {
						ProvenanceMetric pm = new ProvenanceMetric(measurementName, metric,mdflag);
						metrics.add(pm);
					}
				}

			}

		}
		listReader.close();
	}

	private static void parseConstraintsBlock() throws IOException {
//		ICsvListReader listReader = null;
//		namespaces = new ArrayList<Namespace>();
//
//		try {
//			if (found) {
//				listReader = new CsvListReader(new FileReader(getTempFiles(blockTypes[1]).get(0)), CsvPreference.STANDARD_PREFERENCE);
//			} else {
//				listReader = getDefaultNamespacesResource();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		int numColumns = 0;
//		if (found) {
//			numColumns = countColumns((getTempFiles(blockTypes[1]).get(0)),  Character.toString ((char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar()));
//		} else {
//			numColumns = 2;
//
//		}
//		if (numColumns == NamespaceHeadings.values().length) {
//			namespaceProcessor = new CellProcessor[numColumns];			
//			List<Object> recordList;
//			List<Object> headings = null;
//
//			while (( recordList = listReader.read(namespaceProcessor)) != null) {
//
//				if (listReader.getLineNumber() == 1) {
//					headings = trimStringEnds(recordList);
//				} else {
//					String prefix = null;
//					String name = null;
//					for (int i = 0; i < recordList.size(); i++) {
//
//						if (headings.get(i).toString().contains(NamespaceHeadings.PREFIX.getValue())) {
//							prefix = trimStringEnds((String)recordList.get(i));
//						} else if (headings.get(i).toString().contains(NamespaceHeadings.NAME.getValue())) {
//							name = trimStringEnds((String)recordList.get(i));
//						}
//					}
//					if ((prefix != null) && (name != null)) {
//						Namespace ns = new Namespace(prefix, TermValueType.NAMESPACE, name);
//						namespaces.add(ns);
//					}
//				}
//
//			}
//
//		}
//		listReader.close();
	}

	static ContentSchema getContentSchemaTerm (String columnName, List<ContentSchema> schema) {
		ContentSchema term = null;
		for (ContentSchema item : schema) {
			if (item.column.equalsIgnoreCase(columnName)) {
				term = item;
			}
		}
		return term;
	}

	static List<ContentSchema> getContentSchema(String key) {
		List<ContentSchema> contentSchema = null;
		if (schemas.containsKey(key)) {
			contentSchema = schemas.get(key);
		} 		
		return contentSchema;
	}

	private static void parseContentBlocks() {
		for (int i = 0; i < getTempFiles(blockTypes[3]).size(); i++) {
			parseContentBlock(i);
		}
	}
	private static void parseContentBlock(int index) {



		//
		// Get node name and create new temporary file containing only the CSV records
		//
		File contentsTableFile = create_tempFile("__" + blockTypes[3]);
		String nodeName = extractContentTable(getTempFiles(blockTypes[3]).get(index),contentsTableFile);
		String schemaKey = assembleContentKey(nodeName,contentsTableFile);
		List<ContentSchema> schema = getContentSchema(schemaKey); 

		ContentSchema item = schema.get(0);
		if (item.dataformat.equalsIgnoreCase(SchemaHeadings._PARAMETERS.getValue())) {
			parseContentParametersBlock(contentsTableFile,schema);
		} else if (item.dataformat.equalsIgnoreCase(SchemaHeadings._TABLE.getValue())) {
			parseContentTableBlock(contentsTableFile,schema);    		
		}

	}

	private static void parseContentParametersBlock(File contentsFile,List<ContentSchema> schema) {


		//
		// Get node name and create new temporary file containing only the CSV records
		//

		FileReader fis = null;
		try {
			fis = new FileReader(contentsFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedReader br = new BufferedReader(fis);
		String line = new String();
		String emptyBuff = new String("");
		contentHeadings.clear();
		List<Object> recordList = new ArrayList<Object>();
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(MicroSyntax.MICRO_ASSIGNMENT_OPERATOR.getValue());
				if (tokens.length <= 2) {
					contentHeadings.add(trimStringEnds(tokens[0]));
					if (tokens.length == 2) {
						recordList.add(tokens[1]);
					} else if (tokens.length == 1) {
						emptyBuff = new String();
						recordList.add(emptyBuff);
					}
				}
			}
			Node node = populateNode(trimStringEnds(recordList),schema);
			nodes.add(node);
			br.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static int containsFileTypeContent(List<ContentSchema> schema) {
		int results = -1;
		int index = 0;
		for (ContentSchema item : schema) {
			if (item.datatype.equalsIgnoreCase(ContentElements.FILE.getValue())) {
				results = index;
				break;
			}
			index = index + 1;
		}
		return results;
	}

	public static String zero_pad_bin_char(String bin_char){
	    int len = bin_char.length();
	    if(len == 8) return bin_char;
	    String zero_pad = "0";
	    for(int i=1;i<8-len;i++) zero_pad = zero_pad + "0";
	    return zero_pad + bin_char;
	}
	
	public static String hex_to_binary(String hex) {
	    String hex_char,bin_char,binary;
	    binary = "";
	    int len = hex.length()/2;
	    for(int i=0;i<len;i++){
	        hex_char = hex.substring(2*i,2*i+2);
	        int conv_int = Integer.parseInt(hex_char,16);
	        bin_char = Integer.toBinaryString(conv_int);
	        bin_char = zero_pad_bin_char(bin_char);
	        if(i==0) binary = bin_char;
	        else binary = binary+bin_char;
	        //out.printf("%s %s\n", hex_char,bin_char);
	    }
	    return binary;
	}
	public static String hex_to_plaintext(String hex) {
	    String hex_char;
	    StringBuilder plaintext = new StringBuilder();
	    char pt_char;
	    int len = hex.length()/2;
	    for(int i=0;i<len;i++){
	        hex_char = hex.substring(2*i,2*i+2);
	        pt_char = (char)Integer.parseInt(hex_char,16);
	        plaintext.append(pt_char);
	        //out.printf("%s %s\n", hex_char,bin_char);
	    }
	    return plaintext.toString();
	}
	
	private static File substituteContentLobs(File contentsFile, List<ContentSchema> schema) {
		int lobLocation = containsFileTypeContent(schema);
		File newContentFile = null;
		if (lobLocation != -1) {
			FileReader fis = null;
			try {
				fis = new FileReader(contentsFile);
				newContentFile = create_tempFile("_CONTENT_");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            FileWriter newContentWriter = null;
			try {
				newContentWriter = new FileWriter(newContentFile);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fis);
			String line = new String();
			String newline = new String();
			contentHeadings.clear();
			int recordIndex = 0;
			try {
				while ((line = br.readLine()) != null) {
					File lobFile = create_tempFile("_LOB_");
					int index = 0;
					if (recordIndex == 0) {
						newContentWriter.write(line + "\n");
					} else {
						int currPos  = 0;
						while (index < lobLocation) {
							currPos = line.indexOf(GeneralSyntax.DELIMITER.getValue()) + 1;  
							String token = line.substring(0, currPos -1);
							if (newline.length() == 0) {
								newline = token;
							} else {	
								newline = newline + GeneralSyntax.DELIMITER.getValue() + token;
							}
							line = line.substring(currPos);
							index = index + 1;   
						}
						newline = newline +  GeneralSyntax.DELIMITER.getValue() + lobFile.getAbsolutePath();
						newContentWriter.write(newline + "\n");
						try {
							FileWriter lfw = new FileWriter(lobFile);
							lfw.write(line.toString());
							lfw.close();
							
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						
					}
					recordIndex = recordIndex + 1;
                    newline = "";
                    line = "";

				}


				br.close();
				fis.close();
				newContentWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return newContentFile;

	}

	private static void parseContentTableBlock(File contentsFile,List<ContentSchema> schema) {
		ICsvListReader listReader = null;
		File contentsFile1 = null;

		//
		// Get node name and create new temporary file containing only the CSV records
		//

		try {
			//
			// If file content was harvested, put the content in a temporary file
			// replace the content with its temporary file name in the content block
			//
			contentsFile1 = substituteContentLobs(contentsFile,schema);
			if (contentsFile1 != null) {
				Files.copy(Paths.get(contentsFile1.getAbsolutePath()), Paths.get(contentsFile.getAbsolutePath()),StandardCopyOption.REPLACE_EXISTING);
			}
						
			listReader = new CsvListReader(new FileReader(contentsFile), CsvPreference.STANDARD_PREFERENCE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numColumns = countColumns(contentsFile,  Character.toString ((char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar()));
		if (numColumns > 0) {
			CellProcessor contentProcessor[] = new CellProcessor[numColumns];
			List<Object> recordList;
			try {
				try {
					while (( recordList = listReader.read(contentProcessor)) != null) {
						if (listReader.getLineNumber() == 1) {
							contentHeadings = trimStringEnds(recordList);
						} else {
							Node node = populateNode(trimStringEnds(recordList),schema);
							nodes.add(node);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} finally {
				if (listReader != null) {
					try {
						listReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}




	private static void parseSchemaBlocks() {

		for (int i = 0; i < getTempFiles(blockTypes[2]).size(); i++) {
			parseSchemaBlock(i);
		}

	}

	private static void parseSchemaBlock(int index) {
		ICsvListReader listReader = null;
		//
		//TODO FIX, will not create nodes here
		//
		nodes = new ArrayList<Node>();
		//

		//
		// Get node name and create new temporary file containing only the CSV records
		//
		File schemaTableFile = create_tempFile("__" + blockTypes[2]);
		String nodeName = extractSchemaTable(getTempFiles(blockTypes[2]).get(index),schemaTableFile);
		String format = getSchemaFormat(getTempFiles(blockTypes[2]).get(index),schemaTableFile);
		String schemaKey = assembleSchemaTableKey(nodeName,schemaTableFile);
		try {
			//OLD REPLACING			listReader = new CsvListReader(new FileReader(getTempFiles(blockTypes[2]).get(0)), CsvPreference.STANDARD_PREFERENCE);
			listReader = new CsvListReader(new FileReader(schemaTableFile), CsvPreference.STANDARD_PREFERENCE);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<ContentSchema> csList = new ArrayList<ContentSchema>();
		int numColumns = countColumns(schemaTableFile,  Character.toString ((char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar()));
		if (numColumns == NodeHeadings.values().length) {
			CellProcessor schemaProcessor[] = new CellProcessor[numColumns];
			List<Object> recordList;
			List<Object> headings = null;
			try {
				while (( recordList = listReader.read(schemaProcessor)) != null) {
					if (listReader.getLineNumber() == 1) {
						headings = trimStringEnds(recordList);
						//
						// Retrieve Schema block after the format type is specified.
						//
					} else {
						ContentSchema cs = new ContentSchema();
						cs.node = nodeName;
						for (int i = 0; i < recordList.size(); i++) {
							String cellBuff = "";
							if (recordList.get(i) != null) {
								cellBuff = trimStringEnds((String)recordList.get(i));
							}
							if (headings.get(i).toString().equalsIgnoreCase(SchemaHeadings.FIELD.getValue())) {
								cs.column= cellBuff;
							} else if (headings.get(i).toString().equalsIgnoreCase(SchemaHeadings.DATATYPE.getValue())) {
								cs.datatype = cellBuff;
							} else if (headings.get(i).toString().equalsIgnoreCase(SchemaHeadings.CONSTRAINT.getValue())) {
								cs.constraint = cellBuff;
							} 
							cs.dataformat = format;
						}
						csList.add(cs);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		schemas.put(schemaKey,csList);
	}

	//
	// Extract Nodename or Measurement name from Content, Schema, or Metrics Blocks
	//
	private static String extractNodename(File inputBlock) {
		String nodeName = null;
		try {
			InputStream fis = new FileInputStream(inputBlock.getAbsoluteFile());
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			String line = new String();
			line = "";
			line = br.readLine();
				String[] tokens = line.split("=");
					if (tokens.length == 2) {
						nodeName = tokens[1];
					}
			isr.close();
			fis.close();

		} catch (IOException e) {

		}
		return nodeName;
	}		
	
	
	//
	// Extracts Table from Content or Schema Blocks
	//
	private static String extractMetricTable(File inputBlock, File metricFile) {
		String nodeName = extractNodename(inputBlock);
		try {
			InputStream fis = new FileInputStream(inputBlock.getAbsoluteFile());
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			String line = new String();
			line = "";
			FileWriter fos = new FileWriter(metricFile);
			BufferedWriter bw = new BufferedWriter(fos);
			int index = 1;
			while ((line = br.readLine()) != null) {
				if (index > 1) {
					bw.write(line + "\r\n");
				}
				index = index + 1;
			}

			bw.close();
			fos.close();
			isr.close();
			fis.close();

		} catch (IOException e) {

		}
		return nodeName;
	}	
	//
	// Extracts Table from Content or Schema Blocks
	//
	private static String extractSchemaTable(File inputBlock, File tableFile) {
		String nodeName = extractNodename(inputBlock);;
		try {
			// InputStream fis = new FileInputStream(System.getProperty("user.dir")+ File.separator
			// + filename);
			InputStream fis = new FileInputStream(inputBlock.getAbsoluteFile());
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			FileWriter fos = new FileWriter(tableFile);
			BufferedWriter bw = new BufferedWriter(fos);
			
			int index = 1;
			while ((line = br.readLine()) != null) {
				if (index > 2) {
					bw.write(line + "\r\n");
				}
				index = index + 1;
			}

			bw.close();
			fos.close();
			isr.close();
			fis.close();

		} catch (IOException e) {

		}
		return nodeName;
	}		
	//
	// Extracts Table from Content or Schema Blocks
	//
	private static String getSchemaFormat(File inputBlock, File tableFile) {
		String format = null;
		try {
			InputStream fis = new FileInputStream(inputBlock.getAbsoluteFile());
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			String line = new String();
			line = "";
			int index = 1;
			while ((line = br.readLine()) != null) {
				if (index == 2) {
					String[] tokens = line.split("=");
					if (tokens.length == 2) {
						format = tokens[1];
					}
					break;
				}
				index = index + 1;
			}

			isr.close();
			fis.close();

		} catch (IOException e) {

		}
		return format;
	}		
	/** 
	 * Retrieves content from user specified csv file and writes block to temporary file.
	 * @param filename
	 * @param blocktype
	 */
	private static Boolean retrieveBlock(String filename, String blockType) {
		String blockPrefix = "_BEGIN_";
		String blockEnd = "_END";
		Boolean foundBlock = false;
		String blockName = blockPrefix + blockType.toUpperCase();
		try {
			// InputStream fis = new FileInputStream(System.getProperty("user.dir")+ File.separator
			// + filename);
			InputStream fis = new FileInputStream(filename);
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			String line = new String();
			line = "";
			File tmpFile = null;
			BufferedWriter bw = null;
			FileWriter fos = null;
			Boolean found = false;
			while ((line = br.readLine()) != null) {
				if (line.contains(blockName)) {
					found = true;
					foundBlock = found;
					tmpFile = create_tempFile(blockType);
					tempFiles.put(tmpFile.getName(), tmpFile);
					fos = new FileWriter(tmpFile);
					bw = new BufferedWriter(fos);
				}
				if (found && !line.contains(blockName)) {
					if (!line.contains(blockEnd) && !line.contains(blockName)) {
						String tmp = line + "\r\n";
						bw.write(tmp);
					} else if (line.contains(blockEnd)) {
						found = false;
						bw.close();
						fos.close();
					}
				}

			}

			isr.close();
			fis.close();

		} catch (IOException e) {

		}
		return foundBlock;
	}

	public static Node populateNode(List<Object> record,List<ContentSchema> schema) {
		List<Term> terms = new ArrayList<Term>();
		List<Term> idcontext = new ArrayList<Term>();


		for (int i = 0; i < record.size(); i++) {
			Term newterm = populateTerm((String)record.get(i),getContentSchemaTerm((String)contentHeadings.get(i), schema));
			terms.add(newterm);
			if (isTermKey((String)contentHeadings.get(i),getContentSchemaTerm((String)contentHeadings.get(i),schema))) {
				idcontext.add(newterm);
			}
		}
		Node newnode = new Node();
		String[] nodetokens = schema.get(0).node.toString().split((MicroSyntax.NAMESPACE_DELIMITER.getValue()));
		if (nodetokens.length == 2) {
			newnode.setNamespace(getNamespace(nodetokens[0].toString()));
			newnode.setName(nodetokens[1]);
		} else {
			newnode.setNamespace(getDefaultNamespace());
			newnode.setName(nodetokens[0]);
		}

		newnode.setNodeIdType(NodeIdType.LOCAL);
		newnode.setIdContext(idcontext);
		newnode.setMessageTerms(terms);
		return newnode;


	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String inputFile = null;
		String provenserver = null;
		if (args.length == 0) {
			System.out.println("No parameters provided");
			System.out.println("Usage: java -jar proven_harvester.jar <harvester-file> <proven-server-url>");
			System.out.println("Note:  The second argument <proven_server> is optional.  If it is not supplied provenance will be logged to the message.txt file");
			System.exit(0);
		} else if (args.length == 1)  {
			inputFile = args[0];
		} else {
			inputFile = args[0];
			provenserver = args[1];
		}

		try {
			Boolean found = false;
			for (int i = 0; i < blockTypes.length; i++) {
				found = retrieveBlock(inputFile,blockTypes[i]);
				if (i == 1) {
					parseNamespaceBlock(found);
				}
				if ((i == 4) && found) {
					parseMetricBlock();
				}
			}

			parseMessageBlock();
			parseSchemaBlocks();  //Was parseNodeBlock
			parseContentBlocks(); //Was parseContentBlock


		} catch (InvalidHapiHeadingValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		provenanceMessage.setMessageNodes(nodes);
		HarvesterProducer hapiHarvester = new HarvesterProducer(); 
		exchangeinfo = hapiHarvester.getExchangeInfo();
  
		if (provenserver != null) {
			provinfo.setSaveMessagesInFile(false);
			exchangeinfo = hapiHarvester.getExchangeInfo();
			exchangeinfo.setServicesUri(provenserver);
			exchangeinfo.setExchangeType(ExchangeType.REST);
		} else {
			provinfo.setSaveMessagesInFile(true);			
		}
		hapiHarvester.setExchangeInfo(exchangeinfo);
		hapiHarvester.setProvenInfo(provinfo);
		hapiHarvester.setExchangeInfo(exchangeinfo);



		try {
            if (metrics != null) {
            	provenanceMessage.setMetrics(metrics);
            }
			provenanceMessage.setMessageNodes(nodes);
			hapiHarvester.sendMessage(provenanceMessage);


			//			tmpNodes.clear();
			//        	}
		} catch (NullTermValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	//
	// Extracts Table from Content or Schema Blocks
	//
	private static String extractContentTable(File inputBlock, File tableFile) {
		String nodeName = extractNodename(inputBlock);
		try {
			InputStream fis = new FileInputStream(inputBlock.getAbsoluteFile());
			InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
			BufferedReader br = new BufferedReader(isr);
			String line = new String();
			line = "";
			FileWriter fos = new FileWriter(tableFile);
			BufferedWriter bw = new BufferedWriter(fos);
			int index = 1;
			while ((line = br.readLine()) != null) {
				if (index > 1) {
					bw.write(line + "\r\n");
				}
				index = index + 1;
			}
	
			bw.close();
			fos.close();
			isr.close();
			fis.close();
	
		} catch (IOException e) {
	
		}
		return nodeName;
	}



}
