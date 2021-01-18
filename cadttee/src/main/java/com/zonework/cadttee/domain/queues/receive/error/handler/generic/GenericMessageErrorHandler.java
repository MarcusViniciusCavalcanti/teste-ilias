package com.zonework.cadttee.domain.queues.receive.error.handler.generic;

import com.zonework.cadttee.domain.queues.configuration.properties.MessagingProperties;
import com.zonework.cadttee.domain.queues.receive.error.handler.MessageErrorHandler;
import com.zonework.cadttee.domain.queues.send.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Slf4j
public class GenericMessageErrorHandler<T extends MessageError> implements MessageErrorManager, MessageErrorHandler {

	private final MessageErrorRepository<T> repo;

	private final Class<T> messageErrorClazz;

	private final String entityName;

	@Autowired
	private MessagingProperties messagingProperties;

	@Autowired
	private MessageSender messageSender;

	//@Autowired
	private MessageErrorCleaner messageErrorCleaner;

	@PersistenceContext
	private EntityManager em;

	public GenericMessageErrorHandler(MessageErrorHandlerSpecification<T> spec) {
		repo = spec.errorRepository();
		messageErrorClazz = spec.messageErrorClass();
		entityName = getEntityName();

		ThreadFactory threadFactory = new BasicThreadFactory.Builder()
				.daemon(false)
				.namingPattern("MessagesErrorsTasks-%d").build();
		Executor executor = Executors.newFixedThreadPool(2, threadFactory);
	}

	@Override
	public void handleError(Message<String> message, String originalQueueName, String dlqName, Exception ex) {
		log.info("Persisting error for message from queue {}, exception {}", originalQueueName, ex.getMessage());

		T messageError = instantiateEntity(message, originalQueueName, dlqName, ex);

		messageError = repo.save(messageError);

		log.debug("Saved message error: {}", messageError);
	}

	private T instantiateEntity(Message<String> message, String originalQueueName, String dlqName, Exception ex) {
		T messageError = instantiateMessageError();

		messageError.setTimestampUtc(LocalDateTime.now(ZoneId.of("UTC")));
		messageError.setOriginalQueueName(originalQueueName);
		messageError.setDlqQueueName(dlqName);
		messageError.setContents(message.getPayload());
		messageError.setExceptionRootCauseMessage(StringUtils.truncate(ExceptionUtils.getRootCauseMessage(ex),
				messagingProperties.getErrors().getRootCauseMessageMaxLength()));
		messageError.setExceptionStackTrace(ExceptionUtils.getStackTrace(ex));
		messageError.setDescription(getQueueDescription(originalQueueName));

		return messageError;
	}

	private T instantiateMessageError() {
		try {
			return messageErrorClazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Page<MessageError> find(LocalDateTime startDateTimeUtc,
								   LocalDateTime endDateTimeUtc,
								   String description,
								   Pageable pageable) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	@Transactional
	public void reprocessError(UUID errorId) {
		Optional<T> errorOpt = repo.findById(errorId);
		if (errorOpt.isPresent()) {
			T error = errorOpt.get();
			log.info("Reprocessing message error {}, resending to queue {}", errorId, error.getOriginalQueueName());
			messageSender.sendBeforeTransactionCommit(error.getOriginalQueueName(), error.getContents());
			clearError(errorId);
		}
	}

	@Override
	@Transactional
	public void clearError(UUID errorId) {
		log.info("Deleting message error {}", errorId);
		repo.deleteById(errorId);
	}

	@Override
	public void reprocessAllErrorsOfDescription(String description) {
		// Fazer assíncrono. Retornar um Future, delegando para um SimpleAsyncTaskExecutor por ex.

		// Dentro da implementação:
		//  - fazer um query by Example usando o repository.
		//  - fazer um loop onde para cada elemento é chamado reprocessError.
		//    Obs.: a implementação deve ser em outra classe para o @Transactional valer.

		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Transactional
	@Override
	public void cleanAllErrorsOfDescription(String description) {
		String delete = String.format(
				"DELETE FROM %s e " +
				"WHERE e.description = ?1", entityName);

		int amountDeleted = em.createQuery(delete)
			.setParameter(1, description)
			.executeUpdate();

		log.info("Deleted {} message errors for description {}", amountDeleted, description);
	}

	private String getQueueDescription(String originalQueueName) {
		String description = messagingProperties.getQueueDefinition(originalQueueName).getDescription();
		if (description == null) {
			description = originalQueueName;
		}
		return description;
	}

	private String getEntityName() {
		Entity annotation = messageErrorClazz.getAnnotation(Entity.class);
		Assert.notNull(annotation, String.format("Class %s is not annotated with @%s", messageErrorClazz, Entity.class.getSimpleName()));
		String name = annotation.name();
		if (name == null) {
			name = messageErrorClazz.getSimpleName();
		}
		return name;
	}

}
