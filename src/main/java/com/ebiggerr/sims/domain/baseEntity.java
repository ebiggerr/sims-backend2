package com.ebiggerr.sims.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public class baseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    protected boolean isDeleted;

    private LocalDateTime creationTime;

    private LocalDateTime lastModificationTime;

    public boolean isDeleted(){
        return this.isDeleted;
    }

    public LocalDateTime getLastModificationTime(){
        return this.lastModificationTime;
    }

    public LocalDateTime getCreationTime(){
        return this.creationTime;
    }

    public void delete(){
        this.isDeleted = true;
    }

    public UUID getId(){
        return this.id;
    }
}
