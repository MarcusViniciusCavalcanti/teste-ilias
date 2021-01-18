package com.zonework.cadttee.domain.history.entity;

import com.zonework.cadttee.domain.allottee.entities.Allottee;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "HISTORY_OPERATION_ALLOTTEE")
@Data
public class HistoryOperationAllottee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "OPERATION")
    private String operation;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "DATE")
    private LocalDateTime date;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ALLOTTEE_ID", columnDefinition = "INTEGER")
    private Allottee allottee;

    @PrePersist
    private void create() {
        date = LocalDateTime.now(ZoneId.systemDefault());
    }
}
