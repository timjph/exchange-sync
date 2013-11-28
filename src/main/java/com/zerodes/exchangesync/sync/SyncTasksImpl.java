package com.zerodes.exchangesync.sync;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zerodes.exchangesync.Pair;
import com.zerodes.exchangesync.StatisticsCollector;
import com.zerodes.exchangesync.dto.TaskDto;
import com.zerodes.exchangesync.tasksource.TaskSource;

public class SyncTasksImpl {
	private static final Logger LOG = LoggerFactory.getLogger(SyncTasksImpl.class);

	private TaskSource exchangeSource;
	private TaskSource otherSource;

	public SyncTasksImpl(TaskSource exchangeSource, TaskSource otherSource) {
		this.exchangeSource = exchangeSource;
		this.otherSource = otherSource;
	}

	protected Set<Pair<TaskDto, TaskDto>> generatePairs() throws Exception {
		Set<Pair<TaskDto, TaskDto>> results = new HashSet<Pair<TaskDto, TaskDto>>();
		Collection<TaskDto> otherTasks = otherSource.getAllTasks();
		Collection<TaskDto> exchangeTasks = exchangeSource.getAllTasks();
		Map<String, TaskDto> otherTasksMap = generateExchangeIdMap(otherTasks);
		Map<String, TaskDto> exchangeTasksMap = generateExchangeIdMap(exchangeTasks);
		for (TaskDto exchangeTask : exchangeTasks) {
			TaskDto otherTask = otherTasksMap.get(exchangeTask.getExchangeId());
			results.add(new Pair<TaskDto, TaskDto>(exchangeTask, otherTask));
		}
		for (TaskDto otherTask : otherTasks) {
			TaskDto exchangeTask = exchangeTasksMap.get(otherTask.getExchangeId());
			results.add(new Pair<TaskDto, TaskDto>(exchangeTask, otherTask));
		}
		return results;
	}

	/**
	 * Take a matching exchange task and other task and determine what needs to be done to sync them.
	 *
	 * @param exchangeTask Exchange task (or null if no matching task exists)
	 * @param otherTask Task from "other" data source (or null if no matching task exists)
	 */
	public void sync(final TaskDto exchangeTask, final TaskDto otherTask, final StatisticsCollector stats)
			throws Exception {
		if (exchangeTask != null && !exchangeTask.isCompleted() && otherTask == null) {
			// Flagged email exists, but RTM task does not
			otherSource.addTask(exchangeTask);
			stats.taskAdded();
		} else if (otherTask != null && !otherTask.isCompleted() && otherTask.getExchangeId() != null && exchangeTask == null) {
			// RTM task exists, but flagged email does not
			otherTask.setCompleted(true);
			otherSource.updateCompletedFlag(otherTask);
			stats.taskUpdated();
		} else if (exchangeTask != null && otherTask != null) {
			// Both RTM task and flagged email exist
			if (exchangeTask.getLastModified().isAfter(otherTask.getLastModified())) {
				// Exchange task has a more recent modified date, so modify other task
				if (exchangeTask.isCompleted() != otherTask.isCompleted()) {
					otherTask.setCompleted(exchangeTask.isCompleted());
					otherSource.updateCompletedFlag(otherTask);
					stats.taskUpdated();
				}
				if (!ObjectUtils.equals(exchangeTask.getDueDate(), otherTask.getDueDate())) {
					otherTask.setDueDate(exchangeTask.getDueDate());
					otherSource.updateDueDate(otherTask);
					stats.taskUpdated();
				}
			} else {
				// Other task has a more recent modified date, so modify Exchange
			}
		}
	}

	public void syncAll(final StatisticsCollector stats) {
		LOG.info("Synchronizing tasks...");

		// Generate matching pairs of tasks
		try {
			Set<Pair<TaskDto, TaskDto>> pairs = generatePairs();
			// Create/complete/delete as required
			for (Pair<TaskDto, TaskDto> pair : pairs) {
				sync(pair.getLeft(), pair.getRight(), stats);
			}
		} catch (Exception e) {
			LOG.error("Problem synchronizing tasks - sync aborted", e);
		}
	}

	public Map<String, TaskDto> generateExchangeIdMap(Collection<TaskDto> tasks) {
		Map<String, TaskDto> results = new HashMap<String, TaskDto>();
		for (TaskDto task : tasks) {
			results.put(task.getExchangeId(), task);
		}
		return results;
	}
}
