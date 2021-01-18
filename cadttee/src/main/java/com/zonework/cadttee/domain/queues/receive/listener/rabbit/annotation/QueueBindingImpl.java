package com.zonework.cadttee.domain.queues.receive.listener.rabbit.annotation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;

import java.lang.annotation.Annotation;

@Getter
@Setter
@ToString
public class QueueBindingImpl implements QueueBinding {

	private Boolean declare = true;
	private Boolean ignoreDeclarationExceptions = false;

	private ExchangeImpl exchange;
	private QueueImpl queue;

	@Override
	public Class<? extends Annotation> annotationType() {
		return QueueBinding.class;
	}

	@Override
	public Queue value() {
		return queue;
	}

	@Override
	public Exchange exchange() {
		return exchange;
	}

	@Override
	public String[] key() {
		return new String[]{""};
	}

	@Override
	public String ignoreDeclarationExceptions() {
		return ignoreDeclarationExceptions.toString();
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
