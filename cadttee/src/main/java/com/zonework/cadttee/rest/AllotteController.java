package com.zonework.cadttee.rest;

import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.application.service.AllotteService;
import com.zonework.cadttee.rest.error.IlegalRequestBodyException;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/allottee")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AllotteController {

    private final AllotteService allotteService;

    @GetMapping
    public ResponseEntity<Page<AllotteeDTO>> getAll(Pageable pageable) {

        var page = allotteService.getPageAll(pageable);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AllotteeDTO> getAll(@PathVariable("id") Integer id) {

        var allotteeDTO = allotteService.getAllotteeById(id);
        return ResponseEntity.ok(allotteeDTO);

    }

    @PostMapping
    public ResponseEntity<AllotteeDTO> save(@Valid @RequestBody AllotteInput allotteInput, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IlegalRequestBodyException("Invalid request body", bindingResult);
        }

        log.info("Request POST receive");
        log.debug("With body: {}", allotteInput);

        var allotteeDTO = allotteService.createNewAllotte(allotteInput);

        log.info("Request complete");
        return ResponseEntity.accepted().body(allotteeDTO);
    }

    @PostMapping("/active")
    public ResponseEntity<AllotteeDTO> active(@Valid @RequestBody AllotteInput allotteInput, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IlegalRequestBodyException("Invalid request body", bindingResult);
        }

        log.info("Request POST receive");
        log.debug("With body: {}", allotteInput);

        var allotteeDTO = allotteService.activeAllottee(allotteInput);

        log.info("Request complete");
        return ResponseEntity.accepted().body(allotteeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AllotteeDTO> edit(@PathVariable("id") Integer id,
                                            @Valid @RequestBody AllotteInput allotteInput,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IlegalRequestBodyException("Invalid request body", bindingResult);
        }

        var allotteeDTO = allotteService.editAllottee(allotteInput, id);
        return ResponseEntity.accepted().body(allotteeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") Integer id) {
        allotteService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
