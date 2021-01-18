package com.zonework.cadttee.domain.allottee.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ALLOTTEE_UPDATE")
public class AllotteeUpate {
    @Id
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CPF")
    private String cpf;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "RETIREMENT_BALANCE")
    private BigDecimal retirementBalance;

    @Column(name = "AMOUNT_YEARS")
    private int amoutYears;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creation;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime update;
}
