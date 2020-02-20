package com.ameren.outage.order.core.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable {

    @CreatedDate
    @Column(name = "create_timestamp")
    private Timestamp createTimestamp;

    @LastModifiedDate
    @Column(name = "update_Timestamp")
    private Timestamp updateTimestamp;

}
