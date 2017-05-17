package com.zerodes.exchangesync.dto;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

/**
 * A DTO class for holding task values.
 */
public class TaskDto {
	private String exchangeId;
	private DateTime lastModified;
	private String name;
	private DateTime dueDate;
	private Byte priority;
	private String url;
	private boolean completed;
	private Set<String> tags = new HashSet<String>();
	private Set<NoteDto> notes = new HashSet<NoteDto>();

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

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public DateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(final DateTime dueDate) {
		this.dueDate = dueDate;
	}

	public Byte getPriority() {
		return priority;
	}

	public void setPriority(final Byte priority) {
		this.priority = priority;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(final boolean completed) {
		this.completed = completed;
	}

	public Set<String> getTags() {
		return tags;
	}

	/**
	 * Add a tag to the task.
	 * @param tag the tag value
	 */
	public void addTag(final String tag) {
		tags.add(tag);
	}

	public Set<NoteDto> getNotes() {
		return notes;
	}

	/**
	 * Add a note to the task.
	 * @param note the note DTO object
	 */
	public void addNote(final NoteDto note) {
		notes.add(note);
	}

	/**
	 * Shallow copy a task to a destination task object.
	 * @param dest the destination task object
	 */
	public void copyTo(final TaskDto dest) {
		dest.exchangeId = exchangeId;
		dest.lastModified = lastModified;
		dest.name = name;
		dest.dueDate = dueDate;
		dest.priority = priority;
		dest.url = url;
		dest.completed = completed;
		dest.tags = tags;
		dest.notes = notes;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(exchangeId)
			.append(lastModified)
			.append(name)
			.append(dueDate)
			.append(priority)
			.append(url)
			.append(completed)
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
		if (!(obj instanceof TaskDto)) {
			return false;
		}
		final TaskDto other = (TaskDto) obj;
		return new EqualsBuilder()
			.append(exchangeId, other.exchangeId)
			.append(lastModified, other.lastModified)
			.append(name, other.name)
			.append(dueDate, other.dueDate)
			.append(priority, other.priority)
			.append(url, other.url)
			.append(completed, other.completed)
			.isEquals();
	}
}
