package com.zonework.atm.domain.config.job;

import com.zonework.atm.domain.transactional.entity.JobApplication;
import com.zonework.atm.domain.transactional.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobExecutor {
    private static final String JOB_NOT_FOUND = "Job %s not found.";

    private final JobRepository jobRepository;

    public <T> void execute(JobSupplier<T> supplier) {
        var job = getJob(supplier.getJobName());


        log.info("Running job {}.", job.getName());

        List<T> list = supplier.getElements(job.getLastExecution());

        log.info("Request to job: {} return amount items: {}.", job.getName(), list.size());
        list.forEach(supplier::executeForElement);

        job.setLastExecution(supplier.getLastExecution(list));
        jobRepository.saveAndFlush(job);
        log.info("Stop job {}.", job.getName());

    }

    private JobApplication getJob(String jobName) {
        return jobRepository.findByName(jobName).orElseThrow(() -> new IllegalStateException(format(jobName)));
    }

    private static String format(String jobName) {
        return String.format(JOB_NOT_FOUND, jobName);
    }
}
