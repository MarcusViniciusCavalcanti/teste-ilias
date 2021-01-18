package com.zonework.cadttee.integration.queue;

import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingWithTopicForSendingMessage;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.structure.dto.OperationEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SengindResgistrationIntegrationTest extends AbstractSendingTest {
    @Autowired
    private AsynchronousProcessingWithTopicForSendingMessage sendingMessage;

    @Autowired
    private RegistrationConsumerTest registrationConsumerTest;

    @Override
    void sending(Allottee allottee) {
        sendingMessage.scheduleAsynchronousProcessing(allottee, OperationEnum.CREATION);
    }

    @Override
    void assertion(Allottee mockAllottee) {
        await().atMost(1, TimeUnit.SECONDS).until(() -> registrationConsumerTest.isReceiveMessage());

        var message = registrationConsumerTest.getMessage();

        assertAll(() -> {
            var messageAllotteeDTO = message.getAllotteeDTO();
            assertEquals(mockAllottee.getName(), messageAllotteeDTO.getName(), "assert name");
            assertEquals(mockAllottee.getAmoutYears(), messageAllotteeDTO.getAmoutYears(), "assert amount years");
            assertEquals(mockAllottee.getCpf(), messageAllotteeDTO.getCpf(), "assert cpf");
            assertEquals(mockAllottee.getEmail(), messageAllotteeDTO.getEmail(), "assert email");
            assertEquals(mockAllottee.getStatus().getValue(), messageAllotteeDTO.getStatus(), "assert status");
            assertEquals(mockAllottee.getId(), messageAllotteeDTO.getId(), "assert id");
        });
    }
}
