package com.zonework.cadttee.domain.queues.send.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageSendRequestDTO {

	private final String queue;

	private final String message;

}
