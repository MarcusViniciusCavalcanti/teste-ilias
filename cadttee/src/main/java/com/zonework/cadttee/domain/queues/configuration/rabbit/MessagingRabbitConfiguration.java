package com.zonework.cadttee.domain.queues.configuration.rabbit;

import com.zonework.cadttee.domain.queues.configuration.properties.PropertiesNames;
import com.zonework.cadttee.domain.queues.receive.listener.rabbit.RabbitListenerManager;
import com.zonework.cadttee.domain.queues.send.rabbit.RabbitMessageSender;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = PropertiesNames.RABBIT_ACTIVATION, havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MessagingRabbitProperties.class)
public class MessagingRabbitConfiguration {

	public static final String RABBIT_LISTENERS_CONNECTION_BEAN_ID = "rabbit_conn_listeners";

	public static final String RABBIT_SENDERS_CONNECTION_BEAN_ID = "rabbit_conn_senders";

	@Bean(RABBIT_LISTENERS_CONNECTION_BEAN_ID)
	@Primary
	public CachingConnectionFactory rabbitListenersConnectionFactory(MessagingRabbitProperties messagingRabbitProperties,
																	 ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) throws Exception {
		return getRabbitConnectionFactory(messagingRabbitProperties, connectionNameStrategy);
	}

	@Bean(RABBIT_SENDERS_CONNECTION_BEAN_ID)
	public CachingConnectionFactory rabbitSendersConnectionFactory(MessagingRabbitProperties messagingRabbitProperties,
																   ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) throws Exception {
		return getRabbitConnectionFactory(messagingRabbitProperties, connectionNameStrategy);
	}

	@Bean
	public RabbitMessageSender rabbitMessageSender() {
		return new RabbitMessageSender();
	}

	@Bean("rabbitListenerManager")
	public RabbitListenerManager rabbitListenerManager() {
		return new RabbitListenerManager();
	}

	private static CachingConnectionFactory getRabbitConnectionFactory(MessagingRabbitProperties messagingRabbitProperties,
																	   ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) throws Exception {
		var propertyMapper = PropertyMapper.get();
		var messagingRabbitPropertiesConnection = messagingRabbitProperties.getConnection();
		var cachingConnectionFactory = new CachingConnectionFactory(
				getRabbitConnectionFactoryBean(messagingRabbitPropertiesConnection).getObject());

		propertyMapper.from(messagingRabbitPropertiesConnection::determineAddresses).to(cachingConnectionFactory::setAddresses);
		propertyMapper.from(messagingRabbitPropertiesConnection::isPublisherReturns).to(cachingConnectionFactory::setPublisherReturns);
		propertyMapper.from(messagingRabbitPropertiesConnection::getPublisherConfirmType).whenNonNull().to(cachingConnectionFactory::setPublisherConfirmType);

		var channel = messagingRabbitPropertiesConnection.getCache().getChannel();
		propertyMapper.from(channel::getSize).whenNonNull().to(cachingConnectionFactory::setChannelCacheSize);
		propertyMapper.from(channel::getCheckoutTimeout).whenNonNull().as(Duration::toMillis)
				.to(cachingConnectionFactory::setChannelCheckoutTimeout);

		var connection = messagingRabbitPropertiesConnection.getCache().getConnection();
		propertyMapper.from(connection::getMode).whenNonNull().to(cachingConnectionFactory::setCacheMode);
		propertyMapper.from(connection::getSize).whenNonNull().to(cachingConnectionFactory::setConnectionCacheSize);
		propertyMapper.from(connectionNameStrategy::getIfUnique).whenNonNull().to(cachingConnectionFactory::setConnectionNameStrategy);

		return cachingConnectionFactory;
	}

	private static RabbitConnectionFactoryBean getRabbitConnectionFactoryBean(RabbitProperties properties) {
		var propertyMapper = PropertyMapper.get();
		var rabbitConnectionFactoryBean = new RabbitConnectionFactoryBean();

		propertyMapper.from(properties::determineHost).whenNonNull().to(rabbitConnectionFactoryBean::setHost);
		propertyMapper.from(properties::determinePort).to(rabbitConnectionFactoryBean::setPort);
		propertyMapper.from(properties::determineUsername).whenNonNull().to(rabbitConnectionFactoryBean::setUsername);
		propertyMapper.from(properties::determinePassword).whenNonNull().to(rabbitConnectionFactoryBean::setPassword);
		propertyMapper.from(properties::determineVirtualHost).whenNonNull().to(rabbitConnectionFactoryBean::setVirtualHost);
		propertyMapper.from(properties::getRequestedHeartbeat).whenNonNull().asInt(Duration::getSeconds)
				.to(rabbitConnectionFactoryBean::setRequestedHeartbeat);

		var ssl = properties.getSsl();
		if (ssl.determineEnabled()) {
			rabbitConnectionFactoryBean.setUseSSL(true);
			propertyMapper.from(ssl::getAlgorithm).whenNonNull().to(rabbitConnectionFactoryBean::setSslAlgorithm);
			propertyMapper.from(ssl::getKeyStoreType).to(rabbitConnectionFactoryBean::setKeyStoreType);
			propertyMapper.from(ssl::getKeyStore).to(rabbitConnectionFactoryBean::setKeyStore);
			propertyMapper.from(ssl::getKeyStorePassword).to(rabbitConnectionFactoryBean::setKeyStorePassphrase);
			propertyMapper.from(ssl::getTrustStoreType).to(rabbitConnectionFactoryBean::setTrustStoreType);
			propertyMapper.from(ssl::getTrustStore).to(rabbitConnectionFactoryBean::setTrustStore);
			propertyMapper.from(ssl::getTrustStorePassword).to(rabbitConnectionFactoryBean::setTrustStorePassphrase);
			propertyMapper.from(ssl::isValidateServerCertificate)
					.to(validate -> rabbitConnectionFactoryBean.setSkipServerCertificateValidation(!validate));
			propertyMapper.from(ssl::getVerifyHostname).to(rabbitConnectionFactoryBean::setEnableHostnameVerification);
		}
		propertyMapper.from(properties::getConnectionTimeout).whenNonNull().asInt(Duration::toMillis)
				.to(rabbitConnectionFactoryBean::setConnectionTimeout);
		rabbitConnectionFactoryBean.afterPropertiesSet();

		return rabbitConnectionFactoryBean;
	}

}
