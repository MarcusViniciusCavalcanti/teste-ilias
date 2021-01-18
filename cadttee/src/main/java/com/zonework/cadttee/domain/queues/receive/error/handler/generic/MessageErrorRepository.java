package com.zonework.cadttee.domain.queues.receive.error.handler.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MessageErrorRepository<T extends MessageError> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

}
