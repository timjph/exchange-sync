package com.zerodes.exchangesync.tasksource.rtm;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zerodes.exchangesync.dto.NoteDto;
import com.zerodes.exchangesync.dto.TaskDto;
import com.zerodes.exchangesync.settings.Settings;
import com.zerodes.exchangesync.tasksource.TaskSource;

public class RtmTaskSourceImpl implements TaskSource {
	private static final Logger LOG = LoggerFactory.getLogger(RtmTaskSourceImpl.class);
	
	private static final String RTM_API_KEY = "0bcf4c7e3182ec34f45321512e576300";
	private static final String RTM_SHARED_SECRET = "fbf7a0bdb0011532";

	private static final String REST_HOST = "api.rememberthemilk.com";
	private static final String REST_AUTH_PATH = "/services/auth/";
	private static final String REST_METHOD_PATH = "/services/rest/";

	private static final String EXCHANGE_ID_NOTE_TITLE = "ExchangeID";
	private static final String ORIGINAL_SUBJECT_NOTE_TITLE = "Original Email Subject";
	
	private static final String INTERNAL_SETTING_FROB = "frob";
	private static final String INTERNAL_SETTING_AUTH_TOKEN = "authToken";

	private Settings settings;
	private final String defaultRtmListId;

	private enum RtmAuthStatus {
		NEEDS_USER_APPROVAL,
		NEEDS_AUTH_TOKEN,
		AUTHORIZED
	}

	public RtmTaskSourceImpl(final Settings settings) throws RtmServerException {
		this.settings = settings;
		LOG.info("Connecting to Remember The Milk...");
		switch (getAuthStatus()) {
		case NEEDS_USER_APPROVAL:
			throw new RuntimeException("Please go to the following URL to authorize application to sync with Remember The Milk: "
					+ getAuthenticationUrl("write"));
		case NEEDS_AUTH_TOKEN:
			completeAuthentication();
		}
		this.defaultRtmListId = getIdForListName(settings.getUserSettings().rtmListName());
	}
	
	@Override
	public void addTask(final TaskDto task) throws Exception {
		// Add email tag
		task.addTag("email");

		// Add ExchangeID note
		final NoteDto exchangeIdNote = new NoteDto();
		exchangeIdNote.setTitle(EXCHANGE_ID_NOTE_TITLE);
		exchangeIdNote.setBody(task.getExchangeId());
		task.addNote(exchangeIdNote);

		// Add Original Subject note
		final NoteDto originalSubjectNote = new NoteDto();
		originalSubjectNote.setTitle(ORIGINAL_SUBJECT_NOTE_TITLE);
		originalSubjectNote.setBody(task.getName());
		task.addNote(originalSubjectNote);

		final String timelineId = createTimeline();
		addTask(timelineId, defaultRtmListId, task);
		LOG.info("Added RTM task " + task.getName());
	}

	@Override
	public void updateDueDate(final TaskDto task) throws Exception {
		final String timelineId = createTimeline();
		updateDueDate(timelineId, (RtmTaskDto) task);
		LOG.info("Updated RTM task due date for " + task.getName());
	}

	@Override
	public void updateCompletedFlag(final TaskDto task) throws Exception {
		final String timelineId = createTimeline();
		updateCompleteFlag(timelineId, (RtmTaskDto) task);
		if (task.isCompleted()) {
			LOG.info("Marked RTM task as completed for " + task.getName());
		} else {
			LOG.info("Marked RTM task as incomplete for " + task.getName());
		}
	}

	private RtmAuthStatus getAuthStatus() throws RtmServerException {
		if (StringUtils.isNotEmpty(settings.getInternalSetting(INTERNAL_SETTING_FROB))) {
			return RtmAuthStatus.NEEDS_AUTH_TOKEN;
		}
		if (StringUtils.isEmpty(settings.getInternalSetting(INTERNAL_SETTING_AUTH_TOKEN)) || !checkToken()) {
			return RtmAuthStatus.NEEDS_USER_APPROVAL;
		}
		return RtmAuthStatus.AUTHORIZED;
	}

	private URL getAuthenticationUrl(final String perms) throws RtmServerException {
		try {
			// Call getFrob
			final TreeMap<String, String> getFrobParams = new TreeMap<String, String>();
			getFrobParams.put("method", "rtm.auth.getFrob");
			final Document response = parseXML(getRtmUri(REST_METHOD_PATH, getFrobParams));
			final Node node = response.selectSingleNode("/rsp/frob");
			settings.setInternalSetting(INTERNAL_SETTING_FROB, node.getText());

			// Generate url
			final TreeMap<String, String> params = new TreeMap<String, String>();
			params.put("perms", perms);
			params.put("frob", settings.getInternalSetting(INTERNAL_SETTING_FROB));
			return getRtmUri(REST_AUTH_PATH, params).toURL();
		} catch (final MalformedURLException e) {
			throw new RuntimeException("Unable to get authentication url", e);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to get authentication url", e);
		}
	}

