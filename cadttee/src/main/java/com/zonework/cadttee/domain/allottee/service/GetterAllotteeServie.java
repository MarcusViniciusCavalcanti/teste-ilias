package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.factory.AllotteeFactory;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GetterAllotteeServie {
    private static final String MESSAGE = "Allottee not found by: %d";

    private final AllotteeRepository allotteeRepository;

    public Page<AllotteeDTO> findAllByPage(Pageable pageable) {
        var page = allotteeRepository.findAll(pageable);
        var allottesDTOs = page.stream()
            .map(AllotteeFactory::buildEntityByAllottee)
            .collect(Collectors.toList());

        return new PageImpl<>(allottesDTOs, pageable, page.getTotalElements());
    }

    public AllotteeDTO findById(Integer id) {
        return allotteeRepository.findById(id)
            .map(AllotteeFactory::buildEntityByAllottee)
            .orElseThrow(() -> new EntityNotFoundException(String.format(MESSAGE, id)));
    }

}
