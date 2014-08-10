package com.zerodes.exchangesync.settings;

import com.zerodes.exchangesync.exchange.ExchangeSettings;

public interface Settings {
	UserSettings getUserSettings();
	ExchangeSettings getExchangeSettings();
	String getInternalSetting(String key);
	void setInternalSetting(String key, String value);
}
