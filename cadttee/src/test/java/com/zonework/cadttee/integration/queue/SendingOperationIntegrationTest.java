package com.zonework.cadttee.integration.queue;

import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingMessageSendingOperationAllotte;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.structure.dto.OperationEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendingOperationIntegrationTest extends AbstractSendingTest {
    @Autowired
    private AsynchronousProcessingMessageSendingOperationAllotte sendingMessage;

    @Autowired
    private OperationConsumerTest operationConsumerTest;

    @Override
    void sending(Allottee allottee) {
        sendingMessage.scheduleAsynchronousProcessing(allottee, OperationEnum.UPDATE_STATUS);
    }

    @Override
    void assertion(Allottee mockAllottee) {
        await().atMost(10, TimeUnit.SECONDS).until(() -> operationConsumerTest.isReceiveMessage());

        var message = operationConsumerTest.getMessage();

        assertAll(() -> {
            assertEquals(OperationEnum.UPDATE_STATUS, message.getOperation(), "assert operation");
            assertEquals(mockAllottee.getId(), message.getId(), "assert id");
            assertEquals(mockAllottee.getStatus().getValue(), message.getValue(), "assert status");
        });
    }

}