	public void completeAuthentication() throws RtmServerException {
		if (settings.getInternalSetting(INTERNAL_SETTING_FROB) == null) {
			throw new RuntimeException("Unable to complete authentication unless in NEEDS_AUTH_TOKEN status.");
		}
		try {
			final TreeMap<String, String> params = new TreeMap<String, String>();
			params.put("method", "rtm.auth.getToken");
			params.put("frob", settings.getInternalSetting(INTERNAL_SETTING_FROB));
			final Document response = parseXML(getRtmUri(REST_METHOD_PATH, params));
			final Node node = response.selectSingleNode("/rsp/auth/token");
			settings.setInternalSetting(INTERNAL_SETTING_AUTH_TOKEN, node.getText());
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to complete authentication", e);
		} finally {
			settings.setInternalSetting(INTERNAL_SETTING_FROB, null);
		}
	}

	@SuppressWarnings("unchecked")
	private String getIdForListName(final String listName) throws RtmServerException {
		try {
			final Document response = parseXML(getRtmMethodUri("rtm.lists.getList"));
			final List<Node> listNodesList = response.selectNodes("/rsp/lists/list");
			for (final Node listNode : listNodesList) {
				final Node nameNode = listNode.selectSingleNode("@name");
				final Node idNode = listNode.selectSingleNode("@id");
				if (nameNode.getText().equals(listName)) {
					return idNode.getText();
				}
			}
			throw new RuntimeException("Unable to find list named " + listName);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to retrieve list of lists", e);
		}
	}

	/**
	 * Create a new timeline.
	 *
	 * @return timeline id
	 * @throws RtmServerException
	 */
	private String createTimeline() throws RtmServerException {
		try {
			final Document response = parseXML(getRtmMethodUri("rtm.timelines.create"));
			final Node node = response.selectSingleNode("/rsp/timeline");
			return node.getText();
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to create timeline", e);
		}
	}

	/**
	 * Add a new task.
	 * 
	 * @param timelineId
	 * @param listId
	 * @param task
	 * @throws RtmServerException
	 */
	private void addTask(final String timelineId, final String listId, final TaskDto task) throws RtmServerException {
		try {
			final TreeMap<String, String> addTaskParams = new TreeMap<String, String>();
			addTaskParams.put("timeline", timelineId);
			addTaskParams.put("list_id", listId);
			addTaskParams.put("name", task.getName());
			final Document response = parseXML(getRtmMethodUri("rtm.tasks.add", addTaskParams));
			
			final RtmTaskDto rtmTask = new RtmTaskDto();
			task.copyTo(rtmTask);
			final Node idNode = response.selectSingleNode("/rsp/list/taskseries/task/@id");
			rtmTask.setRtmTaskId(idNode.getText());
			final Node taskSeriesIdNode = response.selectSingleNode("/rsp/list/taskseries/@id");
			rtmTask.setRtmTimeSeriesId(taskSeriesIdNode.getText());
			rtmTask.setRtmListId(listId);
			
			// Set due date (if required)
			if (rtmTask.getDueDate() != null) {
				updateDueDate(timelineId, rtmTask);
			}
			
			// Set completed (if required)
			if (rtmTask.isCompleted()) {
				updateCompleteFlag(timelineId, rtmTask);
			}
			
			// Add tags (if required)
			if (!rtmTask.getTags().isEmpty()) {
				addTags(timelineId, rtmTask, rtmTask.getTags());
			}
			
			// Add notes (if required)
			if (!rtmTask.getNotes().isEmpty()) {
				for (final NoteDto note : rtmTask.getNotes()) {
					addNote(timelineId, rtmTask, note);
				}
			}
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to add task", e);
		}
	}

	/**
	 * Update a task's due date.
	 * 
	 * @param timelineId
	 * @param task
	 * @throws RtmServerException
	 * @throws UnsupportedEncodingException
	 */
	private void updateDueDate(final String timelineId, final RtmTaskDto task)
			throws RtmServerException, UnsupportedEncodingException {
		final TreeMap<String, String> setDueDateParams = new TreeMap<String, String>();
		setDueDateParams.put("task_id", task.getRtmTaskId());
		setDueDateParams.put("taskseries_id", task.getRtmTimeSeriesId());
		setDueDateParams.put("timeline", timelineId);
		setDueDateParams.put("list_id", task.getRtmListId());
		setDueDateParams.put("due", convertJodaDateTimeToString(task.getDueDate()));
		parseXML(getRtmMethodUri("rtm.tasks.setDueDate", setDueDateParams));
	}

