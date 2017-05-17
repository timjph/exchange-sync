package com.zerodes.exchangesync.exchange;

/**
 * Interface for Exchange data source settings class.
 */
public interface ExchangeSettings {

	/**
	 * Get the Exchange server host name.
	 * @return Exchange server host name
	 */
	String getExchangeHost();

	/**
	 * Get the Exchange user name.
	 * @return Exchange user name
	 */
	String getExchangeUsername();

	/**
	 * Get the Exchange user password.
	 * @return Exchange user password
	 */
	String getExchangePassword();

	/**
	 * Get the Exchange domain.
	 * @return Exchange domain
	 */
	String getExchangeDomain();

	/**
	 * Get the Exchange server version.
	 * @return exchange server version
	 */
	String getExchangeVersion();

	/**
	 * Determines whether we need to use a proxy to connect to the Exchange server.
	 * @return true if we need to use a proxy to connect to the Exchange server
	 */
	boolean needsExchangeProxy();

	/**
	 * Get the Exchange proxy host name.
	 * @return Exchange proxy host name
	 */
	String getExchangeProxyHost();

	/**
	 * Get the Exchange proxy port number.
	 * @return Exchange proxy port number
	 */
	Integer getExchangeProxyPort();
}
