package com.zonework.cadttee.domain.queues.receive.listener.rabbit;

public class RabbitListenerUtils {

	public static String listenerId(String queueName) {
		return "rabbitListener" + queueName
;	}

}
