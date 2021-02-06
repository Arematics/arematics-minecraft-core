package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auction")
public class Auction implements Serializable {
    @Id
    private Long auctionId;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID creator;
    private double startPrice;
    private double instantSell;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] sell;
    @OneToOne
    @JoinColumn(name = "auction_category", referencedColumnName = "category_id")
    private AuctionCategory auctionCategory;
    private Timestamp endTime;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "auction_bids", joinColumns = @JoinColumn(name = "auction_id"))
    private Set<Bid> bids;
}
