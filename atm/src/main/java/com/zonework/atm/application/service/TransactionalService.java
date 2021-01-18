package com.zonework.atm.application.service;

import com.zonework.atm.application.input.NewTransactionaInput;
import com.zonework.atm.domain.transactional.service.CreatorTransactional;
import com.zonework.atm.domain.transactional.service.GetterTransactionalsService;
import com.zonework.atm.struture.dto.ResponseTransactionalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionalService {
    private final CreatorTransactional creatorTransactional;
    private final GetterTransactionalsService getterTransactionalsService;

    public ResponseTransactionalDTO createNewTransctional(NewTransactionaInput input) {
        return creatorTransactional.createNewTransactional(input);
    }

    public Page<ResponseTransactionalDTO> getAllTransactionaByAllotteeId(Integer idAllottee, Pageable pageable) {
        return getterTransactionalsService.findAllTransactionBy(idAllottee, pageable);
    }

}
