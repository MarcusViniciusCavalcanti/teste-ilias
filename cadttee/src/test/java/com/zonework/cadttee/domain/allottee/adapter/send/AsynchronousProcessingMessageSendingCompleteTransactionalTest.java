package com.zonework.cadttee.domain.allottee.adapter.send;

import com.zonework.cadttee.domain.config.property.QueueCompleteTransactionalProperties;
import com.zonework.cadttee.structure.dto.CompleteTransactionalTriggerDTO;
import com.zonework.cadttee.structure.dto.OperationEnum;
import com.zonework.cadttee.structure.dto.TransactionalDTO;
import com.zonework.cadttee.structure.dto.TransactionalTypeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsynchronousProcessingMessageSendingCompleteTransactionalTest {

    @Mock
    private QueueCompleteTransactionalProperties queueAllotteeProperties;

    @InjectMocks
    private AsynchronousProcessingMessageSendingCompleteTransactional processing;

    @Test
    void shouldBuildExctlyMessage() {
        var transactional = TransactionalDTO.builder()
            .value(10.0)
            .transactionalType(TransactionalTypeDTO.INCREMENT)
            .id(1)
            .build();

        var message = processing.buildMessage(transactional, OperationEnum.UPDATE);

        assertTrue(message instanceof CompleteTransactionalTriggerDTO);

    }
}
