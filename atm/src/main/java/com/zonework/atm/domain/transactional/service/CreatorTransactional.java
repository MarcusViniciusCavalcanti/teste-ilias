package com.zonework.atm.domain.transactional.service;

import com.zonework.atm.application.input.NewTransactionaInput;
import com.zonework.atm.domain.adapter.send.AsyncrhonousProcessingTransactionalReceiveForSending;
import com.zonework.atm.domain.allottee.repository.AllotteeRepository;
import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.domain.transactional.entity.TypeTranctional;
import com.zonework.atm.domain.transactional.factory.FactoryTransactional;
import com.zonework.atm.domain.transactional.repository.TransactionalRepository;
import com.zonework.atm.domain.transactional.validator.ValidatorTransactional;
import com.zonework.atm.struture.dto.ResponseTransactionalDTO;
import com.zonework.atm.struture.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreatorTransactional {
    private final TransactionalRepository transactionalRepository;
    private final AsyncrhonousProcessingTransactionalReceiveForSending receiveForSending;
    private final AllotteeRepository allotteeRepository;

    @Transactional
    public ResponseTransactionalDTO createNewTransactional(NewTransactionaInput input) {
        var allottee = allotteeRepository.findById(input.getId()).orElseThrow(EntityNotFoundException::new);

        if (ValidatorTransactional.validate(allottee, input)) {
            var transactional =
                FactoryTransactional.createEntityByAllotteAndInputAndStatus(input, allottee, StatusTransactionalEnum.ACCEPTED);

            TransactionalBalance transactionaSaved = saveAndSending(transactional);
            return FactoryTransactional.createResponse(transactionaSaved);

        }

        throw new BusinessException("Transactiona invalid");
    }

    @Transactional
    public void createTransactionalReversal(TransactionalBalance transactionalBalance) {
        var clone = FactoryTransactional.cloneBaseData(transactionalBalance);
        clone.setStatus(StatusTransactionalEnum.ACCEPTED);
        clone.setType(TypeTranctional.REVERSAL);
        saveAndSending(clone);
    }

    private TransactionalBalance saveAndSending(TransactionalBalance transactional) {
        var transactionaSaved = transactionalRepository.saveAndFlush(transactional);

        receiveForSending.scheduleAsynchronousProcessing(transactional);
        return transactionaSaved;
    }
}
