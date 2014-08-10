package com.zerodes.exchangesync.exchange;

public class ExchangeSettingsImpl implements ExchangeSettings {
	private String exchangeHost;
	private String exchangeUsername;
	private String exchangePassword;
	private String exchangeDomain;
	private String exchangeVersion;

	public ExchangeSettingsImpl(final String exchangeHost, final String exchangeUsername, final String exchangePassword,
								final String exchangeDomain, final String exchangeVersion) {
		this.exchangeHost = exchangeHost;
		this.exchangeUsername = exchangeUsername;
		this.exchangePassword = exchangePassword;
		this.exchangeDomain = exchangeDomain;
		this.exchangeVersion = exchangeVersion;
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
}
