package com.zonework.cadttee.domain.queues.util.executor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThreadPoolTaskExecutorProperties {

	private int corePoolSize = 3;

	private int maxPoolSize = 3;

	private int queueCapacity = 0;

	private boolean daemon = false;

	private boolean waitForJobsToCompleteOnShutdown = false;

	private int awaitTerminationSeconds = 20;

	private int keepAliveSeconds = 60;

	private String threadNamePrefix = "MessagingConsumption";

}
