package com.zonework.atm.domain.allottee.delegate;

import com.zonework.atm.struture.dto.AllotteeActiveTrigger;
import org.springframework.stereotype.Service;

@FunctionalInterface
@Service
public interface OperationDelegate {

    void process(AllotteeActiveTrigger activeTrigger);

}
