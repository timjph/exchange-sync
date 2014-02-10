package com.zerodes.exchangesync.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.zerodes.exchangesync.ExchangeSettings;

public class SettingsImpl implements Settings, ExchangeSettings {
	private final Properties userSettings;
	private final Properties internalSettings;
	
	public SettingsImpl() {
		userSettings = new Properties();
		internalSettings = new Properties();
		try {
			final InputStream userSettingsStream = new FileInputStream(System.getProperty("exchangesync.properties", "exchangesync.properties"));
			userSettings.load(userSettingsStream);
			userSettingsStream.close();
		} catch (final IOException e) {
			// Do nothing, just use defaults
		}
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
	public boolean syncTasks() {
		return "true".equalsIgnoreCase(userSettings.getProperty("syncTasks"));
	}

	@Override
	public boolean syncAppointments() {
		return "true".equalsIgnoreCase(userSettings.getProperty("syncAppointments"));
	}

	@Override
	public String getExchangeHost() {
		return userSettings.getProperty("exchangeHost");
	}

	@Override
	public String getExchangeUsername() {
		return userSettings.getProperty("exchangeUsername");
	}

	@Override
	public String getExchangePassword() {
		return userSettings.getProperty("exchangePassword");
	}

	@Override
	public String getExchangeDomain() {
		return userSettings.getProperty("exchangeDomain");
	}

	@Override
	public String getExchangeVersion() {
		return userSettings.getProperty("exchangeVersion", "Exchange2010_SP1");
	}

	@Override
	public String getUserSetting(final String key) {
		return userSettings.getProperty(key);
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
