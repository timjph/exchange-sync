package com.zerodes.exchangesync.tasksource.rtm;

/**
 * Special exception class that includes details about a Remember The Milk error code and message.
 */
public class RtmServerException extends Exception {
	private static final long serialVersionUID = 1L;

	private final int rtmErrorCode;
	private final String rtmMessage;

	/**
	 * Constructor for instantiating an RtmServerException.
	 *
	 * @param rtmErrorCode the Remember The Milk error code
	 * @param rtmMessage the Remember The Milk error message
	 */
	public RtmServerException(final int rtmErrorCode, final String rtmMessage) {
		super("Error " + String.valueOf(rtmErrorCode) + ": " + rtmMessage);
		this.rtmErrorCode = rtmErrorCode;
		this.rtmMessage = rtmMessage;
	}

	public String getRtmMessage() {
		return rtmMessage;
	}

	public int getRtmErrorCode() {
		return rtmErrorCode;
	}
}
