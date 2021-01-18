package com.zonework.cadttee.domain.queues.receive.error.handler.generic;

public interface MessageErrorHandlerSpecification<T extends MessageError> {

	MessageErrorRepository<T> errorRepository();

	Class<T> messageErrorClass();

}
