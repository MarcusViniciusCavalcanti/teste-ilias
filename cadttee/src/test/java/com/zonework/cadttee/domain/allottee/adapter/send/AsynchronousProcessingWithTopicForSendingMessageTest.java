package com.zonework.cadttee.domain.allottee.adapter.send;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.config.property.QueueAllotteeProperties;
import com.zonework.cadttee.structure.dto.MessageOperationAllotteTriggerDTO;
import com.zonework.cadttee.structure.dto.OperationEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsynchronousProcessingWithTopicForSendingMessageTest {

    @Mock
    private QueueAllotteeProperties queueAllotteeProperties;

    @InjectMocks
    private AsynchronousProcessingWithTopicForSendingMessage sendingMessage;

    @Test
    void shouldReturnExactlyMessage() {
        var status = new StatusAllotte();
        status.setValue(StatusAllotteEnum.REGISTRATION_PENDING.getStatusEnumName());
        var allottee = new Allottee();
        allottee.setId(1);
        allottee.setStatus(status);

        var message = sendingMessage.buildMessage(allottee, OperationEnum.CREATION);

        assertTrue(message instanceof MessageOperationAllotteTriggerDTO);
    }
}
