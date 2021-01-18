package com.zonework.atm.domain.allottee.repository;

import com.zonework.atm.domain.allottee.entity.Allottee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllotteeRepository extends JpaRepository<Allottee, Integer> {

    Optional<Allottee> findByCpf(String cpf);
}
