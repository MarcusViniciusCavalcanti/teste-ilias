package com.zonework.atm.domain.config.job;

import com.zonework.atm.domain.transactional.entity.JobApplication;
import com.zonework.atm.domain.transactional.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class AbstractJobExecutor {

    private static final String JOB_NOT_FOUND = "Job %s not found";
    private final JobRepository jobRepository;

    protected abstract String getJobName();

    protected abstract void execute();

    protected JobApplication getJob() {
        return jobRepository.findByName(getJobName()).orElseThrow(newIllegalStateExceptionSupplier(getJobName()));
    }

    protected void updateLastExecute(JobApplication job) {
        job.setLastExecution(LocalDateTime.now(ZoneOffset.UTC));
        jobRepository.saveAndFlush(job);
    }

    private static Supplier<IllegalStateException> newIllegalStateExceptionSupplier(String jobName) {
        return () -> new IllegalStateException(String.format(JOB_NOT_FOUND, jobName));
    }
}
