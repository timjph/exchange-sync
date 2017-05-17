package com.zerodes.exchangesync;

import com.zerodes.exchangesync.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zerodes.exchangesync.calendarsource.CalendarSource;
import com.zerodes.exchangesync.calendarsource.google.GoogleCalendarSourceImpl;
import com.zerodes.exchangesync.exchange.ExchangeSourceImpl;
import com.zerodes.exchangesync.settings.SettingsImpl;
import com.zerodes.exchangesync.sync.SyncCalendarsImpl;
import com.zerodes.exchangesync.sync.SyncTasksImpl;
import com.zerodes.exchangesync.tasksource.TaskSource;
import com.zerodes.exchangesync.tasksource.rtm.RtmTaskSourceImpl;

/**
 * Application startup class.
 */
public class App {
	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	/**
	 * Main startup method.
	 *
	 * @param args application command line arguments
	 */
	public static void main(final String[] args) {
		final Settings settings = new SettingsImpl();
		if (settings.getUserSettings().needInternetProxy()) {
			System.setProperty("http.proxyHost", settings.getUserSettings().internetProxyHost());
			System.setProperty("http.proxyPort", String.valueOf(settings.getUserSettings().internetProxyPort()));
		}

		boolean success = true;
		try {
			// Initialize exchange source
			final ExchangeSourceImpl exchangeSource = new ExchangeSourceImpl(settings.getExchangeSettings());

			// Initialize statistics collector
			final StatisticsCollector stats = new StatisticsCollector();

			if (settings.getUserSettings().syncAppointments()) {
				// Initialize Google source
				final CalendarSource googleSource = new GoogleCalendarSourceImpl(settings);

				// Synchronize appointments
				final SyncCalendarsImpl syncCalendars = new SyncCalendarsImpl(exchangeSource, googleSource);
				success = success && syncCalendars.syncAll(stats, settings.getUserSettings().appointmentMonthsToExport());
			}

			if (settings.getUserSettings().syncTasks()) {
				// Initialize RTM source
				final TaskSource rtmSource = new RtmTaskSourceImpl(settings);

				// Synchronize tasks
				final SyncTasksImpl syncTasks = new SyncTasksImpl(exchangeSource, rtmSource);
				success = success && syncTasks.syncAll(stats);
			}

			// Show stats
			stats.display();
		} catch (final Exception e) {
			LOG.error("An unexpected exception occurred", e);
			success = false;
		} finally {
			settings.save();
		}
		if (!success) {
			System.exit(1);
		}
	}
}
