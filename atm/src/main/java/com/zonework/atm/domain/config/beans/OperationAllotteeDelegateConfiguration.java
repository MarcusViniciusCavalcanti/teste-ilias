package com.zonework.atm.domain.config.beans;

import com.zonework.atm.domain.allottee.delegate.ExclusionOperationDelegate;
import com.zonework.atm.domain.allottee.delegate.OperationDelegate;
import com.zonework.atm.domain.allottee.delegate.UpdateOperationDelegate;
import com.zonework.atm.domain.allottee.delegate.UpdateStatusOperationDelegate;
import com.zonework.atm.domain.allottee.service.AllotteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperationAllotteeDelegateConfiguration {
    public static final String EXECLUSION_OPERATION = "EXCLUSION";
    public static final String UPDATE_OPERATION = "UPDATE";
    public static final String UPDATE_STATS_OPERATION = "UPDATE_STATUS";

    @Bean(EXECLUSION_OPERATION)
    public OperationDelegate operationExclusion(AllotteService allotteService) {
        return new ExclusionOperationDelegate(allotteService);
    }

    @Bean(UPDATE_OPERATION)
    public OperationDelegate operationUpdate(AllotteService allotteService) {
        return new UpdateOperationDelegate(allotteService);
    }

    @Bean(UPDATE_STATS_OPERATION)
    public OperationDelegate operationUpdateStatus(AllotteService allotteService) {
        return new UpdateStatusOperationDelegate(allotteService);
    }
}
