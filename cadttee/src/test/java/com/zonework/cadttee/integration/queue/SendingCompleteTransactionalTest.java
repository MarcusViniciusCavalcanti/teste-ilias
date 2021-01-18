package com.zonework.cadttee.integration.queue;

import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingMessageSendingCompleteTransactional;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.structure.dto.OperationEnum;
import com.zonework.cadttee.structure.dto.TransactionalDTO;
import com.zonework.cadttee.structure.dto.TransactionalTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendingCompleteTransactionalTest extends AbstractSendingTest {

    @Autowired
    private AsynchronousProcessingMessageSendingCompleteTransactional sending;

    @Autowired
    private CompleteConsumerTest completeConsumerTest;

    @Override
    void sending(Allottee allottee) {
        var transactional = TransactionalDTO.builder()
            .id(1)
            .transactionalType(TransactionalTypeDTO.INCREMENT)
            .value(10.0)
            .idAllottee(allottee.getId())
            .build();
        sending.scheduleAsynchronousProcessing(transactional, OperationEnum.UPDATE);
    }

    @Override
    void assertion(Allottee mockAllottee) {
        await().atMost(1, TimeUnit.SECONDS).until(() -> completeConsumerTest.isReceiveMessage());

        var message = completeConsumerTest.getMessage();

        assertAll(() -> {
            assertEquals(OperationEnum.UPDATE, message.getOperation(), "assert operation");
            assertEquals(1, message.getIdTransactional(), "assert id");
        });
    }

}
