package com.zonework.atm.domain.transactional.entity;

import lombok.Data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "JOB")
public class JobApplication {

    @Id
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LAST_EXECUTION")
    private LocalDateTime lastExecution;

}
