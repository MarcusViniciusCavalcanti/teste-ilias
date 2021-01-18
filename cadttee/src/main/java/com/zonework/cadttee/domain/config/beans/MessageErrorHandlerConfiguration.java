package com.zonework.cadttee.domain.config.beans;

import com.zonework.cadttee.domain.queues.repository.DefaultMessageErrorRepository;
import com.zonework.cadttee.domain.queues.entity.MessageErrorQueue;
import com.zonework.cadttee.domain.queues.receive.error.handler.generic.MessageErrorHandlerSpecification;
import com.zonework.cadttee.domain.queues.receive.error.handler.generic.MessageErrorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageErrorHandlerConfiguration {

    @Bean
    public MessageErrorHandlerSpecification<MessageErrorQueue> messageErrorQueueEntityMessageErrorHandlerSpecification(
        DefaultMessageErrorRepository defaultMessageErrorRepository) {

        return new MessageErrorQueueEntityMessageErrorHandlerSpecification(defaultMessageErrorRepository);
    }

    private static class MessageErrorQueueEntityMessageErrorHandlerSpecification
        implements MessageErrorHandlerSpecification<MessageErrorQueue> {

        private final DefaultMessageErrorRepository defaultMessageErrorRepository;

        public MessageErrorQueueEntityMessageErrorHandlerSpecification(DefaultMessageErrorRepository defaultMessageErrorRepository) {
            this.defaultMessageErrorRepository = defaultMessageErrorRepository;
        }

        @Override
        public MessageErrorRepository<MessageErrorQueue> errorRepository() {
            return defaultMessageErrorRepository;
        }

        @Override
        public Class<MessageErrorQueue> messageErrorClass() {
            return MessageErrorQueue.class;
        }

    }

}
