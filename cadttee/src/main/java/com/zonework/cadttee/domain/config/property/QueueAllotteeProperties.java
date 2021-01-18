package com.zonework.cadttee.domain.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zonework.cad.queues.allottee.cad-allottee")
@Getter
@Setter
public class QueueAllotteeProperties extends AbstractQueueProperties { }
