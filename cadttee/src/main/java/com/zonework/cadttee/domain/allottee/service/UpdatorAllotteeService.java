package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.factory.AllotteeFactory;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.allottee.repository.AllotteeUpateRepository;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
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
public class UpdatorAllotteeService {
    private final AllotteeRepository allotteeRepository;
    private final AllotteeUpateRepository allotteeUpateRepository;
    private final ExecutorStatusAllotteeService executorStatusAllotteeService;

    @Transactional
    public AllotteeDTO editeAllottee(AllotteInput input, Integer id) {
        var allottee = allotteeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Allottee by id %d", id)));

        var oldValue = AllotteeFactory.clone(allottee, input);

        allottee.setName(input.getName());
        allottee.setEmail(input.getEmail());
        allottee.setCpf(input.getCpf());
        allottee.setAmoutYears(input.getAmountYears());

        executorStatusAllotteeService.adjustmentStatusAllottee(
            allottee,
            StatusAllotteEnum.ACTIVE,
            ReasonMessages.MESSAGE_003.getCode(),
            oldValue,
            allottee
        );

        return AllotteeFactory.buildEntityByAllottee(allottee);
    }
}
