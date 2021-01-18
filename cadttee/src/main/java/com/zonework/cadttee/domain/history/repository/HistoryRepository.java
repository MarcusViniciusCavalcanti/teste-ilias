package com.zonework.cadttee.domain.history.repository;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.history.entity.HistoryOperationAllottee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryOperationAllottee, Integer> {

    List<HistoryOperationAllottee> findAllByAllottee(Allottee allottee);

}
