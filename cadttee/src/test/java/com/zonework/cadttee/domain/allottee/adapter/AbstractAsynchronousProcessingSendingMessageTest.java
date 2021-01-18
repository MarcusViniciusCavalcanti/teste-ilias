package com.zonework.cadttee.domain.allottee.adapter;

import com.zonework.cadttee.domain.config.property.AbstractQueueProperties;
import com.zonework.cadttee.domain.queues.send.MessageSender;
import com.zonework.cadttee.structure.dto.OperationEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractAsynchronousProcessingSendingMessageTest {

    @Mock
    private MessageFormatter messageFormatter;

    @Mock
    private MessageSender messageSender;

    @Test
    void sendMessage() {
        var sender = new AbstractAsynchronousProcessingSendingMessage<>() {
            @Override
            public AbstractQueueProperties getQueue() {
                return new AbstractQueueProperties() {

                    @Override
                    public String getName() {
                        return "queue name";
                    }
                };
            }

            @Override
            public Object buildMessage(Object messageObject, OperationEnum operation) {
                return new Object();
            }
        };

        ReflectionTestUtils.setField(sender, "messageFormatter", messageFormatter);
        ReflectionTestUtils.setField(sender, "messageSender", messageSender);

        when(messageFormatter.formatMessage(any())).thenReturn("message");
        doNothing()
            .when(messageSender)
            .sendBeforeTransactionCommit(anyString(), anyString());

        sender.scheduleAsynchronousProcessing(new Object(), OperationEnum.CREATION);

        verify(messageFormatter).formatMessage(any());
        verify(messageSender).sendBeforeTransactionCommit(anyString(), anyString());
    }
}
