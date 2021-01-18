package com.zonework.cadttee.domain.queues.entity;

import com.zonework.cadttee.domain.queues.receive.error.handler.generic.MessageError;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "MESSAGE_ERROR_QUEUE")
public class MessageErrorQueue extends MessageError {

}
