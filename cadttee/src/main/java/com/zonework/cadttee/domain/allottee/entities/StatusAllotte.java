package com.zonework.cadttee.domain.allottee.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "STATUS_ALLOTTEE")
public class StatusAllotte {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "ALLOWS_REGISTRY")
    @Accessors(fluent = true)
    private Boolean allowsRegistry;

    @Column(name = "ALLOWS_EXCLUSION")
    @Accessors(fluent = true)
    private Boolean allowsExclusion;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creation;

    @Column(name = "PUBLISH")
    @Accessors(fluent = true)
    private Boolean publish;

    @Column(name = "ASYNCHRONOUS_PROCESSING")
    @Accessors(fluent = true)
    private Boolean allowsAsynchronousProcessing;

    @Column(name = "ALLOWS_UPDATE")
    @Accessors(fluent = true)
    private Boolean allowsUpdate;

}
