package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(BidId.class)
@Table(name = "auction_bid")
public class Bid {
    @Id
    private String auctionId;
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID bidder;
    private double amount;
}
