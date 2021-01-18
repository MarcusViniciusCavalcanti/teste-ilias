package com.zonework.cadttee.domain.queues.receive.listener.rabbit.annotation;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.amqp.rabbit.annotation.Argument;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
@ToString
public class ArgumentImpl implements Argument {

	private final String name;

	private final String value;

	private final Class<?> type;

	@Override
	public Class<? extends Annotation> annotationType() {
		return Argument.class;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public String type() {
		return type.getName();
	}

}
