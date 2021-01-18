package com.zonework.atm.domain.queues.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(MessagingConfiguration.class)
@ConditionalOnProperty(name = "application.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class MessagingAutoConfiguration {

}
