package com.zonework.atm.domain.transactional.service;

import com.zonework.atm.domain.allottee.repository.AllotteeRepository;
import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.domain.transactional.entity.TypeTranctional;
import com.zonework.atm.domain.transactional.factory.FactoryTransactional;
import com.zonework.atm.domain.transactional.repository.TransactionalRepository;
import com.zonework.atm.struture.dto.ResponseTransactionalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GetterTransactionalsService {
    private final AllotteeRepository allotteeRepository;
    private final TransactionalRepository transactionalRepository;

    public Page<ResponseTransactionalDTO> findAllTransactionBy(Integer idAllottee, Pageable pageable) {
        var allottee = allotteeRepository.findById(idAllottee)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Entity by id %d not found", idAllottee)));

        var page = transactionalRepository.findAllByAllottee(allottee, pageable);
        var transactionals = page.stream()
            .map(FactoryTransactional::createResponse)
            .collect(Collectors.toList());

        return new PageImpl<>(transactionals, pageable, page.getTotalElements());
    }

    public List<TransactionalBalance> findExpiredTransactiona(LocalDateTime localDateTime) {
        return transactionalRepository.findAll(filterBy(localDateTime));
    }

    private static Specification<TransactionalBalance> filterBy(LocalDateTime time) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return Specification.where(filterDate(time))
                .and(filterStatus(StatusTransactionalEnum.AWATTING).or(filterStatus(StatusTransactionalEnum.SEND)))
                .and(filterType())
                .toPredicate(root, query, criteriaBuilder);

        };
    }

    private static Specification<TransactionalBalance> filterDate(LocalDateTime dateTime) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("date"), dateTime);
    }

    private static Specification<TransactionalBalance> filterStatus(StatusTransactionalEnum statusTransactionalEnum) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), statusTransactionalEnum);
    }

    private static Specification<TransactionalBalance> filterType() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), TypeTranctional.INCREMENT);
    }
}
