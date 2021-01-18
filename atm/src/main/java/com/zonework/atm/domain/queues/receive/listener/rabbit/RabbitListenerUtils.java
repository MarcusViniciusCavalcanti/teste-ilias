package com.zonework.atm.domain.queues.receive.listener.rabbit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RabbitListenerUtils {

	public static String listenerId(String queueName) {
		return "rabbitListener" + queueName;
	}

}
