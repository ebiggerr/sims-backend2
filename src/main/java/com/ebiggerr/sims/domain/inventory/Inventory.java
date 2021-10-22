package com.ebiggerr.sims.domain.inventory;

import com.ebiggerr.sims.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/*@Entity
@Table(name="inventory")*/
public class Inventory extends BaseEntity {

    private UUID itemId;

    private String remark;

}
