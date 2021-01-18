package com.zonework.atm.domain.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("zonework.atm.queues.transactional.send")
public class QueueTransactionalSendProperties extends AbstractQueueProperties {

}
