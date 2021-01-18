package com.zonework.atm.domain.allottee.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zonework.atm.domain.allottee.entity.Allottee;
import com.zonework.atm.domain.allottee.entity.StatusAllotteEnum;
import com.zonework.atm.domain.allottee.repository.AllotteeRepository;
import com.zonework.atm.domain.factory.AllotteeFactory;
import com.zonework.atm.domain.transactional.service.CalculatorBalance;
import com.zonework.atm.struture.dto.AllotteeActiveTrigger;
import com.zonework.atm.struture.dto.AllotteeDTO;
import com.zonework.atm.struture.dto.ResponseAllotteeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AllotteService {
    private final AllotteeRepository allotteeRepository;
    private final CalculatorBalance calculatorBalance;


    @Transactional(propagation = Propagation.MANDATORY)
    public void create(AllotteeDTO allotteeDTO) {
        var allotte = new Allottee();
        allotte.setId(allotteeDTO.getId());
        allotte.setAmoutYears(allotteeDTO.getAmoutYears());
        allotte.setName(allotteeDTO.getName());
        allotte.setCpf(allotteeDTO.getCpf());
        allotte.setStatus(StatusAllotteEnum.PROCESS_PENDING);
        allotte.setRetirementBalance(BigDecimal.ZERO);

        calculatorBalance.calulcateRetirement(allotte);

        allotteeRepository.saveAndFlush(allotte);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void active(Integer id) {
        var allottee = allotteeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        allottee.setStatus(StatusAllotteEnum.ACTIVE);

        allotteeRepository.saveAndFlush(allottee);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void exclusion(Integer id) {
        var allottee = allotteeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        allottee.setStatus(StatusAllotteEnum.EXCLUDED);
        allotteeRepository.saveAndFlush(allottee);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void update(AllotteeActiveTrigger activeTrigger) {
        var allottee = allotteeRepository.findById(activeTrigger.getId()).orElseThrow(EntityNotFoundException::new);
        var mapper = new ObjectMapper();

        try {
            var dto = mapper.readValue(activeTrigger.getValue(), AllotteeDTO.class);
            allottee.setAmoutYears(dto.getAmoutYears());
            allottee.setName(dto.getName());

            var allotee = calculatorBalance.calulcateRetirement(allottee);

            allotteeRepository.saveAndFlush(allottee);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseAllotteeDTO getById(Integer id) {
        return allotteeRepository.findById(id)
            .map(AllotteeFactory::buildResponse)
            .orElseThrow(EntityNotFoundException::new);
    }

}
