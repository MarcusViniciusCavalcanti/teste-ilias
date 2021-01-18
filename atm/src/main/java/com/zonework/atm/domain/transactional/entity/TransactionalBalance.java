package com.zonework.atm.domain.transactional.entity;

import com.zonework.atm.domain.allottee.entity.Allottee;
import lombok.Data;
import lombok.ToString;

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
import javax.persistence.Table;

@Data
@Entity
@Table(name = "TRANSACTIONAL_BALANCE")
public class TransactionalBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "VALUE")
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    private TypeTranctional type;

    @Enumerated(EnumType.STRING)
    private StatusTransactionalEnum status;

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
