package com.zonework.cadttee.domain.queues.configuration;



import com.zonework.cadttee.domain.queues.configuration.properties.MessagingProperties;
import com.zonework.cadttee.domain.queues.receive.error.handler.generic.config.GenericMessageErrorHandlerConfiguration;
import com.zonework.cadttee.domain.queues.receive.listener.ListenerConfiguration;
import com.zonework.cadttee.domain.queues.receive.listener.MessagingListenersBeanDefinitionRegistrar;
import com.zonework.cadttee.domain.queues.send.MessageSendingConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MessagingListenersBeanDefinitionRegistrar.class,
	MessageSendingConfiguration.class,
	ListenerConfiguration.class,
	GenericMessageErrorHandlerConfiguration.class})
public class MessagingConfiguration {

	@Bean
	public MessagingProperties messagingProperties() {
		return new MessagingProperties();
	}

}
