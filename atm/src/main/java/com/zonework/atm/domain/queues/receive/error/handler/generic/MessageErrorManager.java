package com.zonework.atm.domain.queues.receive.error.handler.generic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageErrorManager {

	Page<MessageError> find(LocalDateTime startDateTimeUtc,
			LocalDateTime endDateTimeUtc,
			String description,
			Pageable pageable);

	void reprocessError(UUID errorId);

	void clearError(UUID errorId);

	void reprocessAllErrorsOfDescription(String description);

	void cleanAllErrorsOfDescription(String description);

}
