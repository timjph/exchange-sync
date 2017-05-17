package com.zerodes.exchangesync.settings;

import com.zerodes.exchangesync.exchange.ExchangeSettings;

/**
 * Interface for all application settings.
 */
public interface Settings {

	/**
	 * Get all user settings.
	 * @return the populated user settings object
	 */
	UserSettings getUserSettings();

	/**
	 * Get all Exchange server settings.
	 * @return the populated Exchange server settings object
	 */
	ExchangeSettings getExchangeSettings();

	/**
	 * Get a single internal setting value.
	 * @param key the setting key
	 * @return the setting value
	 */
	String getInternalSetting(String key);

	/**
	 * Set a single internal setting value.
	 * @param key the setting key
	 * @param value the setting value
	 */
	void setInternalSetting(String key, String value);

	/**
	 * Save all settings to the persistent data store (likely a property file).
	 */
	void save();
}
