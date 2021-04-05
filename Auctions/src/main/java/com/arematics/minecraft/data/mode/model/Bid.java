package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(BidId.class)
@Embeddable
@Table(name = "auction_bid")
public class Bid implements Serializable, Comparable<Bid> {
    @Id
    private Long auctionId;
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID bidder;
    private double amount;

    @Override
    public int compareTo(Bid o) {
        return Double.compare(amount, o.getAmount());
    }
}
