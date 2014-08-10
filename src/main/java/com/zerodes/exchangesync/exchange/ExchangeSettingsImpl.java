package com.zerodes.exchangesync.exchange;

public class ExchangeSettingsImpl implements ExchangeSettings {
	private String exchangeHost;
	private String exchangeUsername;
	private String exchangePassword;
	private String exchangeDomain;
	private String exchangeVersion;
	private boolean usingProxy;
	private String proxyHost;
	private Integer proxyPort;

	public ExchangeSettingsImpl(final String exchangeHost, final String exchangeUsername, final String exchangePassword,
								final String exchangeDomain, final String exchangeVersion, final boolean usingProxy,
								final String proxyHost, final Integer proxyPort) {
		this.exchangeHost = exchangeHost;
		this.exchangeUsername = exchangeUsername;
		this.exchangePassword = exchangePassword;
		this.exchangeDomain = exchangeDomain;
		this.exchangeVersion = exchangeVersion;
		this.usingProxy = usingProxy;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
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
	public boolean isUsingProxy() {
		return usingProxy;
	}

	@Override
	public String getProxyHost() {
		return proxyHost;
	}

	@Override
	public Integer getProxyPort() {
		return proxyPort;
	}
}