	/**
	 * Update a task's url.
	 * 
	 * @param timelineId
	 * @param task
	 * @throws RtmServerException
	 * @throws UnsupportedEncodingException
	 */
	private void updateUrl(final String timelineId, final RtmTaskDto task)
			throws RtmServerException, UnsupportedEncodingException {
		final TreeMap<String, String> setUrlParams = new TreeMap<String, String>();
		setUrlParams.put("task_id", task.getRtmTaskId());
		setUrlParams.put("taskseries_id", task.getRtmTimeSeriesId());
		setUrlParams.put("timeline", timelineId);
		setUrlParams.put("list_id", task.getRtmListId());
		setUrlParams.put("url", task.getUrl());
		parseXML(getRtmMethodUri("rtm.tasks.setURL", setUrlParams));
	}

	/**
	 * Add tags to a task.
	 * 
	 * @param timelineId
	 * @param task
	 * @throws RtmServerException
	 * @throws UnsupportedEncodingException
	 */
	private void addTags(final String timelineId, final RtmTaskDto task, final Set<String> tags) throws RtmServerException,
			UnsupportedEncodingException {
		final TreeMap<String, String> addTagsParams = new TreeMap<String, String>();
		addTagsParams.put("task_id", task.getRtmTaskId());
		addTagsParams.put("taskseries_id", task.getRtmTimeSeriesId());
		addTagsParams.put("timeline", timelineId);
		addTagsParams.put("list_id", task.getRtmListId());
		addTagsParams.put("tags", StringUtils.join(tags, ","));
		parseXML(getRtmMethodUri("rtm.tasks.addTags", addTagsParams));
	}

	/**
	 * Add a note to a task.
	 * 
	 * @param timelineId
	 * @param task
	 * @throws RtmServerException
	 * @throws UnsupportedEncodingException
	 */
	private void addNote(final String timelineId, final RtmTaskDto task, final NoteDto note) throws RtmServerException,
			UnsupportedEncodingException {
		final TreeMap<String, String> addNoteParams = new TreeMap<String, String>();
		addNoteParams.put("task_id", task.getRtmTaskId());
		addNoteParams.put("taskseries_id", task.getRtmTimeSeriesId());
		addNoteParams.put("timeline", timelineId);
		addNoteParams.put("list_id", task.getRtmListId());
		addNoteParams.put("note_title", note.getTitle());
		addNoteParams.put("note_text", note.getBody());
		parseXML(getRtmMethodUri("rtm.tasks.notes.add", addNoteParams));
	}

