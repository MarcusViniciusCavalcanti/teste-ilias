package com.zonework.cadttee.domain.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class ConfigurationMessage {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:message/reason_messages");
        return messageSource;
    }
}
