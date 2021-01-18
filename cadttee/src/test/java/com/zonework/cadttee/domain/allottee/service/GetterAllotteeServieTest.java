package com.zonework.cadttee.domain.allottee.service;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetterAllotteeServieTest {

    @Mock
    private AllotteeRepository allotteeRepository;

    @InjectMocks
    private GetterAllotteeServie getterAllotteeServie;

    @Test
    void shouldHaveReturnAll() {
        var list = IntStream.range(0, 5)
            .mapToObj(index -> {
                var allottee = new Allottee();
                var status = new StatusAllotte();

                status.setValue(StatusAllotteEnum.ACTIVE.getStatusEnumName());
                allottee.setStatus(status);
                return allottee;
            }).collect(Collectors.toList());

        var pageRequest = PageRequest.of(0, 5);
        var page = new PageImpl<>(list, pageRequest, list.size());

        when(allotteeRepository.findAll(pageRequest)).thenReturn(page);

        var result = getterAllotteeServie.findAllByPage(pageRequest);
        assertEquals(list.size(), result.getContent().size());
    }

}
