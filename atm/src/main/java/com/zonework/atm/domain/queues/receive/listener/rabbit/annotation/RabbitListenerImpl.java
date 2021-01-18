package com.zonework.atm.domain.queues.receive.listener.rabbit.annotation;

import lombok.Setter;
import lombok.ToString;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.lang.annotation.Annotation;

@Setter
@ToString
public class RabbitListenerImpl implements RabbitListener {

	private String id;

	private QueueImpl[] queuesToDeclare = new QueueImpl[0];

	private boolean exclusive;

	private String priority = "";

	private QueueBindingImpl[] queueBindings = new QueueBindingImpl[0];

	private String concurrency = "";

	private Boolean autoStartup = true;

	private String executor = "";

	private AcknowledgeMode ackMode;

	@Override
	public Class<? extends Annotation> annotationType() {
		return RabbitListener.class;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String containerFactory() {
		return "";
	}

	@Override
	public String[] queues() {
		return new String[0];
	}

	@Override
	public Queue[] queuesToDeclare() {
		return queuesToDeclare;
	}

	@Override
	public boolean exclusive() {
		return exclusive;
	}

	@Override
	public String priority() {
		return priority;
	}

	@Override
	public String admin() {
		return "";
	}

	@Override
	public QueueBinding[] bindings() {
		return queueBindings;
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public String returnExceptions() {
		return "";
	}

	@Override
	public String errorHandler() {
		return "";
	}

	@Override
	public String concurrency() {
		return concurrency;
	}

	@Override
	public String autoStartup() {
		return autoStartup.toString();
	}

	@Override
	public String executor() {
		return executor ;
	}

	@Override
	public String ackMode() {
		return ackMode == null ? "" : ackMode.name();
	}

	@Override
	public String replyPostProcessor() {
		return "";
	}

}
