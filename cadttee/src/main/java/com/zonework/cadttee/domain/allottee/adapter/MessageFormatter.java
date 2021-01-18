package com.zonework.cadttee.domain.allottee.adapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

@Component
public class MessageFormatter {

	private ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setTimeZone(TimeZone.getDefault());
		objectMapper.registerModule(new JavaTimeModule());
	}

	public String formatMessage(Object dto) {
		try {
			return objectMapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error formatting message", e);
		}
	}

	public <T> T parseMessage(String message, Class<T> valueType) {
		try {
			return objectMapper.readValue(message, valueType);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format("Error parsing message %s", message), e);
		}
	}

	public <T> T parseMessage(byte[] message, Class<T> valueType) {
		try {
			return objectMapper.readValue(message, valueType);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error parsing message %s", message), e);
		}
	}

}
