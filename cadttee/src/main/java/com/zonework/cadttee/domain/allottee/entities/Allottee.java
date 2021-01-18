package com.zonework.cadttee.domain.allottee.entities;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "STATUS_ID")
    private StatusAllotte status;

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
