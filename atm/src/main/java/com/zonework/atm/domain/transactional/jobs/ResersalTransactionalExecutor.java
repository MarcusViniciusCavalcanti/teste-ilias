package com.zonework.atm.domain.transactional.jobs;

import com.zonework.atm.domain.config.job.AbstractJobExecutor;
import com.zonework.atm.domain.transactional.repository.JobRepository;
import com.zonework.atm.domain.transactional.service.CreatorTransactional;
import com.zonework.atm.domain.transactional.service.GetterTransactionalsService;
import org.springframework.stereotype.Service;

@Service
public class ResersalTransactionalExecutor extends AbstractJobExecutor {

    private final GetterTransactionalsService getterTransactionalsService;
    private final CreatorTransactional creatorTransactional;

    public ResersalTransactionalExecutor(JobRepository jobRepository,
                                         GetterTransactionalsService getterTransactionalsService,
                                         CreatorTransactional creatorTransactional) {
        super(jobRepository);
        this.getterTransactionalsService = getterTransactionalsService;
        this.creatorTransactional = creatorTransactional;
    }

    @Override
    protected String getJobName() {
        return ReversalTransactionalJob.JOB_NAME;
    }

    @Override
    protected void execute() {
        var job = getJob();

        getterTransactionalsService.findExpiredTransactiona(job.getLastExecution())
            .forEach(creatorTransactional::createTransactionalReversal);

        updateLastExecute(job);
    }
}
