package com.zonework.cadttee.domain.allottee.delegate;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.service.ExecutorStatusAllotteeService;
import com.zonework.cadttee.structure.delegate.ProcessOperationDelegate;
import com.zonework.cadttee.structure.message.ReasonMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProcessPendingRegistration implements ProcessOperationDelegate {

    private final ExecutorStatusAllotteeService statusAllotteeService;

    @Override
    public void process(Allottee allottee) {
        statusAllotteeService.adjustmentStatusAllottee(allottee,
            StatusAllotteEnum.ACTIVE, ReasonMessages.MESSAGE_001.getCode());
    }
}
