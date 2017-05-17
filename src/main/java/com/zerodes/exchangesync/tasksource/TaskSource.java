package com.zerodes.exchangesync.tasksource;

import java.util.Collection;

import com.zerodes.exchangesync.dto.TaskDto;

/**
 * Interface for a Task data source.
 */
public interface TaskSource {
	/**
	 * Get all tasks from the Task data source.
	 *
	 * @return a collection of tasks that were retrieved
	 * @throws Exception if an error occurs
	 */
	Collection<TaskDto> getAllTasks() throws Exception;

	/**
	 * Add a task to the Task data source.
	 *
	 * @param task a DTO holding the task values
	 * @throws Exception if an error occurs
	 */
	void addTask(TaskDto task) throws Exception;

	/**
	 * Update a task in the Task data source.
	 *
	 * @param task a DTO holding the task values
	 * @throws Exception if an error occurs
	 */
	void updateDueDate(TaskDto task) throws Exception;

	/**
	 * Update the task completed flag in the Task data source.
	 *
	 * @param task a DTO holding the task values
	 * @throws Exception if an error occurs
	 */
	void updateCompletedFlag(TaskDto task) throws Exception;
}
