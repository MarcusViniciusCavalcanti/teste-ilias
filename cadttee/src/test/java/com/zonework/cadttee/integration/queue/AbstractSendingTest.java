package com.zonework.cadttee.integration.queue;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.history.repository.HistoryRepository;
import com.zonework.cadttee.integration.rest.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@ActiveProfiles("rabbitmq")
public abstract class AbstractSendingTest extends AbstractIntegrationTest {

    @Autowired
    protected AllotteeRepository allotteeRepository;

    @Autowired
    protected HistoryRepository historyRepository;

    @Autowired
    private TransactionTemplate txTemplate;

    @AfterEach
    void cleanDatabase() {
        historyRepository.deleteAll();
        allotteeRepository.deleteAll();
    }

    @Test
    void sendingMessage() {
        var registrationPending = StatusAllotteEnum.ACTIVE;

        var statusMock = new StatusAllotte();
        statusMock.setValue(registrationPending.getStatusEnumName());
        statusMock.setId(registrationPending.getStatusID());

        var mockAllottee = new Allottee();
        mockAllottee.setStatus(statusMock);
        mockAllottee.setName("Name");
        mockAllottee.setRetirementBalance(BigDecimal.TEN);
        mockAllottee.setEmail("email@email.com");
        mockAllottee.setAmoutYears(10);
        mockAllottee.setCpf("12345678901");

        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                var allottee = allotteeRepository.saveAndFlush(mockAllottee);
                sending(allottee);
            }
        });

        assertion(mockAllottee);
    }

    abstract void sending(Allottee allottee);

    abstract void assertion(Allottee mockAllottee);
}
