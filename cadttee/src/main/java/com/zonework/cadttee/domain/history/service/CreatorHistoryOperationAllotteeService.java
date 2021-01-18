package com.zonework.cadttee.domain.history.service;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.history.entity.HistoryOperationAllottee;
import com.zonework.cadttee.domain.history.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreatorHistoryOperationAllotteeService {
    private static final String DEFAULT_MESSAGE = "Message code: %s / Params: %s";
    private static final String NO_ARG_TO_REASON = "Message code: %s";

    private final HistoryRepository historyRepository;
    private final ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;

    @Transactional(propagation = Propagation.MANDATORY)
    public void create(Allottee allottee, String code, String operation, Object ...args) {
        log.debug("Create history operation {} to allotte {}", operation, allottee);
        var historyOperationAllottee = new HistoryOperationAllottee();

        extratedReason(code, historyOperationAllottee, args);
        log.debug("reason is {}", historyOperationAllottee.getOperation());
        adjustmenetHistoryData(allottee, operation, historyOperationAllottee);
        saveNewHistory(historyOperationAllottee);
    }

    private void saveNewHistory(HistoryOperationAllottee historyOperationAllottee) {
        log.info("Saving in database history operation {}", historyOperationAllottee);
        historyRepository.save(historyOperationAllottee);
    }

    private static void adjustmenetHistoryData(Allottee allottee,
                                               String operation,
                                               HistoryOperationAllottee historyOperationAllottee) {
        historyOperationAllottee.setOperation(operation);
        historyOperationAllottee.setAllottee(allottee);
    }

    private void extratedReason(String code, HistoryOperationAllottee historyOperationAllottee, Object[] args) {
        try {
            var reason = reloadableResourceBundleMessageSource.getMessage(code, args, Locale.getDefault());
            log.debug("reason is {}", reason);
            historyOperationAllottee.setReason(reason);
        } catch (Exception ex) {
            buildDefaultReason(code, historyOperationAllottee, args);
        }
    }

    private static void buildDefaultReason(String code,
                                           HistoryOperationAllottee historyOperationAllottee,
                                           Object[] args) {
        log.warn("No message found for code {}", code);

        var reason = "No message";
        if (Objects.nonNull(args)) {
            var argsAsString = List.of(args).stream()
                .map(String::valueOf)
                .collect(Collectors.joining("|"));

            reason = String.format(DEFAULT_MESSAGE, code, argsAsString);
        } else {
            reason = String.format(NO_ARG_TO_REASON, code);
        }

        historyOperationAllottee.setReason(reason);
    }

}
