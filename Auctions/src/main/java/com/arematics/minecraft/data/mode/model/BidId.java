package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BidId implements Serializable {
    @Id
    private Long auctionId;
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID bidder;
}
