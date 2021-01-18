package com.zonework.atm.domain.queues.receive.listener.rabbit.annotation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;

import java.lang.annotation.Annotation;

@Setter
@Getter
@ToString
public class ExchangeImpl implements Exchange {

	private String name;

	private String type = "fanout";

	private Boolean ignoreDeclarationExceptions = false;

	private Boolean declare = true;

	@Override
	public Class<? extends Annotation> annotationType() {
		return Exchange.class;
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
	public String type() {
		return type;
	}

	@Override
	public String durable() {
		return "true";
	}

	@Override
	public String autoDelete() {
		return "false";
	}

	@Override
	public String internal() {
		return "false";
	}

	@Override
	public String ignoreDeclarationExceptions() {
		return ignoreDeclarationExceptions.toString();
	}

	@Override
	public String delayed() {
		return "false";
	}

	@Override
	public Argument[] arguments() {
		return new Argument[0];
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
