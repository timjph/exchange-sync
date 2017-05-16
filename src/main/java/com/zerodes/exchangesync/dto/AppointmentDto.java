package com.zerodes.exchangesync.dto;

import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

public class AppointmentDto {
	private String exchangeId;
	private DateTime lastModified;
	private String summary;
	private String description;
	private DateTime start;
	private DateTime end;
	private boolean allDay;
	private String location;
	private PersonDto organizer;
	private Set<PersonDto> attendees;
	private Integer reminderMinutesBeforeStart;
	private RecurrenceType recurrenceType;
	private Integer recurrenceCount;
	
	public String getExchangeId() {
		return exchangeId;
	}
	
	public void setExchangeId(final String exchangeId) {
		this.exchangeId = exchangeId;
	}
	
	public DateTime getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(final DateTime lastModified) {
		this.lastModified = lastModified;
	}
	
	public void setSummary(final String summary) {
		this.summary = summary;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(final String name) {
		this.description = name;
	}
	
	public void setStart(final DateTime start) {
		this.start = start;
	}
	
	public DateTime getStart() {
		return start;
	}
	
	public void setEnd(final DateTime end) {
		this.end = end;
	}
	
	public DateTime getEnd() {
		return end;
	}
	
	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(final boolean allDay) {
		this.allDay = allDay;
	}

	public void setLocation(final String location) {
		this.location = location;
	}
	public String getLocation() {
		return location;
	}
	
	public PersonDto getOrganizer() {
		return organizer;
	}
	
	public void setOrganizer(final PersonDto organizer) {
		this.organizer = organizer;
	}
	
	public Set<PersonDto> getAttendees() {
		return attendees;
	}
	
	public void setAttendees(final Set<PersonDto> attendees) {
		this.attendees = attendees;
	}
	
	public Integer getReminderMinutesBeforeStart() {
		return reminderMinutesBeforeStart;
	}
	
	public void setReminderMinutesBeforeStart(final Integer reminderMinutesBeforeStart) {
		this.reminderMinutesBeforeStart = reminderMinutesBeforeStart;
	}
	
	public RecurrenceType getRecurrenceType() {
		return recurrenceType;
	}
	
	public void setRecurrenceType(final RecurrenceType recurrenceType) {
		this.recurrenceType = recurrenceType;
	}
	
	public Integer getRecurrenceCount() {
		return recurrenceCount;
	}
	
	public void setRecurrenceCount(final Integer recurrenceCount) {
		this.recurrenceCount = recurrenceCount;
	}
	
	public void copyTo(final AppointmentDto dest) {
		dest.exchangeId = exchangeId;
		dest.lastModified = lastModified;
		dest.summary = summary;
		dest.description = description;
		dest.start = start;
		dest.end = end;
		dest.location = location;
		dest.organizer = organizer;
		dest.attendees = attendees;
		dest.allDay = allDay;
	}

	public enum RecurrenceType {
		DAILY,
		WEEKLY,
		MONTHLY,
		YEARLY
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(exchangeId)
			.append(summary)
			.append(description)
			.append(start)
			.append(end)
			.append(location)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AppointmentDto)) {
			return false;
		}
		final AppointmentDto other = (AppointmentDto) obj;
		return new EqualsBuilder()
			.append(exchangeId, other.exchangeId)
			.append(summary, other.summary)
			.append(description, other.description)
			.append(start, other.start)
			.append(end, other.end)
			.append(location, other.location)
			.isEquals();
	}
}
