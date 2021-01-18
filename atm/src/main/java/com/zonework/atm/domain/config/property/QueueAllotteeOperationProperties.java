package com.zonework.atm.domain.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zonework.atm.queues.allottee.cad-allottee-operation")
@Getter
@Setter
public class QueueAllotteeOperationProperties extends AbstractQueueProperties { }
