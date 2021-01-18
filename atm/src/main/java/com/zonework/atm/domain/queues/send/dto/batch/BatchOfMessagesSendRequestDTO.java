package com.zonework.atm.domain.queues.send.dto.batch;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BatchOfMessagesSendRequestDTO {

	private BatchOfMessages batchOfMessages;

	private String queue;

}
