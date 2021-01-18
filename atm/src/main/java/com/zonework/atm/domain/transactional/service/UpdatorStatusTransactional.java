package com.zonework.atm.domain.transactional.service;

import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.repository.TransactionalRepository;
import com.zonework.atm.struture.dto.TransactionalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdatorStatusTransactional {
    private final TransactionalRepository transactionalRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateStatusBy(Integer id, StatusTransactionalEnum statusTransactionalEnum) {
        log.info("Update complete transactional by id: {}", id);
        var transactional = transactionalRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Allottee by id %d not found", id)));

        transactional.setStatus(statusTransactionalEnum);
        transactionalRepository.saveAndFlush(transactional);
    }
}
