/*package gov.pnnl.proven.api.exchange;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

@Deprecated
final class RestClientFactory {

	public static Client create() {

		ClientConfig clientConfig = new ClientConfig();

		// values are in milliseconds
		clientConfig.property(ClientProperties.READ_TIMEOUT, 2000);
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 500);

		Client client = ClientBuilder.newClient(clientConfig);
		client.register(MOXyJsonProvider.class);
		return client;
	}
}*/