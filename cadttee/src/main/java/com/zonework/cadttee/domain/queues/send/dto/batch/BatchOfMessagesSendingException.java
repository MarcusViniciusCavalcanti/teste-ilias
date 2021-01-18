package com.zonework.cadttee.domain.queues.send.dto.batch;

import lombok.AllArgsConstructor;

/**
 * An exception when sending a batch of messages (<b>IMPORTANT</b>: some of the
 * messages in the batch may have been sent successfully, it must be checked the results
 * with {@link #getResult()}.
 *
 */
@SuppressWarnings("serial")
@AllArgsConstructor
public class BatchOfMessagesSendingException extends Exception {

	private final BatchOfMessagesSendingResult result;

	/**
	 * Gets the result of sending a batch of messages.
	 *
	 * @return a {@link BatchOfMessagesSendingResult} with the result of the batch sending.
	 */
	public BatchOfMessagesSendingResult getResult() {
		return result;
	}

}
