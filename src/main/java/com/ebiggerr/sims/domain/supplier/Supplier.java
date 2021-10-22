package com.ebiggerr.sims.domain.supplier;

import com.ebiggerr.sims.domain.BaseEntity;
import com.ebiggerr.sims.domain.item.Item;

import javax.persistence.*;

@Entity
@Table(name = "supplier")
public class Supplier extends BaseEntity {

    private String name;

    private String remarks;

    private String contactNumber;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "\"supplierId\"",name="id" ,nullable = false,insertable = false, updatable = false)
    private Item item;*/

}
