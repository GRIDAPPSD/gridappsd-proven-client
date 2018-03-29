package gov.pnnl.proven.api.exchange;

import gov.pnnl.proven.message.ProvenMessage;
import gov.pnnl.proven.api.producer.ProvenInfo;
import gov.pnnl.proven.api.producer.SessionInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;
//import java.util.Base64;
//import java.util.Base64.Encoder;
import java.util.List;


/**
 * Provides implementation for REST based Exchange and an ExchangeServer.
 * 
 * @see Exchange
 * 
 * @see ExchangeServer
 * 
 * @author d3j766
 *
 */
class RestExchange implements Exchange {



	/**
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override
	public boolean addProvenData(ExchangeInfo exchangeInfo, List<ProvenMessage> messages) {
		throw new UnsupportedOperationException();	
	}

	/**
	 * Adds a new provenance message to a REST based exchange. A JSON-LD message is first
	 * generated from the provided provenance message and then POSTed to the exchange. This method
	 * will return true if the POST response is an HTTP Success 2xx code, indicating the message was
	 * added.
	 * 
	 * @see gov.pnnl.proven.api.exchange.Exchange#addProvenance()
	 */
	@Override
	public void addProvenData(ExchangeInfo exchangeInfo, final ProvenMessage message, final SessionInfo sessionInfo, ProvenInfo provenInfo) {

				
//		final String ADD_SERVICE_PATH = "/provenance/" + provenanceInfo.getContext();
//		URI addService = URI.create(exchangeInfo.getServicesUri() + ADD_SERVICE_PATH);
//		//URI exchangeService = URI.create(exchangeInfo.getServicesUri() + ADD_SERVICE_PATH);
//		Client client = null;
		
		String servicePath = "/" + provenInfo.getContext() + "/" + message.getName();
		servicePath = servicePath.replace(" ",  "%20");
		
	
		try {
			//URL url = new URL("http://192.101.107.229/proven/rest/v1/repository/message/client/{domain}/{message name}");
			URL url = new URL(exchangeInfo.getServicesUri() + servicePath);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
			objOut.writeObject(message);
	        objOut.flush();
	        objOut.close();
			//OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			//out.write(message);
			//out.close();

			System.out.println(connection.getResponseCode());
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			System.out.println("REST Service Invoked Successfully..");
			in.close();
			System.out.println(response.toString());
		} catch (Exception e) {
			System.out.println("\nError while calling REST Service");
			System.out.println(e);
		}

		/*try {
			
			client = ClientFactory(true);
			
			// Encode the provenance message
			//Encoder encoder = Base64.getEncoder();
			byte[] j7message = message.generateJsonLd().getBytes("UTF-8");
			byte[] encodedMessage = DatatypeConverter.parseBase64Binary(message.generateJsonLd());
			//byte[] encodedMessage = encoder.encode(message.generateJsonLd().getBytes());

			// POST encoded message
			Invocation.Builder builder = client.target(addService).request();
			builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
			Future<String> res = builder.async().post(Entity.entity(encodedMessage,
					MediaType.APPLICATION_OCTET_STREAM), new InvocationCallback<String>() {
						
			            @Override
			            public void completed(String response) {
			                System.out.println("Response status code "
			                        + response + " received.");			                
			                sessionInfo.setMessageCount(sessionInfo.getMessageCount() + 1L);
			            }
			 
			            @Override
			            public void failed(Throwable throwable) {
			                System.out.println("Invocation failed.");
			                throwable.printStackTrace();			                
			                sessionInfo.setErrorCount(sessionInfo.getErrorCount() + 1L);
			                if (!(sessionInfo.getErrorCache().containsKey(message)))
							{
			                	sessionInfo.setErrorCache(message, null);
							}
			            }
			        } );
			res.get();
			//Response respo = res.get();
			//Response response = responseFuture.get();
			// Set return value based on HTTP response code
			//if (response.getStatusInfo().getFamily().equals(Status.Family.SUCCESSFUL)) {
			//}
			
		} catch (Exception e) {
			log.error("Error sending Provenance.");
			sessionInfo.setErrorCache(message, e.getMessage());
			// Catch all, currently not providing error information to caller
			
		}*/
		
		
	}

	/*private String getExchange(ExchangeInfo exchangeInfo) {
		// TODO Auto-generated method stub
		List<String> exchanges = exchangeInfo.getExchangeUrls();
		//load balancing
		//return one url
		return null;
	}*/

	
	
	/**
	 * Attempts to connect a provenance server using the server's provided REST service.
	 * 
	 * @see gov.pnnl.proven.api.exchange.ExchangeServer#connect()
	 */
	/*
	@SuppressWarnings("unchecked")
	@Override
	public List<ExchangeInfo> getExchanges(ProvenInfo provenanceInfo) {

		final String CONNECT_SERVICE_PATH = "/exchanges/" + provenanceInfo.getDomain();

		// Represents a disconnected session and will be default return value if service is
		// unavailable.
		List<ExchangeInfo> exchangeInfo = new ArrayList<ExchangeInfo>();
		

		try {

			URI connectService = new URI(serverInfo.getExchangeServer().toString()
					+ CONNECT_SERVICE_PATH);

			// POST new session connection
			Client client = ClientFactory();
			Invocation.Builder builder = client.target(connectService).request();
			builder.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			builder.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
			Response response = builder.post(Entity
					.entity(serverInfo, MediaType.APPLICATION_JSON));

			// Set return value based on HTTP response code
			if (response.getStatusInfo().getFamily().equals(Status.Family.SUCCESSFUL)) {
				if (response.hasEntity()) {
					//String output = response.readEntity(String.class);
					exchangeInfo = response.readEntity(List.class);
					System.out.println("");
					//sessionInfo = (SessionInfo) response.getEntity();
				}
			}
		} catch (Exception e) {
			log.warn("Cannot get exchanges from ProvEn Server.");
			// Catch all, currently not providing error information to caller
			// Return default, disconnected session information on error
			//exchangeInfo = new ExchangeInfo();
		}
		

		return exchangeInfo;
	} */



}
