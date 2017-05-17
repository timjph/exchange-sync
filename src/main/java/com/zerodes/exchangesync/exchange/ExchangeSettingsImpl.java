package com.zerodes.exchangesync.exchange;

/**
 * Holds settings for the Exchange data source.
 */
public class ExchangeSettingsImpl implements ExchangeSettings {
	private String exchangeHost;
	private String exchangeUsername;
	private String exchangePassword;
	private String exchangeDomain;
	private String exchangeVersion;
	private boolean needsExchangeProxy;
	private String exchangeProxyHost;
	private Integer exchangeProxyPort;

	/**
	 * Constructor for instantiating Exchange settings.
	 *
	 * @param exchangeHost the Exchange server host name
	 * @param exchangeUsername the Exchange user name
	 * @param exchangePassword the Exchange user password
	 * @param exchangeDomain the Exchange domain
	 * @param exchangeVersion the Exchange server version
	 * @param needsExchangeProxy whether we need to use a proxy to connect to Exchange
	 * @param exchangeProxyHost the Exchange proxy host name
	 * @param exchangeProxyPort the Exchange proxy port number
	 */
	public ExchangeSettingsImpl(final String exchangeHost, final String exchangeUsername,
			final String exchangePassword, final String exchangeDomain, final String exchangeVersion,
			final boolean needsExchangeProxy, final String exchangeProxyHost, final Integer exchangeProxyPort) {
		this.exchangeHost = exchangeHost;
		this.exchangeUsername = exchangeUsername;
		this.exchangePassword = exchangePassword;
		this.exchangeDomain = exchangeDomain;
		this.exchangeVersion = exchangeVersion;
		this.needsExchangeProxy = needsExchangeProxy;
		this.exchangeProxyHost = exchangeProxyHost;
		this.exchangeProxyPort = exchangeProxyPort;
	}

	@Override
	public String getExchangeHost() {
		return exchangeHost;
	}

	@Override
	public String getExchangeUsername() {
		return exchangeUsername;
	}

	@Override
	public String getExchangePassword() {
		return exchangePassword;
	}

	@Override
	public String getExchangeDomain() {
		return exchangeDomain;
	}

	@Override
	public String getExchangeVersion() {
		return exchangeVersion;
	}

	@Override
	public boolean needsExchangeProxy() {
		return needsExchangeProxy;
	}

	@Override
	public String getExchangeProxyHost() {
		return exchangeProxyHost;
	}

	@Override
	public Integer getExchangeProxyPort() {
		return exchangeProxyPort;
	}
}
