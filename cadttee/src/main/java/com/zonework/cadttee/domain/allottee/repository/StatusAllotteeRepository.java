package com.zonework.cadttee.domain.allottee.repository;


import com.zonework.cadttee.domain.allottee.entities.StatusAllotte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusAllotteeRepository extends JpaRepository<StatusAllotte, Integer> {

    Optional<StatusAllotte> findByValue(String value);

}
