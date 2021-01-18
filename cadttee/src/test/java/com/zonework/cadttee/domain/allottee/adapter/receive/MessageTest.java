package com.zonework.cadttee.domain.allottee.adapter.receive;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class MessageTest implements Message<String> {

    @Override
    public String getPayload() {
        return "null";
    }

    @Override
    public MessageHeaders getHeaders() {
        return null;
    }
}
