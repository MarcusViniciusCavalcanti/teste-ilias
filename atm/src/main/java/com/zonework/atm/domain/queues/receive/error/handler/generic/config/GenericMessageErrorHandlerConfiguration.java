package com.zonework.atm.domain.queues.receive.error.handler.generic.config;


import com.zonework.atm.domain.queues.receive.error.handler.MessageErrorHandler;
import com.zonework.atm.domain.queues.receive.error.handler.generic.GenericMessageErrorHandler;
import com.zonework.atm.domain.queues.receive.error.handler.generic.MessageError;
import com.zonework.atm.domain.queues.receive.error.handler.generic.MessageErrorHandlerSpecification;
import com.zonework.atm.domain.queues.receive.error.handler.generic.MessageErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericMessageErrorHandlerConfiguration <T extends MessageError> {

	public static final String GENERIC_MESSAGE_ERROR_HANDLER_BEAN_ID = "MessagingGenericMessageErrorHandler";

	@Autowired(required = false)
	private MessageErrorHandlerSpecification<T> spec;

	@Bean(GENERIC_MESSAGE_ERROR_HANDLER_BEAN_ID)
	@ConditionalOnBean(MessageErrorHandlerSpecification.class)
	public MessageErrorHandler messageErrorHandler() {
		return new GenericMessageErrorHandler<T>(spec);
	}

	@Bean
	@ConditionalOnBean(MessageErrorHandlerSpecification.class)
	public MessageErrorManager messageErrorManager() {
		return (MessageErrorManager) messageErrorHandler();
	}
}
