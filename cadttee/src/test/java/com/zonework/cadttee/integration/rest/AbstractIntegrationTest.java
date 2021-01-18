package com.zonework.cadttee.integration.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.restassured3.RestAssuredOperationPreprocessorsConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
@Testcontainers
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest {

    static final RabbitMQContainer MY_RABBIT_CONTAINER;

    static {
        MY_RABBIT_CONTAINER = new RabbitMQContainer("rabbitmq:3.8.3-management").withExposedPorts(5672, 15672);
        MY_RABBIT_CONTAINER.start();
    }

    @LocalServerPort
    protected int port;

    protected RequestSpecification spec;

    protected RestAssuredOperationPreprocessorsConfigurer configurer;

    protected void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
        configurer = documentationConfiguration(restDocumentationContextProvider)
            .operationPreprocessors();

        spec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentationContextProvider))
            .build();
    }

    protected OperationResponsePreprocessor getResponsePreprocessor() {
        return preprocessResponse(
            prettyPrint(),
            removeHeaders("Vary", "X-Frame-Options", "Date", "Cache-Control", "X-XSS", "Pragma")
        );
    }

    protected OperationRequestPreprocessor getRequestPreprocessor() {
        return preprocessRequest(prettyPrint());
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                "zonework.messages.rabbit.connection.host=" + MY_RABBIT_CONTAINER.getContainerIpAddress(),
                "zonework.messages.rabbit.connection.port=" + MY_RABBIT_CONTAINER.getMappedPort(5672)
            );
            values.applyTo(applicationContext);
        }
    }
}
