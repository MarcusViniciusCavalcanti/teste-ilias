package com.zonework.cadttee.domain.queues.configuration.properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesNames {

	public static final String PREFIX = "application.messages";

	public static final String QUEUES = PREFIX + "." + "queues";

	public static final String QUEUE_NAME = "name";

	public static final String TOPIC = "topic";

	public static final String QUEUE_CONNECTION_TYPE = "connection";

	public static final String READ_PREFIX = "read";

	public static final String SIMPLE_LISTENER_BEAN_ID = READ_PREFIX + "." + "listener-bean-id";

	public static final String READ_QUEUE_DLQ_PREFIX = READ_PREFIX + "." + "dlq";

	public static final String DLQ_NAME = READ_QUEUE_DLQ_PREFIX + "." + "name";

	public static final String DLQ_ERROR_HANDLER = READ_QUEUE_DLQ_PREFIX + "." + "error-handler-bean-id";

	public static final String DLQ_PROCESS_TYPE = READ_QUEUE_DLQ_PREFIX + "." + "process-type";

	public static final String RABBIT_ACTIVATION = "application.messages.rabbit.enabled";

}
