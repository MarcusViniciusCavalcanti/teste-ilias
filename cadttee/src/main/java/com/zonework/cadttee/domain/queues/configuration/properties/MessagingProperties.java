package com.zonework.cadttee.domain.queues.configuration.properties;

import com.zonework.cadttee.domain.queues.util.StreamUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix = "application.messages")
@Getter
@Setter
public class MessagingProperties {

	private List<QueueDefinition> queues = new ArrayList<>();

	private RabbitGeneralProperties rabbit = new RabbitGeneralProperties();

	private ErrorsProperties errors = new ErrorsProperties();

	@Getter
	@Setter
	public static class QueueDefinition {
		private String name;
		private String description;
		private ConnectionType connection;
		private boolean topic = false;
		private JmsProperties jms;
		private RabbitProperties rabbit;

		@NestedConfigurationProperty
		private ReadingProperties read = new ReadingProperties();

		public String getQueueType() {
			return topic ? "topic" : "queue";
		}
	}

	@Getter
	@Setter
	public static class RabbitProperties {
		private String exchange;

		private boolean declare = true;

		private Integer delay = 0;
	}

	@Getter
	@Setter
	public static class RabbitGeneralProperties {

		private String retryQueuesSuffix = ".retry";

		private String delayQueuesSuffix = ".delay";

		private boolean ignoreDeclarationExceptions = false;
	}

	@Getter
	@Setter
	public static class ReadingProperties {

		private String listenerBeanId;

		private DlqProperties dlq = new DlqProperties();

		@NestedConfigurationProperty
		private ReadRabbitProperties rabbit = new ReadRabbitProperties();
	}

	@Getter
	@Setter
	public static class ReadRabbitProperties {

		private Boolean autoStartup = true;

		private String concurrency;

		private Integer retryInterval = 30000;

		private Integer maxRetries = 10;
	}

	@Getter
	@Setter
	public static class DlqProperties {

		private String name;

		private String errorHandlerBeanId;
	}

	public QueueDefinition getQueueDefinition(String queueName) {
		return queues
				.stream()
				.filter(q -> queueName.equals(q.getName()))
				.collect(StreamUtils.toSingleton("There is more than one queue with the name " + queueName));
	}

	@Getter
	@Setter
	public static class ErrorsProperties {

		private Integer rootCauseMessageMaxLength = 400;

	}
}
