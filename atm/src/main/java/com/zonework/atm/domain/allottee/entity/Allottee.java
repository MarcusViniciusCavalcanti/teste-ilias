package com.zonework.atm.domain.allottee.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "ALLOTTEE")
@Data
public class Allottee {
    @Id
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CPF")
    private String cpf;

    @Column(name = "RETIREMENT_BALANCE")
    private BigDecimal retirementBalance;

    @Column(name = "RETIREMENT_VALUE")
    private BigDecimal retirementValue;

    @Column(name = "AMOUNT_YEARS")
    private int amoutYears;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creation;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime update;

    @Enumerated(EnumType.STRING)
    private StatusAllotteEnum status;

    @PrePersist
    private void creation() {
        creation = LocalDateTime.now(ZoneId.systemDefault());
        update = LocalDateTime.now(ZoneId.systemDefault());
    }

    @PreUpdate
    private void update() {
        update = LocalDateTime.now(ZoneId.systemDefault());
    }
}
