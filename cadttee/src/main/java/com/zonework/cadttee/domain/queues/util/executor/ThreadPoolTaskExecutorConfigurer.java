package com.zonework.cadttee.domain.queues.util.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadPoolTaskExecutorConfigurer {

	public static ThreadPoolTaskExecutor create(ThreadPoolTaskExecutorProperties properties) {
		return create(properties, true);
	}

	public static ThreadPoolTaskExecutor create(ThreadPoolTaskExecutorProperties properties, boolean initialize) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		configure(executor, properties);

		if (initialize) {
			executor.afterPropertiesSet();
		}

		return executor;
	}

	public static void configure(ThreadPoolTaskExecutor threadPoolTaskExecutor, ThreadPoolTaskExecutorProperties properties) {
		threadPoolTaskExecutor.setCorePoolSize(properties.getCorePoolSize());
		threadPoolTaskExecutor.setMaxPoolSize(properties.getMaxPoolSize());
		threadPoolTaskExecutor.setQueueCapacity(properties.getQueueCapacity());
		threadPoolTaskExecutor.setDaemon(properties.isDaemon());
		threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(properties.isWaitForJobsToCompleteOnShutdown());
		threadPoolTaskExecutor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
		threadPoolTaskExecutor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
		threadPoolTaskExecutor.setThreadNamePrefix(properties.getThreadNamePrefix());
	}

}
