package com.ebiggerr.sims.domain.batch;

import com.ebiggerr.sims.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="batch")
public class Batch extends BaseEntity {

    @Column(name="\"itemId\"")
    private UUID itemId;

    @Column(name="\"supplierId\"")
    private UUID supplierId;

    @Column(name="\"expiryDateOfBatch\"")
    private LocalDateTime expiryDateOfBatch;

    @Column(name="\"batchUnitPrice\"")
    private BigDecimal batchUnitPrice;

    private String remarks;


}
