package com.zonework.atm.domain.queues.receive.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ListenerConfiguration {

	@Bean
	@Primary
	public DelegateListenerManager delegateListenerManager() {
		return new DelegateListenerManager();
	}
}
