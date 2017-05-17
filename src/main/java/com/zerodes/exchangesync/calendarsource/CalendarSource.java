package com.zerodes.exchangesync.calendarsource;

import java.util.Collection;

import com.zerodes.exchangesync.dto.AppointmentDto;

import org.joda.time.DateTime;

/**
 * Interface for a Calendar data source.
 */
public interface CalendarSource {
	/**
	 * Get all appointments from the Calendar data source.
	 *
	 * @param startDate the start date for appointments to read
	 * @param endDate the end date for appointments to read
	 * @return the collection of appointments that were retrieved
	 * @throws Exception if an error occurs
	 */
	Collection<AppointmentDto> getAllAppointments(DateTime startDate, DateTime endDate) throws Exception;

	/**
	 * Add an appointment to the Calendar data source.
	 *
	 * @param appointment a DTO holding the appointment values
	 * @throws Exception if an error occurs
	 */
	void addAppointment(AppointmentDto appointment) throws Exception;

	/**
	 * Update an appointment in the Calendar data source.
	 *
	 * @param appointment a DTO holding the appointment values
	 * @throws Exception if an error occurs
	 */
	void updateAppointment(AppointmentDto appointment) throws Exception;

	/**
	 * Delete an appointment from the Calendar data source.
	 *
	 * @param appointment a DTO holding the appointment values
	 * @throws Exception if an error occurs
	 */
	void deleteAppointment(AppointmentDto appointment) throws Exception;
}
