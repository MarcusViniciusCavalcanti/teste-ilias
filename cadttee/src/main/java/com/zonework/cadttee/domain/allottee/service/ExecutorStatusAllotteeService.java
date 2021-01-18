package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingMessageSendingOperationAllotte;
import com.zonework.cadttee.domain.allottee.adapter.send.AsynchronousProcessingWithTopicForSendingMessage;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.allottee.repository.StatusAllotteeRepository;
import com.zonework.cadttee.domain.history.service.CreatorHistoryOperationAllotteeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExecutorStatusAllotteeService {
    private static final String STATUS_TO_REASON_CODE =
        "Running update status allottee {} on status {}, to reason code: {}";
    private final AllotteeRepository allotteeRepository;
    private final StatusAllotteeRepository statusAllotteeRepository;
    private final CreatorHistoryOperationAllotteeService creatorHistoryOperationAllotteeService;
    private final AsynchronousProcessingWithTopicForSendingMessage processingWithTopicForSendingMessage;
    private final AsynchronousProcessingMessageSendingOperationAllotte processingMessageSendingOperationAllotte;

    @Transactional(propagation = Propagation.MANDATORY)
    public Allottee adjustmentStatusAllottee(Allottee allottee, StatusAllotteEnum statusAllotteEnum, String code) {
        var statusEntity = getStatusAllotte(allottee, statusAllotteEnum, code);

        updateStatusEntity(allottee, statusEntity);

        creatorHistoryOperationAllotteeService.create(allottee, code, statusAllotteEnum.operation().name());

        verifierPublishInQueue(allottee, statusAllotteEnum);

        return allottee;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Allottee adjustmentStatusAllottee(Allottee allottee,
                                             StatusAllotteEnum statusAllotteEnum,
                                             String code,
                                             Object... args) {
        var statusEntity = getStatusAllotte(allottee, statusAllotteEnum, code);

        updateStatusEntity(allottee, statusEntity);

        creatorHistoryOperationAllotteeService.create(allottee, code, statusAllotteEnum.operation().name(), args);

        verifierPublishInQueue(allottee, statusAllotteEnum);

        return allottee;
    }

    private void updateStatusEntity(Allottee allottee, StatusAllotte statusEntity) {
        allottee.setStatus(statusEntity);
        log.debug("Saving in database new allottee {}", allottee);
        allotteeRepository.save(allottee);
    }

    private void verifierPublishInQueue(Allottee allottee, StatusAllotteEnum statusAllotteEnum) {
        if (Boolean.TRUE.equals(allottee.getStatus().allowsAsynchronousProcessing())) {
            processingMessageSendingOperationAllotte.scheduleAsynchronousProcessing(allottee, statusAllotteEnum.operation());
        }

        if (Boolean.TRUE.equals(allottee.getStatus().publish())) {
            processingWithTopicForSendingMessage.scheduleAsynchronousProcessing(allottee, statusAllotteEnum.operation());
        }
    }

    private StatusAllotte getStatusAllotte(Allottee allottee, StatusAllotteEnum statusAllotteEnum, String code) {
        log.debug(STATUS_TO_REASON_CODE, allottee, statusAllotteEnum, code);
        return statusAllotteeRepository.findById(statusAllotteEnum.getStatusID())
            .orElseThrow(() -> new EntityNotFoundException("Status Allottee not found to id" + statusAllotteEnum.getStatusID()));
    }

}

