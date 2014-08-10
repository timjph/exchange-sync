package com.zerodes.exchangesync.settings;

import org.aeonbits.owner.Config;

/**
 * Settings specified by the user.
 */
@Config.Sources({ "file:exchangesync.properties", "file:${exchangesync.properties}" })
public interface UserSettings extends Config {

	/**
	 * Should flagged Exchange emails be synchronized with Remember the Milk?
	 * @return true if tasks should be synchronized
	 */
	boolean syncTasks();

	/**
	 * Should Exchange appointments be synchronized with Google Calendar?
	 * @return true if appointments should be synchronized
	 */
	boolean syncAppointments();

	/**
	 * Name of the Google Calendar to synchronize to.
	 * @return name of the Google Calendar
	 */
	@DefaultValue("Exchange Calendar")
	String googleCalendarName();

	/**
	 * Name of the Remember The Milk list to synchronize to.
	 * @return name of the Remember The Milk list
	 */
	@DefaultValue("Inbox")
	String rtmListName();

	/**
	 * Hostname of the Microsoft Exchange server.
	 * @return hostname of the Microsoft Exchange server
	 */
	String exchangeHost();

	/**
	 * Microsoft Exchange username.
	 * @return Microsoft Exchange username
	 */
	String exchangeUsername();

	/**
	 * Microsoft Exchange password.
	 * @return Microsoft Exchange password
	 */
	String exchangePassword();

	/**
	 * Microsoft Exchange domain.
	 * @return Microsoft Exchange domain
	 */
	String exchangeDomain();

	/**
	 * Microsoft Exchange version (one of: Exchange2007_SP1, Exchange2010, Exchange2010_SP1).
	 * @return Microsoft Exchange version
	 */
	@DefaultValue("Exchange2010_SP1")
	String exchangeVersion();

	/**
	 * Should client use proxy to connect to services?
	 * @return true if client should use proxy
	 */
	@DefaultValue("false")
	boolean usingProxy();

	/**
	 * Proxy hostname.
	 * @return proxy hostname
	 */
	String proxyHost();

	/**
	 * Proxy port.
	 * @return proxy port
	 */
	Integer proxyPort();
}
