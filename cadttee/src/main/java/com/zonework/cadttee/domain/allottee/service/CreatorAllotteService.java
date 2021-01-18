package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.factory.AllotteeFactory;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
import com.zonework.cadttee.structure.exception.BusinessException;
import com.zonework.cadttee.structure.message.ReasonMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import javax.persistence.EntityExistsException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreatorAllotteService {
    private final AllotteeRepository allotteeRepository;
    private final ExecutorStatusAllotteeService executorStatusAllotteeService;

    @Transactional
    public AllotteeDTO create(AllotteInput allotteInput) {
        log.info("Running process to create new allotte");

        try {
            var newAllottee = createAndSave(allotteInput);
            return createAllotteeDTO(newAllottee);
        } catch (DataIntegrityViolationException exception) {
            throw new EntityExistsException(exception);
        }
    }

    @Transactional
    public AllotteeDTO active(AllotteInput allotteInput) {
        log.info("Running process to active or create new allotte");

        try {
            var reponse = new AtomicReference<AllotteeDTO>();
            allotteeRepository.findByCpf(allotteInput.getCpf())
                .ifPresentOrElse(
                    activeAllottee(allotteInput, reponse),
                    () -> reponse.set(create(allotteInput))
                );
            return reponse.get();
        } catch (DataIntegrityViolationException exception) {
            throw new EntityExistsException(exception);
        }
    }

    private Allottee createAndSave(AllotteInput allotteInput) {
        var allottee = AllotteeFactory.buildEntityByInput(allotteInput);
        allottee.setRetirementBalance(BigDecimal.ZERO);
        return executorStatusAllotteeService.adjustmentStatusAllottee(allottee,
            StatusAllotteEnum.REGISTRATION_PENDING,
            ReasonMessages.MESSAGE_000.getCode());
    }

    private Consumer<Allottee> activeAllottee(AllotteInput allotteInput,
                                              AtomicReference<AllotteeDTO> response) {
        return allottee -> {
            if (allottee.getStatus().allowsRegistry()) {
                AllotteeFactory.replaceBy(allottee, allotteInput);
                configureAllottee(response, allottee);
            } else {
                throw new BusinessException(ReasonMessages.MESSAGE_006.getCode());
            }
        };
    }

    private void configureAllottee(AtomicReference<AllotteeDTO> reponse, Allottee allottee) {
        allottee.setRetirementBalance(BigDecimal.ZERO);

        var updateStatusAllottee = executorStatusAllotteeService
            .adjustmentStatusAllottee(allottee, StatusAllotteEnum.REGISTRATION_PENDING,
                ReasonMessages.MESSAGE_000.getCode());
        reponse.set(createAllotteeDTO(updateStatusAllottee));
    }

    private static AllotteeDTO createAllotteeDTO(Allottee newAllottee) {
        return AllotteeDTO.builder()
            .retirementBalance(newAllottee.getRetirementBalance())
            .name(newAllottee.getName())
            .email(newAllottee.getEmail())
            .cpf(newAllottee.getCpf())
            .amoutYears(newAllottee.getAmoutYears())
            .retirementBalance(BigDecimal.ZERO)
            .build();
    }

}
