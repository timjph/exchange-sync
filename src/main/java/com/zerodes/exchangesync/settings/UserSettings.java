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
	 * Determines whether to synchronize the organizer and attendees for calendar appointments.
	 * Some users have reported that disabling this resolves the "Calendar usage limits exceeded" issue.
	 * @return
	 */
	@DefaultValue("true")
	boolean googleSyncOrganizerAndAttendees();

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
	 * Should client use proxy to connect to Microsoft Exchange?
	 * @return true if client should use proxy
	 */
	@DefaultValue("false")
	boolean needExchangeProxy();

	/**
	 * Proxy hostname for Microsoft Exchange.
	 * @return proxy hostname
	 */
	String exchangeProxyHost();

	/**
	 * Proxy port for Microsoft Exchange.
	 * @return proxy port
	 */
	Integer exchangeProxyPort();

	/**
	 * Should client use proxy to connect to internet (non-exchange) services?
	 * @return true if client should use proxy
	 */
	@DefaultValue("false")
	boolean needInternetProxy();

	/**
	 * Proxy hostname for internet services.
	 * @return proxy hostname
	 */
	String internetProxyHost();

	/**
	 * Proxy port for internet services.
	 * @return proxy port
	 */
	Integer internetProxyPort();

	/**
	 * Obfuscate emails to prevent Google from sending calendar updates to attendees.
	 * @return true if attendee emails should be obfuscated
	 */
	boolean obfuscateAttendeeEmails();

	/**
	 * Number of months in the future to export to Google Calendar.
	 */
	@DefaultValue("1")
	int appointmentMonthsToExport();
}
