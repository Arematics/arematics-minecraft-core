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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID creator;
    private double startPrice;
    private double instantSell;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] sell;
    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private AuctionCategory auctionCategory;
    @Enumerated(EnumType.STRING)
    private AuctionType auctionType;
    private Timestamp endTime;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "auctionId", referencedColumnName = "auctionId")
    private Set<Bid> bids;
}
