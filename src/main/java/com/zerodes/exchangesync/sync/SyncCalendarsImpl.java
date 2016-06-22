package com.zerodes.exchangesync.sync;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zerodes.exchangesync.Pair;
import com.zerodes.exchangesync.StatisticsCollector;
import com.zerodes.exchangesync.calendarsource.CalendarSource;
import com.zerodes.exchangesync.dto.AppointmentDto;

public class SyncCalendarsImpl {
	private static final Logger LOG = LoggerFactory.getLogger(SyncCalendarsImpl.class);
	
	private final CalendarSource exchangeSource;
	private final CalendarSource otherSource;

	public SyncCalendarsImpl(final CalendarSource exchangeSource, final CalendarSource otherSource) {
		this.exchangeSource = exchangeSource;
		this.otherSource = otherSource;
	}

	protected Set<Pair<AppointmentDto, AppointmentDto>> generatePairs(final int monthsToExport) throws Exception {
		final Set<Pair<AppointmentDto, AppointmentDto>> results = new HashSet<Pair<AppointmentDto, AppointmentDto>>();
		// Set time frame to one month as temporary workaround for "Calendar usage limits exceeded." issue.
		final DateTime now = new DateTime();
		final DateTime startDate = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
		final DateTime endDate = startDate.plusMonths(monthsToExport);
		final Collection<AppointmentDto> otherAppointments = otherSource.getAllAppointments(startDate, endDate);
		final Collection<AppointmentDto> exchangeAppointments = exchangeSource.getAllAppointments(startDate, endDate);
		final Map<String, AppointmentDto> otherAppointmentsMap = generateExchangeIdMap(otherAppointments);
		final Map<String, AppointmentDto> exchangeAppointmentsMap = generateExchangeIdMap(exchangeAppointments);
		for (final AppointmentDto exchangeAppointment : exchangeAppointments) {
			final AppointmentDto otherAppointment = otherAppointmentsMap.get(exchangeAppointment.getExchangeId());
			results.add(new Pair<AppointmentDto, AppointmentDto>(exchangeAppointment, otherAppointment));
		}
		for (final AppointmentDto otherAppointment : otherAppointments) {
			final AppointmentDto exchangeAppointment = exchangeAppointmentsMap.get(otherAppointment.getExchangeId());
			results.add(new Pair<AppointmentDto, AppointmentDto>(exchangeAppointment, otherAppointment));
		}
		return results;
	}

	/**
	 * Take a matching exchange CalendarEntry and other CalendarEntry and determine what needs to be done to sync them.
	 *
	 * @param exchangeCalendarEntry Exchange CalendarEntry (or null if no matching CalendarEntry exists)
	 * @param otherCalendarEntry CalendarEntry from "other" data source (or null if no matching CalendarEntry exists)
	 */
	public void sync(final AppointmentDto exchangeCalendarEntry, final AppointmentDto otherCalendarEntry, final StatisticsCollector stats)
			throws Exception {
		if (exchangeCalendarEntry != null && otherCalendarEntry == null) {
			otherSource.addAppointment(exchangeCalendarEntry);
			stats.appointmentAdded();
		} else if (exchangeCalendarEntry == null && otherCalendarEntry != null && otherCalendarEntry.getExchangeId() != null) {
			otherSource.deleteAppointment(otherCalendarEntry);
			stats.appointmentDeleted();
		} else if (exchangeCalendarEntry != null && otherCalendarEntry != null && !exchangeCalendarEntry.equals(otherCalendarEntry)) {
			if (exchangeCalendarEntry.getLastModified().isAfter(otherCalendarEntry.getLastModified())) {
				// Exchange CalendarEntry has a more recent modified date, so modify other CalendarEntry
				exchangeCalendarEntry.copyTo(otherCalendarEntry);
				otherSource.updateAppointment(otherCalendarEntry);
				stats.appointmentUpdated();
			} else {
				// Other CalendarEntry has a more recent modified date, so modify Exchange
			}
		}
	}

	public boolean syncAll(final StatisticsCollector stats, final int monthsToExport) {
		LOG.info("Synchronizing calendars...");

		// Generate matching pairs of appointments
		try {
			final Set<Pair<AppointmentDto, AppointmentDto>>pairs = generatePairs(monthsToExport);

			// Create/complete/delete as required
			for (final Pair<AppointmentDto, AppointmentDto> pair : pairs) {
				sync(pair.getLeft(), pair.getRight(), stats);
			}
			return true;
		} catch (final Exception e) {
			LOG.error("Problem synchronizing appointments - sync aborted", e);
		}
		return false;
	}

	public Map<String, AppointmentDto> generateExchangeIdMap(final Collection<AppointmentDto> calendarEntrys) {
		final Map<String, AppointmentDto> results = new HashMap<String, AppointmentDto>();
		for (final AppointmentDto calendarEntry : calendarEntrys) {
			results.put(calendarEntry.getExchangeId(), calendarEntry);
		}
		return results;
	}
}
