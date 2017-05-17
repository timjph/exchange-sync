package com.zerodes.exchangesync.calendarsource.google;

import com.zerodes.exchangesync.dto.AppointmentDto;

/**
 * An AppointmentDto extension for holding Google Calendar specific values.
 */
public class GoogleAppointmentDto extends AppointmentDto {
	private String googleId;

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(final String googleId) {
		this.googleId = googleId;
	}
}
