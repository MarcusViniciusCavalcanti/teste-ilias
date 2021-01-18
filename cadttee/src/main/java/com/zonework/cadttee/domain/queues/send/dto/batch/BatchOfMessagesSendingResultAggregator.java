package com.zonework.cadttee.domain.queues.send.dto.batch;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO refatorar
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BatchOfMessagesSendingResultAggregator {

	public static BatchOfMessagesSendingResult aggregate(BatchOfMessagesSendingResult... results) {
		var allSuccessful = new AtomicBoolean(true);

		var completeResults = Arrays.stream(results)
			.peek(result -> {
				if (!result.isAllMessagesWereSuccesfull()) {
					allSuccessful.set(false);
				}
			})
			.filter(result -> !result.isAllMessagesWereSuccesfull())
			.map(BatchOfMessagesSendingResult::getIndividualResults)
			.reduce(new ArrayList<>(), (acctualList, individualResults) -> {
				acctualList.addAll(individualResults);
				return acctualList;
			});

		return BatchOfMessagesSendingResult.builder()
				.allMessagesWereSuccesfull(allSuccessful.get())
				.individualResults(completeResults)
				.build();
	}

}
