package com.zonework.atm.rest;

import com.zonework.atm.application.input.NewTransactionaInput;
import com.zonework.atm.application.service.AllotteeApplicationService;
import com.zonework.atm.application.service.TransactionalService;
import com.zonework.atm.rest.error.IlegalRequestBodyException;
import com.zonework.atm.struture.dto.ResponseAllotteeDTO;
import com.zonework.atm.struture.dto.ResponseTransactionalDTO;
import com.zonework.atm.struture.dto.TransactionalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transactional")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionController {

    private final AllotteeApplicationService allotteeApplicationService;
    private final TransactionalService transactionalService;

    @GetMapping("/allottee/{id}")
    public ResponseEntity<ResponseAllotteeDTO> getAllottee(@PathVariable("id") Integer id) {
        var response = allotteeApplicationService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseTransactionalDTO> create(@Valid @RequestBody NewTransactionaInput input, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IlegalRequestBodyException("create new trasanctional", bindingResult);
        }

        var response = transactionalService.createNewTransctional(input);

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{allotteId}")
    public ResponseEntity<Page<ResponseTransactionalDTO>> getAll(@PathVariable("allotteId") Integer idAllottee, Pageable pageable) {
        var pages = transactionalService.getAllTransactionaByAllotteeId(idAllottee, pageable);
        return ResponseEntity.ok(pages);
    }
}

