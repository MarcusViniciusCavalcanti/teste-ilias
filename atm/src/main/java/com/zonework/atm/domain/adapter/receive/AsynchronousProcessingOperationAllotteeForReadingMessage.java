package com.zonework.atm.domain.adapter.receive;

import com.zonework.atm.domain.adapter.MessageFormatter;
import com.zonework.atm.domain.allottee.delegate.OperationDelegate;
import com.zonework.atm.domain.queues.receive.listener.MessageListener;
import com.zonework.atm.struture.dto.AllotteeActiveTrigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component("asynchronousProcessingOperationAllotteeForReadingMessage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingOperationAllotteeForReadingMessage implements MessageListener {
    private final BeanFactory beanFactory;
    private final MessageFormatter messageFormatter;

    @Transactional
    @Override
    public void onMessage(Message<String> message) {
        log.info("Consumed from queue {}", message);
        var allotteTrigger = messageFormatter.parseMessage(message.getPayload(), AllotteeActiveTrigger.class);

        try {
            var operationDelegate = beanFactory.getBean(allotteTrigger.getOperation().name(), OperationDelegate.class);
            operationDelegate.process(allotteTrigger);
        } catch (BeansException exception) {
            log.warn("Operation is invalid {}", allotteTrigger.getOperation());
        }
    }
}
