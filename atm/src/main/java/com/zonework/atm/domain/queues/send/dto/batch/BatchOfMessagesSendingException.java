package com.zonework.atm.domain.queues.send.dto.batch;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class BatchOfMessagesSendingException extends Exception {

	private final BatchOfMessagesSendingResult result;

	public BatchOfMessagesSendingResult getResult() {
		return result;
	}

}
