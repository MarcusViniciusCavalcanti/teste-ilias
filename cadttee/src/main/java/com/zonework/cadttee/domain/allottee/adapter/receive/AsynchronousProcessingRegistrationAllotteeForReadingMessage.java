package com.zonework.cadttee.domain.allottee.adapter.receive;

import com.zonework.cadttee.domain.allottee.adapter.MessageFormatter;
import com.zonework.cadttee.domain.allottee.delegate.ProcessPendingRegistration;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.queues.receive.listener.MessageListener;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
import com.zonework.cadttee.structure.dto.MessageOperationAllotteTriggerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Slf4j
@Component("asynchronousProcessingRegistrationAllotteeForReadingMessage")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsynchronousProcessingRegistrationAllotteeForReadingMessage implements MessageListener {
    private final MessageFormatter messageFormatter;
    private final ProcessPendingRegistration processPendingRegistration;
    private final AllotteeRepository allotteeRepository;

    @Transactional
    @Override
    public void onMessage(Message<String> message) {
        var activeTrigger = messageFormatter
            .parseMessage(message.getPayload(), MessageOperationAllotteTriggerDTO.class);

        log.info("Message consumed from queue: {}", message.getPayload());

        var allotteeDTO = activeTrigger.getAllotteeDTO();

        allotteeRepository.findById(allotteeDTO.getId())
            .ifPresentOrElse(completeProcessWithSuccess(), completeProcessWithError(activeTrigger, allotteeDTO));
    }

    private static Runnable completeProcessWithError(MessageOperationAllotteTriggerDTO activeTrigger, AllotteeDTO allotteeDTO) {
        return () -> log
            .warn("Allottee by CPF {} not found to operation {}", allotteeDTO.getCpf(), activeTrigger.getOperation());
    }

    private Consumer<Allottee> completeProcessWithSuccess() {
        return processPendingRegistration::process;
    }

    private static StatusAllotteEnum fromAllottee(Allottee allottee) {
        return StatusAllotteEnum.getById(allottee.getStatus().getId());
    }

}
