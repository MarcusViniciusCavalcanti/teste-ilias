package com.zonework.cadttee.domain.queues.repository;

import com.zonework.cadttee.domain.queues.entity.MessageErrorQueue;
import com.zonework.cadttee.domain.queues.receive.error.handler.generic.MessageErrorRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultMessageErrorRepository extends MessageErrorRepository<MessageErrorQueue> {}
