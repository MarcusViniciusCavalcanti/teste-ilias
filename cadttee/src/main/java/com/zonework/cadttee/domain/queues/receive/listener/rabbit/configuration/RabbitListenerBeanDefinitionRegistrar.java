package com.zonework.cadttee.domain.queues.receive.listener.rabbit.configuration;


import com.zonework.cadttee.domain.queues.configuration.properties.ConnectionType;
import com.zonework.cadttee.domain.queues.configuration.properties.PropertiesNames;
import com.zonework.cadttee.domain.queues.configuration.rabbit.RabbitPropertiesNames;
import com.zonework.cadttee.domain.queues.receive.listener.AbstractListenerBeanDefinitionRegistrar;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.BaseRabbitListener;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.RabbitListenerUtils;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.annotation.ArgumentImpl;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.annotation.ExchangeImpl;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.annotation.QueueBindingImpl;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.annotation.QueueImpl;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.annotation.RabbitListenerImpl;
import com.zonework.cadttee.domain.queues.util.environment.EnvironmentArrayPropertiesReader;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Slf4j
public class RabbitListenerBeanDefinitionRegistrar extends AbstractListenerBeanDefinitionRegistrar {

	private final EnvironmentArrayPropertiesReader propertiesReader;

	private final Environment environment;

	public RabbitListenerBeanDefinitionRegistrar(EnvironmentArrayPropertiesReader queuePropertiesReader,
												 BeanDefinitionRegistry registry,
												 Environment environment) {
		super(queuePropertiesReader, registry, ConnectionType.RABBIT);

		propertiesReader = queuePropertiesReader;
		this.environment = environment;
	}

	@Override
	protected Class<?> createSpecificListenerType(String queueName,
												  EnvironmentArrayPropertiesReader arrayPropertiesReader) {
		var annotation = rabbitListenerImpl(queueName);

		return createRabbitSpecificListenerType(annotation);
	}

	@Override
	protected Class<?> createSpecificDlqListenerType(String dlqName,
													 EnvironmentArrayPropertiesReader propertiesReader) {
		throw new UnsupportedOperationException();
	}

	private static Class<?> createRabbitSpecificListenerType(RabbitListenerImpl rabbitListenerAnnotation) {
		return new ByteBuddy()
				  .subclass(BaseRabbitListener.class)
				  .method(ElementMatchers.named(BaseRabbitListener.METHOD_NAME))
				  	.intercept(SuperMethodCall.INSTANCE)
				  	.annotateMethod(rabbitListenerAnnotation)
				  .make()
				  .load(RabbitListenerBeanDefinitionRegistrar.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
				  .getLoaded();
	}

	// TODO Refatorar
	private RabbitListenerImpl rabbitListenerImpl(String queueName) {
		RabbitListenerImpl annotation;
		if (isTopic()) {
			annotation = annotationForTopic(queueName);
		} else {
			annotation = annotationForQueue(queueName);
		}

		log.info("Rabbit listener configurations for queue {}: {}", queueName, annotation);

		return annotation;
	}

	private RabbitListenerImpl annotationForQueue(String queueName) {
		var queueImpl = queueImpl(queueName);
		var annotation = initializeAnnotation(queueName);

		annotation.setQueuesToDeclare(new QueueImpl[]{queueImpl});

		return annotation;
	}

	// TODO Refatorar
	private RabbitListenerImpl annotationForTopic(String queueName) {
		var queueImpl = queueImpl(queueName);

		ExchangeImpl exchangeImpl = null;

		var declare = propertiesReader.getProperty(RabbitPropertiesNames.DECLARE, Boolean.class, true);
		var ignoreDeclarationExceptions = environment.getProperty(RabbitPropertiesNames.IGNORE_DECLARED_EXCEPTIONS, Boolean.class, false);

		var exchange = propertiesReader.getProperty(RabbitPropertiesNames.EXCHANGE);

		if (StringUtils.hasLength(exchange)) {
			exchangeImpl = new ExchangeImpl();
			exchangeImpl.setName(exchange);
			exchangeImpl.setIgnoreDeclarationExceptions(ignoreDeclarationExceptions);
			exchangeImpl.setDeclare(declare);
		}

		var queueBindingImpl = new QueueBindingImpl();
		queueBindingImpl.setQueue(queueImpl);
		queueBindingImpl.setExchange(exchangeImpl);
		queueBindingImpl.setDeclare(declare);
		queueBindingImpl.setIgnoreDeclarationExceptions(ignoreDeclarationExceptions);

		var annotation = initializeAnnotation(queueName);
		annotation.setQueueBindings(new QueueBindingImpl[] {queueBindingImpl});

		return annotation;
	}

	private RabbitListenerImpl initializeAnnotation(String queueName) {
		var annotation = new RabbitListenerImpl();

		annotation.setId(RabbitListenerUtils.listenerId(queueName));
		annotation.setAutoStartup(propertiesReader.getProperty(RabbitPropertiesNames.READ_AUTO_STARTUP, Boolean.class, true));
		annotation.setConcurrency(propertiesReader.getProperty(RabbitPropertiesNames.READ_CONCURRENCY, String.class, ""));

		return annotation;
	}

	private QueueImpl queueImpl(String queueName) {
		var queueImpl = new QueueImpl();
		queueImpl.setName(queueName);
		queueImpl.setDeclare(propertiesReader.getProperty(RabbitPropertiesNames.DECLARE, Boolean.class, true));
		queueImpl.setIgnoreDeclarationExceptions(environment.getProperty(
			RabbitPropertiesNames.IGNORE_DECLARED_EXCEPTIONS, Boolean.class, false));

		queueImpl.setArguments(queueArguments(queueName));
		return queueImpl;
	}

	private ArgumentImpl[] queueArguments(String queueName) {
		var arguments = new ArgumentImpl[2];

		var retryQueuesSuffix = environment.getProperty(RabbitPropertiesNames.RETRY_QUEUES_SUFFIX, ".retry");

		arguments[0] = new ArgumentImpl("x-dead-letter-exchange", "", String.class);
		arguments[1] = new ArgumentImpl("x-dead-letter-routing-key", queueName + retryQueuesSuffix, String.class);

		return arguments;
	}

	private boolean isTopic() {
		return propertiesReader.getProperty(PropertiesNames.TOPIC, Boolean.class, false);
	}
}
