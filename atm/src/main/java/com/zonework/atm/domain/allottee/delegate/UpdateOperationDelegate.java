package com.zonework.atm.domain.allottee.delegate;

import com.zonework.atm.domain.allottee.service.AllotteService;
import com.zonework.atm.struture.dto.AllotteeActiveTrigger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateOperationDelegate implements OperationDelegate{

    private final AllotteService allotteService;

    @Override
    public void process(AllotteeActiveTrigger activeTrigger) {
        allotteService.update(activeTrigger);
    }

}
