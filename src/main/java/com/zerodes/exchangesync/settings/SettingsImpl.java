package com.zerodes.exchangesync.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.zerodes.exchangesync.exchange.ExchangeSettings;
import com.zerodes.exchangesync.exchange.ExchangeSettingsImpl;

import org.aeonbits.owner.ConfigFactory;

public class SettingsImpl implements Settings {
	private final UserSettings userSettings;
	private final Properties internalSettings;
	
	public SettingsImpl() {
		userSettings = ConfigFactory.create(UserSettings.class);
		internalSettings = new Properties();
		try {
			final InputStream internalSettingsStream = new FileInputStream("internal.properties");
			internalSettings.load(internalSettingsStream);
			internalSettingsStream.close();
		} catch (final IOException e) {
			// Do nothing, just use defaults
		}
	}
	
	public void save() {
		try {
			final OutputStream internalSettingsStream = new FileOutputStream("internal.properties");
			internalSettings.store(internalSettingsStream, null);
			internalSettingsStream.close();
		} catch (final FileNotFoundException e) {
			throw new RuntimeException("Unable to save settings.", e);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to save settings.", e);
		}
	}

	@Override
	public UserSettings getUserSettings() {
		return userSettings;
	}

	@Override
	public ExchangeSettings getExchangeSettings() {
		return new ExchangeSettingsImpl(userSettings.exchangeHost(), userSettings.exchangeUsername(), userSettings.exchangePassword(),
				userSettings.exchangeDomain(), userSettings.exchangeVersion(), userSettings.usingProxy(), userSettings.proxyHost(),
				userSettings.proxyPort());
	}

	@Override
	public String getInternalSetting(final String key) {
		return internalSettings.getProperty(key);
	}

	@Override
	public void setInternalSetting(final String key, final String value) {
		if (value == null) {
			internalSettings.remove(key);
		} else {
			internalSettings.setProperty(key, value);
		}
		save();
	}
}
