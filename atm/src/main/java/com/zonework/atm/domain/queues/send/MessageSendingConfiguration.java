package com.zonework.atm.domain.queues.send;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = MessageSendingConfiguration.class)
public class MessageSendingConfiguration {

}
