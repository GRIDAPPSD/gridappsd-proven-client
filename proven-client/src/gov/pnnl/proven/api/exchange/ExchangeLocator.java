package gov.pnnl.proven.api.exchange;


/**
 * Service locators for exchange and exchange servers.
 * 
 * @author d3j766
 *
 */
public class ExchangeLocator {

	/**
	 * Returns an exchange service based on the requested type.
	 * 
	 * @param exchangeType
	 *            Indicates protocol used by the exchange.
	 * 
	 * @return the exchange service
	 * 
	 * @throws UnsupportedOperationException
	 *             if the exchange type is not supported.
	 * 
	 */
	public static Exchange getExchange(ExchangeType exchangeType) {

		Exchange ret = null;

		if (exchangeType.equals(ExchangeType.REST)) {
			ret = new RestExchange();
		}else if (exchangeType.equals(ExchangeType.MQ)) {
			ret = new MqExchange();
		}		
		else {
			throw new UnsupportedOperationException();
		}

		return ret;
	}

	/**
	 * Returns an exchange server service based on the requested type. Currently only a REST
	 * exchange server is supported.
	 * 
	 * @param exchangeType
	 *            Indicates protocol used by the exchange server.
	 * 
	 * @return the exchange service
	 * 
	 * @throws UnsupportedOperationException
	 *             if the exchange type is not supported.
	 */
	public static ExchangeServer getExchangeServer(ExchangeType exchangeType) {

		ExchangeServer ret = null;

		if (exchangeType.equals(ExchangeType.REST)) {
			ret = (ExchangeServer) new RestExchange();
		} else {
			throw new UnsupportedOperationException();
		}

		return ret;
	}
}
