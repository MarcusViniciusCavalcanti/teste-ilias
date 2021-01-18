package com.zonework.atm.domain.allottee.delegate;

import com.zonework.atm.domain.allottee.service.AllotteService;
import com.zonework.atm.struture.dto.AllotteeActiveTrigger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExclusionOperationDelegate implements OperationDelegate{

    private final AllotteService allotteService;

    @Override
    public void process(AllotteeActiveTrigger activeTrigger) {
        allotteService.exclusion(activeTrigger.getId());
    }

}
