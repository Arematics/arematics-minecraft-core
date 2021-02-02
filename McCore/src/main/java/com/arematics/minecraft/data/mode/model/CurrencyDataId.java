package com.arematics.minecraft.data.mode.model;

import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Embeddable
public class CurrencyDataId implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID executor;
    @Id
    private Timestamp executed;
}
