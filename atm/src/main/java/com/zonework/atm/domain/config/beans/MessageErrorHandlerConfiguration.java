package com.zonework.atm.domain.config.beans;

import com.zonework.atm.domain.queues.entity.MessageErrorQueueEntity;
import com.zonework.atm.domain.queues.receive.error.handler.generic.MessageErrorHandlerSpecification;
import com.zonework.atm.domain.queues.receive.error.handler.generic.MessageErrorRepository;
import com.zonework.atm.domain.queues.repository.DefaultMessageErrorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageErrorHandlerConfiguration {

    @Bean
    public MessageErrorHandlerSpecification<MessageErrorQueueEntity> messageErrorQueueEntityMessageErrorHandlerSpecification(
        DefaultMessageErrorRepository defaultMessageErrorRepository) {

        return new MessageErrorQueueEntityMessageErrorHandlerSpecification(defaultMessageErrorRepository);
    }

    private static final class MessageErrorQueueEntityMessageErrorHandlerSpecification
        implements MessageErrorHandlerSpecification<MessageErrorQueueEntity> {

        private final DefaultMessageErrorRepository defaultMessageErrorRepository;

        private MessageErrorQueueEntityMessageErrorHandlerSpecification(DefaultMessageErrorRepository defaultMessageErrorRepository) {
            this.defaultMessageErrorRepository = defaultMessageErrorRepository;
        }

        @Override
        public MessageErrorRepository<MessageErrorQueueEntity> errorRepository() {
            return defaultMessageErrorRepository;
        }

        @Override
        public Class<MessageErrorQueueEntity> messageErrorClass() {
            return MessageErrorQueueEntity.class;
        }

    }

}
