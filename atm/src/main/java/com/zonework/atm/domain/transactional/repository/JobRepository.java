package com.zonework.atm.domain.transactional.repository;

import com.zonework.atm.domain.transactional.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobApplication, Integer> {

    Optional<JobApplication> findByName(String name);
}
