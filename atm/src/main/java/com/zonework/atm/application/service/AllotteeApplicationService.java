package com.zonework.atm.application.service;

import com.zonework.atm.domain.allottee.service.AllotteService;
import com.zonework.atm.struture.dto.ResponseAllotteeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AllotteeApplicationService {

    private final AllotteService allotteService;

    public ResponseAllotteeDTO getById(Integer id) {
        return allotteService.getById(id);
    }
}
