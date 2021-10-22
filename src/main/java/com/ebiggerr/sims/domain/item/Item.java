package com.ebiggerr.sims.domain.item;

import com.ebiggerr.sims.domain.BaseEntity;
import com.ebiggerr.sims.domain.inventory.Inventory;
import com.ebiggerr.sims.domain.supplier.Supplier;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="item")
public class Item extends BaseEntity {

    private String sku;

    private String name;

    private String description;

    private byte[] image;

    private String category;

    @Column(name="\"subCategory\"")
    private String subCategory;

    /*@OneToMany(fetch= FetchType.LAZY, mappedBy = "item")
    private List<Supplier> suppliers;*/

    /*@OneToOne
    private Inventory inventory;*/
}
