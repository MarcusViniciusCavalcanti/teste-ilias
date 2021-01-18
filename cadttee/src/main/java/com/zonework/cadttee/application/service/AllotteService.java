package com.zonework.cadttee.application.service;

import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.domain.allottee.service.CreatorAllotteService;
import com.zonework.cadttee.domain.allottee.service.ExecutorExclutionAllotteeService;
import com.zonework.cadttee.domain.allottee.service.GetterAllotteeServie;
import com.zonework.cadttee.domain.allottee.service.UpdatorAllotteeService;
import com.zonework.cadttee.structure.dto.AllotteeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AllotteService {
    private final CreatorAllotteService creatorAllotteService;
    private final GetterAllotteeServie getterAllotteeServie;
    private final UpdatorAllotteeService updatorAllotteeService;
    private final ExecutorExclutionAllotteeService executorExclutionAllotteeService;

    public AllotteeDTO createNewAllotte(AllotteInput input) {
        return creatorAllotteService.create(input);
    }

    public Page<AllotteeDTO> getPageAll(Pageable pageable) {
        return getterAllotteeServie.findAllByPage(pageable);
    }

    public AllotteeDTO editAllottee(AllotteInput input, Integer id) {
        return updatorAllotteeService.editeAllottee(input, id);
    }

    public void remove(Integer id) {
        executorExclutionAllotteeService.remove(id);
    }

    public AllotteeDTO getAllotteeById(Integer id) {
        return getterAllotteeServie.findById(id);
    }

    public AllotteeDTO activeAllottee(AllotteInput allotteInput) {
        return creatorAllotteService.active(allotteInput);
    }

}
