package com.zonework.atm.domain.queues.configuration.rabbit;

import com.zonework.atm.domain.queues.configuration.properties.PropertiesNames;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RabbitPropertiesNames {

	public static final String RABBIT_GENERAL_PROPERTIES_PREFIX = PropertiesNames.PREFIX + "." + "rabbit";

	public static final String IGNORE_DECLARED_EXCEPTIONS = RABBIT_GENERAL_PROPERTIES_PREFIX + "." + "ignore-declaration-exceptions";

	public static final String RETRY_QUEUES_SUFFIX = RABBIT_GENERAL_PROPERTIES_PREFIX + "." + "retry-queues-suffix";

	public static final String RABBIT_PREFIX = "rabbit";

	public static final String DECLARE = RABBIT_PREFIX + "." + "declare";

	public static final String EXCHANGE = RABBIT_PREFIX + "." + "exchange";

	private static final String READ_RABBIT_PREFIX = PropertiesNames.READ_PREFIX + "." + "rabbit";

	public static final String READ_AUTO_STARTUP = READ_RABBIT_PREFIX + "." + "auto-startup";

	public static final String READ_CONCURRENCY = READ_RABBIT_PREFIX + "." + "concurrency";

}
