package com.zerodes.exchangesync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for collecting simple statistics to show the user when the application completes.
 */
public class StatisticsCollector {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsCollector.class);

	private int appointmentsAdded = 0;
	private int appointmentsUpdated = 0;
	private int appointmentsDeleted = 0;
	private int tasksAdded = 0;
	private int tasksUpdated = 0;
	private int tasksDeleted = 0;

	/**
	 * Increment the added appointments collector.
	 */
	public void appointmentAdded() {
		appointmentsAdded++;
	}

	/**
	 * Increment the updated appointments collector.
	 */
	public void appointmentUpdated() {
		appointmentsUpdated++;
	}

	/**
	 * Increment the deleted appointments collector.
	 */
	public void appointmentDeleted() {
		appointmentsDeleted++;
	}

	/**
	 * Increment the added tasks collector.
	 */
	public void taskAdded() {
		tasksAdded++;
	}

	/**
	 * Increment the updated tasks collector.
	 */
	public void taskUpdated() {
		tasksUpdated++;
	}

	/**
	 * Increment the deleted tasks collector.
	 */
	public void taskDeleted() {
		tasksDeleted++;
	}

	/**
	 * Display the collected statistics using Log4j info statements.
	 */
	public void display() {
		LOG.info(String.format("Appointments added/updated/deleted: %d/%d/%d",
				appointmentsAdded, appointmentsUpdated, appointmentsDeleted));
		LOG.info(String.format("Tasks added/updated/deleted: %d/%d/%d",
				tasksAdded, tasksUpdated, tasksDeleted));
	}
}
