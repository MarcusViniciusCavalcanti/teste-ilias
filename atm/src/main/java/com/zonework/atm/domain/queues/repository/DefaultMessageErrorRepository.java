package com.zonework.atm.domain.queues.repository;


import com.zonework.atm.domain.queues.entity.MessageErrorQueueEntity;
import com.zonework.atm.domain.queues.receive.error.handler.generic.MessageErrorRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultMessageErrorRepository extends MessageErrorRepository<MessageErrorQueueEntity> {}
