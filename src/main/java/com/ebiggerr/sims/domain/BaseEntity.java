package com.ebiggerr.sims.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name="\"isDeleted\"")
    protected boolean isDeleted;

    @Column(name ="\"creationTime\"")
    private LocalDateTime creationTime;

    @Column(name ="\"lastModificationTime\"")
    private LocalDateTime lastModificationTime;

    protected BaseEntity(){
        this.creationTime = LocalDateTime.now();
        this.isDeleted = false;
    }

    /*private BaseEntity(UUID id, boolean isDeleted, LocalDateTime creationTime, LocalDateTime lastModificationTime) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
    }*/

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

    public String getIdInString(){ return this.id.toString(); }

}
