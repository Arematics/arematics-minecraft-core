package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.auctions.handler.AuctionSort;
import com.arematics.minecraft.auctions.handler.AuctionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_auction_settings")
public class PlayerAuctionSettings {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @OneToOne
    @JoinColumn(name = "auction_category", referencedColumnName = "category_id")
    private AuctionCategory category;
    @Enumerated(EnumType.STRING)
    private AuctionType auctionType;
    @Enumerated(EnumType.STRING)
    private AuctionSort auctionSort;
    private String search;
}
