package com.zonework.cadttee.domain.queues.send.dto.batch;


import com.zonework.cadttee.domain.queues.send.dto.MessageToSend;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class BatchOfMessages {

	@Builder.Default
	private List<MessageToSend> messagesToSend = new ArrayList<>();

	public BatchOfMessages(List<MessageToSend> messagesToSend) {
		this.messagesToSend = messagesToSend;
	}

	void addMessage(MessageToSend message) {
		messagesToSend.add(message);
	}

}
