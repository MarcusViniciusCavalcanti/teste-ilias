package com.zonework.atm.domain.queues.send.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class MessageSendingResult {

	public static final MessageSendingResult SUCCESSFUL_RESULT = new MessageSendingResult(true, null);

	private final boolean succesfull;

	private final String errorMessage;

}
