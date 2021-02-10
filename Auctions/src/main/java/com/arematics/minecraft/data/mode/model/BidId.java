package com.arematics.minecraft.data.mode.model;

import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class BidId implements Serializable {
    @Id
    private Long auctionId;
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID bidder;
}
