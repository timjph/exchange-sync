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
	
	private CalendarSource exchangeSource;
	private CalendarSource otherSource;

	public SyncCalendarsImpl(CalendarSource exchangeSource, CalendarSource otherSource) {
		this.exchangeSource = exchangeSource;
		this.otherSource = otherSource;
	}

	protected Set<Pair<AppointmentDto, AppointmentDto>> generatePairs() throws Exception {
		Set<Pair<AppointmentDto, AppointmentDto>> results = new HashSet<Pair<AppointmentDto, AppointmentDto>>();
		// Set time frame to one month as temporary workaround for "Calendar usage limits exceeded." issue.
		final DateTime now = new DateTime();
		final DateTime startDate = now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
		final DateTime endDate = startDate.plusMonths(1);
		Collection<AppointmentDto> otherAppointments = otherSource.getAllAppointments(startDate, endDate);
		Collection<AppointmentDto> exchangeAppointments = exchangeSource.getAllAppointments(startDate, endDate);
		Map<String, AppointmentDto> otherAppointmentsMap = generateExchangeIdMap(otherAppointments);
		Map<String, AppointmentDto> exchangeAppointmentsMap = generateExchangeIdMap(exchangeAppointments);
		for (AppointmentDto exchangeAppointment : exchangeAppointments) {
			AppointmentDto otherAppointment = otherAppointmentsMap.get(exchangeAppointment.getExchangeId());
			results.add(new Pair<AppointmentDto, AppointmentDto>(exchangeAppointment, otherAppointment));
		}
		for (AppointmentDto otherAppointment : otherAppointments) {
			AppointmentDto exchangeAppointment = exchangeAppointmentsMap.get(otherAppointment.getExchangeId());
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

	public void syncAll(final StatisticsCollector stats) {
		LOG.info("Synchronizing calendars...");

		// Generate matching pairs of appointments
		try {
			Set<Pair<AppointmentDto, AppointmentDto>>pairs = generatePairs();

			// Create/complete/delete as required
			for (Pair<AppointmentDto, AppointmentDto> pair : pairs) {
				sync(pair.getLeft(), pair.getRight(), stats);
			}
		} catch (Exception e) {
			LOG.error("Problem synchronizing appointments - sync aborted", e);
		}
	}

	public Map<String, AppointmentDto> generateExchangeIdMap(Collection<AppointmentDto> CalendarEntrys) {
		Map<String, AppointmentDto> results = new HashMap<String, AppointmentDto>();
		for (AppointmentDto CalendarEntry : CalendarEntrys) {
			results.put(CalendarEntry.getExchangeId(), CalendarEntry);
		}
		return results;
	}
}
