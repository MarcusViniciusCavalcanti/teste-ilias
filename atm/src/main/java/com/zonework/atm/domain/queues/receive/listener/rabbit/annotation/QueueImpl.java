package com.zonework.atm.domain.queues.receive.listener.rabbit.annotation;

import lombok.Setter;
import lombok.ToString;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Queue;

import java.lang.annotation.Annotation;

@Setter
@ToString
public class QueueImpl implements Queue {

	private String name;
	private Boolean ignoreDeclarationExceptions = false;
	private Boolean declare = true;
	private ArgumentImpl[] arguments = new ArgumentImpl[0];

	@Override
	public Class<? extends Annotation> annotationType() {
		return Queue.class;
	}

	@Override
	public String value() {
		return name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String durable() {
		return "true";
	}

	@Override
	public String exclusive() {
		return "";
	}

	@Override
	public String autoDelete() {
		return "";
	}

	@Override
	public String ignoreDeclarationExceptions() {
		return ignoreDeclarationExceptions.toString();
	}

	@Override
	public Argument[] arguments() {
		return arguments;
	}

	@Override
	public String declare() {
		return declare.toString();
	}

	@Override
	public String[] admins() {
		return new String[0];
	}

}