	/**
	 * Mark a task as completed or incomplete.
	 * 
	 * @param timelineId
	 * @param task
	 * @throws RtmServerException
	 */
	private void updateCompleteFlag(final String timelineId, final RtmTaskDto task) throws RtmServerException {
		try {
			final TreeMap<String, String> setCompletedParams = new TreeMap<String, String>();
			setCompletedParams.put("task_id", task.getRtmTaskId());
			setCompletedParams.put("taskseries_id", task.getRtmTimeSeriesId());
			setCompletedParams.put("timeline", timelineId);
			setCompletedParams.put("list_id", task.getRtmListId());
			if (task.isCompleted()) {
				parseXML(getRtmMethodUri("rtm.tasks.complete", setCompletedParams));
			} else {
				parseXML(getRtmMethodUri("rtm.tasks.uncomplete", setCompletedParams));
			}
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to add task", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<TaskDto> getAllTasks() throws Exception {
		try {
			final List<TaskDto> results = new ArrayList<TaskDto>();
			final TreeMap<String, String> params = new TreeMap<String, String>();
			final Document response = parseXML(getRtmMethodUri("rtm.tasks.getList", params));
			final List<Node> taskListList = response.selectNodes("/rsp/tasks/list");
			for (final Node listNode : taskListList) {
				final Node listIdNode = listNode.selectSingleNode("@id");
				final List<Node> taskSeriesNodesList = listNode.selectNodes("taskseries");
				for (final Node taskSeriesNode : taskSeriesNodesList) {
					final Node timeSeriesIdNode = taskSeriesNode.selectSingleNode("@id");
					final Node lastModifiedNode = taskSeriesNode.selectSingleNode("@modified");
					final Node nameNode = taskSeriesNode.selectSingleNode("@name");
					final Node idNode = taskSeriesNode.selectSingleNode("task/@id");
					final Node dueNode = taskSeriesNode.selectSingleNode("task/@due");
					final Node completedNode = taskSeriesNode.selectSingleNode("task/@completed");
					final RtmTaskDto rtmTask = new RtmTaskDto();
					rtmTask.setRtmTaskId(idNode.getText());
					rtmTask.setRtmTimeSeriesId(timeSeriesIdNode.getText());
					rtmTask.setRtmListId(listIdNode.getText());
					rtmTask.setLastModified(convertStringToJodaDateTime(lastModifiedNode.getText()));
					rtmTask.setName(nameNode.getText());
					rtmTask.setDueDate(convertStringToJodaDateTime(dueNode.getText()));
					rtmTask.setCompleted(StringUtils.isNotEmpty(completedNode.getText()));
					final List<Node> tagNodes = taskSeriesNode.selectNodes("tags/tag");
					for (final Node tagNode : tagNodes) {
						rtmTask.addTag(tagNode.getText());
					}
					final List<Node> noteNodes = taskSeriesNode.selectNodes("notes/note");
					for (final Node noteNode : noteNodes) {
						final NoteDto note = new NoteDto();
						note.setTitle(noteNode.selectSingleNode("@title").getText());
						note.setBody(noteNode.getText());
						rtmTask.addNote(note);
						if (note.getTitle().equals(EXCHANGE_ID_NOTE_TITLE)) {
							rtmTask.setExchangeId(note.getBody());
						}
					}
					results.add(rtmTask);
				}
			}
			return results;
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to add task", e);
		}
	}

	private boolean checkToken() throws RtmServerException {
		try {
			final Document response = parseXML(getRtmMethodUri("rtm.auth.checkToken"));
			final Node tokenNode = response.selectSingleNode("/rsp/auth/token");
			final Node usernameNode = response.selectSingleNode("/rsp/auth/user/@username");
			LOG.info("Connected to Remember The Milk as " + usernameNode.getText());
			return tokenNode.getText().equals(settings.getInternalSetting(INTERNAL_SETTING_AUTH_TOKEN));
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to check token", e);
		}
	}

	private URI getRtmMethodUri(final String methodName) throws UnsupportedEncodingException {
		return getRtmMethodUri(methodName, new TreeMap<String, String>());
	}

	private URI getRtmMethodUri(final String methodName, final TreeMap<String, String> params) throws UnsupportedEncodingException {
		params.put("method", methodName);
		params.put("auth_token", settings.getInternalSetting(INTERNAL_SETTING_AUTH_TOKEN));
		return getRtmUri(REST_METHOD_PATH, params);
	}

	private URI getRtmUri(final String uriPath, final TreeMap<String, String> params) throws UnsupportedEncodingException {
		params.put("api_key", RTM_API_KEY);
		final StringBuilder uriString = new StringBuilder("http://" + REST_HOST + uriPath + "?");
		for (final String key : params.keySet()) {
			uriString.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8")).append("&");
		}
		uriString.append("api_sig").append("=").append(getApiSig(params));
		return URI.create(uriString.toString());
	}

	private Document parseXML(final URI uri) throws RtmServerException {
		final SAXReader reader = new SAXReader();
		final Document response;
		try {
			response = reader.read(uri.toURL());
			final Node status = response.selectSingleNode("/rsp/@stat");
			if (status != null) {
				if (status.getText().equals("fail")) {
					final Node errCode = response.selectSingleNode("/rsp/err/@code");
					final Node errMessage = response.selectSingleNode("/rsp/err/@msg");
					throw new RtmServerException(Integer.valueOf(errCode.getText()), errMessage.getText());
				}
			}
			return response;
		} catch (final MalformedURLException e) {
			throw new RuntimeException("A malformed URL was specified", e);
		} catch (final DocumentException e) {
			throw new RuntimeException("There was a problem parsing the response from RTM", e);
		}
	}

	private String getApiSig(final TreeMap<String, String> params) {
		final StringBuilder rawString = new StringBuilder(RTM_SHARED_SECRET);
		for (final String key : params.keySet()) {
			rawString.append(key);
			rawString.append(params.get(key));
		}
		try {
			final byte[] bytesOfMessage = rawString.toString().getBytes("UTF-8");
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final byte[] thedigest = md.digest(bytesOfMessage);
			return new String(Hex.encodeHex(thedigest));
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to create API signature", e);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to create API signature", e);
		}
	}
	
	private String convertJodaDateTimeToString(final DateTime theDate) {
		final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		return fmt.print(theDate) + "T23:59:59Z";
	}

	private DateTime convertStringToJodaDateTime(final String theDate) {
		if (StringUtils.isEmpty(theDate)) {
			return null;
		}
		final DateTimeFormatter dateFormat = ISODateTimeFormat.dateTimeNoMillis();
		return new DateTime(dateFormat.parseDateTime(theDate).toDate());
	}
}
