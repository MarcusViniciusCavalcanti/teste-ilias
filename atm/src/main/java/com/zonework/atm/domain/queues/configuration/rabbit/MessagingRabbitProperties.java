package com.zonework.atm.domain.queues.configuration.rabbit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "zonework.messages.rabbit")
public class MessagingRabbitProperties {

	@NestedConfigurationProperty
	private RabbitProperties connection = new RabbitProperties();

}
