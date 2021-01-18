package com.zonework.cadttee.domain.allottee.adapter.send;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.config.property.QueueAllotteeOperationProperties;
import com.zonework.cadttee.structure.dto.AllotteeActiveTrigger;
import com.zonework.cadttee.structure.dto.OperationEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsynchronousProcessingMessageSendingOperationAllotteTest {

    @Mock
    private QueueAllotteeOperationProperties queueAllotteeProperties;

    @InjectMocks
    private AsynchronousProcessingMessageSendingOperationAllotte processing;

    @Test
    void shouldReturnExactlyMessage() {
        var allottee = new Allottee();
        allottee.setId(1);
        allottee.setStatus(new StatusAllotte());

        var message = processing.buildMessage(allottee, OperationEnum.CREATION);

        assertTrue(message instanceof AllotteeActiveTrigger);
    }
}
