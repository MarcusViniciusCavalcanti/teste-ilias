package com.zonework.atm.domain.queues.send.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageToSend {

	private final String body;

}
