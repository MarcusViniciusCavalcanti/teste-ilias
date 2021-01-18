package com.zonework.cadttee.domain.allottee.repository;

import com.zonework.cadttee.domain.allottee.entities.AllotteeUpate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllotteeUpateRepository extends CrudRepository<AllotteeUpate, Integer> {

}
