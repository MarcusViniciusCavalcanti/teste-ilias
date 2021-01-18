package com.zonework.atm.domain.queues.send.dto.batch;


import com.zonework.atm.domain.queues.send.dto.MessageSendingResult;
import lombok.Builder;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The result of sending a batch of messages.
 */
@Builder
@ToString // TODO refatorar
public class BatchOfMessagesSendingResult {

	/**
	 * Whether the sending of all messages in the batch were successfull.
	 */
	private final boolean allMessagesWereSuccesfull;

	/**
	 * The result of each individual message, in the same order of the corresponding {@link BatchOfMessages}.
	 */
	@Builder.Default
	private final List<MessageSendingResult> individualResults = new ArrayList<>();

	public static BatchOfMessagesSendingResult resultWithAllSuccessful(int amountOfMessages) {
		List<MessageSendingResult> individualResults =
			IntStream.range(0, amountOfMessages)
				.mapToObj(i -> MessageSendingResult.SUCCESSFUL_RESULT)
				.collect(Collectors.toList());

		return builder()
				.allMessagesWereSuccesfull(true)
				.individualResults(individualResults)
				.build();
	}

	/**
	 * Return whether the sending of all messages in the batch were successfull.
	 *
	 * @return whether the sending of all messages in the batch were successfull.
	 */
	public boolean isAllMessagesWereSuccesfull() {
		return allMessagesWereSuccesfull;
	}

	/**
	 * The result of each individual message, in the same order of the corresponding {@link BatchOfMessages}.
	 *
	 * @return the result of each individual message, in the same order of the corresponding {@link BatchOfMessages}.
	 */
	public List<MessageSendingResult> getIndividualResults() {
		return individualResults;
	}


}
