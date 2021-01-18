package com.zonework.atm.domain.transactional.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReversalTransactionalJob {

    protected static final String JOB_NAME = "TRANSACTIONA_RESERVAL";
    private final ResersalTransactionalExecutor executor;

    @Scheduled(fixedDelayString = "${application.job.reserval.fixedDelay}")
    public void reservalTransactiona() {
        log.info("Running job to expired transactional");
        executor.execute();
    }
}
