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
@Table(name = "player_auction_settings")
public class PlayerAuctionSettings implements Serializable {

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @OneToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    private AuctionCategory category;
    @Enumerated(EnumType.STRING)
    private AuctionType auctionType;
    @Enumerated(EnumType.STRING)
    private AuctionSort auctionSort;
    private String search;
}
