package com.zonework.atm.domain.transactional.repository;

import com.zonework.atm.domain.allottee.entity.Allottee;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionalRepository extends JpaRepository<TransactionalBalance, Integer>,
    JpaSpecificationExecutor<TransactionalBalance> {

    Page<TransactionalBalance> findAllByAllottee(Allottee allottee, Pageable pageable);
}
