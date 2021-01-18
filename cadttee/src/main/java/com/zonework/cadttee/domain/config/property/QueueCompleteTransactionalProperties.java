package com.zonework.cadttee.domain.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("zonework.cad.queues.transactional.complete-process")
public class QueueCompleteTransactionalProperties extends AbstractQueueProperties{}
