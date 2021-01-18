package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.structure.message.ReasonMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExecutorExclutionAllotteeService {
    private static final String FOUND = "Allottee not found by id: %d";
    private final AllotteeRepository allotteeRepository;
    private final ExecutorStatusAllotteeService executorStatusAllotteeService;


    @Transactional
    public void remove(Integer id) {
        var allottee = allotteeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format(FOUND, id)));

        executorStatusAllotteeService.adjustmentStatusAllottee(
            allottee,
            StatusAllotteEnum.EXCLUDED, ReasonMessages.MESSAGE_005.getCode());
    }

}
