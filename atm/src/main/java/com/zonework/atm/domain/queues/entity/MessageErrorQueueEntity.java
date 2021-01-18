package com.zonework.atm.domain.queues.entity;




import com.zonework.atm.domain.queues.receive.error.handler.generic.MessageError;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "MESSAGE_ERROR_QUEUE")
public class MessageErrorQueueEntity extends MessageError {

}
