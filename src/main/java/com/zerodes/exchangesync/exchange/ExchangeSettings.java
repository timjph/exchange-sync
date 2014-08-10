package com.zerodes.exchangesync.exchange;

public interface ExchangeSettings {

	String getExchangeHost();

	String getExchangeUsername();

	String getExchangePassword();

	String getExchangeDomain();

	String getExchangeVersion();

	boolean isUsingProxy();

	String getProxyHost();

	Integer getProxyPort();
}
