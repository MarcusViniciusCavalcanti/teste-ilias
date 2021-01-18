package com.zonework.cadttee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class CadtteeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CadtteeApplication.class, args);
    }

}
