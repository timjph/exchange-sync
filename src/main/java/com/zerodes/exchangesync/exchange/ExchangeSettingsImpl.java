package com.zerodes.exchangesync.exchange;

public class ExchangeSettingsImpl implements ExchangeSettings {
	private String exchangeHost;
	private String exchangeUsername;
	private String exchangePassword;
	private String exchangeDomain;
	private String exchangeVersion;
	private boolean needsExchangeProxy;
	private String exchangeProxyHost;
	private Integer exchangeProxyPort;

	public ExchangeSettingsImpl(final String exchangeHost, final String exchangeUsername, final String exchangePassword,
								final String exchangeDomain, final String exchangeVersion, final boolean needsExchangeProxy,
								final String exchangeProxyHost, final Integer exchangeProxyPort) {
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
