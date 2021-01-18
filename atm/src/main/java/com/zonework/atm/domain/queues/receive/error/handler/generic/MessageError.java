package com.zonework.atm.domain.queues.receive.error.handler.generic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@ToString
public abstract class MessageError {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Integer id;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "TIMESTAMP_UTC")
	private LocalDateTime timestampUtc;

	@Column(name = "ORIGINAL_QUEUE_NAME")
	private String originalQueueName;

	@Column(name = "DLQ_QUEUE_NAME")
	private String dlqQueueName;

	@Column(name="MESSAGE_CONTENT")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String contents;

	@Column(name="EXCEPTION_ROOT_CAUSE_MESSAGE")
	private String exceptionRootCauseMessage;

	@Column(name="EXCEPTION_STACK_TRACE")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String exceptionStackTrace;

}
